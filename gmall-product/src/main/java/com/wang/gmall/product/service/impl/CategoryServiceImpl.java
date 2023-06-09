package com.wang.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.wang.gmall.product.vo.Category2VO;
import com.wang.gmall.product.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.CategoryDao;
import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.CategoryService;
import redis.clients.jedis.Jedis;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryVO> listWithTree() {
        List<CategoryEntity> categoryEntities = categoryService.list();
        List<CategoryVO> categoryVOS = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(categoryEntity, categoryVO);
            categoryVOS.add(categoryVO);
        }
        List<CategoryVO> levelMenu = categoryVOS.stream()
                .filter(item -> item.getParentCid()==0)
                .map(menu->{
                    menu.setChildCategoryEntity(getChildCategory(menu,categoryVOS));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return levelMenu;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        categoryDao.deleteBatchIds(catIds);
    }

    @Override
    public List<CategoryEntity> getLevel1Cate() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper.eq("parent_cid","0"));
    }

    /**
     * 使用springcache进行缓存
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    public Map<String,List<Category2VO>> getWithSpringCache() throws InterruptedException {
        return this.getCategoryMap();
    }


    @CacheEvict(value = {"category"},allEntries = true)
    public void deleteCache(){
    }


    /**
     * 优化使用缓存，解决缓存击穿问题
     */
    public Map<String,List<Category2VO>> getWithLock() throws InterruptedException {
        //设置只有当前持有锁用户才可以删除锁
        String uuid = UUID.randomUUID().toString();
        ValueOperations<String,String> ops = stringRedisTemplate.opsForValue();
        //设置锁过期时间具有原子性,但是依然存在问题，可能锁过期了还没有执行完查询操作，需要锁自动延期
        Boolean lock = ops.setIfAbsent("lock",uuid,20000, TimeUnit.MILLISECONDS);
        if(lock){
            Map<String,List<Category2VO>> map = getCategoryMap();
            String lockValue = ops.get("lock");
            //TODO 使用lua脚本，将删除锁操作和判断锁是否正确拥有原子性
            String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            stringRedisTemplate.execute(new DefaultRedisScript<Long>(luaScript,Long.class), Arrays.asList("lock"), lockValue);
            return map;
        }else {
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e);
            }
            //自旋锁
            return getWithLock();
        }
    }

    /**
     *使用缓存获取三级菜单
     * @return
     */
    public Map<String,List<Category2VO>> getCategoryMap() throws InterruptedException {
        ValueOperations<String,String> ops = stringRedisTemplate.opsForValue();
        String category = ops.get("category");
        if(category==null){
            System.out.println("没有数据数据库查一次");
            Thread.sleep(5000);
            Map<String,List<Category2VO>> categoryMap = this.getCatalogJson();
            category = JSON.toJSONString(categoryMap);
            ops.set("category",category);
            return categoryMap;
        }
        return JSON.parseObject(category,new TypeReference<Map<String,List<Category2VO>>>(){});
    }


    @Override
    public Map<String, List<Category2VO>> getCatalogJson() {
        //获取所有2级菜单
        List<CategoryEntity> level2cates = this.list(new QueryWrapper<CategoryEntity>().eq("cat_level","2"));
        //优化：先查出所有3级菜单
//        List<CategoryEntity> level3cates = this.list(new QueryWrapper<CategoryEntity>().eq("cat_level","3"));
        //封装catelog2VO
        List<Category2VO> category2VOS = level2cates.stream().map(entity->{
            //封装catelog3VO
            List<CategoryEntity> level3cates = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",entity.getCatId()));
            List<Category2VO.Category3Vo> category3VOS =level3cates.stream().map(level3->{
//            List<Category2VO.Category3Vo> category3VOS =level3cates.stream().filter(l->(l.getParentCid().equals(entity.getCatId()))).map(level3->{
                Category2VO.Category3Vo category3Vo = new Category2VO.Category3Vo();
                category3Vo.setCatalog2Id(level3.getParentCid().toString());
                category3Vo.setId(level3.getCatId().toString());
                category3Vo.setName(level3.getName());
                return category3Vo;
            }).collect(Collectors.toList());
            //填入封装catelog2VO属性
            Category2VO category2VO = new Category2VO();
            category2VO.setCatalog3List(category3VOS);
            category2VO.setCatalog1Id(entity.getParentCid().toString());
            category2VO.setId(entity.getCatId().toString());
            category2VO.setName(entity.getName());
            return category2VO;
        }).collect(Collectors.toList());
        //将2级菜单封装为map
        Map<String,List<Category2VO>> cateMap = new HashMap<>();
        for (Category2VO category2VO : category2VOS) {
            List<Category2VO> list = cateMap.getOrDefault(category2VO.getCatalog1Id(), new ArrayList<>());
            list.add(category2VO);
            cateMap.put(category2VO.getCatalog1Id(),list);
        }
        return cateMap;
    }


    public List<CategoryVO> getChildCategory(CategoryVO pCategory,List<CategoryVO> allCategory){
        List<CategoryVO> childMenu = allCategory.stream()
                .filter(item->{
                    return item.getParentCid().equals(pCategory.getCatId());
                })
                .map(menu->{
                    menu.setChildCategoryEntity(getChildCategory(menu,allCategory));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return childMenu;
    }

}
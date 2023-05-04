package com.wang.gmall.product.web;

import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.impl.CategoryServiceImpl;
import com.wang.gmall.product.vo.Category2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {


    @Autowired
    CategoryServiceImpl categoryService;

    @GetMapping({"/","/index.html"})
    public String getIndex(Model model){
        List<CategoryEntity> categories = categoryService.getLevel1Cate();
        model.addAttribute("categories",categories);
        return "index";
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String,List<Category2VO>> getCatalogJson() throws InterruptedException {
        return categoryService.getWithSpringCache();
    }

    @ResponseBody
    @GetMapping("index/deletecache")
    public void deleteCache() {
        categoryService.deleteCache();
    }

}

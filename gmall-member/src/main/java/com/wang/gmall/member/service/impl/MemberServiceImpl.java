package com.wang.gmall.member.service.impl;

import com.wang.common.exception.MobileExistException;
import com.wang.common.exception.UserExistException;
import com.wang.common.vo.RegistVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.member.dao.MemberDao;
import com.wang.gmall.member.entity.MemberEntity;
import com.wang.gmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(RegistVO registVO) {
        //检查手机号是否存在
        checkIsPhoneExist(registVO.getMobile());
        //检查用户名是否存在
        checkIsUserExist(registVO.getUsername());
        //将用户存入数据库
        MemberEntity member = new MemberEntity();
        member.setUsername(registVO.getUsername());
        member.setMobile(registVO.getMobile());
        member.setCreateTime(new Date());
        //密码加密存入
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(registVO.getPassword());
        member.setPassword(encodePassword);
        this.save(member);
    }

    public void checkIsPhoneExist(String phoneNum){
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile",phoneNum));
        if (count>0){
            throw new MobileExistException();
        }
    }

    public void checkIsUserExist(String userName){
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username",userName));
        if (count>0){
            throw new UserExistException();
        }
    }

}
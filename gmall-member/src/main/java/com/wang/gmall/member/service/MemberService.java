package com.wang.gmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.common.vo.RegistVO;
import com.wang.gmall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:14:38
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(RegistVO registVO);
}


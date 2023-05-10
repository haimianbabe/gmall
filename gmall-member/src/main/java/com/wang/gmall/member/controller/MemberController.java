package com.wang.gmall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.wang.common.exception.BizCodeEnum;
import com.wang.common.exception.MobileExistException;
import com.wang.common.exception.UserExistException;
import com.wang.common.vo.RegistVO;
import com.wang.gmall.member.feign.CouponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wang.gmall.member.entity.MemberEntity;
import com.wang.gmall.member.service.MemberService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * 会员
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:14:38
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/regist")
    public R regist(@RequestBody RegistVO registVO){
        try {
            memberService.regist(registVO);
        }catch (UserExistException userExistException){
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }catch (MobileExistException mobileExistException){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/coupon")
    public R getCouponList(){
        return couponFeignService.getCouponList();
    }

}

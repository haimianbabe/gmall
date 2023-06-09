package com.wang.gmall.product.exception;

import com.wang.common.exception.BizCodeEnum;
import com.wang.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.wang.gmall.product.controller")
public class GmallValidateException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}, 异常类型:{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        // 获取校验的错误结果
        result.getFieldErrors().forEach((item) -> {
            // 获取错误的属性名字 + 获取到错误提示FieldError
            errorMap.put(item.getField(), item.getDefaultMessage());
        });
        return R.ok().error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data",errorMap);
    }

//    @ExceptionHandler(value = Throwable.class)
//    public R handleValidException(Throwable throwable) {
//        log.error("Throwable错误，未处理：" + throwable);
//        return R.ok().error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
//    }

}

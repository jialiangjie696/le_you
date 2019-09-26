package com.leyou.common.advice;


import com.leyou.common.exception.ExceptionResult;
import com.leyou.common.exception.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice  //所有的controller都会经过这个类
public class BaseExceptionAdvice {


    @ExceptionHandler(LyException.class)  //如果出现了LyException的异常都会进入这个方法
    public ResponseEntity<ExceptionResult>  handleException(LyException e){

        return ResponseEntity.ok(new ExceptionResult(e));

    }

}

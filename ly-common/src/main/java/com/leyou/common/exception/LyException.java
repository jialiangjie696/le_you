package com.leyou.common.exception;


import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;


@Data
public class LyException extends RuntimeException {


    private int status;

   /* public LyException(int status,String message) {
        super(message);
        this.status = status;
    }*/

    public LyException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMassage());
        this.status = exceptionEnum.getStatus();
    }
}

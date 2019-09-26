package com.leyou.common.exception;


import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class ExceptionResult {

    private int status;
    private String massage;
    private String timestamp;



    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.massage = e.getMessage();
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }


}

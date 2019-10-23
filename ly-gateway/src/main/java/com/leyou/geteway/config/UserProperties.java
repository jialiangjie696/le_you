package com.leyou.geteway.config;

import lombok.Data;

@Data
public class UserProperties{
    private int expire;
    private String cookieName;
    private String cookieDomain;
}
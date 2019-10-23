package com.leyou.order.entity;

public class UserHolder {
    private static ThreadLocal threadLocal = new ThreadLocal();

    public static void setUserId(String userId){
        threadLocal.set(userId);
    }

    public static String getUserId(){
       return threadLocal.get().toString();
    }

    public static void romoveUserId(){
        threadLocal.remove();
    }
}

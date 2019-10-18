package com.leyou.user.clent;


import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClent {

    @GetMapping("/query")
    public UserDTO queryByUserNameAndPassword(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password
    );
}

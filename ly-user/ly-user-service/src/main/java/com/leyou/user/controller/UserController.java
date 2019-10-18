package com.leyou.user.controller;


import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entiy.User;
import com.leyou.user.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {


    @Autowired
    private UserService userService;


    /**
     *
     * @param data
     * @param type  1.用户名  2.手机号码
     * @return
     *      true:可用  表示表中没有此数据
     *      false:不可用   表示表中有数据
     *
     *
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(

            @PathVariable("data") String data,
            @PathVariable("type") Integer type
    ){

       Boolean b =  userService.checkData(data,type);
       return ResponseEntity.ok(b);
    }


    @GetMapping("/check1/{data1}/{type1}")
    @ApiOperation(value = "校验用户名数据是否可用，如果不存在则可用")
    @ApiResponses({
            @ApiResponse(code = 200, message = "校验结果有效，true或false代表可用或不可用"),
            @ApiResponse(code = 400, message = "请求参数有误，比如type不是指定值")
    })
    public ResponseEntity<Boolean> checkUserData(
            @ApiParam(value = "要校验的数据", example = "lisi") @PathVariable("data") String data,
            @ApiParam(value = "数据类型，1：用户名，2：手机号", example = "1") @PathVariable(value = "type") Integer type) {
        return ResponseEntity.ok(userService.checkData(data, type));
    }


    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping ("/code")
    public ResponseEntity<Boolean> sendCode(@RequestParam("phone") String phone ){

        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     *
     * @param user
     * @return
     */
    @PostMapping ("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code ){

        if (result.hasErrors()){//判断是否有错误

            //       errorMessage =  密码格式不正确|用户名格式不正确
            String errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("|"));
            throw new LyException(404,errorMessage);
        }



        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 登录功能
     * @param userName
     * @param password
     * @return
     */
    @GetMapping ("/query")
    public ResponseEntity<UserDTO> queryByUserNameAndPassword(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password

            ){

      UserDTO userDTO = userService.queryByUserNameAndPassword(userName,password);
        return ResponseEntity.ok(userDTO);
    }

}

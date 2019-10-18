package com.leyou.user.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.common.utlis.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entiy.User;
import com.leyou.user.mapper.UserMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.SMS_CODE_KEY;

@Service
public class UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


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
    public Boolean checkData(String data, Integer type) {

        User user = new User();  //接收的条件

        if (type==1){
            user.setUsername(data);

        }else if(type==2){
            user.setPhone(data);
        }else {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }


        int count = userMapper.selectCount(user);


        return count==0;
    }


    /**
     * 发送短信验证码
     * @param phone
     * @return
     */

    public void sendCode(String phone) {

//        -验证手机号格式     通过正则表达式
        if (!RegexUtils.isPhone(phone)){
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }

//        -生成验证码
        String s = RandomStringUtils.randomNumeric(6);

//        -保存验证码到redis
        stringRedisTemplate.boundValueOps("sms_code"+phone).set(s,5, TimeUnit.MINUTES);
//        -发送rabbitMQ消息到ly-sms
        Map<String ,String > map = new HashMap<>();
        map.put("phone",phone);
        map.put("signName","良杰旅游网");
        map.put("TemplateCode","SMS_171113243");
        map.put("TemplateParam","{\"code\":\""+s+"\"}");
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,SMS_CODE_KEY,map);

    }


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 验证码验证
     * @param user
     * @param code
     */
    public void register(User user, String code) {
//        -验证码验证  页面上输入的code  redis中获取
        String redisCode = stringRedisTemplate.boundValueOps("sms_code" + user.getPhone()).get();
        if (redisCode==null){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }

        if (!code.equals(redisCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
//        Md5Hash(原密码，自己的盐，加几次)
//        spring的加密算法  很变态 加随机盐
//        - 对密码加密
        String password = passwordEncoder.encode("123456");//加密

//        -写入数据库
        user.setPassword(password);
        userMapper.insertSelective(user);


    }


    /**
     * 登录功能
     * @param userName
     * @param password
     * @return
     */
    public UserDTO queryByUserNameAndPassword(String userName, String password) {
        //        select * from tb_user where username=lisi and password=123456
//        select * from tb_user where username=?
//        先通过用户名查询用户然后在比较密码

//        spring的BCrypt算法加密：加密的逻辑是会使用随机盐（我们不知道，但是spring框架知道）
        User u  = new User();
        u.setUsername(userName);
        User user = userMapper.selectOne(u);

        if(user==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        String passwordDb = user.getPassword(); // $2a$10$5EWTZySAYAAw5Ox6.QhSY.VHhV6GG/0aSEhhNViZt1p5Ixr8sv116
//        123456----Md5->sfoqiw6efturhuff-->123456
        boolean matches = passwordEncoder.matches(password, passwordDb); //比较密码
        if(!matches){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return BeanHelper.copyProperties(user,UserDTO.class);

    }
}

package com.leyou.sms.Utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.common.utlis.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SmsHelper {


    @Autowired
    private SmsProperties smsProperties;

    public void sendSms(String phone,String signName,String templateCode,String templateParam){


        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAccessKeyID(), smsProperties.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);//SMS_171113243
        request.putQueryParameter("TemplateParam", templateParam);//{"code":"1111"}
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            Map<String, String> messageResopnseMap = JsonUtils.toMap(data, String.class, String.class);
            if (!messageResopnseMap.get("Code").equals("OK")){

                log.error("发送短信异常");
            }

            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
            log.error("发送短信服务端异常");
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("发送短信客户端异常");
        }

    }

}

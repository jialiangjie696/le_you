package com.leyou.upload.service;


import com.aliyun.oss.OSS;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@Service
public class UploadService {


//    图片保存的路径
    private static final String SAVE_PATH = "F:\\sort\\nginx\\nginx-1.13.12\\nginx-1.13.12\\html\\brand-img\\";

//    图片返回的路径
    private static final String RETURN_IMAGE_URL="http://image.leyou.com/brand-img/";


//        定义常见的mime类型
//        imge/png   image/gif  image/jpeg
    private static final List<String> IMAGE_TYPE = Arrays.asList("image/png","image/gif","image/jpeg");

    public String upload(MultipartFile file) {

        try {


//        优化
//        1.只允许时图片
//        2.防止假图片
//        获取图片的mime类型
        String imageType = file.getContentType();//mime类型
        if (!IMAGE_TYPE.contains(imageType)){   //如果不包含就抛异常
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());  //只有真图片才能获得值
       if (bufferedImage==null){
//            表示时假图片
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }









//        file.getOriginalFilename();//图片的原名称

            String fileName = UUID.randomUUID() + file.getOriginalFilename();
//            上传图片路径+图片名称
            file.transferTo(new File(SAVE_PATH+fileName));

//                返回图片路径
            return RETURN_IMAGE_URL+fileName;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }



    }


    @Autowired
    private OSS client;

    @Autowired
    private OSSProperties properties;


    public Map<String, Object> signature() {

/*
        String accessId = "<yourAccessKeyId>"; // 请填写您的AccessKeyId。
        String accessKey = "<yourAccessKeySecret>"; // 请填写您的AccessKeySecret。
        String endpoint = "oss-cn-hangzhou.aliyuncs.com"; // 请填写您的 endpoint。
        String bucket = "bucket-name"; // 请填写您的 bucketname 。
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = "http://88.88.88.88:8888";
        String dir = "user-dir-prefix/"; // 用户上传文件时指定的前缀。*/

//        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        try {
//            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + properties.getExpireTime() * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, properties.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, properties.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<String, Object>();
            respMap.put("accessId", properties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", properties.getDir());
            respMap.put("host", properties.getHost());
            respMap.put("expire", expireEndTime);  //毫秒
            // respMap.put("expire", formatISO8601Date(expiration));


            return respMap;
        }catch (Exception e){


            throw new LyException(ExceptionEnum.INVALID_NOTIFY_SIGN);
        }

    }
}

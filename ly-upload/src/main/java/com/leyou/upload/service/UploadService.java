package com.leyou.upload.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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


}

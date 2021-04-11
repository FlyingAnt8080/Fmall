package com.suse.fmall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.suse.fmall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class FmallThirdPartyApplicationTests {
    @Resource
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;
    @Test
    void contextLoads() {

    }
    @Test
    void upload() throws FileNotFoundException {
        //上传文件流
        InputStream inputStream = new FileInputStream("C:\\Users\\hp\\Desktop\\photo.jpg");
        ossClient.putObject("fmall8080", "phone.jpg", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }

    @Test
    void testSms(){
//        try {
//            smsComponent.sendSmsCode("18384623913","34582");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

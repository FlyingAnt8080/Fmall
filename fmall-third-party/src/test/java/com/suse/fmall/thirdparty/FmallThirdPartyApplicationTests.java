package com.suse.fmall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class FmallThirdPartyApplicationTests {
    @Resource
    OSSClient ossClient;
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
}

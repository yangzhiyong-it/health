package com.itheima;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

public class QiNiuTest {
    @Test
    public void test() throws QiniuException {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "Y849bi9ex-w9QoDdFiCyXSYhm0fB0KopUWflFP4D";
        String secretKey = "qOVtVfelL83_acqb9T9M3bM8G_09RwokJPYD3Gw2";
        String bucket = "itcast22-health";
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\Administrator.USER-20191221KY\\Desktop\\截图资料\\Snipaste_2020-02-07_13-55-43.png";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "abc.png";

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            System.out.println("上传成功");
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            System.out.println("上传失败");
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    //删除七牛云服务器中的图片
    @Test
    public void test2() {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        String accessKey = "Y849bi9ex-w9QoDdFiCyXSYhm0fB0KopUWflFP4D";
        String secretKey = "qOVtVfelL83_acqb9T9M3bM8G_09RwokJPYD3Gw2";
        String bucket = "itcast22-health";
        String key = "abc.png";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
            System.out.println("删除成功");
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
            System.out.println("删除失败");
        }
    }
}

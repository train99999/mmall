package com.quan.service.impl;

import com.google.common.collect.Lists;
import com.quan.service.IFileService;
import com.quan.util.FTPUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    public String upload(MultipartFile file,String path){
        String originalFilename = file.getOriginalFilename();
        System.out.println("originalFilename="+originalFilename);
        //获取扩展名,从指定的索引处开始切割
        String fileExtensionName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        System.out.println("开始上传文件，上传的路径"+path+"上传的文件名"+uploadFileName);
        File fileDir = new File(path);
        // 如果不存在该路径
        if(!fileDir.exists()){
            // 赋予可写权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        // 创建文件
        File targetFile = new File(path,uploadFileName);
//        List<File> list = new ArrayList<>();
        try {
            // 把文件上传到 upload 文件夹下
            file.transferTo(targetFile);
            ArrayList<File> files = Lists.newArrayList(targetFile);

            // 将 targetFile 上传到 FTP 服务器上
            FTPUtil.uploadFile(files);
//            FTPUtil.uploadFile(list);
            // 上传完之后，删除 upload 下面的文件
//            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile.getName();
    }
}

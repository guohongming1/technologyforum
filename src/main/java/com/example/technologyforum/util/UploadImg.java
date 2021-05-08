package com.example.technologyforum.util;

import com.example.technologyforum.config.SysParamCache;
import com.example.technologyforum.constants.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2020/04/14
 */
@Controller
public class UploadImg {

    /**
     * 文件上传实现方式
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
      @ResponseBody
      @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
      public Map upload(MultipartFile file, HttpServletRequest request)throws IOException{

          //本地使用,上传位置
          //文件的完整名称,如spring.jpeg
          String filename = file.getOriginalFilename();
          //文件名,如spring
          String name = "userid" ;//filename.substring(0, filename.indexOf("."));
          //文件后缀,如.jpeg
          String suffix = filename.substring(filename.lastIndexOf("."));
          filename = name+suffix;
          //创建年月文件夹
          Calendar date = Calendar.getInstance();
          File dateDirs = new File(date.get(Calendar.YEAR)
                  + File.separator + (date.get(Calendar.MONTH) + 1));
          // 文件上传路径
          String path = SysParamCache.getParam(Constants.UPLOAD_PATH);
          if(path == null){
              path = "D:\\Picturesupload";
          }
          //目标文件
          File descFile = new File(path + File.separator + dateDirs + File.separator + filename);
          int i = 1;
          //若文件存在重命名
          String newFilename = filename;
          while (descFile.exists()) {
              newFilename = name + "-" + i + suffix;
              String parentPath = descFile.getParent();
              descFile = new File(parentPath + File.separator + newFilename);
              i++;
          }
          //判断目标文件所在的目录是否存在
          if (!descFile.getParentFile().exists()) {
              //如果目标文件所在的目录不存在，则创建父目录
              descFile.getParentFile().mkdirs();
          }
          //将内存中的数据写入磁盘
          file.transferTo(descFile);
          //完整的url
          String fileUrl = "/uploads/" + dateDirs + "/" + newFilename;


          Map<String,Object> map=new HashMap<>();
          Map<String,Object> map2=new HashMap<>();
          map.put("code",1);
          map.put("msg","");
          map2.put("src",fileUrl);
          map.put("data",map2);
          return map;

      }
}

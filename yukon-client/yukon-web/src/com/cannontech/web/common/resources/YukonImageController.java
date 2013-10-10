package com.cannontech.web.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.lite.LiteYukonUser;

@Controller
public class YukonImageController {

    @Autowired YukonImageDao yid;
    @Autowired ResourceLoader loader;
    @Autowired RolePropertyDao rpDao;

    @RequestMapping(value="/images/{id}", method=RequestMethod.GET)
    public void image(HttpServletResponse resp, @PathVariable int id) throws IOException, SQLException {
        
        LiteYukonImage image = yid.getLiteYukonImage(id);
        if (image == null) {
            if (id == YukonImage.DEFAULT_LOGO.getId()) {
                image = yid.add(YukonImage.DEFAULT_LOGO.getId(), 
                                YukonImage.DEFAULT_LOGO.getCategory(), 
                                YukonImage.DEFAULT_LOGO.getName(), 
                                loader.getResource(YukonImage.DEFAULT_LOGO.getPath()));
            } else if (id == YukonImage.DEFAULT_BACKGROUND.getId()) {
                image = yid.add(YukonImage.DEFAULT_BACKGROUND.getId(), 
                                YukonImage.DEFAULT_BACKGROUND.getCategory(), 
                                YukonImage.DEFAULT_BACKGROUND.getName(), 
                                loader.getResource(YukonImage.DEFAULT_BACKGROUND.getPath()));
            }
        }
        
        String name = image.getImageName();
        String ext = name.substring(name.lastIndexOf('.') + 1);
        
        String contentType = "image/" + (ext.equals("jpg") ? "jpeg" : ext);
        
        resp.setContentType(contentType);
        resp.getOutputStream().write(image.getImageValue());
    }
    
    @RequestMapping(value="/images", method=RequestMethod.POST)
    public @ResponseBody Map<String, Object> upload(HttpServletRequest request, LiteYukonUser user) {
        
        Map<String, Object> json = new HashMap<>();
        
        try {
            rpDao.verifyProperty(YukonRoleProperty.ADMIN_SUPER_USER, user);
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                throw new IllegalArgumentException("not multipart file");
            }
            
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = mRequest.getFile("file");
            if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
                throw new IllegalArgumentException("Blank file.");
            }
            
            if(!file.getContentType().startsWith("image")) {
                throw new IllegalArgumentException("Only image files are valid.");
            }
            
            InputStream inputStream = file.getInputStream();
            LiteYukonImage image = yid.add("logos", file.getOriginalFilename(), new InputStreamResource(inputStream));
            Map<String, Object> imageStats = new HashMap<>(); 
            imageStats.put("id", image.getImageID());
            imageStats.put("name", image.getImageName());
            imageStats.put("category", image.getImageCategory());
            imageStats.put("size", image.getImageValue().length);
            json.put("image", imageStats);
            
            json.put("status", "success");
        } catch (Exception e) {
            json.put("status", "error");
            json.put("message", e.getMessage());
        }
        
        return json;
    }
    
    private enum YukonImage {
        DEFAULT_LOGO (1, "logos", "eaton_logo.png", "classpath:com/cannontech/web/common/resources/eaton_logo.png"),
        DEFAULT_BACKGROUND (2, "backgrounds", "yukon_background.jpg", "classpath:com/cannontech/web/common/resources/yukon_background.jpg");
        
        private int id;
        private String category;
        private String name;
        private String path;
        
        private YukonImage(int id, String category, String name, String path) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.path = path;
        }
        
        public int getId() {
            return id;
        }
        
        public String getCategory() {
            return category;
        }
        
        public String getName() {
            return name;
        }
        
        public String getPath() {
            return path;
        }
    }
    
}
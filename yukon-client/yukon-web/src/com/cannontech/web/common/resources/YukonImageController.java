package com.cannontech.web.common.resources;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

@Controller
public class YukonImageController {

    @Autowired YukonImageDao yid;
    @Autowired ResourceLoader loader;
    @Autowired DbChangeManager dbChangeManager;

    @RequestMapping(value="/images/{id}", method=RequestMethod.GET)
    public void image(HttpServletResponse resp, @PathVariable int id) throws IOException, SQLException {
        
        LiteYukonImage image = yid.getLiteYukonImage(id);
        if (image == null) {
            if (id == YukonImage.DEFAULT_LOGO.getId()) {
                image = loadImage(YukonImage.DEFAULT_LOGO);
            } else if (id == YukonImage.DEFAULT_BACKGROUND.getId()) {
                image = loadImage(YukonImage.DEFAULT_BACKGROUND);
            }
        }
        
        String name = image.getImageName();
        String ext = name.substring(name.lastIndexOf('.') + 1);
        
        String contentType = "image/" + (ext.equals("jpg") ? "jpeg" : ext);
        
        resp.setContentType(contentType);
        resp.getOutputStream().write(image.getImageValue());
    }
    
    private LiteYukonImage loadImage(YukonImage image) throws IOException, SQLException {
            
        Resource resouce = loader.getResource(image.getPath());
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        PreparedStatement pstmt = null;
        
        try {
            
            pstmt = connection.prepareStatement("insert into YukonImage values (?, ?, ?, ?)");                    
            pstmt.setInt(1, image.getId());
            pstmt.setString(2, image.getCategory());
            pstmt.setString(3, image.getName());
            pstmt.setBinaryStream(4, resouce.getInputStream(), (int) resouce.getFile().length());
            pstmt.execute();
            
            DBChangeMsg dbChange = new DBChangeMsg(image.getId(), DBChangeMsg.CHANGE_YUKON_IMAGE, DBChangeMsg.CAT_STATEGROUP, DBChangeMsg.CAT_STATEGROUP, DbChangeType.ADD);
            dbChangeManager.processDbChange(dbChange);
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        }
        
        return yid.getLiteYukonImage(image.getId());
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
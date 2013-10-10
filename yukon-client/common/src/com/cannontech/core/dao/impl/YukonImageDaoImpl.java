package com.cannontech.core.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.FileCopyUtils;

import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
/**
 * @author rneuharth
 * @author alauinger
 * Aug 28, 2002 at 8:08:44 AM
 */
public final class YukonImageDaoImpl implements YukonImageDao {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private NextValueHelper nvh;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonJdbcTemplate template;
    
    @Override
    public List<String> getAllCategories() {
        
        List<String> categories = new ArrayList<>();

        synchronized (cache) {
            List<LiteYukonImage> images = cache.getAllYukonImages();
            Collections.sort(images, LiteComparators.liteYukonImageCategoryComparator);
            
            String currCat = null;
            for (LiteYukonImage image : images) {
                if (currCat == null) {
                    currCat = image.getImageCategory();
                }
                
                if (!currCat.equalsIgnoreCase(image.getImageCategory())) {
                    categories.add(image.getImageCategory());
                    currCat = image.getImageCategory();
                }
            }
        }
        
        return categories;
    }
    
   	public LiteYukonImage getLiteYukonImage(int id) {
   	    
   	    synchronized (cache) {
   	        Iterator<LiteYukonImage> iter = cache.getAllYukonImages().iterator();
            while (iter.hasNext()) {
               LiteYukonImage img = iter.next();
               if(img.getImageID() == id) {
                  return img;
               }
            }
        }        
        
        return null;
    }
      
    public LiteYukonImage getLiteYukonImage(String name) {
        synchronized (cache) {
            Iterator<LiteYukonImage> iter = cache.getAllYukonImages().iterator();
            while (iter.hasNext()) {
               LiteYukonImage img = iter.next();
               if (img.getImageName().equalsIgnoreCase(name)) {
                  return img;
               }
            }
        }        
        
        return null;
    }

    public String yukonImageUsage(int id) {
        synchronized (cache) {
            for (LiteStateGroup group : cache.getAllStateGroupMap().values()) {
                for (LiteState state : group.getStatesList()) {
                    if (state.getImageID() == id) {
                        return group.getStateGroupName();
                    }
                }
            }
        }
        
        //this image is not used, just return null
        return null;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public LiteYukonImage add(final int id, final String category, final String name, final Resource resource) throws IOException {
        
        InputStream in = resource.getInputStream();
        final File temp = File.createTempFile("yukon_image", ".tmp");
        OutputStream out = new FileOutputStream(temp);
        FileCopyUtils.copy(in, out);

        template.getJdbcOperations().update("insert into YukonImage values (?, ?, ?, ?)", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, id);
                ps.setString(2, category);
                ps.setString(3, name);
                try {
                    ps.setBinaryStream(4, new FileInputStream(temp), (int) temp.length());
                } catch (FileNotFoundException e) {
                    throw new SQLException("file not found");
                }
            }
        });
        
        DBChangeMsg dbChange = new DBChangeMsg(id, DBChangeMsg.CHANGE_YUKON_IMAGE, DBChangeMsg.CAT_STATEGROUP, DBChangeMsg.CAT_STATEGROUP, DbChangeType.ADD);
        dbChangeManager.processDbChange(dbChange);
        
        return getLiteYukonImage(id);
    }
    
    
    
    @Override
    public LiteYukonImage add(final String category, final String name, final Resource resource) throws IOException {
        return add(nvh.getNextValue("YukonImage"), category, name, resource);
    }
    
}
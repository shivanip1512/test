package com.cannontech.core.image.dao.impl;

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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.FileCopyUtils;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public final class YukonImageDaoImpl implements YukonImageDao {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonJdbcTemplate template;
    
    private final static String select = "select ImageId, ImageCategory, ImageName, ImageValue from YukonImage";
    
    private final static YukonRowMapper<LiteYukonImage> mapper = new YukonRowMapper<LiteYukonImage>() {
        @Override
        public LiteYukonImage mapRow(YukonResultSet rs) throws SQLException {
            LiteYukonImage image = new LiteYukonImage(rs.getInt("ImageId"));
            image.setImageCategory(rs.getString("ImageCategory"));
            image.setImageName(rs.getString("imageName"));
            image.setImageValue(rs.getBytes("ImageValue"));
            return image;
        }
    };
    
    @Override
    public List<String> getAllCategories() {
        
        List<String> categories = new ArrayList<>();
        
        synchronized (cache) {
            List<LiteYukonImage> images = new ArrayList<>(cache.getImages().values());
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
    
    @Override
    public LiteYukonImage getLiteYukonImage(int id) {
        return cache.getImages().get(id);
    }
    
    @Override
    public LiteYukonImage getLiteYukonImage(String name) {
        
        synchronized (cache) {
            for (LiteYukonImage img : cache.getImages().values()) {
               if (img.getImageName().equalsIgnoreCase(name)) return img;
            }
        }
        
        return null;
    }
    
    @Override
    public String yukonImageUsage(int id) {
        
        for (LiteStateGroup group : YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups()) {
            for (LiteState state : group.getStatesList()) {
                if (state.getImageID() == id) {
                    return group.getStateGroupName();
                }
            }
        }
        
        //this image is not used, just return null
        return null;
    }
    
    @Override
    public LiteYukonImage add(final int id, final String category, final String name, final Resource resource) 
            throws IOException {
        
        InputStream in = resource.getInputStream();
        final File temp = File.createTempFile("yukon_image", ".tmp");
        OutputStream out = new FileOutputStream(temp);
        FileCopyUtils.copy(in, out);

        template.update("insert into YukonImage values (?, ?, ?, ?)", new PreparedStatementSetter() {
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
        
        DBChangeMsg dbChange = new DBChangeMsg(id, DBChangeMsg.CHANGE_YUKON_IMAGE, DBChangeMsg.CAT_STATEGROUP, 
                DBChangeMsg.CAT_STATEGROUP, DbChangeType.ADD);
        dbChangeManager.processDbChange(dbChange);
        
        return getLiteYukonImage(id);
    }
    
    @Override
    public LiteYukonImage add(final String category, final String name, final Resource resource) throws IOException {
        return add(nextValueHelper.getNextValue("YukonImage"), category, name, resource);
    }
    
    @Override
    public void delete(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from YukonImage where ImageID").eq(id);
        template.update(sql);
        
        DBChangeMsg dbChange = new DBChangeMsg(id, DBChangeMsg.CHANGE_YUKON_IMAGE, DBChangeMsg.CAT_STATEGROUP, 
                DBChangeMsg.CAT_STATEGROUP, DbChangeType.DELETE);
        dbChangeManager.processDbChange(dbChange);
    }
    
    @Override
    public List<LiteYukonImage> getImagesForCategory(String category) {
        
        if (StringUtils.isBlank(category)) {
            return new ArrayList<>(cache.getImages().values());
        }
        
        List<LiteYukonImage> images = new ArrayList<>();
        for (LiteYukonImage image : cache.getImages().values()) {
            if (category.equalsIgnoreCase(image.getImageCategory())) {
                images.add(image);
            }
        }
        
        return images;
    }
    
    @Override
    public List<LiteYukonImage> load() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(select);
        sql.append("where ImageId").neq(YukonImage.NONE_IMAGE_ID);
        
        return template.query(sql, mapper);
    }
    
    @Override
    public LiteYukonImage load(int imageId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(select);
        sql.append("where ImageId").eq(imageId);
        
        return template.queryForObject(sql, mapper);
    }
    
}
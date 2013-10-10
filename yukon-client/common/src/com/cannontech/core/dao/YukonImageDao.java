package com.cannontech.core.dao;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonImage;

public interface YukonImageDao {

    /**
     * Returns all available YukonImage categories from the cache.
     * Creation date: (3/26/2001 9:47:28 AM)
     */
    public List<String> getAllCategories();

    /**
     * Returns the LiteYukonImage in the cache with the given id
     * @param id
     * @return LiteYukonImage
     */
    public LiteYukonImage getLiteYukonImage(int id);

    /**
     * Returns the first LiteYukonImage in the cache with the given name
     * @param name
     * @return LiteYukonImage
     */
    public LiteYukonImage getLiteYukonImage(String name);

    /** 
     * Returns the StateGroup that uses the YukonImage id.
     * If no StateGroup uses the YukonImage id, a null is returned.
     */
    public String yukonImageUsage(int id);

    /**
     * Inserts a default image with a known id.  Sends db change message to update the cache.
     * @throws IOException
     */
    public LiteYukonImage add(int id, String category, String name, Resource resource) throws IOException;
    
    /**
     * Inserts an image. Sends db change message to update the cache.
     * @throws IOException
     */
    public LiteYukonImage add(String category, String name, Resource resource) throws IOException;

    /**
     * Returns the images in the provided category.  If category is null, all images are returned.
     */
    public List<LiteYukonImage> getImagesForCategory(String category);

}
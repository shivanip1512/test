package com.cannontech.common.gui.util;

import java.awt.Image;
import java.util.HashMap;

/**
 * Model for a number of categories of images.
 * 
 * Example use would be when you want the user to select
 * an image from several directories.
 * @author alauinger
 */
public class CategoryImageModel {	
	// key = String, value = Image[]
	private HashMap categoryImageMap = new HashMap();
		
	/**
	 * Default Constructor
	 * @see java.lang.Object#Object()
	 */
	public CategoryImageModel() {
	}
	
	/**
	 * Returns all the categories.
	 * @return String[]
	 */
	public String[] getCategories() {
		String[] catArr = new String[categoryImageMap.size()];
		categoryImageMap.keySet().toArray(catArr);
		return catArr;
	}
	/**
	 * Adds a category and all the categories images
	 * to the model.
	 * @param category
	 * @param images
	 */
	public void addCategory(String category, Image[] images) {
		categoryImageMap.put(category, images);
	}
	
	/**
	 * Removes a category from the model.
	 * @param category
	 */
	public void removeCategory(String category) {
		categoryImageMap.remove(category);
	}
	
	/**
	 * Returns all the images associated with a category
	 * @param category
	 * @return Image[]
	 */
	public Image[] getImages(String category) {
		return (Image[]) categoryImageMap.get(category);
	}
}

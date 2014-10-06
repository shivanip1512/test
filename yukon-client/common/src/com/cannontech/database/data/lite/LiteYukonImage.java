package com.cannontech.database.data.lite;

import static com.cannontech.core.image.model.YukonImage.DEFAULT_BACKGROUND;
import static com.cannontech.core.image.model.YukonImage.DEFAULT_LOGO;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.state.YukonImage;

public class LiteYukonImage extends LiteBase {
    
    public static final LiteYukonImage NONE_IMAGE = new LiteYukonImage(YukonImage.NONE_IMAGE_ID,
                                                                       CtiUtilities.STRING_NONE,
                                                                       CtiUtilities.STRING_NONE,
                                                                       null);
    
    private String imageCategory;
    private String imageName;
    private byte[] imageValue;
    
    public LiteYukonImage(int imageId) {
        super();
        setImageID(imageId);
        setLiteType(LiteTypes.STATE_IMAGE);
    }
    
    public LiteYukonImage(int imageId, String imageName) {
        this(imageId);
        setImageName(imageName);
    }
    
    public LiteYukonImage(int imageId, String imageCategory, String imageName, byte[] imageValue) {
        this(imageId);
        setImageName(imageName);
        setImageCategory(imageCategory);
        setImageValue(imageValue);
        setLiteType(LiteTypes.STATE_IMAGE);
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public void setImageID(int imageId) {
        setLiteID(imageId);
    }
    
    public int getImageID() {
        return getLiteID();
    }
    
    public String getImageCategory() {
        return imageCategory;
    }
    
    public byte[] getImageValue() {
        return imageValue;
    }
    
    public void setImageCategory(String imageCategory) {
        this.imageCategory = imageCategory;
    }
    
    public void setImageValue(byte[] imageValue) {
        this.imageValue = imageValue;
    }
    
    public String toString() {
        return getImageName();
    }
    
    public boolean isDeletable() {
        return getImageID() != DEFAULT_BACKGROUND.getId() && getImageID() != DEFAULT_LOGO.getId();
    }
    
}
package com.cannontech.database.data.lite;

import java.sql.Blob;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.state.YukonImage;

/**
 * @author rneuharth
 *         Aug 1, 2002 at 3:04:49 PM
 */
public class LiteYukonImage extends LiteBase {
    
    public static final LiteYukonImage NONE_IMAGE =
        new LiteYukonImage(com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID,
                           com.cannontech.common.util.CtiUtilities.STRING_NONE,
                           com.cannontech.common.util.CtiUtilities.STRING_NONE,
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

    public void retrieve(String databaseAlias) {
        SqlStatement s = new SqlStatement("SELECT ImageCategory,ImageName,ImageValue from "
                                              + YukonImage.TABLE_NAME
                                              + " where ImageID = " + getImageID(), CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0) {
                throw new IllegalStateException("Unable to find YukonImage with imageID = " + getLiteID());
            }

            setImageCategory(s.getRow(0)[0].toString());
            setImageName(s.getRow(0)[1].toString());
            Blob tempBlob = (Blob) s.getRow(0)[2];
            setImageValue(tempBlob.getBytes(1, (int) tempBlob.length()));
            
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }

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
        if (getImageID() == com.cannontech.core.image.model.YukonImage.DEFAULT_BACKGROUND.getId()
                || getImageID() == com.cannontech.core.image.model.YukonImage.DEFAULT_LOGO.getId()) {
            return false;
        }
        return true;
    }
}
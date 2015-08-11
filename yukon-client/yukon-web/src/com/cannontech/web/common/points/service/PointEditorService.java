package com.cannontech.web.common.points.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.PointModel;

public interface PointEditorService {

    /**
     * Gets the PointModel fully constructed for the supplied id
     */
    PointModel getModelForId(int id);

    /**
     * Save all data about the point
     * 
     * @return the id of the point
     */
    int save(PointModel model);

    /**
     * @param interfaceType
     * @return List of DB values for FdrDirections supported by interfaceType
     */
    List<String> getDirectionsFor(FdrInterfaceType interfaceType);

    /**
     * 
     * @param interfaceType - Used to determine what type each field is
     * @param pointType - Used for the POINTTYPE field
     * @return A list of translation field objects, represented as maps with keys
     * <dl>
     *  <dt>name</dt><dd>Name of the translation part</dd>
     *  <dt>value</dt><dd>Default value</dd>
     *  <dt>options</dt><dd>List of options (for select fields)</dd>
     *  <dt>hidden</dt><dd>true when option shouldn't be user selectable (POINTTYPE)</dd>
     * </dl> 
     */
    List<Map<String, Object>> getTranslationFieldsFor(FdrInterfaceType interfaceType, String pointType);

    /**
     * 
     * @param originalString - Assumed to be formatted as "key1:value1;key2:value2;"
     * @param interfaceType - Used to determine what type each field is
     * @return A list of translation field objects, represented as maps with keys
     * <dl>
     *  <dt>name</dt><dd>Name of the translation part</dd>
     *  <dt>value</dt><dd>Current value (if present) or default value</dd>
     *  <dt>options</dt><dd>List of options (for select fields)</dd>
     *  <dt>hidden</dt><dd>true when option shouldn't be user selectable (POINTTYPE)</dd>
     * </dl> 
     */
    List<Map<String, Object>> breakIntoTranslationFields(String originalString, FdrInterfaceType interfaceType);

    public static enum AttachmentStatus implements DisplayableEnum {

        SUBSTATION_BUS(false),
        CAP_BANK(false),
        LM_TRIGGER(false),
        LM_GROUP(false),
        RAW_POINT_HISTORY(true),
        SYSTEM_LOG(true),
        NO_CONFLICT(true);
        
        private static final String baseKey = "yukon.web.modules.common.point.attachment.";

        private final boolean deletable;

        AttachmentStatus(boolean deletable) {
            this.deletable = deletable;
        }


        public boolean isDeletable() {
            return deletable;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

    AttachmentStatus getAttachmentStatus(int id);

    boolean delete(int id);

    int create(int pointType, int paoId, YukonUserContext userContext);

}

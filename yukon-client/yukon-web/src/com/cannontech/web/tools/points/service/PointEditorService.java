package com.cannontech.web.tools.points.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.dao.DevicePointDao.SortBy;
import com.cannontech.common.device.model.DevicePointsFilter;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PaoPointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.PointCopy;
import com.cannontech.web.tools.points.model.PointModel;

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
    int save(PointBase base, List<AlarmTableEntry> alarmTableEntries, LiteYukonUser liteYukonUser);

    /**
     * Save staleData
     * @param staleData
     */
    public void saveStaleData(StaleData staleData);

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
        
        private static final String baseKey = "yukon.web.modules.tools.point.attachment.";

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
    
    public static class AttachedException extends Exception {
        
        private final AttachmentStatus status;
        
        public AttachedException(AttachmentStatus status) {
            this.status = status;
        }

        public AttachmentStatus getStatus() {
            return status;
        }
    }
    

    /**
     * @return an {@link AttachmentStatus} to indicate what types of attachments the point has that may prevent it from
     * being deleted.
     */
    AttachmentStatus getAttachmentStatus(int pointId);

    /**
     * Attempts to delete the point with the given id.
     * @throws AttachedException if the point has attachments that prevent it from being deleted
     */
    int delete(int id, YukonUserContext userContext) throws AttachedException;

    /**
     * Creates a new point with the specified parent id
     *
     * @param pointType - must pass {@link PointTypes#isValidPointType(pointType)}
     * @param paoId - parent of the point
     * @param userContext - Current user context
     * @return the id of the new point
     */
    int create(int pointType, int paoId, YukonUserContext userContext);
    
    /**
     * Copies the point and assigns the point to pao object specified by paoId in the LitePointModel. 
     * 
     * @param pointBase - pointBase object whose field data needs to be copied.
     * @return the id of the new point copied.
     */
    int copy(LitePointModel pointToCopy);
    
    /**
     * Returns LitePointModel for the pointId passed as a parameter.
     * 
     * @param pointId
     * @return LitePointModel
     */
    LitePointModel getLitePointModel(Integer pointId);
    
    /**
     * Create the Point.
     */
    PointBaseModel<? extends PointBase> create(PointBaseModel<? extends PointBase> point, YukonUserContext userContext);

    /**
     * Update the Point.
     */
    PointBaseModel<? extends PointBase> update(int portId, PointBaseModel<? extends PointBase> point, YukonUserContext userContext);

    /**
     * Retrieve Point for passed pointId.
     */
    PointBaseModel<? extends PointBase> retrieve(int pointId);
    
    /**
     * Copy Point.
     */
    PointBaseModel<? extends PointBase> copy(int portId, PointCopy pointCopy);
    
    /**
     * Retrieve Points info for passed paoId.
     */
    PaoPointModel getDevicePointDetail(int paoId, DevicePointsFilter devicePointsFilter, Direction direction,
                                       SortBy sortBy, PagingParameters paging);

    /**
     * Retrieve states for point Id.
     */
    List<LMDto> retrieveStates(int pointId);

}

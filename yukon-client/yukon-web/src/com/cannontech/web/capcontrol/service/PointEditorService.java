package com.cannontech.web.capcontrol.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.fdr.FdrInterfaceType;
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

}

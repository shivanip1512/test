package com.cannontech.web.capcontrol.service;

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

}

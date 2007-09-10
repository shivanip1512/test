package com.cannontech.web.editor.model;


import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.capcontrol.CapControlSpecialArea;
import com.cannontech.database.db.DBPersistent;

public class DataModelFactory {

    public static EditorDataModel createModel(DBPersistent dbPersistent) {
        if (dbPersistent instanceof CapControlArea)
        {
            return new CBCAreaDataModel((CapControlArea)dbPersistent);
        }else if (dbPersistent instanceof CapControlSpecialArea)
        {
            return new CBCSpecialAreaDataModel((CapControlSpecialArea)dbPersistent);
        }
        return new EditorDataModelImpl(dbPersistent);
        
    }

}

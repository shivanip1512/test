package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class RouteEnumeratedType extends BaseEnumeratedType<Integer> {

    
    private PaoDao paoDao = null;
    private List<InputOption> optionList = new ArrayList<InputOption>();
    private InputType<Integer> enumeratedType;
    
    public List<InputOption> getOptionList() {

        // re-get available routes
        optionList = new ArrayList<InputOption>();
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        for (LiteYukonPAObject route : routes) {
            
            InputOption option = new InputOption();
            option.setText(route.getPaoName());
            option.setValue(String.valueOf(route.getLiteID()));
            optionList.add(option);
        }
        
        return optionList;
    }

    public InputType<Integer> getEnumeratedType() {
        return enumeratedType;
    }

    public void setEnumeratedType(InputType<Integer> enumeratedType) {
        this.enumeratedType = enumeratedType;
    }

    public Class<Integer> getTypeClass() {
        return enumeratedType.getTypeClass();
    }

    public PropertyEditor getPropertyEditor() {
        return enumeratedType.getPropertyEditor();
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}

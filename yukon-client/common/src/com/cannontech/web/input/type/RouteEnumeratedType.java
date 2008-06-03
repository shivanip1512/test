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
public class RouteEnumeratedType extends BaseEnumeratedType<String> {

    
    private PaoDao paoDao = null;
    private List<InputOption> optionList = new ArrayList<InputOption>();
    private InputType<String> enumeratedType;
    
    public List<InputOption> getOptionList() {

        // re-get available routes
        optionList = new ArrayList<InputOption>();
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        for (LiteYukonPAObject route : routes) {
            
            String routeName = route.getPaoName();
            
            InputOption option = new InputOption();
            option.setText(routeName);
            option.setValue(routeName);
            optionList.add(option);
        }
        
        return optionList;
    }

    public InputType<String> getEnumeratedType() {
        return enumeratedType;
    }

    public void setEnumeratedType(InputType<String> enumeratedType) {
        this.enumeratedType = enumeratedType;
    }

    public Class<String> getTypeClass() {
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

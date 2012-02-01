package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.Lists;

/**
 * Implementation of input type which represents a list input type. This class
 * gets it's property editor and type class from the input type member.
 */
public class RouteEnumeratedType extends BaseEnumeratedType<String> {

    
    private PaoDao paoDao = null;
    private List<InputOptionProvider> optionList = Lists.newArrayList();
    private InputType<String> enumeratedType;
    
    public List<InputOptionProvider> getOptionList() {

        // re-get available routes
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        optionList = Lists.newArrayListWithCapacity(routes.length);
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

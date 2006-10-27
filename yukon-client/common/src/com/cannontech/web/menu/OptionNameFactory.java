package com.cannontech.web.menu;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

public abstract class OptionNameFactory {

    public OptionNameFactory() {
        super();
    }
    
    public abstract String getName(LiteYukonUser user);
    
    public static OptionNameFactory createPlain(final String name) {
        OptionNameFactory factory = new OptionNameFactory() {
            @Override
            public String getName(LiteYukonUser user) {
                return name;
            }
        };
        return factory;
    }
    
    public static OptionNameFactory createPropertyRetriever(final String propertyName) {
        final int propertyId = ReflectivePropertySearcher.getRoleProperty().getIntForName(propertyName);
        OptionNameFactory factory = new OptionNameFactory() {
            @Override
            public String getName(LiteYukonUser user) {
                AuthDao authDao = DaoFactory.getAuthDao();
                String value = authDao.getRolePropertyValue(user, propertyId);
                return value;
            }
        };
        return factory;
    }
    


}

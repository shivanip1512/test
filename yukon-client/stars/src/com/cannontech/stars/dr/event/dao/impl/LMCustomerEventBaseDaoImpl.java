package com.cannontech.stars.dr.event.dao.impl;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.model.LMCustomerEventBase;

public class LMCustomerEventBaseDaoImpl implements LMCustomerEventBaseDao {
    private static final String[] insertSql;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;

    static {
        
        insertSql = new String[]{
                "INSERT INTO LMCustomerEventBase (EventID,EventTypeID,ActionID,EventDateTime,Notes,AuthorizedBy) VALUES (?,?,?,?,?,?)",
                "INSERT INTO ECToLMCustomerEventMapping (EventID,EnergyCompanyID) VALUES (?,?)",
                "INSERT INTO LMHardwareEvent (EventID,InventoryID) VALUES (?,?)"
        };
        
    }
    
    public boolean addHardwareEvent(final LMCustomerEventBase eventBase, final int energyCompanyId, final int inventoryId) {
        final int eventId = nextValueHelper.getNextValue("LMCustomerEventBase");
        eventBase.setEventId(eventId);
        
        int rows = simpleJdbcTemplate.update(insertSql[0], eventId,
                                                eventBase.getEventTypeId(),
                                                eventBase.getActionId(),
                                                eventBase.getEventDateTime(),
                                                eventBase.getNotes(),
                                                eventBase.getAuthorizedBy());
        
        int rows2 = simpleJdbcTemplate.update(insertSql[1], eventId, energyCompanyId);
        
        int rows3 = simpleJdbcTemplate.update(insertSql[2], eventId, inventoryId);
        
        boolean result = ((rows == 1) && (rows2 == 1) && (rows3 == 1));
        return result;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
}

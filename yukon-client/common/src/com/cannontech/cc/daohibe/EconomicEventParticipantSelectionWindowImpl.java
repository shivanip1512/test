package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.EconomicEventParticipantSelectionWindowDao;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventParticipantSelectionWindowImpl extends YukonBaseHibernateDao implements
    EconomicEventParticipantSelectionWindowDao {

    public EconomicEventParticipantSelectionWindowImpl() {
        super();
    }

    public void save(EconomicEventParticipantSelectionWindow object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEventParticipantSelectionWindow object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventParticipantSelectionWindow getForId(Integer id) {
        return (EconomicEventParticipantSelectionWindow) 
            getHibernateTemplate().get(EconomicEventParticipantSelectionWindow.class, id);
    }
    
    public 
    List<EconomicEventParticipantSelectionWindow> 
    getForParticipantAndRevision(EconomicEventParticipant participant, 
                                 EconomicEventPricing revision) {
        return null;
    }

}

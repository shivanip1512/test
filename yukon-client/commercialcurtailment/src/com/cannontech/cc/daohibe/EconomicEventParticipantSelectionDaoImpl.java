package com.cannontech.cc.daohibe;

import com.cannontech.cc.dao.EconomicEventParticipantSelectionDao;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventParticipantSelectionDaoImpl extends YukonBaseHibernateDao 
    implements EconomicEventParticipantSelectionDao {

    public EconomicEventParticipantSelectionDaoImpl() {
        super();
    }

    public void save(EconomicEventParticipantSelection object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEventParticipantSelection object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventParticipantSelection getForId(Integer id) {
        return (EconomicEventParticipantSelection) 
            getHibernateTemplate().get(EconomicEventParticipantSelection.class, id);
    }

}

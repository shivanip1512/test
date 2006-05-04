package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.cc.dao.EconomicEventPricingDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventPricingDaoImpl extends YukonBaseHibernateDao implements
    EconomicEventPricingDao {

    public EconomicEventPricingDaoImpl() {
        super();
    }

    public void save(EconomicEventPricing object) {
        getHibernateTemplate().saveOrUpdate(object);
        // argh... should this just cascade (if so where???)
        for (EconomicEventPricingWindow window : object.getWindows().values()) {
            getHibernateTemplate().saveOrUpdate(window);
        }
        //TODO might have to do something clever here
        //getHibernateTemplate().refresh(object.getEvent());
    }

    public void delete(EconomicEventPricing object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventPricing getForId(Integer id) {
        return (EconomicEventPricing) getHibernateTemplate().get(EconomicEventPricing.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<EconomicEventPricing> getPricingForEvent(EconomicEvent event) {
        getHibernateTemplate().lock(event, LockMode.NONE);
        String query = "select eep from EconomicEventPricing eep " 
            + "inner join fetch eep.event "
            + "where eep.event = ?";
        return getHibernateTemplate().find(query, event);
    }

    @SuppressWarnings("unchecked")
    public EconomicEventPricing getPricingForEvent(EconomicEvent event, int revision) {
        getHibernateTemplate().lock(event, LockMode.NONE);
        String query = "select eep from EconomicEventPricing eep " 
            + "inner join fetch eep.event "
            + "where eep.event = ?"
            + "  and revision = ?";
        List result = getHibernateTemplate().find(query, new Object[]{event, revision});
        if (result.size() != 1) {
            throw new IncorrectResultSizeDataAccessException(1,result.size());
        }
        return (EconomicEventPricing) result.get(0);
    }

}

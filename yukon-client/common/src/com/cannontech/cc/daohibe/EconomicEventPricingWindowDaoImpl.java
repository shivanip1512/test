package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;

import com.cannontech.cc.dao.EconomicEventPricingWindowDao;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventPricingWindowDaoImpl extends YukonBaseHibernateDao implements
    EconomicEventPricingWindowDao {

    public EconomicEventPricingWindowDaoImpl() {
        super();
    }

    public void save(EconomicEventPricingWindow object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEventPricingWindow object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventPricingWindow getForId(Integer id) {
        return (EconomicEventPricingWindow) 
            getHibernateTemplate().get(EconomicEventPricingWindow.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<EconomicEventPricingWindow> getForRevision(EconomicEventPricing revision) {
        getHibernateTemplate().lock(revision, LockMode.NONE);
        String query = "select pw from EconomicEventPricingWindow pw " 
        //    + "inner join fetch pw.pricingRevision "
            + "where pw.pricingRevision = ?";
        return getHibernateTemplate().find(query, revision);
    }

}

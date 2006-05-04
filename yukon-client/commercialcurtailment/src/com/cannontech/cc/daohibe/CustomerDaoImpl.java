package com.cannontech.cc.daohibe;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.cannontech.cc.dao.CustomerDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class CustomerDaoImpl extends YukonBaseHibernateDao implements
        CustomerDao {

    public void save(CICustomerStub object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(CICustomerStub object) {
        getHibernateTemplate().delete(object);
    }

    public CICustomerStub getForId(Integer id) {
        return (CICustomerStub) getHibernateTemplate().get(CICustomerStub.class, id);
    }
    
    public CICustomerStub getForLite(LiteCICustomer liteCustomer) {
        return getForId(liteCustomer.getCustomerID());
    }

    @SuppressWarnings("unchecked")
    public List<CICustomerStub> getUnassignedCustomers(final Group group) {
        // This function doesn't use hibernate query language because
        // the ECToAccountMapping table isn't an entity and I
        // don't want to make it become one.
        final String q = 
            "select {cust.*} " + 
            "from CICustomerBase cust " +
            "  join CustomerAccount ca on (ca.CustomerID = cust.CustomerID)" + 
            "  join ECToAccountMapping eta on (eta.AccountID = ca.AccountID)" + 
            "where cust.CustomerID not in  (" + 
            "   select CCurtGroupCustomerNotif.CustomerId " + 
            "   from CCurtGroupCustomerNotif " + 
            "   where CCurtGroupCustomerNotif.GroupId = :groupId" + 
            ")" + 
            "  and eta.EnergyCompanyId = :ecId";
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(q);
                query.addEntity("cust", CICustomerStub.class);
                query.setInteger("groupId", group.getId());
                query.setInteger("ecId", group.getEnergyCompanyId());
                return query.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<CICustomerStub> getCustomersForEC(final Integer energyCompanyId) {
        // This function doesn't use hibernate query language because
        // the ECToAccountMapping table isn't an entity and I
        // don't want to make it become one.
        final String q = 
            "select {cust.*} " + 
            "from CICustomerBase cust " +
            "  join CustomerAccount ca on (ca.CustomerID = cust.CustomerID)" + 
            "  join ECToAccountMapping eta on (eta.AccountID = ca.AccountID)" + 
            "where eta.EnergyCompanyId = :ecId";
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(q);
                query.addEntity("cust", CICustomerStub.class);
                query.setInteger("ecId", energyCompanyId);
                return query.list();
            }
        });
    }

}

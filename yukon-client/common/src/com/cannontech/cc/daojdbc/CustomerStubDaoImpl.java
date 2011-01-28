package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.CICustomerPointData;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class CustomerStubDaoImpl implements CustomerStubDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<CICustomerPointData> template;
    private NextValueHelper nextValueHelper;

    @Override
    public CICustomerStub getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select CustomerID, CompanyName");
        sql.append("from CICustomerBase");
        sql.append("where CustomerID").eq(id);
        
        List<CICustomerStub> result = yukonJdbcTemplate.query(sql, rowMapper);
        setPointDataForCustomers(result);
        return result.get(0);
    }
   
    @Override
    public CICustomerStub getForLite(LiteCICustomer liteCustomer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select CustomerID, CompanyName");
        sql.append("from CICustomerBase");
        sql.append("where CustomerID").eq(liteCustomer.getCustomerID());
        
        List<CICustomerStub> result = yukonJdbcTemplate.query(sql, rowMapper);
        setPointDataForCustomers(result);
        return result.get(0);
    }

    @Override
    public List<CICustomerStub> getCustomersForEC(Integer energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select cust.CustomerID, cust.CompanyName");
        sql.append("from CICustomerBase cust");
        sql.append(  "join CustomerAccount ca on ca.CustomerID = cust.CustomerID");
        sql.append(  "join ECToAccountMapping eta on eta.AccountID = ca.AccountID");
        sql.append("where eta.EnergyCompanyID").eq(energyCompanyId);
        
        List<CICustomerStub> result = yukonJdbcTemplate.query(sql, rowMapper);
        setPointDataForCustomers(result);
        return result;
    }
    
    @Override
    public List<CICustomerStub> getUnassignedCustomers(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select cust.CustomerID, cust.CompanyName");
        sql.append("from CICustomerBase cust");
        sql.append(  "join CustomerAccount ca on ca.CustomerID = cust.CustomerID");
        sql.append(  "join ECToAccountMapping eta on eta.AccountID = ca.AccountID");
        sql.append("where cust.CustomerID not in (");
        sql.append(  "select gcn.CustomerID");
        sql.append(  "from CCurtGroupCustomerNotif gcn");
        sql.append(  "where gcn.CCurtGroupID").eq(group.getId());
        sql.append(")");
        sql.append(  "and eta.EnergyCompanyID").eq(group.getEnergyCompanyId());

        List<CICustomerStub> result = yukonJdbcTemplate.query(sql, rowMapper);
        setPointDataForCustomers(result);
        return result;
    }

    @Override
    public void save(CICustomerPointData customerPoint) {
        template.insert(customerPoint);
    }
    
    private FieldMapper<CICustomerPointData> pointDataFieldMapper = new FieldMapper<CICustomerPointData>() {
        public void extractValues(MapSqlParameterSource p, CICustomerPointData pointData) {
            p.addValue("PointID", pointData.getPointId());
            p.addValue("Type", pointData.getType());
            p.addValue("OptionalLabel", pointData.getOptionalLabel());
        }
        public Number getPrimaryKey(CICustomerPointData pointData) {
            return pointData.getCustomerId();
        }
        public void setPrimaryKey(CICustomerPointData pointData, int value) {
            pointData.setCustomerId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CICustomerPointData>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CICustomerPointData");
        template.setPrimaryKeyField("CustomerID");
        template.setFieldMapper(pointDataFieldMapper); 
    }

    private void setPointDataForCustomers(List<CICustomerStub> customerList) {
        ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select * from CICustomerPointData");
                sql.append("where CustomerID").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer,CICustomerPointData>> pointDataMapper = new YukonRowMapper<Map.Entry<Integer,CICustomerPointData>>() {
            public Map.Entry<Integer,CICustomerPointData> mapRow(YukonResultSet rs) throws SQLException {
                CICustomerPointType key = CICustomerPointType.valueOf(rs.getString("Type"));
                CICustomerPointData value = new CICustomerPointData();
                value.setCustomerId(rs.getInt("CustomerID"));
                value.setType(key);
                value.setPointId(rs.getInt("PointID"));
                value.setOptionalLabel(rs.getString("OptionalLabel"));
                return Maps.immutableEntry(value.getCustomerId(), value);
            }
        };

        Function<CICustomerStub, Integer> func = new Function<CICustomerStub, Integer>() {
            public Integer apply(CICustomerStub customer) {
                return customer.getId();
            }
        };

        Multimap<CICustomerStub, CICustomerPointData> pointLookup = mappedSqlTemplate.multimappedQuery(sqlGenerator, customerList, new YukonRowMapperAdapter<Map.Entry<Integer,CICustomerPointData>>(pointDataMapper), func);

        for (CICustomerStub ciCustomerStub : customerList) {
            Collection<CICustomerPointData> collection = pointLookup.get(ciCustomerStub);
            ciCustomerStub.setPointData(collection);
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    private YukonRowMapper<CICustomerStub> rowMapper = new YukonRowMapper<CICustomerStub>() {
        public CICustomerStub mapRow(YukonResultSet rs) throws SQLException {
            CICustomerStub customer = new CICustomerStub();
            customer.setId(rs.getInt("CustomerID"));
            customer.setCompanyName(rs.getString("CompanyName"));
            return customer;
        }
    };
}

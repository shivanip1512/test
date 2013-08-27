package com.cannontech.common.userpage.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Category;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class UserPageDaoImpl implements UserPageDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private static SimpleTableAccessTemplate<UserPage> userPageTemplate;
    private static SimpleTableAccessTemplate<UserPageParameterEntry> userPageParamTemplate;

    @Override
    public boolean contains(UserPage page) {
        Integer id = getId(page);
        return id != null;
    }

    @Override
    public UserPage save(UserPage page) {

        Integer userPageId = getId(page);

        if (userPageId == null) { //No entry in database
            userPageId = userPageTemplate.insert(page);
            page = page.updateId(userPageId);

            if (page.getCategory().equals(Category.HISTORY)) {
                maintainHistory(page.getUserId());
            }

        } else {
            page = page.updateId(userPageId);
            userPageTemplate.update(page);
        }

        clearParameters(page);
        List<UserPageParameterEntry> params = UserPageParameterEntry.getParamEntries(page);
        for (UserPageParameterEntry entry : params) {
            userPageParamTemplate.save(entry);
        }

        return page;
    }

    private void clearParameters(UserPage page) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPageParam");
        sql.append("WHERE UserPageId").eq(page.getId());
        yukonJdbcTemplate.update(sql);

    }

    @Override
    public void delete(UserPage page) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPage");
        sql.appendFragment(buildUniquenessCriterion(page));
        yukonJdbcTemplate.update(sql);
    }

    private void maintainHistory(int userId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPage");
        sql.append("WHERE UserId").eq(userId);
        sql.append("AND Category").eq(Category.HISTORY);
        sql.append("AND UserPageId NOT IN (");
        sql.append("  SELECT TOP 10 UserPageId FROM UserPage");
        sql.append("  WHERE UserId").eq(userId);
        sql.append("  AND Category").eq(Category.HISTORY);
        sql.append("  ORDER BY CreatedDate DESC");
        sql.append(")");

        yukonJdbcTemplate.update(sql);
    }

    @Override
    public Multimap<Category, UserPage> getPagesForUser(LiteYukonUser user) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserPage.UserPageId, UserId, PagePath, PageName, Module, Category, CreatedDate");
        sql.append(getFromStatement());
        sql.append("WHERE UserId").eq(user.getUserID());

        List<UserPage> results = yukonJdbcTemplate.query(sql, userPageRowMapper);

        Multimap<Category, UserPage> pages = ArrayListMultimap.create();
        for (UserPage page : results) {
            pages.put(page.getCategory(), page);
        }

        return pages;
    }

    private Integer getId(UserPage page) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserPageId, UserId, PagePath, PageName, Module, Category, CreatedDate");
        sql.append(getFromStatement());
        sql.appendFragment(buildUniquenessCriterion(page));
        List<UserPage> pages = yukonJdbcTemplate.query(sql, userPageRowMapper);

        switch(pages.size()){
        case 0: return null;
        default: return pages.get(0).getId();
        }
    }

   private static SqlFragmentSource buildUniquenessCriterion(UserPage page) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WHERE PagePath").eq(page.getPath());
        sql.append("AND UserId").eq(page.getUserId());
        sql.append("AND Category").eq(page.getCategory());
        return sql;
    }

   private static SqlFragmentSource getFromStatement() {
       SqlStatementBuilder sql = new SqlStatementBuilder();
       sql.append("FROM UserPage");
       return sql;
   }

   private List<String> getParameters(Integer pageId) {
       SqlStatementBuilder sql = new SqlStatementBuilder();
       sql.append("SELECT Parameter");
       sql.append("FROM UserPageParam");
       sql.append("WHERE UserPageId").eq(pageId);
       sql.append("ORDER BY Parameter");
       List<String> params = yukonJdbcTemplate.query(sql, RowMapper.STRING);
       return params;
   }

    private YukonRowMapper<UserPage> userPageRowMapper = 
            new YukonRowMapper<UserPage>() {

        public UserPage mapRow(YukonResultSet rs) throws SQLException {
            Integer id = rs.getInt("UserPageId");
            Integer userId = rs.getInt("UserId");
            String pagePath = rs.getString("PagePath");
            String pageName = rs.getString("PageName");
            String module = rs.getString("Module");
            Category category = rs.getEnum("Category", UserPage.Category.class);
            Date createdDate = rs.getDate("CreatedDate");

            List<String> params = getParameters(id);

            UserPage userPage = new UserPage(userId, pagePath, category, module, pageName, params, createdDate, id);

            return userPage;
        }
    };

    private static AdvancedFieldMapper<UserPage> userPageMapper = new AdvancedFieldMapper<UserPage>() {

        public void extractValues(SqlParameterChildSink p, UserPage page) {
            p.addValue("UserId", page.getUserId());
            p.addValue("PagePath", page.getPath());
            p.addValue("PageName", page.getName());
            p.addValue("Module", page.getModule());
            p.addValue("Category", page.getCategory());
            p.addValue("CreatedDate", page.getCreatedDate());
        }
        public Number getPrimaryKey(UserPage page) {
            return page.getId();
        }
        public void setPrimaryKey(UserPage page, int value) {
            //Immutable object
        }
    };

    private static AdvancedFieldMapper<UserPageParameterEntry> userPageParamMapper = new AdvancedFieldMapper<UserPageParameterEntry>() {

        public void extractValues(SqlParameterChildSink p, UserPageParameterEntry paramEntry) {
            p.addValue("UserPageId", paramEntry.getUserPageId());
            p.addValue("ParamNumber", paramEntry.getParamNumber());
            p.addValue("Parameter", paramEntry.getParameter());
        }
        public Number getPrimaryKey(UserPageParameterEntry page) {
            return page.getId();
        }
        public void setPrimaryKey(UserPageParameterEntry page, int value) {
            //Immutable object
        }
    };

    private static class UserPageParameterEntry {
        private final Integer id;
        private final Integer userPageId;
        private final Integer paramNumber;
        private final String parameter;

        public UserPageParameterEntry(Integer id, Integer userPageId, Integer paramNumber, String parameter) {
            this.id = id;
            this.userPageId = userPageId;
            this.paramNumber = paramNumber;
            this.parameter = parameter;
        }

        public static List<UserPageParameterEntry> getParamEntries(UserPage page) {
            List<UserPageParameterEntry> result = Lists.newArrayList();
            int i=0;
            for (String parameter : page.getArguments()) {
                result.add( new UserPageParameterEntry(null, page.getId(), i, parameter));
                i++;
            }

            return result;
        }

        public final Integer getId() {
            return id;
        }

        public final Integer getUserPageId() {
            return userPageId;
        }

        public final Integer getParamNumber() {
            return paramNumber;
        }

        public final String getParameter() {
            return parameter;
        }
    }

    @PostConstruct
    public void init() throws Exception {
        userPageTemplate = new SimpleTableAccessTemplate<UserPage>(yukonJdbcTemplate, nextValueHelper);
        userPageTemplate.setTableName("UserPage");
        userPageTemplate.setPrimaryKeyField("UserPageId");
        userPageTemplate.setAdvancedFieldMapper(userPageMapper);

        userPageParamTemplate = new SimpleTableAccessTemplate<UserPageParameterEntry>(yukonJdbcTemplate, nextValueHelper);
        userPageParamTemplate.setTableName("UserPageParam");
        userPageParamTemplate.setPrimaryKeyField("UserPageParamId");
        userPageParamTemplate.setAdvancedFieldMapper(userPageParamMapper);
    }

    @Override
    public List<UserPage> getAllPages() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserPageId, UserId, PagePath, PageName, Module, Category, CreatedDate");
        sql.append("FROM UserPage");

        List<UserPage> results = yukonJdbcTemplate.query(sql, userPageRowMapper);
        return results;
    }

    private static List<Pattern> paoUrls = Lists.newArrayList();

    static {
        paoUrls.add(Pattern.compile("/meter/home\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/meter/moveIn\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/meter/moveOut\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/meter/highBill/view\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/amr/profile/home\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/amr/voltageAndTou/home\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/amr/manualCommand/home\\?deviceId=(\\d+)"));
        paoUrls.add(Pattern.compile("/bulk/routeLocate/home\\?.*?idList.ids=(\\d+(?:,\\d+)).*?"));
    }

    @Override
    public void deletePagesForPao(PaoIdentifier paoIdentifier) {
        List<UserPage> pages = getAllPages();

        for (UserPage page : pages) {
            Integer pagePaoId = paoIdInPath(page.getPath());
            if ( pagePaoId != null && pagePaoId == paoIdentifier.getPaoId() ) {
                delete(page);
            }
        }
    }

    private Integer paoIdInPath(String path) {
        for (Pattern url : paoUrls) {
            Matcher m = url.matcher(path);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }
        return null;
    }
}
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
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class UserPageDaoImpl implements UserPageDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private static SimpleTableAccessTemplate<UserPage> userPageTemplate;
    private static SimpleTableAccessTemplate<UserPageParameterEntry> userPageParamTemplate;

    @Override
    public boolean toggleFavorite(UserPage page) {
        UserPage dbPage = findPage(page);
        if (dbPage == null) {
            page = page.updateFavorite(true);
        } else {
            page = dbPage.updateFavorite( ! dbPage.isFavorite());
        }
        UserPage afterUpdate = save(page);
        return afterUpdate.isFavorite();
    }

    @Override
    public boolean isFavorite(UserPage page) {

        UserPage dbPage = findPage(page);
        if(dbPage == null) return false;
        return dbPage.isFavorite();
    }

    @Override
    public void updateHistory(UserPage page) {
        UserPage dbPage = findPage(page);
        if (dbPage != null) {
            page = dbPage.updateLastAccess(page.getLastAccess());
        }
        save(page);
        maintainHistory(page.getUserId());
    }

    @Override
    public List<UserPage> getPagesForUser(LiteYukonUser user) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserPage.UserPageId, UserId, PagePath, PageName, Module, Favorite, LastAccess");
        sql.append(buildFromStatement());
        sql.append("WHERE UserId").eq(user.getUserID());

        List<UserPage> results = yukonJdbcTemplate.query(sql, userPageRowMapper);

        return results;
    }

    private UserPage save(UserPage page) {

        int userPageId = userPageTemplate.save(page);
        page = page.updateId(userPageId);

        clearParameters(page);
        List<UserPageParameterEntry> params = UserPageParameterEntry.getParamEntries(page);
        for (UserPageParameterEntry entry : params) {
            userPageParamTemplate.save(entry);
        }

        return page;
    }

    private void delete(UserPage page) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPage");
        sql.appendFragment(buildUniquenessCriterion(page));
        yukonJdbcTemplate.update(sql);
    }

    private UserPage findPage(UserPage page) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(buildSelectStatement()).append(buildFromStatement());
        sql.appendFragment(buildUniquenessCriterion(page));
        List<UserPage> pages = yukonJdbcTemplate.query(sql, userPageRowMapper);

        switch(pages.size()){
        case 0: return null;
        default: return pages.get(0);
        }
    }

    private void maintainHistory(int userId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPage");
        sql.append("WHERE UserPageId IN (");
        sql.append("  SELECT T.UserPageId FROM (");
        sql.append("    SELECT ROW_NUMBER() OVER (ORDER BY LastAccess DESC) RowNumber, UP.UserPageId, UP.Favorite");
        sql.append("    FROM UserPage UP");
        sql.append("    WHERE UP.UserId").eq(userId);
        sql.append("  ) T");
        sql.append("  WHERE T.RowNumber").gt(MAX_HISTORY);
        sql.append("  AND T.Favorite").eq("N");
        sql.append(")");
        yukonJdbcTemplate.update(sql);
    }

    private void clearParameters(UserPage page) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPageParam");
        sql.append("WHERE UserPageId").eq(page.getId());
        yukonJdbcTemplate.update(sql);

    }

   private static SqlFragmentSource buildUniquenessCriterion(UserPage page) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WHERE PagePath").eq(page.getPath());
        sql.append("AND UserId").eq(page.getUserId());
        return sql;
    }

   private static SqlFragmentSource buildSelectStatement() {
       SqlStatementBuilder sql = new SqlStatementBuilder();
       sql.append("SELECT UserPageId, UserId, PagePath, PageName, Module, Favorite, LastAccess");
       return sql;
   }
   
   private static SqlFragmentSource buildFromStatement() {
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
            boolean isFavorite = rs.getBoolean("Favorite");
            Date lastAccess = rs.getDate("LastAccess");

            List<String> params = getParameters(id);

            UserPage userPage = new UserPage(userId, pagePath, isFavorite, module, pageName, params, lastAccess, id);

            return userPage;
        }
    };

    private static AdvancedFieldMapper<UserPage> userPageMapper = new AdvancedFieldMapper<UserPage>() {

        public void extractValues(SqlParameterChildSink p, UserPage page) {
            p.addValue("UserId", page.getUserId());
            p.addValue("PagePath", page.getPath());
            p.addValue("PageName", page.getName());
            p.addValue("Module", page.getModule());
            p.addValue("Favorite", YNBoolean.valueOf(page.isFavorite()));
            p.addValue("LastAccess", page.getLastAccess());
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

    private List<UserPage> getAllPages() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(buildSelectStatement()).append(buildFromStatement());

        List<UserPage> results = yukonJdbcTemplate.query(sql, userPageRowMapper);
        return results;
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
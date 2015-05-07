package com.cannontech.common.userpage.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Key;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * DAO for reading and writing UserPage and UserPageParam tables.  Some important things to note:
 * 
 * The UserPage table is most easily understood if you thinks of it as a history table.  If the "favorite" column
 * is set to true (1), the page is also a "favorite".
 * 
 * The UserPageParam table does not store URL parameters but rather "label arguments" used in module_config.xml.
 * The URL parameters are stored as part of the path in the UserPage table.
 */
public class UserPageDaoImpl implements UserPageDao {
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private static SimpleTableAccessTemplate<UserPage> userPageTemplate;

    /**
     * Since the UserPage class has a few convenience columns that basically duplicate the key, this class represents
     * the "value" or "state" of a user page.  That is to say, the things that can change for any specific given page.
     */
    private static class PageData {
        Integer userPageId = null;
        boolean isFavorite = false;
        Instant lastAccess = new Instant();
    };

    private static YukonRowMapper<PageData> pageDataRowMapper = new YukonRowMapper<PageData>() {
        @Override
        public PageData mapRow(YukonResultSet rs) throws SQLException {
            PageData pageData = new PageData();
            pageData.userPageId = rs.getInt("UserPageId");
            pageData.isFavorite = rs.getBoolean("Favorite");
            pageData.lastAccess = rs.getInstant("LastAccess");
            return pageData;
        };
    };

    @Override 
    @Transactional
    public boolean toggleFavorite(Key userPageKey, SiteModule module, String name, List<String> arguments) {
        PageData pageData = findPageData(userPageKey);

        UserPage page = new UserPage(pageData.userPageId, userPageKey, module, name, arguments, !pageData.isFavorite,
            pageData.lastAccess);
        page = save(page);
        return page.isFavorite();
    }

    @Override
    public boolean isFavorite(Key userPageKey) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select Favorite from UserPage");
        sql.append(buildUniquenessCriterion(userPageKey));
        try {
            boolean isFavorite = jdbcTemplate.queryForObject(sql, RowMapper.BOOLEAN);
            return isFavorite;
        } catch (EmptyResultDataAccessException erdae) {
            // No history at all...it's not a favorite.
            return false;
        }
    }

    @Override
    @Transactional
    public void updateHistory(Key userPageKey, SiteModule module, String name, List<String> arguments) {
        PageData pageData = findPageData(userPageKey);

        UserPage page = new UserPage(pageData.userPageId, userPageKey, module, name, arguments, pageData.isFavorite,
            new Instant());

        if (!isUserPageInHistory(page)) {
            pruneHistory(page.getUserId());
        }
        page = save(page);
        
    }
    
    /**
     * Checks if the currently accessed page exists in UserPage table.
     */
    private boolean isUserPageInHistory(UserPage page) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(*)");
    	sql.append("FROM UserPage WHERE USERID ").eq(page.getUserId());
    	sql.append( "AND PagePath ").eq(page.getKey().getPath());
    	
    	int count = jdbcTemplate.queryForInt(sql); 
    	return count > 0;
    }

    private PageData findPageData(Key userPageKey) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select UserPageId, Favorite, LastAccess");
        sql.append("from UserPage");
        sql.append(buildUniquenessCriterion(userPageKey));

        PageData pageData;
        try {
            pageData = jdbcTemplate.queryForObject(sql, pageDataRowMapper);
        } catch (EmptyResultDataAccessException erdae) {
            pageData = new PageData();
        }
        return pageData;
    }

    @Override
    public List<UserPage> getPagesForUser(LiteYukonUser user) {
        SqlStatementBuilder whereClause = new SqlStatementBuilder();
        whereClause.append("where UserId").eq(user.getUserID());

        return getPages(whereClause, "order by LastAccess desc");
    }

    private List<UserPage> getPages(SqlFragmentSource whereClause, String orderBy) {
        final ListMultimap<Integer, String> labelArgumentsByUserPageId = ArrayListMultimap.create();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select UserPageId, Parameter");
        sql.append("from UserPageParam");
        if (whereClause != null) {
            sql.append("where UserPageId in (");
            sql.append(    "select UserPageId from UserPage").append(whereClause);
            sql.append(")");
        }
        sql.append("order by ParamNumber");
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                labelArgumentsByUserPageId.put(rs.getInt("UserPageId"), SqlUtils.convertDbValueToString(rs.getString("Parameter")));
            }
        });

        sql = new SqlStatementBuilder();
        sql.append("select UserPageId, UserId, PagePath, PageName, Module, Favorite, LastAccess");
        sql.append("from UserPage");
        if (whereClause != null) {
            sql.append(whereClause);
        }
        if (orderBy != null) {
            sql.append(orderBy);
        }

        YukonRowMapper<UserPage> userPageRowMapper = new YukonRowMapper<UserPage>() {
            @Override
            public UserPage mapRow(YukonResultSet rs) throws SQLException {
                int id = rs.getInt("UserPageId");
                int userId = rs.getInt("UserId");
                String pagePath = rs.getString("PagePath");
                String pageName = rs.getString("PageName");
                SiteModule module = rs.getEnum("Module", SiteModule.class);
                boolean isFavorite = rs.getBoolean("Favorite");
                Instant lastAccess = rs.getInstant("LastAccess");

                List<String> params = labelArgumentsByUserPageId.get(id);

                Key key = new Key(userId, pagePath);
                UserPage userPage = new UserPage(id, key, module, pageName, params, isFavorite, lastAccess);

                return userPage;
            }
        };

        List<UserPage> results = jdbcTemplate.query(sql, userPageRowMapper);
        return results;
    }

    /**
     * Performs the insert/update operation
     * @return UserPage with id to match database structure.
     * If an insert is performed, this id is generated.
     */
    private UserPage save(UserPage page) {
        int userPageId = userPageTemplate.save(page);
        
        List<String> labelArguments = page.getArguments();
        page = new UserPage(userPageId, page.getKey(), page.getModule(), page.getName(), labelArguments,
            page.isFavorite(), page.getLastAccess());

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from UserPageParam");
        sql.append("where UserPageId").eq(page.getId());
        jdbcTemplate.update(sql);

        for (int index = 0; index < labelArguments.size(); index++) {
            String labelArgument = labelArguments.get(index);
            sql = new SqlStatementBuilder();
            int id = nextValueHelper.getNextValue("UserPageParam");
            sql.append("insert into UserPageParam");
            sql.append("(UserPageParamId, UserPageId, ParamNumber, Parameter)");
            sql.values(id, page.getId(), index, SqlUtils.convertStringToDbValue(labelArgument));
            jdbcTemplate.update(sql);
        }
        return page;
    }

    private void delete(Key userPageKey) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserPage");
        sql.append(buildUniquenessCriterion(userPageKey));
        jdbcTemplate.update(sql);
    }

    /**
     * Deletes all but the most recent MAX_HISTORY-1 elements for a userId that are not favorites.
     */
    private void pruneHistory(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from UserPage");
        sql.append("where UserId").eq(userId);
        sql.append("and UserPageId not in (");
        sql.append("  select t.UserPageId from (");
        sql.append("    select row_number() over (order by LastAccess desc) RowNumber, up.UserPageId, up.Favorite");
        sql.append("    from UserPage up");
        sql.append("    where up.UserId").eq(userId);
        sql.append("  ) t");
        sql.append("  where t.RowNumber").lte(MAX_HISTORY-1);
        sql.append("  or t.Favorite").eq(true);
        sql.append("  union");
        sql.append("  select t2.UserPageId from (");
        sql.append("    select row_number() over (order by LastAccess desc) RowNumber, up.UserPageId");
        sql.append("    from UserPage up");
        sql.append("    where up.UserId").eq(userId);
        sql.append("    and up.Module").eq("dr");
        sql.append("    and up.PageName").contains("Detail");
        sql.append("  ) t2");
        sql.append("  where t2.RowNumber").lte(MAX_DR_RECENT_VIEWED);
        sql.append(")");
        jdbcTemplate.update(sql);
    }

   /**
     * @return WHERE clause for path and UserId
     */
    private static SqlFragmentSource buildUniquenessCriterion(Key userPageKey) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("where PagePath").eq(userPageKey.getPath());
        sql.append("and UserId").eq(userPageKey.getUserId());
        return sql;
    }

    private static AdvancedFieldMapper<UserPage> userPageMapper = new AdvancedFieldMapper<UserPage>() {
        @Override
        public void extractValues(SqlParameterChildSink sink, UserPage page) {
            sink.addValue("UserId", page.getUserId());
            sink.addValue("PagePath", page.getPath());
            sink.addValue("PageName", page.getName());
            sink.addValue("Module", page.getModule());
            sink.addValue("Favorite", page.isFavorite());
            sink.addValue("LastAccess", page.getLastAccess());
        }

        @Override
        public Number getPrimaryKey(UserPage page) {
            return page.getId();
        }

        @Override
        public void setPrimaryKey(UserPage page, int value) {
            // Immutable object
        }
    };

    @PostConstruct
    public void init() throws Exception {
        userPageTemplate = new SimpleTableAccessTemplate<UserPage>(jdbcTemplate, nextValueHelper);
        userPageTemplate.setTableName("UserPage");
        userPageTemplate.setPrimaryKeyField("UserPageId");
        userPageTemplate.setAdvancedFieldMapper(userPageMapper);
    }

    private static List<Pattern> paoUrls = new ArrayList<>();
    private static List<Pattern> drFavoritesUrls = new ArrayList<>();
    private static List<Pattern> capControlUrls = new ArrayList<>();
    private static List<Pattern> meterUrls = new ArrayList<>();

    private static Pattern compileUrlParam(String url, String param){
        String regex = url + "\\?.*" + param + "=(\\d+).*";
        return Pattern.compile(regex);
    }

    static {
        drFavoritesUrls.add(compileUrlParam("/dr/program/detail", "programId"));
        drFavoritesUrls.add(compileUrlParam("/dr/scenario/detail", "scenarioId"));
        drFavoritesUrls.add(compileUrlParam("/dr/loadGroup/detail", "loadGroupId"));
        drFavoritesUrls.add(compileUrlParam("/dr/controlArea/detail", "controlAreaId"));

        paoUrls.addAll(drFavoritesUrls);

        capControlUrls.add(compileUrlParam("/capcontrol/tier/substations", "bc_areaId"));
        capControlUrls.add(compileUrlParam("/capcontrol/tier/feeders", "substationId"));
        capControlUrls.add(compileUrlParam("/capcontrol/ivvc/bus/detail", "subBusId"));
        capControlUrls.add(Pattern.compile("/capcontrol/regulators/(\\d+)"));

        paoUrls.addAll(capControlUrls);

        meterUrls.add(Pattern.compile("/common/pao/(\\d+).*"));
        meterUrls.add(compileUrlParam("/meter/home", "deviceId"));
        meterUrls.add(compileUrlParam("/meter/moveIn", "deviceId"));
        meterUrls.add(compileUrlParam("/meter/moveOut", "deviceId"));
        meterUrls.add(compileUrlParam("/meter/highBill/view", "deviceId"));
        meterUrls.add(compileUrlParam("/amr/profile/home", "deviceId"));
        meterUrls.add(compileUrlParam("/amr/voltageAndTou/home", "deviceId"));
        meterUrls.add(compileUrlParam("/amr/manualCommand/home", "deviceId"));
        meterUrls.add(Pattern.compile("/bulk/routeLocate/home\\?.*?idList.ids=(\\d+(?:,\\d+)).*?"));

        paoUrls.addAll(meterUrls);
    }

    @Override
    public void deletePagesForPao(YukonPao pao) {
        List<UserPage> pages = getPages(null, null);

        for (UserPage page : pages) {
            Integer pagePaoId = paoIdInPath(page.getPath(), paoUrls);
            if (pagePaoId != null && pagePaoId == pao.getPaoIdentifier().getPaoId()) {
                delete(page.getKey());
            }
        }
    }
    
    @Override
    public void updatePagesForPao(YukonPao pao, String paoName) {
        List<UserPage> pages = getPages(null, null);

        for (UserPage page : pages) {
            Integer pagePaoId = paoIdInPath(page.getPath(), paoUrls);
            if (pagePaoId != null && pagePaoId == pao.getPaoIdentifier().getPaoId()) {
                List<String> args = Arrays.asList(paoName);
                page = new UserPage(page.getId(), page.getKey(), page.getModule(), page.getName(), args,
                    page.isFavorite(), page.getLastAccess());
                save(page);
            }
        }
    }

    private Integer paoIdInPath(String path, List<Pattern> possibleUrls) {
        for (Pattern url : possibleUrls) {
            Matcher m = url.matcher(path);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }
        return null;
    }

    @Override
    public List<DisplayablePao> getDrFavorites(LiteYukonUser user) {
        List<UserPage> pages = getPagesForUser(user);
        List<Integer> paoIds = new ArrayList<>();
        for (UserPage page : pages) {
            if (page.isFavorite()){
                Integer paoId = paoIdInPath(page.getPath(), drFavoritesUrls);
                if (paoId != null) {
                    paoIds.add(paoId);
                }
            }
        }

        List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
        List<DisplayablePao> displayablePaos = paoLoadingService.getDisplayableDevices(paoIdentifiers);

        return displayablePaos;
    }

    @Override
    public List<DisplayablePao> getDrRecentViewed(LiteYukonUser user) {
        List<UserPage> pages = getPagesForUser(user);
        List<Integer> recentPaoIds = new ArrayList<>();

        for (UserPage page : pages) {
                Integer paoId = paoIdInPath(page.getPath(), drFavoritesUrls);
                if (paoId != null) {
                    recentPaoIds.add(paoId);
                }
        }

        if (recentPaoIds.isEmpty() ) {
            return Collections.emptyList();
        }

        if (recentPaoIds.size() > MAX_DR_RECENT_VIEWED) {
            recentPaoIds = recentPaoIds.subList(0, MAX_DR_RECENT_VIEWED);
        }

        List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(recentPaoIds);
        List<DisplayablePao> displayablePaos = paoLoadingService.getDisplayableDevices(paoIdentifiers);

        return displayablePaos;
    }
}

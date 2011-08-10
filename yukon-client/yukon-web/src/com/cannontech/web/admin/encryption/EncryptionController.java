package com.cannontech.web.admin.encryption;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.PoolManager;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.db.pao.StaticPaoInfo;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/encryption/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class EncryptionController {
    
    private Logger log = YukonLogManager.getLogger(EncryptionController.class);
    private YukonJdbcTemplate yukonJdbcTemplate;
    private static final String CPS_ONE_WAY_ENCRYPTION_KEY = "CPS_ONE_WAY_ENCRYPTION_KEY";

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model) {

        List<EncryptedRoute> encryptedRoutes = getAllEncryptedRoutes();

        model.addAttribute("encryptedRoutes", encryptedRoutes);

        return "encryptionSettings.jsp";
    }

    @RequestMapping("save")
    public String save(HttpServletRequest request, ModelMap model, String[] value,
                       Integer[] paobjectId) {

        Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        StaticPaoInfo spi = new StaticPaoInfo(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY);
        spi.setDbConnection(conn);

        if (paobjectId != null
                && value != null
                && paobjectId.length == value.length
                && paobjectId.length != 0) {
            
            for (int i = 0; i < paobjectId.length; i++) {
                spi.setInfoKey(CPS_ONE_WAY_ENCRYPTION_KEY);
                spi.setPaobjectId(paobjectId[i]);
                try {
                    spi.retrieve();

                    if (value[i].length() == 0) {
                        spi.setValue(null);
                    } else {
                        spi.setValue(value[i]);
                    }

                    spi.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.warn("Unable to save encryption keys in database.");
        }

        return "redirect:view";
    }

    private List<EncryptedRoute> getAllEncryptedRoutes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectid, paoname, type");
        sql.append("FROM yukonpaobject");
        sql.append("WHERE paoclass = 'ROUTE'");
        sql.append("    AND (Type ").eq(RouteTypes.STRING_RDS_TERMINAL_ROUTE);
        sql.append("    OR Type ").eq(RouteTypes.STRING_TAP_PAGING);
        sql.append("    OR Type ").eq(RouteTypes.STRING_SNPP_TERMINAL_ROUTE);
        sql.append("    OR Type ").eq(RouteTypes.STRING_WCTP_TERMINAL_ROUTE).append(")");

        final Connection conn =
            PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        final List<EncryptedRoute> encryptedIDs = new ArrayList<EncryptedRoute>();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                StaticPaoInfo spi = new StaticPaoInfo(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY);
                spi.setDbConnection(conn);
                spi.setPaobjectId(new Integer(rs.getInt("PaobjectId")));
                try {
                    spi.retrieve();
                } catch (NotFoundException e) {
                    spi.setValue(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY.getDefaultValue());
                    spi.add();
                    spi.retrieve();
                }

                EncryptedRoute route = new EncryptedRoute();
                route.setPaoName(rs.getString("PaoName"));
                route.setType(rs.getString("Type"));
                route.setInfoKey(spi.getPaoInfo().name());
                route.setPaobjectId(spi.getPaobjectId());
                route.setValue(spi.getValue());
                encryptedIDs.add(route);
            }
        });

        return encryptedIDs;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
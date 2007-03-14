package com.cannontech.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.util.ParamUtil;

@SuppressWarnings("serial")
public class CBCTagUpdaterServlet extends HttpServlet {

    //private LiteYukonUser user;
    private CBCTagHandler handler;
    
  
    

    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ServletContext servletContext = req.getSession().getServletContext();
        Integer paoID = ParamUtil.getInteger(req, "id", 0);
        String tagDesc = ParamUtil.getString(req, "tagDesc", null);
        String reason = ParamUtil.getString(req, "reason", null);
        
        if (tagDesc.equalsIgnoreCase("feeder"))
        {
            int i=0;   
        }
        else if (tagDesc.equalsIgnoreCase("sub"))
        {
            int i=0;
        }
            
        else
        {
            handler = (CBCTagHandler) servletContext.getAttribute(OnelineCBCServlet.TAGHANDLER);
            LiteYukonPAObject liteYukonPAO = DaoFactory.getPaoDao()
                                                           .getLiteYukonPAO(paoID.intValue());
            DBPersistent dbPers = LiteFactory.convertLiteToDBPers(liteYukonPAO);
            retrieveDBPersistent(dbPers);
            String userName = ((LiteYukonUser)req.getSession().getAttribute(LoginController.YUKON_USER)).getUsername();
            handler.init(dbPers, tagDesc, userName);
            handler.process(tagDesc, reason);
        }
   
    }

    private void retrieveDBPersistent(DBPersistent dbPers) {
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        dbPers.setDbConnection(connection);
        try {
            dbPers.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
        finally {
            if (connection != null) {try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }}
        }
    }

}

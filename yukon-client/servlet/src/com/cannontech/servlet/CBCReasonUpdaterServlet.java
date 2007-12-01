package com.cannontech.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

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
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.conns.ConnPool;

@SuppressWarnings("serial")
public class CBCReasonUpdaterServlet extends HttpServlet {
    private ClientConnection dispatchConn;
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ServletContext servletContext = req.getSession().getServletContext();
        Integer paoID = ParamUtil.getInteger(req, "id", 0);
        String tagDesc = ParamUtil.getString(req, "tagDesc", null);
        String reason = ParamUtil.getString(req, "reason", "(none)");
        String userName = ((LiteYukonUser) req.getSession()
                                              .getAttribute(LoginController.YUKON_USER)).getUsername();

        if (tagDesc.equalsIgnoreCase("feeder")) {
            String actionStr = "Feeder command. PaoID = " + paoID;
            Signal message = createSignal(reason, userName, actionStr);
            executeCommand(message);

        } else if (tagDesc.equalsIgnoreCase("sub")) {
            String actionStr = "Sub command. PaoID = " + paoID;
            Signal message = createSignal(reason, userName, actionStr);
            executeCommand(message);
        }

        else {
            CBCTagHandler handler = (CBCTagHandler) servletContext.getAttribute(OnelineCBCServlet.TAGHANDLER);
            LiteYukonPAObject liteYukonPAO = DaoFactory.getPaoDao()
                                                       .getLiteYukonPAO(paoID.intValue());
            DBPersistent dbPers = LiteFactory.convertLiteToDBPers(liteYukonPAO);
            retrieveDBPersistent(dbPers);
            handler.init(dbPers, tagDesc, userName);
            handler.process(tagDesc, reason);
        }

    }

    private synchronized void executeCommand(Signal message) {
        getDispatchConn().write(message);
    }

    private Signal createSignal(String reason, String userName, String actionStr) {
        Signal message = new Signal();
        message.setDescription(reason);
        message.setTimeStamp(new Date());
        message.setAction(actionStr);
        message.setUserName(userName);
        return message;
    }

    private void retrieveDBPersistent(DBPersistent dbPers) {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        dbPers.setDbConnection(connection);
        try {
            dbPers.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    CTILogger.error(e);
                }
            }
        }
    }

    private synchronized ClientConnection getDispatchConn() {
        if (dispatchConn == null) {
            dispatchConn = (ClientConnection) ConnPool.getInstance().getDefDispatchConn();
        }    
        return dispatchConn;
    }


}

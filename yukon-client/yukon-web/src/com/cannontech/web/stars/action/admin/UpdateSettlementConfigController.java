package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SettlementConfigFuncs;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateSettlementConfigController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        List<String> paramNames = new ArrayList<String>();
        java.util.Enumeration enum1 = request.getParameterNames();
                  while (enum1.hasMoreElements()) {
                    String ele = enum1.nextElement().toString();
                    paramNames.add(ele);
                    String [] vals = request.getParameterValues(ele);
                    for (int i= 0; i < vals.length; i++)
                     CTILogger.info(ele + " - "+i + ": " + vals[i]);
                }
        
        int yukDefID = ServletRequestUtils.getIntParameter(request, "YukonDefID", -1);
        int stlmtEntryID = ServletRequestUtils.getIntParameter(request, "SettlementEntryID", -1);
        
        List<Integer> validRateEntryID = new ArrayList<Integer>();
        for (final String param : paramNames) {

            String paramVal = request.getParameter(param);
            if( param.startsWith("ConfigID") )
            {
                String configIDStr = param.substring(8);
                int configID = Integer.valueOf(configIDStr).intValue();
                LiteSettlementConfig lsc = SettlementConfigFuncs.getLiteSettlementConfig(configID);
                
                if (lsc == null) { 
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "SettlementConfig cannot be null");
                    String redirect = this.getReferer(request);
                    response.sendRedirect(redirect);
                    return;
                }
                
                if (lsc.getConfigID() >= 0) {
                    if (!lsc.getFieldValue().equalsIgnoreCase(paramVal))//only update the DBPers if the field Value actually changed
                        SettlementConfigFuncs.updateSettlementConfigTrx(lsc, paramVal);
                } else {
                    SettlementConfig newSC = new SettlementConfig();
                    newSC.setConfigID(SettlementConfig.getNextConfigID());
                    newSC.setFieldName(lsc.getFieldName());
                    newSC.setFieldValue(paramVal);
                    newSC.setCtiSettlement(SettlementConfigFuncs.getCTISettlementStr(yukDefID));
                    newSC.setYukonDefID(new Integer(yukDefID));
                    newSC.setDescription(lsc.getDescription());
                    newSC.setEntryID(new Integer(stlmtEntryID));
                    newSC.setRefEntryID(new Integer(0));
                    try
                    {
                        Transaction.createTransaction(Transaction.INSERT, newSC).execute();
                        DBChangeMsg msg = new DBChangeMsg(
                            newSC.getConfigID().intValue(),
                            DBChangeMsg.CHANGE_SETTLEMENT_DB,
                            DBChangeMsg.CAT_SETTLEMENT,
                            DBChangeMsg.CAT_SETTLEMENT,
                            DBChangeMsg.CHANGE_TYPE_ADD
                            );
                        ServerUtils.handleDBChangeMsg(msg);
                    }
                    catch (TransactionException e) {
                        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new SettlementConfig");
                        String redirect = this.getReferer(request);
                        response.sendRedirect(redirect);
                        return;
                    }
                        
                }
                    //TODO if configid<0 then create new entry, else update entry.
            }
            else if( param.startsWith("EntryID"))
            {
                String entryIDStr = param.substring(7);
                int entryID = 0;
                int rateIndex = -1;
                if( (rateIndex=param.indexOf("RateID")) > 0)    //There is a Rate Config in this string also
                {
                    entryID = Integer.valueOf(param.substring(7, rateIndex)).intValue();
                    String configIDStr = param.substring(rateIndex+6);
                    int configID = Integer.valueOf(configIDStr).intValue();
                    
                    if( configID >= 0)  //custom rate, IDs < 0 are Yukon defaults.  Just need to update the SettlementConfig entry
                    {
                        LiteSettlementConfig lsc = SettlementConfigFuncs.getLiteSettlementConfig(configID);
                        if( lsc != null && !lsc.getFieldValue().equalsIgnoreCase(paramVal)) //only update the DBPers if the field Value actually changed
                            SettlementConfigFuncs.updateSettlementConfigTrx(lsc, paramVal);
                    }
                    else    //Need to create a new entry in SettlementConfig
                    {
                        LiteSettlementConfig defaultLSC = SettlementConfigFuncs.getLiteSettlementConfig(configID);
                        
                        //Create a new (but very similar, so we can mostly copy) SettlementConfig entry
                        SettlementConfig newSC = new SettlementConfig();
                        newSC.setConfigID(SettlementConfig.getNextConfigID());
                        newSC.setFieldName(defaultLSC.getFieldName());  //the default FieldValue becomes the New entries FieldName
                        newSC.setFieldValue(paramVal);
                        newSC.setCtiSettlement(SettlementConfigFuncs.getCTISettlementStr(yukDefID));
                        newSC.setYukonDefID(new Integer(yukDefID));
                        newSC.setDescription(defaultLSC.getDescription());
                        newSC.setEntryID(new Integer(stlmtEntryID));
                        newSC.setRefEntryID(new Integer(entryID));
                        
                        try
                        {
                            Transaction.createTransaction(Transaction.INSERT, newSC).execute();
                            DBChangeMsg msg = new DBChangeMsg(
                                newSC.getConfigID().intValue(),
                                DBChangeMsg.CHANGE_SETTLEMENT_DB,
                                DBChangeMsg.CAT_SETTLEMENT,
                                DBChangeMsg.CAT_SETTLEMENT,
                                DBChangeMsg.CHANGE_TYPE_ADD
                                );
                            ServerUtils.handleDBChangeMsg(msg);
                        }
                        catch (TransactionException e) {
                            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new SettlementConfig");
                            String redirect = this.getReferer(request);
                            response.sendRedirect(redirect);
                            return;
                        }                       
                    }
                }
                else    //only an EntryID
                { 
                    entryID = Integer.valueOf(entryIDStr).intValue();
                }
                validRateEntryID.add(new Integer(entryID));
            }
        }
        
        //loop through all updated/inserted Rates and remove all from the settlement config that were not submitted in this request
        List allConfigs = SettlementConfigFuncs.getAllLiteConfigsBySettlementType(yukDefID);
        for (int i = 0; i < allConfigs.size(); i++)
        {
            LiteSettlementConfig lsc = (LiteSettlementConfig)allConfigs.get(i);
            if( lsc.getEntryID() > 0 && lsc.getEntryID() != stlmtEntryID)   //0 is default for no mapping to a YukonListEntry
            {
                boolean deleteRate = true;
                for (final Integer entryId : validRateEntryID) {
                    if (entryId.equals(lsc.getEntryID())) {
                        deleteRate = false;
                        break;
                    }
                }

                if( deleteRate)
                {
                    try
                    {
                        CTILogger.info("DELETEING RATE: " + lsc.getConfigID());
                        SettlementConfig delSC = (SettlementConfig)LiteFactory.createDBPersistent(lsc);
                        Transaction.createTransaction(Transaction.DELETE, delSC).execute();
                        DBChangeMsg msg = new DBChangeMsg(
                            lsc.getConfigID(),
                            DBChangeMsg.CHANGE_SETTLEMENT_DB,
                            DBChangeMsg.CAT_SETTLEMENT,
                            DBChangeMsg.CAT_SETTLEMENT,
                            DBChangeMsg.CHANGE_TYPE_DELETE
                            );
                        ServerUtils.handleDBChangeMsg(msg);
                    }
                    catch (TransactionException e)
                    {
                        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to remove SettlementConfig with ID: " + lsc.getConfigID());
                        String redirect = this.getReferer(request);
                        response.sendRedirect(redirect);
                        return;
                    }                       
                }
            }
        }

        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}

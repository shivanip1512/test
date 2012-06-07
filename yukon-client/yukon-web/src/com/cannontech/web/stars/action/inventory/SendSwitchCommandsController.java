package com.cannontech.web.stars.action.inventory;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class SendSwitchCommandsController extends StarsInventoryActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            Map<Integer,Object[]> batchConfig = InventoryManagerUtil.getBatchConfigSubmission();
            
            if (request.getParameter("All") != null) {
                int memberID = Integer.parseInt(request.getParameter("All"));
                
                List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
                for (int i = 0; i < descendants.size(); i++) {
                    LiteStarsEnergyCompany company = descendants.get(i);
                    if (memberID >= 0 && company.getLiteID() != memberID) continue;
                    
                    SwitchCommandQueue.SwitchCommand[] commands = SwitchCommandQueue.getInstance().getCommands( company.getLiteID(), false );
                    if (commands != null && commands.length > 0) {
                        for (int j = 0; j < commands.length; j++)
                            InventoryManagerUtil.sendSwitchCommand( commands[j] );
                        
                        String msg = commands.length + " switch commands sent successfully";
                        ActivityLogger.logEvent(user.getUserID(), -1, company.getLiteID(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
                        batchConfig.put( company.getEnergyCompanyId(), new Object[]{new Date(), msg} );
                    }
                }
            }
            else {
                String[] values = request.getParameterValues( "InvID" );
                Hashtable numCmdSentMap = new Hashtable();
                
                for (int i = 0; i < values.length; i++) {
                    int invID = Integer.parseInt( values[i] );
                    SwitchCommandQueue.SwitchCommand cmd = SwitchCommandQueue.getInstance().getCommand( invID, false );
                    InventoryManagerUtil.sendSwitchCommand( cmd );
                    
                    Integer energyCompanyID = new Integer(cmd.getEnergyCompanyID());
                    Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
                    if (numCmdSent == null)
                        numCmdSent = new Integer(1);
                    else
                        numCmdSent = new Integer(numCmdSent.intValue() + 1);
                    numCmdSentMap.put( energyCompanyID, numCmdSent );
                }
                
                Iterator it = numCmdSentMap.keySet().iterator();
                while (it.hasNext()) {
                    Integer energyCompanyID = (Integer) it.next();
                    Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
                    String msg = numCmdSent + " switch commands sent successfully";
                    ActivityLogger.logEvent(user.getUserID(), -1, energyCompanyID.intValue(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
                    batchConfig.put( energyCompanyID, new Object[]{new Date(), msg} );
                }
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands sent out successfully");
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}

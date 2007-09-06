package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsMsgUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateThermostatScheduleController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            StarsThermostatTypes thermType = StarsThermostatTypes.valueOf( request.getParameter("type") );
            int hwTypeDefID = StarsMsgUtils.getLMHardwareTypeDefID(thermType).intValue();
            
            StarsUpdateThermostatSchedule updateSched = UpdateThermostatScheduleAction.getRequestOperation(request).getStarsUpdateThermostatSchedule();
            
            LiteLMThermostatSchedule liteSchedule = energyCompany.getDefaultThermostatSchedule( hwTypeDefID );
            StarsUpdateThermostatScheduleResponse resp = UpdateThermostatScheduleAction.updateThermostatSchedule( updateSched, liteSchedule, energyCompany );
            
            StarsDefaultThermostatSchedules dftSchedules = energyCompany.getStarsDefaultThermostatSchedules();
            for (int i = 0; i < dftSchedules.getStarsThermostatProgramCount(); i++) {
                StarsThermostatProgram schedule = dftSchedules.getStarsThermostatProgram(i);
                if (schedule.getThermostatType().getType() == thermType.getType()) {
                    UpdateThermostatScheduleAction.parseResponse( resp, schedule );
                    break;
                }
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Default thermostat schedule updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update default thermostat schedule");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}

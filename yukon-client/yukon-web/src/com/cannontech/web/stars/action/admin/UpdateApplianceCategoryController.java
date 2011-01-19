package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateApplianceCategoryController extends StarsAdminActionController {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        try {
            int appCatID = ServletRequestUtils.getIntParameter(request,"AppCatID");
            boolean newAppCat = (appCatID == -1);

            com.cannontech.database.db.web.YukonWebConfiguration config =
                new com.cannontech.database.db.web.YukonWebConfiguration();
            config.setLogoLocation( request.getParameter("IconName") );
            if (Boolean.valueOf( request.getParameter("SameAsName") ).booleanValue())
                config.setAlternateDisplayName( request.getParameter("Name") );
            else
                config.setAlternateDisplayName( request.getParameter("DispName") );
            config.setDescription( request.getParameter("Description").replaceAll(LINE_SEPARATOR, "<br>") );
            config.setURL( "" );

            com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
                new com.cannontech.database.data.stars.appliance.ApplianceCategory();
            com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
            appCatDB.setCategoryID( Integer.valueOf(request.getParameter("Category")) );
            appCatDB.setDescription( request.getParameter("Name") );
            appCat.setWebConfiguration( config );

            LiteApplianceCategory liteAppCat = null;
            if (newAppCat) {
                appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );

                appCat = Transaction.createTransaction( Transaction.INSERT, appCat ).execute();

                liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
                energyCompany.addApplianceCategory( liteAppCat );
                LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
                this.starsDatabaseCache.addWebConfiguration( liteConfig );
            }
            else {
                liteAppCat = energyCompany.getApplianceCategory( appCatID );
                if (energyCompany.isApplianceCategoryInherited(appCatID)) {
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Cannot update an inherited appliance category" );
                    String redirect = this.getRedirect(request);
                    response.sendRedirect(redirect);
                    return;
                }

                appCat.setApplianceCategoryID( new Integer(appCatID) );
                appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );

                appCat = Transaction.createTransaction( Transaction.UPDATE, appCat ).execute();

                StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
                LiteWebConfiguration liteConfig = this.starsDatabaseCache.getWebConfiguration( appCat.getWebConfiguration().getConfigurationID().intValue() );
                StarsLiteFactory.setLiteWebConfiguration( liteConfig, appCat.getWebConfiguration() );
            }

            List<LiteLMProgramWebPublishing> pubProgList = new ArrayList<LiteLMProgramWebPublishing>();
            for(LiteLMProgramWebPublishing program : liteAppCat.getPublishedPrograms()) {
            	pubProgList.add(program);
            }

            String[] progIDs = request.getParameterValues( "ProgIDs" );
            String[] deviceIDs = request.getParameterValues( "DeviceIDs" );
            String[] progDispNames = request.getParameterValues( "ProgDispNames" );
            String[] progShortNames = request.getParameterValues( "ProgShortNames" );
            String[] progDescriptions = request.getParameterValues( "ProgDescriptions" );
            String[] progDescFiles = request.getParameterValues( "ProgDescFiles" );
            String[] progCtrlOdds = request.getParameterValues( "ProgChanceOfCtrls" );
            String[] progIconNames = request.getParameterValues( "ProgIconNames" );

            if (progIDs != null) {
                for (int i = 0; i < progIDs.length; i++) {
                    int progID = Integer.parseInt( progIDs[i] );
                    int deviceID = Integer.parseInt( deviceIDs[i] );
                    LiteLMProgramWebPublishing liteProg = null;

                    for (int j = 0; j < pubProgList.size(); j++) {
                        LiteLMProgramWebPublishing lProg = (LiteLMProgramWebPublishing) pubProgList.get(j);
                        if (lProg.getProgramID() == progID || deviceID > 0 && lProg.getDeviceID() == deviceID) {
                            pubProgList.remove(j);
                            liteProg = lProg;
                            break;
                        }
                    }

                    String newDispName = progDispNames[i];
                    if (newDispName.length() == 0 && deviceID > 0)
                    {
                        try
                        {
                            newDispName = this.paoDao.getYukonPAOName( deviceID );
                        }
                        catch(NotFoundException e) {}
                    }
                    if (newDispName.length() == 0) 
                    {
                        session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "The display name of a virtual program cannot be empty" );
                        String redirect = this.getReferer(request);
                        response.sendRedirect(redirect);
                        return;
                    }

                    com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
                        new com.cannontech.database.data.stars.LMProgramWebPublishing();
                    com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB = pubProg.getLMProgramWebPublishing();
                    pubProgDB.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
                    pubProgDB.setDeviceID( new Integer(deviceID) );
                    pubProgDB.setChanceOfControlID( Integer.valueOf(progCtrlOdds[i]) );
                    pubProgDB.setProgramOrder( new Integer(i+1) );

                    if (progDispNames[i].indexOf(",") >= 0)
                        progDispNames[i] = "\"" + progDispNames[i] + "\"";
                    if (progShortNames[i].indexOf(",") >= 0)
                        progShortNames[i] = "\"" + progShortNames[i] + "\"";

                    com.cannontech.database.db.web.YukonWebConfiguration cfg =
                        new com.cannontech.database.db.web.YukonWebConfiguration();
                    cfg.setLogoLocation( progIconNames[i] );
                    cfg.setAlternateDisplayName( progDispNames[i] + "," + progShortNames[i] );
                    cfg.setDescription( progDescriptions[i].replaceAll(LINE_SEPARATOR, "<br>") );
                    cfg.setURL( progDescFiles[i] );
                    pubProg.setWebConfiguration( cfg );

                    if (liteProg != null) {
                        pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
                        pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
                        pubProg = Transaction.createTransaction( Transaction.UPDATE, pubProg ).execute();

                        liteProg.setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
                        liteProg.setProgramOrder( pubProg.getLMProgramWebPublishing().getProgramOrder().intValue() );

                        LiteWebConfiguration liteCfg = this.starsDatabaseCache.getWebConfiguration( liteProg.getWebSettingsID() );
                        StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
                    }
                    else {
                        pubProg = Transaction.createTransaction( Transaction.INSERT, pubProg ).execute();
                        liteProg = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite( pubProg.getLMProgramWebPublishing() );
                        energyCompany.addProgram( liteProg, liteAppCat );

                        LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite( pubProg.getWebConfiguration() );
                        this.starsDatabaseCache.addWebConfiguration( liteCfg );
                    }
                }
            }

            // Delete the rest of published programs
            for (int i = 0; i < pubProgList.size(); i++) {
                LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) pubProgList.get(i);

                // Delete Stars LMProgramWebPublishing
                StarsAdminUtil.deleteLMProgramWebPublishing(liteProg.getProgramID(), energyCompany, user.getYukonUser());
            }

            if (newAppCat)
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category is created successfully");
            else
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category information updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update appliance category information");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}

package com.cannontech.stars.dr.optout.service;

import java.util.List;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.util.StarsUtils;

public final class OptOutNotificationUtil {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private OptOutNotificationUtil() {
        
    }
    
    public static String getProgramInformation(final LiteStarsEnergyCompany energyCompany,
            final LiteAccountInfo liteAcctInfo,
                final List<LiteLmHardwareBase> hardwares) {
        
        final StringBuilder text = new StringBuilder();
        
        for (final LiteLmHardwareBase liteHw : hardwares) {
            text.append("Serial #: ").append(liteHw.getManufacturerSerialNumber()).append(LINE_SEPARATOR);
            
            boolean hasAssignedProg = false;
            for (final LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0) {
                    LiteStarsLMProgram liteProg = OptOutNotificationUtil.getLMProgram( liteAcctInfo, liteApp.getProgramID() );
                    
                    String progName = StarsUtils.getPublishedProgramName( liteProg.getPublishedProgram() );
                    text.append("    Program: ").append( progName );
                    
                    String groupName = "(none)";
                    if (liteApp.getAddressingGroupID() > 0)
                    {
                        try
                        {
                            groupName = DaoFactory.getPaoDao().getYukonPAOName( liteApp.getAddressingGroupID() );
                        }
                        catch(NotFoundException e) {}
                    }
                    text.append(", Group: ").append(groupName).append(LINE_SEPARATOR);
                    
                    hasAssignedProg = true;
                }
            }
            text.append(LINE_SEPARATOR);
            
            if (!hasAssignedProg)
                text.append("    (No Assigned Program)").append(LINE_SEPARATOR);
        }
        
        return text.toString();
    }
    
    public static String getAccountInformation(final LiteStarsEnergyCompany energyCompany, 
            final LiteAccountInfo liteAcctInfo) {
        
        final StringBuilder text = new StringBuilder();
        
        text.append("Account #").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append(LINE_SEPARATOR);
        
        LiteContact cont = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
        String name = StarsUtils.formatName( cont );
        if (name.length() > 0)
            text.append( name ).append(LINE_SEPARATOR);
        
        LiteAddress addr = energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
        if (addr.getLocationAddress1().trim().length() > 0) {
            text.append(addr.getLocationAddress1());
            if (addr.getLocationAddress2().trim().length() > 0)
                text.append(", ").append(addr.getLocationAddress2());
            text.append(LINE_SEPARATOR);
            if (addr.getCityName().trim().length() > 0)
                text.append(addr.getCityName()).append(", ");
            if (addr.getStateCode().trim().length() > 0)
                text.append(addr.getStateCode()).append(" ");
            if (addr.getZipCode().trim().length() > 0)
                text.append(addr.getZipCode());
            text.append(LINE_SEPARATOR);
        }
        
        String homePhone = StarsUtils.getNotification(
                DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(cont, ContactNotificationType.HOME_PHONE) );
        String workPhone = StarsUtils.getNotification(
                DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(cont, ContactNotificationType.WORK_PHONE) );
        String email = StarsUtils.getNotification(
                DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(cont, ContactNotificationType.EMAIL) );
        
        if (homePhone.length() > 0)
            text.append(homePhone).append(LINE_SEPARATOR);
        else if (workPhone.length() > 0)
            text.append(workPhone).append(LINE_SEPARATOR);
        else if (email.length() > 0)
            text.append(email).append(LINE_SEPARATOR);
        
        return text.toString();
    }
    
    public static String getQuestions(final List<ScheduledOptOutQuestion> questions) {
        final StringBuilder sb = new StringBuilder();
        
        for (ScheduledOptOutQuestion question : questions) {
            sb.append("Q: ").append(question.getQuestion()).append(LINE_SEPARATOR);
            sb.append("A: ").append(question.getAnswer()).append(LINE_SEPARATOR);
            sb.append(LINE_SEPARATOR);
        }
        
        String result = sb.toString();
        return result;
    }

    private static LiteStarsLMProgram getLMProgram(LiteAccountInfo liteAcctInfo, int programId) {
        for (final LiteStarsLMProgram program : liteAcctInfo.getPrograms()) {
            if (program.getProgramID() == programId) return program;
        }
        return null;
    }
}

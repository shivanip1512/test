package com.cannontech.web.stars.action.workorder;

import java.io.File;

import java.io.FileOutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SendWorkOrderController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int orderID = ServletRequestUtils.getIntParameter(request, "OrderID");
        LiteWorkOrderBase liteOrder = starsWorkOrderBaseDao.getById(orderID);
        
        if (liteOrder.getServiceCompanyID() == 0) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "You have not assigned this work order to a service company yet." );
            String location = this.getRedirect(request);
            response.sendRedirect(location);
            return;
        }
        
        LiteServiceCompany sc = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
        String email = null;
        if (sc.getPrimaryContactID() > 0) {
            LiteContact contact = this.contactDao.getContact( sc.getPrimaryContactID() );
            LiteContactNotification emailNotif = this.contactNotificationDao.getFirstNotificationForContactByType( contact, ContactNotificationType.EMAIL);
            if (emailNotif != null) email = emailNotif.getNotification();
        }
        
        if (email == null) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "There is no email address assigned to service company \"" + sc.getCompanyName() + "\"." );
            String location = this.getRedirect(request);
            response.sendRedirect(location);
            return;
        }
        
        try {
            ReportBean reportBean = new ReportBean();
            reportBean.setType(ReportTypes.EC_WORK_ORDER);
            reportBean.getModel().setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
            ((WorkOrderModel)reportBean.getModel()).setOrderID( new Integer(orderID) );
            
            JFreeReport report = reportBean.createReport();;
            
            File tempDir = new File( ServerUtils.getStarsTempDir(), "/WorkOrder" );
            if (!tempDir.exists()) tempDir.mkdirs();
            File tempFile = File.createTempFile("WorkOrder", ".pdf", tempDir);
            tempFile.deleteOnExit();
            
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream( tempFile );
                ReportFuncs.outputYukonReport( report, "pdf", fos );
            }
            catch (Exception e) {
                // There will always be an exception because the PDF encoder will try to write two versions
                // of data into the file, while the output stream will stop accepting data after the first
                // EOF is met. The exception is simply ignored here, could miss some other exceptions.
            }
            finally {
                if (fos != null) fos.close();
            }
            
            Date now = new Date();
            String fileName = "WorkOrder_" + StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".pdf";
            
            EmailMessage emailMsg = new EmailMessage();
            emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
            emailMsg.setTo( email );
            emailMsg.setSubject( "Work Order" );
            emailMsg.setBody( "" );
            emailMsg.addAttachment( tempFile, fileName );
            
            emailMsg.send();
            
            session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Sent work order to the service company successfully." );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to send the work order." );
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}

package com.cannontech.web.stars.action.importmanager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class ImportCustAccountsController extends StarsImportManagerActionController {

	@Override
	public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
			final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {

	    MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        try {
            final String email = mRequest.getParameter("email");
            
            // Setting up temp files that will be used in the import process
            MultipartFile custMultipartFile = mRequest.getFile("CustFile");
            MultipartFile hwMultipartFile = mRequest.getFile("HwFile");
            
            if (custMultipartFile == null && hwMultipartFile == null) {
                throw new WebClientException( "No import file is provided" );
            } 
                 
            final File custFile = convertToTempFile(custMultipartFile, "CustomerImport", ".csv");
            final File hwFile = convertToTempFile(hwMultipartFile, "HardwareImport", ".csv");
            
            String logMsg = "Customer account import process started.";
            ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, logMsg );

            final Integer userId = Integer.valueOf(user.getUserID());

            final TimeConsumingTask task = new ImportCustAccountsTask() {
                private TimeConsumingTask preScanTask;
                private TimeConsumingTask realTask;
                
                public void run() {
                    this.setStatus(ImportCustAccountsTask.STATUS_RUNNING);
                    preScanTask = new ImportCustAccountsTask(energyCompany, custFile, hwFile, email, true, userId);
                    long preScanId = ProgressChecker.addTask(preScanTask);
                    
                    for (preScanTask = ProgressChecker.getTask(preScanId); ((preScanTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) ||
                            (preScanTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR));) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignore) {}
                        
                        if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
                            ProgressChecker.removeTask(preScanId);
                            break;
                        }
                        
                        if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                            ProgressChecker.removeTask(preScanId);
                            this.setStatus(ImportCustAccountsTask.STATUS_ERROR);
                            return;
                        }
                    }
                    
                    realTask = new ImportCustAccountsTask(energyCompany, custFile, hwFile, email, false, userId);
                    long realTaskId = ProgressChecker.addTask(realTask);
                    
                    for (realTask = ProgressChecker.getTask(realTaskId); ((realTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) ||
                            (realTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR));) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignore) {}
                        
                        if (realTask.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
                            ProgressChecker.removeTask(realTaskId);
                            this.setStatus(ImportCustAccountsTask.STATUS_FINISHED);
                            break;
                        }
                        
                        if (realTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                            ProgressChecker.removeTask(realTaskId);
                            this.setStatus(ImportCustAccountsTask.STATUS_ERROR);
                            return;
                        }
                    }
                    
                    this.setStatus(ImportCustAccountsTask.STATUS_FINISHED);
                }
                
                public String getProgressMsg() {
                    return (realTask != null) ? realTask.getProgressMsg() : (preScanTask != null) ? preScanTask.getProgressMsg() : "Pre-scanning import file(s)"; 
                }
                
                public Collection getErrorList() {
                    if (preScanTask != null && preScanTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                        return ((ImportCustAccountsTask) preScanTask).getErrorList();
                    }
                    if (realTask != null && realTask.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                        return ((ImportCustAccountsTask) realTask).getErrorList();
                    }
                    return new java.util.HashSet();
                }
                
                public void cancel() {
                    if (this.getStatus() == ImportCustAccountsTask.STATUS_RUNNING) {
                        this.setStatus(ImportCustAccountsTask.STATUS_CANCELING);
                        if (preScanTask != null) {
                            preScanTask.cancel();
                            while (preScanTask.getStatus() != ImportCustAccountsTask.STATUS_CANCELED||
                                   preScanTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR ||
                                   preScanTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignore) {}
                                
                                if (preScanTask.getStatus() == ImportCustAccountsTask.STATUS_CANCELED) {
                                    break;
                                }
                            }
                        }
                        if (realTask != null) {
                            realTask.cancel();
                            while (realTask.getStatus() != ImportCustAccountsTask.STATUS_CANCELED ||
                                   realTask.getStatus() != ImportCustAccountsTask.STATUS_ERROR ||
                                   realTask.getStatus() != ImportCustAccountsTask.STATUS_FINISHED) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignore) {}
                                
                                if (realTask.getStatus() == ImportCustAccountsTask.STATUS_CANCELED) {
                                    break;
                                }
                            }
                        }
                        this.setStatus(ImportCustAccountsTask.STATUS_CANCELED);
                    }
                }
            };
            
            
            long id = ProgressChecker.addTask(task);
            int x;
            TimeConsumingTask t;
            for (t = ProgressChecker.getTask(id), x = 0; x < 5; x++ ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {}
                
                if (t.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, t.getProgressMsg());
                    ProgressChecker.removeTask(id);
                    String redirect = this.getRedirect(request);
                    response.sendRedirect(redirect);
                    return;
                }
                
                if (t.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                    session.setAttribute("errorList", ((ImportCustAccountsTask) t).getErrorList());
                    ProgressChecker.removeTask(id);
                    String redirect = request.getContextPath() + "/operator/Consumer/ImportManagerView.jsp";
                    response.sendRedirect(redirect);
                    return;
                }
            }
            
            session.setAttribute(ServletUtils.ATT_REDIRECT, this.getRedirect(request));
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getRedirect(request));
            String redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
            response.sendRedirect(redirect);
            return;
        }
        catch (IOException ioe) {
            CTILogger.error( ioe.getMessage(), ioe );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, ioe.getMessage());
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
		
	}

    private File convertToTempFile(MultipartFile multipartFile, String prefix, String postfix) throws IOException{
        File tempFile = null;
        
        if (!StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            tempFile = File.createTempFile(prefix, postfix);
            tempFile.deleteOnExit();
            multipartFile.transferTo(tempFile);
        } 
        
        return tempFile;
    }
}

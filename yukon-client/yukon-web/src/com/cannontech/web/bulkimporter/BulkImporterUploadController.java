package com.cannontech.web.bulkimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.importer.DBFuncs;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.util.StarsUtils;

import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.exception.NoImportFileException;


@Controller
@CheckRoleProperty(YukonRoleProperty.IMPORTER_ENABLED)
@RequestMapping("/bulkimporter/*")
public class BulkImporterUploadController  {
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private BulkImportDataDao bulkImportDataDao;
    
    /**
     * Check for basic problems in file submission. Call the saveFileData method, consolidate success/error
     * messages for display
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/upload",  params = "importFile" , method = RequestMethod.POST)
    public ModelAndView importFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContextUtils.getYukonUserContext(request));
        // mav
        ModelAndView mav = new ModelAndView("redirect:/amr/bulkimporter/home");
        
        // check if file is ok, process file if it good
        Map<String, List<String>> msgs = new HashMap<String, List<String>>();
        List<String> goodMsgs = new ArrayList<String>();
        List<String> badMsgs = new ArrayList<String>();
        
        MultipartFile dataFile = null;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        
        if(isMultipart) {
            
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            dataFile = mRequest.getFile("dataFile");

            try {
                FileUploadUtils.validateDataUploadFileType(dataFile);
            } catch (NoImportFileException | EmptyImportFileException | ImportFileFormatException e) {
                badMsgs.add(dataFile.getOriginalFilename() + " " + accessor.getMessage(e.getMessage()));
            }
            if (badMsgs.size() == 0) {
                msgs = saveFileData(dataFile);
                goodMsgs = msgs.get("good");
                badMsgs = msgs.get("bad");
            }
        }
        
        // return any error msg
        String msgType = "1";
        String msgStr = "";
        if (badMsgs.size() > 0) {
            for (String badMsg : badMsgs) {
                msgStr += badMsg;
            }
            msgType = "0";
        }
        else {
            for (String goodMsg : goodMsgs) {
                msgStr += goodMsg;
            }
        }
        
        mav.addObject("msgType", msgType);
        mav.addObject("msgStr", msgStr);
            
        return mav;
    }
    
    
    /**
     * Call method that overrides the normal wait period that the bulk importer uses before checking for 
     * new imported meters. Prev/next import times will be updated, and the next refresh will show that
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/upload", params = "forceManualImportEvent" , method = RequestMethod.POST)
    public ModelAndView forceManualImportEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        ModelAndView mav = new ModelAndView("redirect:/amr/bulkimporter/home");
        
        // check if force manual import event button was clicked
        DBFuncs.forceImport();
        DBFuncs.writeNextImportTime(new java.util.Date(), true);
        
        
        return mav;
    }
    
    /**
     * remove items from the importData or ImportFail tables 
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/upload", params = "clearImports" , method = RequestMethod.POST)
    public ModelAndView clearImports(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        ModelAndView mav = new ModelAndView("redirect:/amr/bulkimporter/home");
        
        // check if force manual import event button was clicked
        String clearImportsSelect = ServletRequestUtils.getStringParameter(request, "clearImportsSelect");
        
        if (clearImportsSelect.equals("failed") || clearImportsSelect.equals("all")) {
            bulkImportDataDao.deleteAllDataFailures();
        }
        
        if (clearImportsSelect.equals("pendingComm") || clearImportsSelect.equals("all")) {
            bulkImportDataDao.deleteAllPending();
        }
        
        if (clearImportsSelect.equals("failedComm") || clearImportsSelect.equals("all")) {
            bulkImportDataDao.deleteAllCommunicationFailures();
        }
        
        return mav;
    }
    
    
    /**
     * Parse the data file and insert items into the ImportData table
     * 
     * @param dataFile
     * @return
     */
    public Map<String, List<String>> saveFileData(MultipartFile dataFile) {
        
        Map<String, List<String>> msgs = new HashMap<String, List<String>>();
        List<String> goodMsgs = new ArrayList<String>();
        List<String> badMsgs = new ArrayList<String>();
        
        List<ImportData> goodEntries = new ArrayList<ImportData>();
        boolean errorOccurred = false;
        
        try {
            
            int lineNo = 0;

            InputStreamReader isr = new InputStreamReader(dataFile.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String line = null;
            
			// Checks to see if the file contains any bits

            while ((line = reader.readLine()) != null) {
                
                lineNo++;
                
                if (lineNo == 1 || line.trim().length() == 0 || line.charAt(0) == '#') {
                    continue;
                }

                String[] columns = StarsUtils.splitString( line, "," );
                if (columns.length < ImportData.SETTER_COLUMNS.length - 2) {
                    badMsgs.add("Incorrect number of fields on line " + lineNo + ", fix and try again. No Meters Added.");
                    errorOccurred = true;
                    break;
                }
                
                ImportData currentEntry = new ImportData();
                currentEntry.setAddress(columns[0].trim());
                currentEntry.setName(columns[1].trim());
                currentEntry.setRouteName(columns[2].trim());
                currentEntry.setMeterNumber(columns[3].trim());
                currentEntry.setCollectionGrp(columns[4].trim());
                currentEntry.setAltGrp(columns[5].trim());
                currentEntry.setTemplateName(columns[6].trim());
                currentEntry.setBillingGroup(" ");
                currentEntry.setSubstationName(" ");
                //possibly ignore these non-mandatory columns
                if(columns.length >= ImportData.SETTER_COLUMNS.length - 1)
                    currentEntry.setBillingGroup(columns[7].trim());
                if(columns.length == ImportData.SETTER_COLUMNS.length)
                    currentEntry.setSubstationName(columns[8].trim());
                    
                try {
                    Transaction.createTransaction(Transaction.INSERT, currentEntry).execute();
                    goodEntries.add(currentEntry);
                }
                catch (TransactionException e) {
                    CTILogger.error(e);
                    badMsgs.add("Unable to insert line " + lineNo + ", fix and try again. No meters added.");
                    errorOccurred = true;
                    break;
                }
            }
        }
        catch (IOException e) {
            CTILogger.error(e);
            badMsgs.add("Unable to read file.");
            errorOccurred = true;
        }
        
        // IF THERE WAS AN ERROR, BACKOUT ALL METERS THAT WERE ADDED
        if (errorOccurred) {
            int remainingEntries = goodEntries.size();
            for (ImportData goodEntry : goodEntries) {
                try {
                    Transaction.createTransaction(Transaction.DELETE, goodEntry).execute();
                    remainingEntries--;
                }
                catch (TransactionException e) {
                    badMsgs.add("An error occurred while backing out imported meters, " + remainingEntries + " meters were not removed.");
                }
            }
        }
        else {
            goodMsgs.add(goodEntries.size() + " meters loaded and ready for import.");
        }
        
        msgs.put("good", goodMsgs);
        msgs.put("bad", badMsgs);
        
        return msgs;
    }

}
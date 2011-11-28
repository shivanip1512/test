package com.cannontech.web.bulk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.db.point.fdr.FDRInterfaceOption;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.web.bulk.service.FdrTranslationManagerService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("fdrTranslationManager/*")
public class FdrTranslationManagerController {
    private Logger log = YukonLogManager.getLogger(FdrTranslationManagerController.class);
    private FdrTranslationManagerService fdrTranslationManagerService;
    private RecentResultsCache<TranslationImportCallbackResult> recentResultsCache;
    
    @RequestMapping
    public String home(ModelMap model, YukonUserContext userContext) {
        List<FdrInterfaceDisplayable> displayables = fdrTranslationManagerService.getAllInterfaceDisplayables(userContext);
        model.addAttribute("displayableInterfaces", displayables);
        
        List<FdrInterfaceType> interfaceTypes = Lists.newArrayList(FdrInterfaceType.values());
        model.addAttribute("interfaceTypes", interfaceTypes);
        
        return "fdrTranslationManager/home.jsp";
    }
    
    @RequestMapping
    public String report(ModelMap model, HttpServletResponse response, String reportInterface) throws IOException {
        List<String> formattedHeaders = Lists.newArrayList();
        fdrTranslationManagerService.addDefaultColumnsToList(formattedHeaders);
        
        List<FdrTranslation> filteredTranslationsList = fdrTranslationManagerService.getFilteredTranslationList(reportInterface);
        
        //Add all headers for each translation type to the headers list
        fdrTranslationManagerService.addHeadersFromTranslations(formattedHeaders, filteredTranslationsList);
        
        //Create data array with dimensions [# of translations + 1][headers]
        String[][] dataGrid = new String[filteredTranslationsList.size()+1][];
        for(int i = 0; i < dataGrid.length; i++) {
            dataGrid[i] = new String[formattedHeaders.size()];
        }
        
        //Insert headers row
        for(int i = 0; i < formattedHeaders.size(); i++) {
            dataGrid[0][i] = formattedHeaders.get(i);
        }
        
        //Insert translation data
        fdrTranslationManagerService.populateExportArray(dataGrid, filteredTranslationsList);
        
        //Set up CSV stream
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = ServletUtil.makeWindowsSafeFileName("FdrTranslationsReport.csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        //Write array values to csv
        for(String[] line : dataGrid) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    @RequestMapping
    public String submitImport(ModelMap model, HttpServletRequest request, FlashScope flashScope, YukonUserContext userContext, 
                               boolean ignoreInvalidColumns) throws IOException {
        
        if(!ServletFileUpload.isMultipartContent(request)) {
            //Error - not a multipart request
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile dataFile = multipartRequest.getFile("importFile");
        
        File tempFile = new File("");
        try {
            if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                //Error - no file
                MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
                flashScope.setError(errorMsg);
                return "redirect:home";
            } else if(dataFile.getInputStream().available() <= 0){
                //Error - empty file
                MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.emptyImportFile");
                flashScope.setError(errorMsg);
                return "redirect:home";
            } else {
                tempFile = WebFileUtils.convertToTempFile(dataFile, "bulkImport", "");
            }
        } catch (IOException e) {
            //Error - unable to read file
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.noImportFile");
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        FileSystemResource fileResource = new FileSystemResource(tempFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileResource.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        CSVReader csvReader = new CSVReader(bufferedReader);
        
        //1. Get headers, strip whitespace
        String[] headersArray = csvReader.readNext();
        List<String> headers = Lists.newArrayList();
        for(int i = 0; i < headersArray.length; i++) {
            String noWhitespaceHeaderToAdd = StringUtils.deleteWhitespace(headersArray[i]);
            for(String addedHeader: headers) {
                //2. look for duplicate headers
                if(headersArray[i].equalsIgnoreCase(addedHeader)) {
                    //Error - duplicate found!
                    MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.duplicateColumn", headersArray[i]);
                    flashScope.setError(errorMsg);
                    return "redirect:home";
                }
            }
            headers.add(noWhitespaceHeaderToAdd);
        }
        
        List<Integer> columnsToIgnore = Lists.newArrayList();
        
        //Check for all default headers
        String missingHeader = fdrTranslationManagerService.checkForMissingDefaultImportHeaders(headers);
        if(missingHeader != null) {
            //Error - missing default header
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.defaultHeaderMissing", missingHeader);
            flashScope.setError(errorMsg);
            return "redirect:home";
        }
        
        //3. Get list of all interfaces represented in the file
        FDRInterface[] allFdrInterfaces = com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();
        List<FDRInterface> allFdrInterfacesList = Lists.newArrayList(allFdrInterfaces);
        List<FDRInterface> interfacesInFile = Lists.newArrayList();
        for(String header : headers) {
            boolean headerMatchFound = false;
            
            for(FDRInterface fdrInterface : allFdrInterfacesList) {
                if(fdrTranslationManagerService.matchesDefaultColumn(header)) {
                    //Default column. Move on to the next header.
                    headerMatchFound = true;
                    break;
                } else if(header.startsWith(fdrInterface.toString() + "_")) {
                    if(interfacesInFile.contains(fdrInterface)) {
                        //Interface found, but already added. Move on to the next header.
                        headerMatchFound = true;
                        break;
                    } else {
                        interfacesInFile.add(fdrInterface);
                        headerMatchFound = true;
                        break;
                    }
                }
            }
            if(!headerMatchFound) {
                //Warning - had to ignore this header - doesn't match any interface and isn't a default
                if(ignoreInvalidColumns) {
                    int colIndex = headers.indexOf(header);
                    columnsToIgnore.add(colIndex);
                    log.warn("FDR Translation Import ignoring column \"" + header + "\" - not a recognized default or interface-specific column.");
                } else {
                    MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.badColumn", header);
                    flashScope.setError(errorMsg);
                    return "redirect:home";
                }
            }
        }
        
        //Check to make sure that all columns are present for each interface represented in the file
        for(FDRInterface fdrInterface : interfacesInFile) {
            String interfaceName = fdrInterface.toString();
            //a. Get list of all columns required for each interface
            List<FDRInterfaceOption> options = fdrInterface.getInterfaceOptionList();
            //b. Check headers for all columns for each interface
            for(FDRInterfaceOption option : options) {
                String formattedOption = fdrTranslationManagerService.formatOptionForColumnHeader(option.getOptionLabel(), interfaceName);
                if(!headers.contains(formattedOption)) {
                    //error - column missing for this interface
                    MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable("yukon.web.modules.amr.fdrTranslationManagement.error.missingColumn", formattedOption, interfaceName);
                    flashScope.setError(errorMsg);
                    return "redirect:home";
                }
            }
        }

        //4. Get all file lines
        List<String[]> fileLines = Lists.newArrayList();
        String[] line;
        while((line = csvReader.readNext()) != null) {
            fileLines.add(line);
        }
        
        //start import
        String resultId = fdrTranslationManagerService.startImport(headers, columnsToIgnore, fileLines, userContext);
        
        model.addAttribute("resultId", resultId);
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        model.addAttribute("fileName", dataFile.getOriginalFilename());
        
        return "redirect:importResults";
    }
    
    @RequestMapping
    public String importResults(ModelMap model, String resultId, String fileName, boolean ignoreInvalidColumns) {
        BackgroundProcessResultHolder result = recentResultsCache.getResult(resultId);
        
        model.addAttribute("resultId", resultId);
        model.addAttribute("result", result);
        model.addAttribute("ignoreInvalidColumns", ignoreInvalidColumns);
        model.addAttribute("fileName", fileName);
        
        return "fdrTranslationManager/import.jsp";
    }
    
    @RequestMapping
    public String downloadFailed(ModelMap model, HttpServletResponse response, String resultId) throws IOException {
        TranslationImportCallbackResult result = recentResultsCache.getResult(resultId);
        List<String> headers = result.getHeaders();
        List<Integer> failedRowNums = result.getFailedRowNumbers();
        List<String> log = result.getLog();
        List<String[]> importRows = result.getImportRows();
        
        headers.add("FAIL_REASON");
        
        //build 2D array with appropriate width and height
        String[][] dataGrid = new String[failedRowNums.size()+1][];
        for(int i = 0; i < dataGrid.length; i++) {
            dataGrid[i] = new String[headers.size()];
        }
        
        //add headers
        dataGrid[0] = headers.toArray(new String[headers.size()]);
        int dataGridIndex = 1;
        
        //add the rest of the rows
        for(int i = 0; i < importRows.size(); i++) {
            if(failedRowNums.contains(i)) {
                String[] row = importRows.get(i);
                
                for(int j = 0; j < row.length; j++) {
                    dataGrid[dataGridIndex][j] = row[j];
                    
                }
                dataGrid[dataGridIndex][row.length] = log.get(i);
                dataGridIndex++;
            }
        }

        //Set up CSV stream
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = ServletUtil.makeWindowsSafeFileName("FailedFdrTranslationImports.csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        //Write array values to csv
        for(String[] line : dataGrid) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    @Resource(name="recentResultsCache")
    public void setResultsCache(RecentResultsCache<TranslationImportCallbackResult> resultsCache) {
        this.recentResultsCache = resultsCache;
    }
    
    /*@Resource(name="recentResultsCache")
    public void setBackgroundProcessRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }*/
    
    @Autowired
    public void setFdrTranslationManagerService(FdrTranslationManagerService fdrTranslationManagerService) {
        this.fdrTranslationManagerService = fdrTranslationManagerService;
    }
}

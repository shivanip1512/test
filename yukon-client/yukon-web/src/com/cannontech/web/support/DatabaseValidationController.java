package com.cannontech.web.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.service.NotSupportedException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.outageProcessing.OutageMonitorEditorController;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cariboulake.db.schemacompare.app.SchemaCompare;
import com.cariboulake.db.schemacompare.app.SchemaCompareConnection;
import com.cariboulake.db.schemacompare.app.SchemaCompareParameters;
import com.cariboulake.db.schemacompare.business.domain.dbcompare.DbDifferences;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
public class DatabaseValidationController implements ResourceLoaderAware {
    private final static Logger log = YukonLogManager.getLogger(OutageMonitorEditorController.class);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private JdbcTemplate jdbcTemplate;

    private ResourceLoader resourceLoader;
    private Semaphore running = new Semaphore(1);
        
    @RequestMapping(value="/database/validate/home")
    public String home(ModelMap map, final YukonUserContext context) {
        
        // Checks to see if the validation is currently running.
        if(running.availablePermits() == 0){
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
            String msgError = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseValidate.msgError.alreadyRunning");
            map.addAttribute("msgError", msgError);
            log.error(msgError);
            return "database/validate.jsp";
        }
        
        // Checks to see if the database being checked is oracle or not.
        boolean displayOracleWarning = false;
        try{
            displayOracleWarning = isDatabaseOracle();
        }catch (MetaDataAccessException e) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
            map.addAttribute("msgError", messageSourceAccessor.getMessage("yukon.web.modules.support.databaseValidate.msgError.noAccessToMetadata"));
            log.error(e.getStackTrace());
            return "database/validate.jsp";
        }
        
        map.addAttribute("displayOracleWarning", displayOracleWarning);
        return "database/validate.jsp";
    }
    
    @RequestMapping(value = "/database/validate/results", method = RequestMethod.POST)
    public String validate(HttpServletResponse response, final YukonUserContext context) throws Exception {

        boolean isRunnable = running.tryAcquire();
        
        if (!isRunnable){
            return null;
        }
        try{
            
            Resource databaseSnapshot = getDatabaseXMLFile(context);
            File xmlCompareFile = convertToTempFile(databaseSnapshot);
            
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            
            // Builds up the compare
            SchemaCompare schemaCompare = new SchemaCompare();
            SchemaCompareConnection schCompConn1 = 
                new SchemaCompareConnection("Local Database", connection);
            SchemaCompareConnection schCompConn2 = 
                new SchemaCompareConnection(xmlCompareFile.getName(), 
                                            xmlCompareFile.getPath());
            SchemaCompareParameters schCompParams = new SchemaCompareParameters(schCompConn1, schCompConn2);
            DbDifferences schemaDifferences = schemaCompare.checkDifferences(schCompParams);
            
            // Writes the comparison information to the browser
            StringWriter stringWriter = new StringWriter();
            schemaDifferences.printDifferences(stringWriter);
            PrintWriter writer = response.getWriter();
            writer.write("<pre>");
            writer.write(StringEscapeUtils.escapeHtml4(stringWriter.toString()));
            writer.write("</pre>");
    
            return null;
        } finally{
            running.release();
        }
    }

    private File convertToTempFile(Resource resource) throws IOException{
        File tempFile = null;
        
        tempFile = File.createTempFile("compareResults", ".txt");
        tempFile.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        
        FileCopyUtils.copy(resource.getInputStream(), fileOutputStream);
        
        return tempFile;
    }

    /**
     * This method looks to see what database type the current user has, 
     * and then retrieves the corresponding database XML file.
     * 
     * @return
     * @throws NotSupportedException
     * @throws MetaDataAccessException
     */
    private Resource getDatabaseXMLFile(final YukonUserContext context) throws UnsupportedOperationException, MetaDataAccessException  {
        String databaseProductName = (String)JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), "getDatabaseProductName");
        
        if (databaseProductName.contains("SQL Server")) {
            return resourceLoader.getResource("classpath:com/cannontech/database/snapshot/mssql.xml");
        }
        if (databaseProductName.contains("Oracle")) {
            return resourceLoader.getResource("classpath:com/cannontech/database/snapshot/oracle.xml");
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        throw new UnsupportedOperationException(messageSourceAccessor.getMessage("yukon.web.modules.support.databaseValidate.msgError.valitationNotSupported"));
    }
    
    /**
     * 
     * @return
     * @throws MetaDataAccessException
     */
    private boolean isDatabaseOracle() throws MetaDataAccessException {
        String databaseProductName = (String)JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), "getDatabaseProductName");
        if (databaseProductName.contains("Oracle")) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

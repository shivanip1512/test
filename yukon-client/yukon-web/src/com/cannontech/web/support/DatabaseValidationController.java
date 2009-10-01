package com.cannontech.web.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.NotSupportedException;

import org.apache.commons.lang.StringEscapeUtils;
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

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cariboulake.db.schemacompare.app.SchemaCompare;
import com.cariboulake.db.schemacompare.app.SchemaCompareConnection;
import com.cariboulake.db.schemacompare.app.SchemaCompareParameters;
import com.cariboulake.db.schemacompare.business.domain.dbcompare.DbDifferences;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
public class DatabaseValidationController implements ResourceLoaderAware {
    
    private ResourceLoader resourceLoader;
    private JdbcTemplate jdbcTemplate;
    private Semaphore running = new Semaphore(1);
    
    @RequestMapping(value="/database/validate/home")
    public String home(ModelMap map) {
        
        // Checks to see if the validation is currently running.
        if(running.availablePermits() == 0){
            return null;
        }
        
        boolean displayOracleWarning = false;
        try{
            displayOracleWarning = isDatabaseOracle();
        }catch (MetaDataAccessException e) {
            return null;
        }
        
        map.addAttribute("displayOracleWarning", displayOracleWarning);
        return "database/validate.jsp";
    }
    
    @RequestMapping(value = "/database/validate/results", method = RequestMethod.POST)
    public String validate(HttpServletResponse response) throws Exception {

        try{
            boolean isRunnable = running.tryAcquire();
            
            if (!isRunnable){
                return null;
            }
            
            Resource databaseSnapshot = getDatabaseXMLFile();
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
            StringEscapeUtils.escapeHtml(writer, stringWriter.toString());
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
    private Resource getDatabaseXMLFile() throws NotSupportedException, MetaDataAccessException  {
        String databaseProductName = (String)JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), "getDatabaseProductName");
        
        if (databaseProductName.contains("SQL Server"))
            return resourceLoader.getResource("classpath:com/cannontech/database/snapshot/mssql.xml");
        if (databaseProductName.contains("Oracle"))
            return resourceLoader.getResource("classpath:com/cannontech/database/snapshot/oracle.xml");
        
        throw new NotSupportedException("Your current database configuration is not currently supported by our validation system.");
    }
    
    /**
     * 
     * @return
     * @throws MetaDataAccessException
     */
    private boolean isDatabaseOracle() throws MetaDataAccessException {
        String databaseProductName = (String)JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), "getDatabaseProductName");
        if (databaseProductName.contains("Oracle"))
            return true;
        return false;
    }
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

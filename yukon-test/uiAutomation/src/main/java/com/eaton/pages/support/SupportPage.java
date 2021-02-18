package com.eaton.pages.support;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.Section;
import com.eaton.elements.SimpleList;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class SupportPage extends PageBase {

        //Public
    public static final String DEFAULT_URL = Urls.SUPPORT;
    
    public static final int RANGE_LAST_WEEK_INDEX = 0;
    public static final int RANGE_LAST_TWO_WEEKS_INDEX = 1;
    public static final int RANGE_LAST_MONTH_INDEX = 2;
    public static final int RANGE_EVERYTHING_INDEX = 3;
    
    //Private
    private Section supportSection;
    private Section manualsSection;
    private Section customerSupportSection;
    private Section todaysLogsSection;
    private String todaysLogsViewAllLogsAnchorText;
    private String todaysLogsViewAllLogsLinkText;
    private Section databaseInfoSection;
    //private SimpleList databaseInfoSectionList;
    //private String databaseValidationAnchorText;
    //private String databaseValidationLinkText;
    private TextEditElement customerName;
    private DropDownElement range;
    private TrueFalseCheckboxElement commLogFiles;
    private MultiLineTextElement notes;
    private Button createBundleBtn;

        //================================================================================
    // Constructors Section
    //================================================================================
    
    public SupportPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
        
        supportSection = new Section(driverExt, "Support Pages");
        manualsSection = new Section(driverExt, "Manuals");
        customerSupportSection = new Section(driverExt, "Contact Customer Support");
        todaysLogsSection = new Section(driverExt, "Today's Logs");
        WebElement todaysLogs = getTodaysLogsSection().getSection();
        WebElement todaysLogsViewAllLogs = todaysLogs.findElement(By.cssSelector("div > a"));
        todaysLogsViewAllLogsAnchorText = todaysLogsViewAllLogs.getText();
        todaysLogsViewAllLogsLinkText = SimpleList.getLinkFromOuterHTML(todaysLogsViewAllLogs.getAttribute("outerHTML"));
        databaseInfoSection = new Section(driverExt, "Database Info");
//        databaseInfoSectionList = new SimpleList(driverExt, "simple-list", todaysLogsSection.getSection());
//        WebElement databaseInfo = databaseInfoSection.getSection();
//        WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
//        databaseValidationAnchorText = databaseValidation.getText();
//        databaseValidationLinkText = SimpleList.getLinkFromOuterHTML(databaseValidation.getAttribute("outerHTML"));
        customerName = new TextEditElement(driverExt, "customerName");
        range = new DropDownElement(driverExt, "bundleRangeSelection");
        commLogFiles = new TrueFalseCheckboxElement(driverExt, "optionalWritersToInclude");
        notes = new MultiLineTextElement(driverExt, "comments");
        createBundleBtn = new Button(driverExt, "Create Bundle");
    }
    
    //================================================================================
    // Public Methods
    //================================================================================
    
    public String getCurrentLogTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public Section getSupportSection() {
        return supportSection;
    }
    
    public SimpleList getSupportSectionPageList() {
        return new SimpleList(driverExt, "simple-list", getSupportSection().getSection());
    }
    
    public Section getManualsSection() {
        return manualsSection;
    }
    
    public SimpleList getManualsSectionPageList() {
        return new SimpleList(driverExt, "simple-list", manualsSection.getSection());
    }
    
    public Section getCustomerSupportSection() {
        return customerSupportSection;
    }
    
    public SimpleList getCustomerSupportSectionPageList() {
        return new SimpleList(driverExt, "simple-list", customerSupportSection.getSection());
    }
    
    public Section getTodaysLogsSection() {
        return todaysLogsSection;
    }
    
    public SimpleList getTodaysLogsSectionPageList() {
        return new SimpleList(driverExt, "simple-list", todaysLogsSection.getSection());
    }
    
    public String getTodaysLogsViewAllLogsAnchorText() {
        return todaysLogsViewAllLogsAnchorText;
    }
    
    public String getTodaysLogsViewAllLogsLinkText() {
        return todaysLogsViewAllLogsLinkText;
    }
    
    public Section getDatabaseInfoSection() {
        return databaseInfoSection;
    }    
    
    public String getDatabaseValidationText() {
      WebElement databaseInfo = getDatabaseInfoSection().getSection();
      
      WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("a"));
      
      SeleniumTestSetup.scrollToElement(databaseValidation);
      
      return databaseValidation.getText();
    }
    
    public String getDatabaseValidationLink() {
        WebElement databaseInfo = getDatabaseInfoSection().getSection();
        WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("a"));
        return databaseValidation.getAttribute("href");
    }
    
    public String getViewAllLogsText() {
        WebElement databaseInfo = getTodaysLogsSection().getSection();
        WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
        return databaseValidation.getText();
      }
      
      public String getViewAllLogsLink() {
          WebElement databaseInfo = getTodaysLogsSection().getSection();
          WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
          return databaseValidation.getAttribute("href");
      }
    
    public TextEditElement getCustomerName() {
        return customerName;
    }
    
    public DropDownElement getRange() {
        return range;
    }
    
    public TrueFalseCheckboxElement getCommLogFiles() {
        return commLogFiles;
    }
    
    public MultiLineTextElement getNotes() {
        return notes;
    }
    
    public Button getCreateBundleBtn() {
        return createBundleBtn;
    }
}
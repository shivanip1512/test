package com.eaton.pages.support;

import com.eaton.framework.DriverExtensions;
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
    private SimpleList supportSectionList;
    private Section manualsSection;
    private SimpleList manualsSectionList;
    private Section customerSupportSection;
    private SimpleList customerSupportSectionList;
    private Section todaysLogsSection;
    private SimpleList todaysLogsSectionList;
    private String todaysLogsViewAllLogsAnchorText;
    private String todaysLogsViewAllLogsLinkText;
    private Section databaseInfoSection;
    private SimpleList databaseInfoSectionList;
    private String databaseValidationAnchorText;
    private String databaseValidationLinkText;
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
        supportSectionList = new SimpleList(driverExt, "simple-list", supportSection.getSection());
        manualsSection = new Section(driverExt, "Manuals");
        manualsSectionList = new SimpleList(driverExt, "simple-list", manualsSection.getSection());
        customerSupportSection = new Section(driverExt, "Contact Customer Support");
        customerSupportSectionList = new SimpleList(driverExt, "simple-list", customerSupportSection.getSection());
        todaysLogsSection = new Section(driverExt, "Today's Logs");
        todaysLogsSectionList = new SimpleList(driverExt, "simple-list", todaysLogsSection.getSection());
        WebElement todaysLogs = getTodaysLogsSection().getSection();
    	WebElement todaysLogsViewAllLogs = todaysLogs.findElement(By.cssSelector("div > a"));
    	todaysLogsViewAllLogsAnchorText = todaysLogsViewAllLogs.getText();
    	todaysLogsViewAllLogsLinkText = SimpleList.getLinkFromOuterHTML(todaysLogsViewAllLogs.getAttribute("outerHTML"));
    	databaseInfoSection = new Section(driverExt, "Database Info");
    	databaseInfoSectionList = new SimpleList(driverExt, "simple-list", todaysLogsSection.getSection());
    	WebElement databaseInfo = databaseInfoSection.getSection();
    	WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
    	databaseValidationAnchorText = databaseValidation.getText();
    	databaseValidationLinkText = SimpleList.getLinkFromOuterHTML(databaseValidation.getAttribute("outerHTML"));
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
    
    public SimpleList getSupportSectionSimpleList() {
    	return supportSectionList;
    }
    
    public Section getManualsSection() {
    	return manualsSection;
    }
    
    public SimpleList getManualsSectionSimpleList() {
    	return manualsSectionList;
    }
    
    public Section getCustomerSupportSection() {
    	return customerSupportSection;
    }
    
    public SimpleList getCustomerSupportSectionSimpleList() {
    	return customerSupportSectionList;
    }
    
    public Section getTodaysLogsSection() {
    	return todaysLogsSection;
    }
    
    public SimpleList getTodaysLogsSectionSimpleList() {
    	return todaysLogsSectionList;
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
    
    public SimpleList getDatabaseInfoSectionSimpleList() {
    	return databaseInfoSectionList;
    }
    
    public String getDatabaseValidationAnchorText() {
    	return databaseValidationAnchorText;
    }
    
    public String getDatabaseValidationLinkText() {
    	return databaseValidationLinkText;
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

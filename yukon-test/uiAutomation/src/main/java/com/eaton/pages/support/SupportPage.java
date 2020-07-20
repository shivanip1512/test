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
import com.eaton.elements.EditElement;
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
    private TextEditElement notes;
    private Button createBundleBtn;

	//================================================================================
    // Constructors Section
    //================================================================================
    
    public SupportPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
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
    	if(supportSection == null) {
    		supportSection = new Section(driverExt, "Support Pages");
    	}
    	return supportSection;
    }
    
    public SimpleList getSupportSectionSimpleList() {
    	if(supportSectionList == null) {
    		supportSectionList = new SimpleList(driverExt, "simple-list", getSupportSection().getSection());
    	}
    	return supportSectionList;
    }
    
    public Section getManualsSection() {
    	if(manualsSection == null) {
    		manualsSection = new Section(driverExt, "Manuals");
    	}
    	return manualsSection;
    }
    
    public SimpleList getManualsSectionSimpleList() {
    	if(manualsSectionList == null) {
    		manualsSectionList = new SimpleList(driverExt, "simple-list", getManualsSection().getSection());
    	}
    	return manualsSectionList;
    }
    
    public Section getCustomerSupportSection() {
    	if(customerSupportSection == null) {
    		customerSupportSection = new Section(driverExt, "Contact Customer Support");
    	}
    	return customerSupportSection;
    }
    
    public SimpleList getCustomerSupportSectionSimpleList() {
    	if(customerSupportSectionList == null) {
    		customerSupportSectionList = new SimpleList(driverExt, "simple-list", getCustomerSupportSection().getSection());
    	}
    	return customerSupportSectionList;
    }
    
    public Section getTodaysLogsSection() {
    	if(todaysLogsSection == null) {
    		todaysLogsSection = new Section(driverExt, "Today's Logs");
    	}
    	return todaysLogsSection;
    }
    
    public SimpleList getTodaysLogsSectionSimpleList() {
    	if(todaysLogsSectionList == null) {
    		todaysLogsSectionList = new SimpleList(driverExt, "simple-list", getTodaysLogsSection().getSection());
    	}
    	return todaysLogsSectionList;
    }
    
    public String getTodaysLogsViewAllLogsAnchorText() {
    	if(todaysLogsViewAllLogsAnchorText == null) {
	    	WebElement todaysLogs = getTodaysLogsSection().getSection();
	    	WebElement todaysLogsViewAllLogs = todaysLogs.findElement(By.cssSelector("div > a"));
	    	todaysLogsViewAllLogsAnchorText = todaysLogsViewAllLogs.getText();
    	}
    	return todaysLogsViewAllLogsAnchorText;
    }
    
    public String getTodaysLogsViewAllLogsLinkText() {
    	if(todaysLogsViewAllLogsLinkText == null) {
	    	WebElement todaysLogs = getTodaysLogsSection().getSection();
	    	WebElement todaysLogsViewAllLogs = todaysLogs.findElement(By.cssSelector("div > a"));
	    	todaysLogsViewAllLogsLinkText = SimpleList.getLinkFromOuterHTML(todaysLogsViewAllLogs.getAttribute("outerHTML"));
    	}
    	return todaysLogsViewAllLogsLinkText;
    }
    
    public Section getDatabaseInfoSection() {
    	if(databaseInfoSection == null) {
    		databaseInfoSection = new Section(driverExt, "Database Info");
    	}
    	return databaseInfoSection;
    }
    
    public SimpleList getDatabaseInfoSectionSimpleList() {
    	if(databaseInfoSectionList == null) {
    		databaseInfoSectionList = new SimpleList(driverExt, "simple-list", getTodaysLogsSection().getSection());
    	}
    	return databaseInfoSectionList;
    }
    
    public String getDatabaseValidationAnchorText() {
    	if(databaseValidationAnchorText == null) {
	    	WebElement databaseInfo = getDatabaseInfoSection().getSection();
	    	WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
	    	databaseValidationAnchorText = databaseValidation.getText();
    	}
    	return databaseValidationAnchorText;
    }
    
    public String getDatabaseValidationLinkText() {
    	if(databaseValidationLinkText == null) {
	    	WebElement databaseInfo = getDatabaseInfoSection().getSection();
	    	WebElement databaseValidation = databaseInfo.findElement(By.cssSelector("div > a"));
	    	databaseValidationLinkText = SimpleList.getLinkFromOuterHTML(databaseValidation.getAttribute("outerHTML"));
    	}
    	return databaseValidationLinkText;
    }
    
    public TextEditElement getCustomerName() {
    	if(customerName == null) {
    		customerName = new TextEditElement(driverExt, "customerName");
    	}
        return customerName;
    }
    
    public DropDownElement getRange() {
    	if(range == null) {
    		range = new DropDownElement(driverExt, "bundleRangeSelection");
    	}
        return range;
    }
    
    public TrueFalseCheckboxElement getCommLogFiles() {
    	if(commLogFiles == null) {
    		commLogFiles = new TrueFalseCheckboxElement(driverExt, "optionalWritersToInclude");
    	}
        return commLogFiles;
    }
    
    public TextEditElement getNotes() {
    	if(notes == null) {
    		notes = new TextEditElement(driverExt, "comments", null, EditElement.ELEMENT_TYPE_TEXTAREA);
    	}
        return notes;
    }
    
    public Button getCreateBundleBtn() {
    	if(createBundleBtn == null) {
    		createBundleBtn = new Button(driverExt, "Create Bundle");
    	}
        return createBundleBtn;
    }
}

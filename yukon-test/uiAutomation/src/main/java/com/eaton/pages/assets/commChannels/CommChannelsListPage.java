package com.eaton.pages.assets.commChannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.eaton.elements.Button;
import com.eaton.elements.Link;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;

public class CommChannelsListPage extends PageBase {
    
    private WebTable table;
    
    
    public CommChannelsListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNELS_LIST;
        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
    
    public Boolean getLinkVisibility(String channelName) {
        Link link = new Link(driverExt, channelName);
        return link.isDisplayed();
    }
    public void actionsButtonClick() {
    	List<WebElement> buttons = driverExt.findElements(By.cssSelector(".button"),Optional.empty());
    	buttons.get(3).click(); 	
    }
    
    public ConfirmModal showCreateCommChannelModal() {     
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-create-comm-channel-popup");    
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));        
    }
     
    public void createButtonClick() {
    	List<WebElement> buttons = driverExt.findElements(By.cssSelector("a.clearfix"),Optional.empty());
    	buttons.get(0).click();
}
}
package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;


public class CommChannelCreateCommonTests extends SeleniumTestSetup {
	
	 	private CommChannelsListPage listPage;
	    private DriverExtensions driverExt;
	    private String commChannelName;
	    private JSONObject jo;

	    @BeforeClass(alwaysRun = true)
	    public void beforeClass() {
	        driverExt = getDriverExt();
	        new SoftAssertions();

	        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
	        commChannelName = "Unique Name " + timeStamp;

	        // Creating one UDP port comm channel using hard coded json file.
	        String payloadFile = System.getProperty("user.dir")
	                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

	        Object body = JsonFileHelper.parseJSONFile(payloadFile);
	        jo = (JSONObject) body;
	        jo.put("name", commChannelName);
	        AssetsCreateRequestAPI.createCommChannel(body);
	    }

	    @BeforeMethod(alwaysRun = true)
	    public void beforeMethod() {
	        navigate(Urls.Assets.COMM_CHANNELS_LIST);
	        listPage = new CommChannelsListPage(driverExt);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
	    public void createCommChannel_NameRequiredValidation() {
	    	CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
	    	
	    	waitForLoadingSpinner();
	    	
	    	final String EXPECTED_MSG = "Name is required.";
	    	
	    	createModal.clickOkAndWait();
	    	
	    	String errorMsg = createModal.getName().getValidationError();
	        
	        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
	    public void createCommChannel_NameInvalidCharValidation() {
	    	CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
	    	
	    	waitForLoadingSpinner();
	    	
	    	final String name = "Comm Channel / \\ , ' ";
	    	
	    	final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
	    	
	    	createModal.getName().setInputValue(name);
	    	
	    	createModal.clickOkAndWait();
	    
	    	String errorMsg = createModal.getName().getValidationError();
	        
	        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
	    public void createCommChannel_UniqueNameValidation() {
	    	CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
	    	
	    	waitForLoadingSpinner();
	    	
	    	final String EXPECTED_MSG = "Name already exists";
	    	
	    	createModal.getName().setInputValue(commChannelName);
	    	
	    	createModal.clickOkAndWait();
	    
	    	String errorMsg = createModal.getName().getValidationError();
	        
	        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
	    public void createCommChannel_CancelNavigatesToCorrectUrl() {
	    	CreateCommChannelModal createModal = listPage.showAndWaitCreateCommChannelModal();
	    	
	    	waitForLoadingSpinner();
	    	
	    	createModal.clickCancelAndWait();
	    	
	    	String EXPECTED_TITLE = "Comm Channels";
	        String actualPageTitle = listPage.getPageTitle();
	        
	        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
	    }
}

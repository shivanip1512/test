package com.eaton.tests.assets.commchannels;	
	import static org.assertj.core.api.Assertions.assertThat;

	import java.text.SimpleDateFormat;
	import org.assertj.core.api.SoftAssertions;
	import org.json.simple.JSONObject;
	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.BeforeMethod;
	import org.testng.annotations.Test;
	import com.eaton.elements.modals.EditCommChannelModal;
	import com.eaton.framework.DriverExtensions;
	import com.eaton.framework.SeleniumTestSetup;
	import com.eaton.framework.TestConstants;
	import com.eaton.framework.Urls;
	import com.eaton.pages.assets.commChannels.CommChannelDetailPage;
	import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
	import com.eaton.rest.api.dbetoweb.JsonFileHelper;
	import io.restassured.response.ExtractableResponse;
	
	public class CommChannelTcpEditTests extends SeleniumTestSetup {
	
		private CommChannelDetailPage channelDetailPage;
	    private DriverExtensions driverExt;
	    private SoftAssertions softly;
	    private String commChannelId;
	    private String commChannelName;
	    private JSONObject jo;
	
	    @BeforeClass(alwaysRun = true)
	    public void beforeClass() {
	        driverExt = getDriverExt();
	        softly = new SoftAssertions();
	        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
	        commChannelName = "TCP Comm Channel " + timeStamp;

	        // Creating one UDP port comm channel using hard coded json file.
	        String payloadFile = System.getProperty("user.dir")
	                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

	        Object body = JsonFileHelper.parseJSONFile(payloadFile);
	        jo = (JSONObject) body;
	        jo.put("name", commChannelName);
	        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
	        commChannelId = createResponse.path("id").toString();
	    }
	  
	    @BeforeMethod(alwaysRun = true)
	    public void beforeMethod() {
	        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
	        channelDetailPage = new CommChannelDetailPage(driverExt);
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL },priority=0)										
	    public void commChannelTcpEdit_NameRequired() {										
	        String expectedModalTitle = "Edit " + commChannelName;	
	        String EXPECTED_MSG = "Name is required.";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);										
									
	        editModal.getChannelName().setInputValue(" ");										
	        editModal.clickOkAndWait();										
	        									
	        String userMsg = editModal.getUserMessage();										
	        assertThat(userMsg).isEqualTo(EXPECTED_MSG);										
	    }										
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=1)											
	    public void commChannelDetailsTcp_NameInvalidChars() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
										
	        editModal.getChannelName().setInputValue("/,tcp|$%Channel&!");											
	        editModal.clickOkAndWait();											
	        											
	        String userMsg = editModal.getUserMessage();											
	        assertThat(userMsg).isEqualTo(EXPECTED_MSG);											
	    }	
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=-2)											
	    public void commChannelTcpEdit_CancelNavigatesCorrectly() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_TITLE = commChannelName;
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
									
	        editModal.clickCancelAndWait();											
	        											
	        String actualPageTitle = channelDetailPage.getPageTitle();											
	        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);											
	    }	
	    											
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=-1)											
	    public void commChannelDetailsTcp_NameAlreadyExists() {											
	    	String EXPECTED_MSG = "Name already exists";									
	        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());											
	        String commChannelNameTcp = "TCP Comm Channel " + timeStamp;											
												
	        String payloadFile = System.getProperty("user.dir")											
	                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";											
												
	        Object body = JsonFileHelper.parseJSONFile(payloadFile);											
	        jo = (JSONObject) body;											
	        jo.put("name", commChannelNameTcp);											
	        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);											
	        commChannelId = createResponse.path("id");											
	        											
	        String expectedModalTitle = "Edit " + commChannelName;											
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
										
	        editModal.getChannelName().setInputValue(commChannelNameTcp);											
	        editModal.clickOkAndWait();											
	        											
	        String userMsg = editModal.getUserMessage();											
	        assertThat(userMsg).isEqualTo(EXPECTED_MSG);											
	    }																		
	    
	}

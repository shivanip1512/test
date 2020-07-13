package com.eaton.tests.assets.commchannels;	
	import static org.assertj.core.api.Assertions.assertThat;

	import java.text.SimpleDateFormat;
import java.util.List;

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
	    
	    		
/*	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=-2)											
	    public void commChannelTcpEdit_TabLabelsCorrectly() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_TITLE = commChannelName;
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
			editModal.getTabElement().clickTab("Configuration");
			List<String> labels = editModal.getTabElement().getTabLabels("Configuration");
			System.out.println(labels);
			System.out.println("dadad");
										
	    }*/
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=-2)											
	    public void commChannelTcpEdit_ConfigurationLabelsCorrect() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);
	        
	        String tabName = "Configuration";
			editModal.getTabElement().clickTab(tabName);
			
			List<String> configTabValues = editModal.getTabElement().getTabValues(tabName);
			editModal.getChannelPreTx().setInputValue("ds56");
			List<String> labels = editModal.getTabElement().getTabLabels("Configuration");
	        softly.assertThat(labels.size()).isEqualTo(5);
	        softly.assertThat(labels.get(0)).isEqualTo("Pre Tx Wait:");
	        softly.assertThat(labels.get(1)).isEqualTo("RTS To Tx Wait:");
	        softly.assertThat(labels.get(2)).isEqualTo("Post Tx Wait:");
	        softly.assertThat(labels.get(3)).isEqualTo("Receive Data Wait:");
	        softly.assertThat(labels.get(4)).isEqualTo("Additional Time Out:");
	        softly.assertAll();							
	    }
		
			
}

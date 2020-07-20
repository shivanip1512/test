package com.eaton.tests.assets.commchannels;

import com.eaton.framework.SeleniumTestSetup;
import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.EditCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
//import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelUdpEditTests extends SeleniumTestSetup {
	
	private CommChannelDetailPage channelDetailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private String commChannelId="44209";
    private String commChannelName;
    private JSONObject jo;
	
	@BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "UDP Comm Channel " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";

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
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_ModalTitleCorrect() {	
    	
        String expectedModalTitle = "Edit " + commChannelName;										
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);										
        String actualModalTitle = editModal.getModalTitle();										
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);     
    }	
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })												
    public void commChannelUdpEdit_NameRequired() {												
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
										
        editModal.getChannelName().setInputValue(" ");												
        editModal.clickOkAndWait();												
        																								
        assertThat(editModal.getChannelName().getValidationError()).isEqualTo(EXPECTED_MSG);												
    }	
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })												
    public void commChannelUdpEdit_NameInvalidChars() {												
        String expectedModalTitle = "Edit " + commChannelName;	
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
										
        editModal.getChannelName().setInputValue("/,udp|");												
        editModal.clickOkAndWait();												
											
        assertThat(editModal.getChannelName().getValidationError()).isEqualTo(EXPECTED_MSG);												
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })												
    public void commChannelUdpEdit_PortNumberMinValidation() {												
        String expectedModalTitle = "Edit " + commChannelName;	
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "0";
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
				
        editModal.getPortNumber().setInputValue(portNumber);											
        editModal.clickOkAndWait();												
											
        assertThat(editModal.getPortNumber().getValidationError()).isEqualTo(EXPECTED_MSG);												
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })												
    public void commChannelUdpEdit_PortNumberMaxValidation() {												
        String expectedModalTitle = "Edit " + commChannelName;	
        String EXPECTED_MSG = "Port Number must be between 1 and 65,535.";
        String portNumber = "65536";
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
				
        editModal.getPortNumber().setInputValue(portNumber);											
        editModal.clickOkAndWait();												
											
        assertThat(editModal.getPortNumber().getValidationError()).isEqualTo(EXPECTED_MSG);												
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_PreTxWaitMinValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelPreTxWait().setInputValue("-1");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_PreTxWaitMaxValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelPreTxWait().setInputValue("10000001");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_RTSTxWaitMinValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelRTSTxWait().setInputValue("-1");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelRTSTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })									
    public void commChannelUdpEdit_RTSTxWaitMaxValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelRTSTxWait().setInputValue("10000001");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelRTSTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }   
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_PostTxWaitMinValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelPostTxWait().setInputValue("-1");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_PostTxWaitMaxValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelPostTxWait().setInputValue("10000001");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_RecDataWaitMinValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelRecDataWait().setInputValue("-1");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelRecDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_RecDataWaitMaxValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelRecDataWait().setInputValue("1001");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelRecDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_AdditionalTimeOutMinValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelAdditionalTimeOut().setInputValue("-1");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })											
    public void commChannelUdpEdit_AdditionalTimeOutMaxValueValidation() {											
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";
        
        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

		editModal.getTabElement().clickTab(tabName);
		editModal.getChannelAdditionalTimeOut().setInputValue("1000");
		editModal.clickOkAndWait();
		
		assertThat(editModal.getChannelAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);					
    }
}

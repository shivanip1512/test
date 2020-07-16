package com.eaton.tests.assets.commchannels;	
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
	import com.eaton.rest.api.assets.AssetsGetRequestAPI;
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
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })												
	    public void commChannelTcpEdit_ModalTitleCorrect() {	
	    	
	        String expectedModalTitle = "Edit " + commChannelName;										
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);										
	        String actualModalTitle = editModal.getModalTitle();										
	        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
	        												
	    }	
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })												
	    public void commChannelTcpEdit_NameRequired() {												
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Name is required.";
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
											
	        editModal.getChannelName().setInputValue(" ");												
	        editModal.clickOkAndWait();												
	        																								
	        assertThat(editModal.getChannelName().getValidationError()).isEqualTo(EXPECTED_MSG);												
	    }	
	    		
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })												
	    public void commChannelTcpEdit_NameInvalidChars() {												
	        String expectedModalTitle = "Edit " + commChannelName;	
	        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
											
	        editModal.getChannelName().setInputValue("/,tcp|");												
	        editModal.clickOkAndWait();												
												
	        assertThat(editModal.getChannelName().getValidationError()).isEqualTo(EXPECTED_MSG);												
	    }	
	    
		
		@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })												
		public void commChannelTcpEdit_NameAlreadyExists() {												
				
			String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());												
			String commChannelNameTcp = "TCP Comm Channel " + timeStamp;												
			String EXPECTED_MSG = "Name already exists";												
			String payloadFile = System.getProperty("user.dir")												
			+ "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";												
				
			Object body = JsonFileHelper.parseJSONFile(payloadFile);												
			jo = (JSONObject) body;												
			jo.put("name", commChannelNameTcp);												
			ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);												
																	
			String expectedModalTitle = "Edit " + commChannelName;												
			EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);												
											
			editModal.getChannelName().setInputValue(commChannelNameTcp);												
			editModal.clickOkAndWait();												
																							
			assertThat(editModal.getChannelName().getValidationError()).isEqualTo(EXPECTED_MSG);												
		}	

	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })												
	    public void commChannelTcpEdit_CancelNavigatesCorrectly() {												
	        String expectedModalTitle = "Edit " + commChannelName;	
	        String EXPECTED_TITLE = commChannelName;
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);																							
	        editModal.clickCancelAndWait();												
	        												
	        String actualPageTitle = channelDetailPage.getPageTitle();												
	        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);												
	    }
		
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_CreateOpensPopupCorrect() {											
	        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";												
	        CreateCommChannelModal createModel = channelDetailPage.showCreateCommChannelModal(EXPECTED_CREATE_MODEL_TITLE);												
	        String actualCreateModelTitle = createModel.getModalTitle();
	        
	        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);												
	}
	    
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_TabLabelsCorrectly() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											

	        List<String> titles = editModal.getTabElement().getTitles();

	        softly.assertThat(titles.size()).isEqualTo(2);
	        softly.assertThat(titles.get(0)).isEqualTo("Info");
	        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
	        softly.assertAll();									
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_InfoTabLabelsCorrect() {											
	        String expectedModalTitle = "Edit " + commChannelName;											
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
	        											
	        String tabName = "Info";											
			editModal.getTabElement().clickTab(tabName);									
												
			List<String> labels = editModal.getTabElement().getTabLabels(tabName);									
	        softly.assertThat(labels.size()).isEqualTo(4);
	        softly.assertThat(labels.get(0)).isEqualTo("Name:");
	        softly.assertThat(labels.get(1)).contains("Type:");
	        softly.assertThat(labels.get(2)).contains("Baud Rate:");
	        softly.assertThat(labels.get(3)).contains("Status:");
	        softly.assertAll();										
	    }

	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_ConfigurationLabelsCorrect() {											
	        String expectedModalTitle = "Edit " + commChannelName;											
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
	        											
	        String tabName = "Configuration";											
			editModal.getTabElement().clickTab(tabName);									
												
			List<String> labels = editModal.getTabElement().getTabLabels("Configuration");									
	        softly.assertThat(labels.size()).isEqualTo(5);											
	        softly.assertThat(labels.get(0)).isEqualTo("Pre Tx Wait:");											
	        softly.assertThat(labels.get(1)).isEqualTo("RTS To Tx Wait:");											
	        softly.assertThat(labels.get(2)).isEqualTo("Post Tx Wait:");											
	        softly.assertThat(labels.get(3)).isEqualTo("Receive Data Wait:");											
	        softly.assertThat(labels.get(4)).isEqualTo("Additional Time Out:");											
	        softly.assertAll();											
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_ConfigurationsValuesCorrect() {											
	        String expectedModalTitle = "Edit " + commChannelName;											
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);											
	        											
	        String tabName = "Configuration";											
			editModal.getTabElement().clickTab(tabName);	
			editModal.getTabElement().clickTab(tabName);

	        List<String> values = editModal.getTabElement().getTabValues(tabName);

	        softly.assertThat(values.size()).isEqualTo(5);
	        softly.assertThat(values.get(0)).isEqualTo("25  ms");
	        softly.assertThat(values.get(1)).isEqualTo("0  ms");
	        softly.assertThat(values.get(2)).isEqualTo("10  ms");
	        softly.assertThat(values.get(3)).isEqualTo("0  ms");
	        softly.assertThat(values.get(4)).isEqualTo("0  sec");
	        softly.assertAll();
	    }
		
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_PreTxWaitMinValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelPreTxWait().setInputValue("-1");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_PreTxWaitMaxValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelPreTxWait().setInputValue("10000001");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
			
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_RTSTxWaitMinValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelRTSTxWait().setInputValue("-1");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelRTSTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })									
	    public void commChannelTcpEdit_RTSTxWaitMaxValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelRTSTxWait().setInputValue("10000001");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelRTSTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
			
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_PostTxWaitMinValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelPostTxWait().setInputValue("-1");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_PostTxWaitMaxValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelPostTxWait().setInputValue("10000001");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_RecDataWaitMinValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelRecDataWait().setInputValue("-1");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelRecDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_RecDataWaitMaxValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelRecDataWait().setInputValue("1001");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelRecDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_AdditionalTimeOutMinValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelAdditionalTimeOut().setInputValue("-1");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_AdditionalTimeOutMaxValueValidation() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
	        String tabName = "Configuration";
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);
			editModal.getChannelAdditionalTimeOut().setInputValue("1000");
			editModal.clickOkAndWait();
			
			assertThat(editModal.getChannelAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);					
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_InfoFieldsValuesCorrect() {											
	        String expectedModalTitle = "Edit " + commChannelName;
	        String tabName = "Info";
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);

			editModal.getTabElement().clickTab(tabName);									
			
			List<String> values = editModal.getTabElement().getTabValues(tabName);	
			
	        softly.assertThat(values.size()).isEqualTo(4);
	        softly.assertThat(values).contains(commChannelName);
	        softly.assertThat(values).contains("TCP");
	        softly.assertThat(values).contains("1200");
	        softly.assertThat(values).contains("Enabled");
	        softly.assertAll();
	    }
	    
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
	    public void commChannelTcpEdit_ConfigTabTimingSectionDisplayed() {
	    	String expectedModalTitle = "Edit " + commChannelName;
	    	String tabName = "Configuration";
	    	
	    	EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);
	    	editModal.getTabElement().clickTab(tabName);
	    	
	        Section timing = editModal.getTimingSection();        		
	        assertThat(timing.getSection()).isNotNull();
	    }
	        
	    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })											
	    public void commChannelTcpEdit_UpdatedFieldsCorrect() {	
			
	        String expectedModalTitle = "Edit " + commChannelName;
	        String commChannelName = "CommChannel_Tcp_Update";
	        String baudRate = "4800";
	        String configFieldsValues[] = {"55", "10", "20", "15", "500"};
	        String tabName = "Configuration";	
	        
	        EditCommChannelModal editModal = channelDetailPage.showCommChannelEditModal(expectedModalTitle);
	        editModal.getChannelName().setInputValue(commChannelName);
	        editModal.getBaudRate().selectItemByText(baudRate);
	        
	        
	        editModal.getTabElement().clickTab(tabName);
	        editModal.getChannelPreTxWait().setInputValue(configFieldsValues[0]);
	        editModal.getChannelRTSTxWait().setInputValue(configFieldsValues[1]);
	        editModal.getChannelPostTxWait().setInputValue(configFieldsValues[2]);
	        editModal.getChannelRecDataWait().setInputValue(configFieldsValues[3]);
	        editModal.getChannelAdditionalTimeOut().setInputValue(configFieldsValues[4]);
	        editModal.clickOkAndWait();

	        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(commChannelId);
	        softly.assertThat(response.path("name").toString()).isEqualTo(commChannelName);
	        
	        softly.assertThat(response.path("baudRate").toString()).isEqualTo(baudRate);
	        softly.assertThat(response.path("timing.preTxWait").toString()).isEqualTo((configFieldsValues[0]));
	        softly.assertThat(response.path("timing.rtsToTxWait").toString()).isEqualTo((configFieldsValues[1]));
	        softly.assertThat(response.path("timing.postTxWait").toString()).isEqualTo((configFieldsValues[2]));
	        softly.assertThat(response.path("timing.receiveDataWait").toString()).isEqualTo((configFieldsValues[3]));
	        softly.assertThat(response.path("timing.extraTimeOut").toString()).isEqualTo((configFieldsValues[4]));
	        softly.assertAll();	
	        
	    }
	    

}	  
	
	
	
	    
	    
	    
	    
	    
	    
	    
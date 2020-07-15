package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelDetailPage;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;

public class CommChannelTerminalServerCreateTests extends SeleniumTestSetup{
	private CommChannelsListPage channelCreatePage;
	private DriverExtensions driverExt;
	private SoftAssertions softly;
	private Random randomNum;
	String type = "Terminal Server";
	
	@BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
    }
	
	@BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        channelCreatePage = new CommChannelsListPage(driverExt);
    }

	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
	public void createCommChannelTerminalServer_LabelsCorrect() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
		
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT Comm Channel Terminal Server " + timeStamp;
        
        String ipAddress = "127.0.0.1";
        
        String portNumber;
        randomNum = getRandomNum();
        portNumber = Integer.toString(randomNum.nextInt(65536));
        
        String baudRate = "14400";
       
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createModal.getName().setInputValue(name);
		createModal.getType().selectItemByText(type);
		createModal.getIPAddress().setInputValue(ipAddress);
		createModal.getPortNumber().setInputValue(portNumber);
		createModal.getBaudRate().selectItemByText(baudRate);
		
		createModal.clickOkAndWait();
		
		waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})	
	public void createCommChannelTerminalServer_AllFieldsSuccess() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
	
		createModal.getType().selectItemByText(type);
		
		List<String> labels = createModal.getFieldLabels();
		
		softly.assertThat(labels.size()).isEqualTo(6);
	    softly.assertThat(labels.get(0)).isEqualTo("Name:");
		softly.assertThat(labels.get(1)).contains("Type:");
		softly.assertThat(labels.get(2)).contains("IP Address:");
		softly.assertThat(labels.get(3).contains("Port Number:"));
		softly.assertThat(labels.get(4)).contains("Baud Rate:");
		softly.assertThat(labels.get(5)).contains("Status:");
		softly.assertAll();
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})			
	public void createCommChannelTerminalServer_IpAddressRequiredValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
		
		final String EXPECTED_MSG ="IP Address is required.";

		createModal.getType().selectItemByText(type);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getIPAddress().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG); 
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})		
	public void createCommChannelTerminalServer_IpAddressInvalidValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
		
        String ipAddress = "#123";
        
        final String EXPECTED_MSG ="Invalid IP/Host Name.";
        
		createModal.getType().selectItemByText(type);
		createModal.getIPAddress().setInputValue(ipAddress);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getIPAddress().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG); 
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})		
	public void createCommChannelTerminalServer_PortNumberMinValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
	
        String portNumber = "0";
        
        final String EXPECTED_MSG ="Port Number must be between 1 and 65,535.";
        
		createModal.getType().selectItemByText(type);
		createModal.getPortNumber().setInputValue(portNumber);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getPortNumber().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);               
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})			
	public void createCommChannelTerminalServer_PortNumberMaxValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
		
        String portNumber = "65536";
        
        final String EXPECTED_MSG ="Port Number must be between 1 and 65,535.";
        
		createModal.getType().selectItemByText(type);
		createModal.getPortNumber().setInputValue(portNumber);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getPortNumber().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG); 
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})			
	public void createCommChannelTerminalServer_PortNumberEmptyValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
        
        final String EXPECTED_MSG ="Port Number must be between 1 and 65,535.";

		createModal.getType().selectItemByText(type);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getPortNumber().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG); 
	}
}

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

public class CommChannelUdpCreateTests extends SeleniumTestSetup {
	private CommChannelsListPage channelCreatePage;
	private DriverExtensions driverExt;
	private SoftAssertions softly;
	private Random randomNum;
    String modalTitle = "Create Comm Channel";
	String type = "UDP";
	
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
	public void createCommChannel_UdpAllFieldsSuccess() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
		
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT Comm Channel UDP " + timeStamp;
        
        String baudRate = "4800";
        
        String portNumber;
        randomNum = getRandomNum();
        portNumber = Integer.toString(randomNum.nextInt(65536));
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createModal.getName().setInputValue(name);
		createModal.getType().selectItemByText(type);
		createModal.getPortNumber().setInputValue(portNumber);
		createModal.getBaudRate().selectItemByText(baudRate);
		
		createModal.clickOkAndWait();
		
		waitForUrlToLoad(Urls.Assets.COMM_CHANNEL_DETAIL, Optional.of(10));

        CommChannelDetailPage detailPage = new CommChannelDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})	
	public void createCommChannelUdp_LabelsCorrect() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
	
		createModal.getType().selectItemByText(type);
		
		List<String> labels = createModal.getFieldLabels();
		
		softly.assertThat(labels.size()).isEqualTo(5);
	    softly.assertThat(labels.get(0)).isEqualTo("Name:");
		softly.assertThat(labels.get(1)).contains("Type:");
		softly.assertThat(labels.get(2)).contains("Port Number:");
		softly.assertThat(labels.get(3)).contains("Baud Rate:");
		softly.assertThat(labels.get(4)).contains("Status:");
		softly.assertAll();
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL})		
	public void createCommChannelUdp_PortNumberMinValidation() {
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
	public void createCommChannelUdp_PortNumberMaxValidation() {
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
	public void createCommChannelUdp_PortNumberEmptyValidation() {
		CreateCommChannelModal createModal = channelCreatePage.showAndWaitCreateCommChannelModal();
        
        final String EXPECTED_MSG ="Port Number must be between 1 and 65,535.";

		createModal.getType().selectItemByText(type);
		  
		createModal.clickOkAndWait();
		
        String errorMsg = createModal.getPortNumber().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG); 
	}
}

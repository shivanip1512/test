package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.elements.WebTableColumn;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.SortWebColumn;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelsListTests extends SeleniumTestSetup{
	
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private WebTableColumn webTableColumn;

   	@BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        webTableColumn = new WebTableColumn(driverExt);
        listPage = new CommChannelsListPage(driverExt);
            
        String [] tcpChannel = {"123","TCP_port","1@tcp"};
    	String [] udpChannel = {"channeludp","UDPport","2$udp"};
    	int [] udpPortNumber = {23469, 34566, 34565};
    	
    	for (int i=0;i<tcpChannel.length;i++) {
        String payloadFile = System.getProperty("user.dir")	
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";	    	
        Object bodyTcp = JsonFileHelper.parseJSONFile(payloadFile);	
        JSONObject joTcp = (JSONObject) bodyTcp;       
        String tcpChannelName = tcpChannel[i];	
        joTcp.put("name", tcpChannelName);    	
        ExtractableResponse<?> createResponseTcp = AssetsCreateRequestAPI.createCommChannel(bodyTcp);           
    	}   	
    	for (int j=0;j<udpChannel.length;j++) {
            String payloadFileUdp = System.getProperty("user.dir")	
                    + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";	
    	
            Object bodyUdp = JsonFileHelper.parseJSONFile(payloadFileUdp);	
            JSONObject joUdp = (JSONObject) bodyUdp;             
            String udpChannelName = udpChannel[j];	
            int udpPortNum = udpPortNumber[j];
            joUdp.put("name", udpChannelName);
            joUdp.put("portNumber", udpPortNum);	
            ExtractableResponse<?> createResponseUdp = AssetsCreateRequestAPI.createCommChannel(bodyUdp);   
            String commChannelId = createResponseUdp.path("id").toString();
    	}           
}      
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=0)
    public void commChannelListTitleCorrect() {
        String EXPECTED_TITLE = "Comm Channels";
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        String actualPageTitle = listPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }   
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=1)
    public void commChannelListHeadersCorrect() {
        int EXPECTED_COUNT = 3;
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        List<String> headers = this.listPage.getTable().getListTableHeaders();
        softly.assertThat(headers.size()).isEqualTo(EXPECTED_COUNT);
        System.out.println(headers.size());
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("Type");
        softly.assertThat(headers).contains("Status");
        softly.assertAll();
    }   
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=2)
    public void commChannelListNameLinkCorrect() { 
        String payloadFileUdp = System.getProperty("user.dir")	
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";	
        Boolean flag = false;
        Object bodyUdp = JsonFileHelper.parseJSONFile(payloadFileUdp);	
        JSONObject joUdp = (JSONObject) bodyUdp;             
        joUdp.put("name", "UdpNameLink");
        joUdp.put("portNumber", 25991);	
        ExtractableResponse<?> createResponseUdp = AssetsCreateRequestAPI.createCommChannel(bodyUdp);   
        String commChannelId = createResponseUdp.path("id").toString();    	
    	String EXPECTED_HREF = SeleniumTestSetup.getBaseUrl() + Urls.Assets.COMM_CHANNEL_DETAIL.concat(commChannelId);
    	navigate(Urls.Assets.COMM_CHANNELS_LIST);
			List<WebElement> columnList = webTableColumn.getLinkValues(1);
			System.out.println(columnList);
			for(WebElement we:columnList){
				if(we.getAttribute("href").equals(EXPECTED_HREF)) {
					flag = true;
					assertThat(we.getAttribute("href").equals(EXPECTED_HREF)).isTrue();
					break;
				}
    		} 
			if(!flag) {
				assertThat(false);
			}
    }		
      
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=3)
    public void commChannelListNameSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.COMM_CHANNEL_NAME_ASC);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(1);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=4)
    public void commChannelListNameSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.COMM_CHANNEL_NAME_DESC);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(1);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=5)
    public void commChannelListTypeSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(2);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.COMM_CHANNEL_TYPE_ASC);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(2);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=6)
    public void commChannelListTypeSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(2);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.COMM_CHANNEL_TYPE_DESC);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(2);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=7)
    public void commChannelListStatusSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(3);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.COMM_CHANNEL_STATUS_ASC);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(3);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=8)
    public void commChannelListStatusSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.COMM_CHANNELS_LIST);
    		List<WebElement> columnName = webTableColumn.getColumnValues(3);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.COMM_CHANNEL_STATUS_DESC);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(3);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL }, priority=9)
    public void commChannelListCreateOpensPopupCorrect() throws Exception {
        String EXPECTED_TITLE = "Comm Channels";
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        String actualPageTitle = listPage.getPageTitle();
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";      
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
        listPage.actionsButtonClick();
        listPage.createButtonClick(); 
        ConfirmModal createModel = listPage.showCreateCommChannelModal();
        String actualCreateModelTitle = createModel.getModalTitle();
        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);

    }  
   }


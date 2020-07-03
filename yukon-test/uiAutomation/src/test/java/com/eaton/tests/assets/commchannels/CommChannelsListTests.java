package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.elements.WebTableColumn;
import com.eaton.elements.modals.CreateCommChannelModel;
import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.SortWebColumn;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dr.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelsListTests extends SeleniumTestSetup{
	
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private WebTableColumn webTableColumn;
    private ActionBtnDropDownElement actionBtn;
    private CreateCommChannelModel cm;

   	@BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
        webTableColumn = new WebTableColumn(driverExt);
        listPage = new CommChannelsListPage(driverExt);
       
        
        String [] tcpChannel = {"123","TCP_port","1@tcp"};
    	String [] udpChannel = {"thUdp","UDPport","2$udp"};
    	int [] udpPortNumber = {23459, 34567, 34568};
    	
    	for (int i=0;i<tcpChannel.length;i++) {
        String payloadFile = System.getProperty("user.dir")	
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";	    	
        Object bodyTcp = JsonFileHelper.parseJSONFile(payloadFile);	
        JSONObject joTcp = (JSONObject) bodyTcp;       
        String tcpChannelName = tcpChannel[i];	
        joTcp.put("name", tcpChannelName);    	
        ExtractableResponse<?> createResponseTcp = AssetsCreateRequestAPI.createCommChannelTCP(bodyTcp);           
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
            ExtractableResponse<?> createResponseUdp = AssetsCreateRequestAPI.createCommChannelUDP(bodyUdp);   
            String commChannelId = createResponseUdp.path("id").toString();
    	}           
}      
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListTitleCorrect() {
        String EXPECTED_TITLE = "Comm Channels";
        navigate(Urls.Assets.Comm_Channels_List);
        String actualPageTitle = listPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }   
     
    
    @Test(priority = 0)
    public void commChannelListCreateOpensPopupCorrect() {
        String EXPECTED_TITLE = "Comm Channels";
        navigate(Urls.Assets.Comm_Channels_List);
        String actualPageTitle = listPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
        listPage.actionsButtonClick();
        listPage.createButtonClick(); 
        assertThat(listPage.getPopupVisibility()).isEqualTo(true);
    }  
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListHeadersCorrect() {
        int EXPECTED_COUNT = 3;
        navigate(Urls.Assets.Comm_Channels_List);
        List<String> headers = this.listPage.getTable().getListTableHeaders();
        softly.assertThat(headers.size()).isEqualTo(EXPECTED_COUNT);
        System.out.println(headers.size());
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("Type");
        softly.assertThat(headers).contains("Status");
        softly.assertAll();
    }   
    
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListNameLinkCorrect() { 
        String payloadFileUdp = System.getProperty("user.dir")	
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";	
        Boolean flag = false;
        Object bodyUdp = JsonFileHelper.parseJSONFile(payloadFileUdp);	
        JSONObject joUdp = (JSONObject) bodyUdp;             
        joUdp.put("name", "UdpNameLink");
        joUdp.put("portNumber", 25999);	
        ExtractableResponse<?> createResponseUdp = AssetsCreateRequestAPI.createCommChannelUDP(bodyUdp);   
        String commChannelId = createResponseUdp.path("id").toString();    	
    	String EXPECTED_HREF = SeleniumTestSetup.getBaseUrl() + Urls.Assets.Comm_Channel.concat(commChannelId);
    	navigate(Urls.Assets.Comm_Channels_List);
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
  
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListNameSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.Comm_Channel_Name_Desc);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(1);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListNameSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.Comm_Channel_Name_Asc);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(1);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListTypeSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(2);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.Comm_Channel_Name_Asc);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(1);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListTypeSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(2);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.Comm_Channel_Name_Desc);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(1);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListStatusSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(3);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);  		
			navigate(Urls.Assets.Comm_Channel_Name_Asc);
			ArrayList<String> obtainedListAsc = new ArrayList<>();
    		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(1);
    		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    	} 
        	assertThat(sortedListName.equals(obtainedListAsc)).isTrue();       	
    	}
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelListStatusSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(3);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
			navigate(Urls.Assets.Comm_Channel_Name_Desc);
			ArrayList<String> obtainedListDesc = new ArrayList<>();
    		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(1);
        	for(WebElement webelement:columnNameDesc){
        		obtainedListDesc.add(webelement.getText());
 	    		} 
        	assertThat(sortedListName.equals(obtainedListDesc)).isTrue();		
    	}
   }


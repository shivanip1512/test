package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.WebTableColumn;
import com.eaton.elements.SortWebColumn;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commChannels.CommChannelDetailPage;
import com.eaton.pages.assets.commChannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dr.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelsListPageValidation extends SeleniumTestSetup{
	
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
    	String [] udpChannel = {"34TestUdp","UDPport","2$udp"};
    	
    	for (int i=0;i<=tcpChannel.length;i++) {
        String payloadFile = System.getProperty("user.dir")	
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";	    	
        Object bodyTcp = JsonFileHelper.parseJSONFile(payloadFile);	
        JSONObject joTcp = (JSONObject) bodyTcp;       
        String tcpChannelName = tcpChannel[i];	
        joTcp.put("name", tcpChannelName);    	
        ExtractableResponse<?> createResponseTcp = AssetsCreateRequestAPI.createCommChannelTCP(bodyTcp);           
    	}     	
    	for (int j=0;j<=udpChannel.length;j++) {
            String payloadFileUdp = System.getProperty("user.dir")	
                    + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelUDP.json";	
    	
            Object bodyUdp = JsonFileHelper.parseJSONFile(payloadFileUdp);	
            JSONObject joUdp = (JSONObject) bodyUdp;             
            String udpChannelName = udpChannel[j];	
            joUdp.put("name", udpChannelName);	       	
            ExtractableResponse<?> createResponseUdp = AssetsCreateRequestAPI.createCommChannelUDP(bodyUdp);          
    	}           
}      
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "CommChannelListTitleCorrect()" })
    public void CommChannelListTitleCorrect() {
        String EXPECTED_TITLE = "Comm Channels";
        navigate(Urls.Assets.Comm_Channels_List);
        String actualPageTitle = listPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }   
     
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelListHeadersCorrect()" })
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
       
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelListNameSortDescCorrectly()" })
    public void commChannelListNameSortDescCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortDesc(columnName);
    		
    		System.out.println(sortedListName);
    		
    			navigate(Urls.Assets.Comm_Channel_Name_Desc);
    			ArrayList<String> obtainedListDesc = new ArrayList<>();
        		List<WebElement> columnNameDesc = webTableColumn.getColumnValues(1);

        		for(WebElement webelement:columnNameDesc){
        			obtainedListDesc.add(webelement.getText());
 	    			} 
        		System.out.println(obtainedListDesc);
        		Assert.assertTrue(sortedListName.equals(obtainedListDesc));       	
    	}
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, "commChannelListNameSortAscCorrectly" })
    public void commChannelListNameSortAscCorrectly() {   	  	
    		navigate(Urls.Assets.Comm_Channels_List);
    		List<WebElement> columnName = webTableColumn.getColumnValues(1);
    		List<String> sortedListName = SortWebColumn.getSortAsc(columnName);
    		
    		System.out.println(sortedListName);
    		
    			navigate(Urls.Assets.Comm_Channel_Name_Asc);
    			ArrayList<String> obtainedListAsc = new ArrayList<>();
        		List<WebElement> columnNameAsc = webTableColumn.getColumnValues(1);

        		for(WebElement webelement:columnNameAsc){
        			obtainedListAsc.add(webelement.getText());
 	    			} 
        		System.out.println(obtainedListAsc);
        		Assert.assertTrue(sortedListName.equals(obtainedListAsc));       	
    	}
    }


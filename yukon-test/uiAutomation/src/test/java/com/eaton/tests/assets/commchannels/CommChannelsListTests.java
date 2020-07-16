package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelsListTests extends SeleniumTestSetup {

    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private List<String> names;
    private List<String> types;
    private List<String> statuses;
    private String udpCommChannelId;
    private String udpCommChannelName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();

        String[] tcpChannel = { "123", "TCP_port", "1@tcp" };
        String[] udpChannel = { "channeludp", "UDPport", "2$udp" };
        int[] udpPortNumber = { 23469, 34566, 34565 };

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
            udpCommChannelId = createResponseUdp.path("id").toString();
            udpCommChannelName = createResponseUdp.path("name").toString();
    	}   
        
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        listPage = new CommChannelsListPage(driverExt);
        
        names = listPage.getTable().getDataRowsTextByCellIndex(1);
        types = listPage.getTable().getDataRowsTextByCellIndex(2);
        statuses = listPage.getTable().getDataRowsTextByCellIndex(3);
    }
    
    @BeforeMethod
    public void beforeTest() {
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        listPage = new CommChannelsListPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_TitleCorrect() {
        String EXPECTED_TITLE = "Comm Channels";
        String actualPageTitle = listPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_HeadersCorrect() {
        int EXPECTED_COUNT = 3;
        List<String> headers = this.listPage.getTable().getListTableHeaders();

        softly.assertThat(headers.size()).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers.get(0)).isEqualTo("Name");
        softly.assertThat(headers.get(1)).contains("Type");
        softly.assertThat(headers.get(2)).contains("Status");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_NameLinkCorrect() { 
        WebTableRow row = listPage.getTable().getDataRowByName(udpCommChannelName); 
        String link = row.getCellLinkByIndex(0);
        
        assertThat(link).contains(Urls.Assets.COMM_CHANNEL_DETAIL.concat(udpCommChannelId));
    }		

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortNamesAscCorrectly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        
        navigate(Urls.Assets.COMM_CHANNEL_NAME_ASC);
        listPage = new CommChannelsListPage(driverExt);
        
        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortNamesDescCorrectly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(names);
        
        navigate(Urls.Assets.COMM_CHANNEL_NAME_DESC);
        listPage = new CommChannelsListPage(driverExt);
        
        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortTypesAscCorrectly() {
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);
        
        navigate(Urls.Assets.COMM_CHANNEL_TYPE_ASC);
        listPage = new CommChannelsListPage(driverExt);
        
        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);
        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortTypesDescCorrectly() {
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(types);
        navigate(Urls.Assets.COMM_CHANNEL_TYPE_DESC);
        listPage = new CommChannelsListPage(driverExt);
        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);
        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortStatusesAscCorrectly() {
        Collections.sort(statuses, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.COMM_CHANNEL_STATUS_ASC);
        listPage = new CommChannelsListPage(driverExt);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(3);

        assertThat(statuses).isEqualTo(statusList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_SortStatusesDescCorrectly() {
        Collections.sort(statuses, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(statuses);

        navigate(Urls.Assets.COMM_CHANNEL_STATUS_DESC);
        listPage = new CommChannelsListPage(driverExt);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(3);

        assertThat(statuses).isEqualTo(statusList);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelList_CreateOpensPopupCorrect() {
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        CreateCommChannelModal createModel = listPage.showAndWaitCreateCommChannelModal();
        String actualCreateModelTitle = createModel.getModalTitle();
        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);
    }
}

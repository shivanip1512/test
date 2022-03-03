package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.commchannel.CreateTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelsListTests extends SeleniumTestSetup {
    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private List<String> names;
    private List<String> types;
    private List<String> statuses;
    private String udpCommChannelId;
    private String udpCommChannelName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);        

        String[] tcpChannel = { "123", "TCP_port", "1@tcp" };
        String[] udpChannel = { "channeludp", "UDPport", "2$udp" };
        int[] udpPortNumber = { 23469, 34566, 34565 };

        for (int i = 0; i < tcpChannel.length; i++) {
            String payloadFile = System.getProperty("user.dir")
                    + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";
            Object bodyTcp = JsonFileHelper.parseJSONFile(payloadFile);
            JSONObject joTcp = (JSONObject) bodyTcp;
            String tcpChannelName = tcpChannel[i];
            joTcp.put("name", tcpChannelName);
            AssetsCreateRequestAPI.createCommChannel(bodyTcp);
        }
        for (int j = 0; j < udpChannel.length; j++) {
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

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(listPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_Page_TitleCorrect() {
        String EXPECTED_TITLE = "Comm Channels";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_ColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        int EXPECTED_COUNT = 3;
        List<String> headers = this.listPage.getTable().getListTableHeaders();

        softly.assertThat(headers.size()).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers.get(0)).isEqualTo("Name");
        softly.assertThat(headers.get(1)).contains("Type");
        softly.assertThat(headers.get(2)).contains("Status");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_Name_LinkCorrect() {
        WebTableRow row = listPage.getTable().getDataRowByName(udpCommChannelName);
        String link = row.getCellLinkByIndex(0);

        assertThat(link).contains(Urls.Assets.COMM_CHANNEL_DETAIL.concat(udpCommChannelId));
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_NameSortAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.COMM_CHANNEL_NAME_ASC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_NameSortDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(names);

        navigate(Urls.Assets.COMM_CHANNEL_NAME_DESC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_TypeSortAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.COMM_CHANNEL_TYPE_ASC);
        listPage = new CommChannelsListPage(driverExt);

        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_TypeSortDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(types);
        navigate(Urls.Assets.COMM_CHANNEL_TYPE_DESC);

        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_StatusesSortAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(statuses, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.COMM_CHANNEL_STATUS_ASC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(3);

        assertThat(statuses).isEqualTo(statusList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_StatusesSortDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(statuses, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(statuses);

        navigate(Urls.Assets.COMM_CHANNEL_STATUS_DESC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(3);

        assertThat(statuses).isEqualTo(statusList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS, TestConstants.Features.ASSETS })
    public void commChannelList_Create_OpensCorrectModal() {
        setRefreshPage(true);
        String EXPECTED_CREATE_MODEL_TITLE = "Create Comm Channel";
        CreateTcpCommChannelModal createModel = listPage.showAndWaitCreateTcpCommChannelModal();
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);
    }
}

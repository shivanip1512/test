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
import com.eaton.elements.modals.commchannel.EditTerminalServerCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelTerminalServerDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;
import io.restassured.response.ExtractableResponse;

public class CommChannelTcpEditTests extends SeleniumTestSetup {

    private CommChannelTerminalServerDetailPage detailPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;
    private Integer commChannelId;
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
        commChannelId = createResponse.path("id");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTerminalServerDetailPage(driverExt, commChannelId);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_RequiredValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(" ");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_InvalidChars() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue("/,tcp|");
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_AlreadyExists() {
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
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(commChannelNameTcp);
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_CancelNavigatesCorrectly() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_TITLE = commChannelName;

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_CreateOpensPopupCorrect() {
        String expectedModalTitle = "Create Comm Channel";
        
        EditTerminalServerCommChannelModal createModel = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(actualCreateModelTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_TabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);
        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Baud Rate:");
        softly.assertThat(labels.get(3)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigurationLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels("Configuration");
        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Pre Tx Wait:");
        softly.assertThat(labels.get(1)).isEqualTo("RTS To Tx Wait:");
        softly.assertThat(labels.get(2)).isEqualTo("Post Tx Wait:");
        softly.assertThat(labels.get(3)).isEqualTo("Receive Data Wait:");
        softly.assertThat(labels.get(4)).isEqualTo("Additional Time Out:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigurationsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> values = editModal.getTabs().getTabValues(tabName);

        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(values.get(0)).isEqualTo("25  ms");
        softly.assertThat(values.get(1)).isEqualTo("0  ms");
        softly.assertThat(values.get(2)).isEqualTo("10  ms");
        softly.assertThat(values.get(3)).isEqualTo("0  ms");
        softly.assertThat(values.get(4)).isEqualTo("0  sec");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPreTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getRtsToTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWai_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOkAndWait();

        assertThat(editModal.getPostTxWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOkAndWait();

        assertThat(editModal.getReceiveDataWait().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MaxValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoFieldsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Info";
        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        List<String> values = editModal.getTabs().getTabValues(tabName);

        softly.assertThat(values.size()).isEqualTo(4);
        softly.assertThat(values).contains(commChannelName);
        softly.assertThat(values).contains("TCP");
        softly.assertThat(values).contains("1200");
        softly.assertThat(values).contains("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTabTimingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_UpdateAllFieldsSuccess() {
        String expectedModalTitle = "Edit " + commChannelName;
        String commChannelName = "CommChannel_Tcp_Update";
        String baudRate = "4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";

        EditTerminalServerCommChannelModal editModal = detailPage.showTerminalServerCommChannelEditModal(expectedModalTitle);
        editModal.getName().setInputValue(commChannelName);
        editModal.getBaudRate().selectItemByText(baudRate);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWait();
                       
        String userMsg = detailPage.getUserMessage();        

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(commChannelId.toString());
        
        softly.assertThat(userMsg).isEqualTo(commChannelName + "saved successfully.");
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

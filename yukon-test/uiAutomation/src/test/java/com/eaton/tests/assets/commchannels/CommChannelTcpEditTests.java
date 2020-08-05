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
import com.eaton.elements.modals.commchannel.EditTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelTcpDetailPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsGetRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class CommChannelTcpEditTests extends SeleniumTestSetup {

    private CommChannelTcpDetailPage detailPage;
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
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + commChannelId);
        detailPage = new CommChannelTcpDetailPage(driverExt, commChannelId);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ModalTitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_RequiredValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name is required.";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        editModal.getName().clearInputValue();
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_InvalidChars() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        editModal.getName().setInputValue(commChannelNameTcp);
        editModal.clickOkAndWait();

        assertThat(editModal.getName().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_CancelNavigatesCorrectly() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_TITLE = commChannelName;

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_TabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTabLabelsCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);
        
        softly.assertThat(editModal.getPreTxWait().getInputValue()).isEqualTo("25");
        softly.assertThat(editModal.getRtsToTxWait().getInputValue()).isEqualTo("0");
        softly.assertThat(editModal.getPostTxWait().getInputValue()).isEqualTo("10");
        softly.assertThat(editModal.getReceiveDataWait().getInputValue()).isEqualTo("0");
        softly.assertThat(editModal.getAdditionalTimeOut().getInputValue()).isEqualTo("0");
        softly.assertAll();       
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MinValueValidation() {
        String expectedModalTitle = "Edit " + commChannelName;
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

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

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOkAndWait();

        assertThat(editModal.getAdditionalTimeOut().getValidationError()).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoFieldsValuesCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Info";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).isEqualTo("1200");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();        
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTabTimingSectionDisplayed() {
        String expectedModalTitle = "Edit " + commChannelName;
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal(expectedModalTitle);
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelTcpEdit_EditAllFieldsSuccess() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "TCP Comm Channel " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);
        CommChannelTcpDetailPage page = new CommChannelTcpDetailPage(driverExt, id);        
        
        String expectedModalTitle = "Edit " + name;
        String editName = "Edit TCP " + timeStamp;
        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = page.showTcpCommChannelEditModal(expectedModalTitle);
        editModal.getName().setInputValue(editName);
        editModal.getBaudRate().selectItemByValue(baudRate);

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue(configFieldsValues[0]);
        editModal.getRtsToTxWait().setInputValue(configFieldsValues[1]);
        editModal.getPostTxWait().setInputValue(configFieldsValues[2]);
        editModal.getReceiveDataWait().setInputValue(configFieldsValues[3]);
        editModal.getAdditionalTimeOut().setInputValue(configFieldsValues[4]);
        editModal.clickOkAndWaitForModalToClose();
                       
        String userMsg = detailPage.getUserMessage();        

        ExtractableResponse<?> response = AssetsGetRequestAPI.getCommChannel(id.toString());
        
        softly.assertThat(userMsg).isEqualTo(editName + " saved successfully.");
        softly.assertThat(editName).isEqualTo(response.path("name").toString());               
        softly.assertThat(baudRate).isEqualTo(response.path("baudRate").toString());
        softly.assertThat(configFieldsValues[0]).isEqualTo(response.path("timing.preTxWait").toString());
        softly.assertThat(configFieldsValues[1]).isEqualTo(response.path("timing.rtsToTxWait").toString());
        softly.assertThat(configFieldsValues[2]).isEqualTo(response.path("timing.postTxWait").toString());
        softly.assertThat(configFieldsValues[3]).isEqualTo(response.path("timing.receiveDataWait").toString());
        softly.assertThat(configFieldsValues[4]).isEqualTo(response.path("timing.extraTimeOut").toString());
        softly.assertAll();
    }
}

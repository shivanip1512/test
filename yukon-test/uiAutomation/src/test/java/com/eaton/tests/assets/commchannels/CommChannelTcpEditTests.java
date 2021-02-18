package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
    private Integer commChannelId;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
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

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Modal_TitleCorrect() {
        String expectedModalTitle = "Edit " + commChannelName;
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        String actualModalTitle = editModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_RequiredValidation() {
        String EXPECTED_MSG = "Name is required.";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().clearInputValue();
        editModal.clickOk();
        
        String errorMsg = editModal.getName().getValidationError(); 

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_InvalidCharsValidation() {
        String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().setInputValue("/,tcp|");
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getName().getValidationError(); 

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Name_AlreadyExistsValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String commChannelNameTcp = "TCP Comm Channel " + timeStamp;
        String EXPECTED_MSG = "Name already exists";
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelNameTcp);
        AssetsCreateRequestAPI.createCommChannel(body);

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getName().setInputValue(commChannelNameTcp);
        editModal.clickOkAndWaitForSpinner();
        
        String errorMsg = editModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Cancel_NavigatesToCorrectUrl() {
        String EXPECTED_TITLE = commChannelName;

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        editModal.clickCancelAndWait();

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        List<String> titles = editModal.getTabs().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Info");
        softly.assertThat(titles.get(1)).isEqualTo("Configuration");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String tabName = "Info";
        editModal.getTabs().clickTabAndWait(tabName);

        List<String> labels = editModal.getTabs().getTabLabels(tabName);

        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).isEqualTo("Baud Rate:");
        softly.assertThat(labels.get(3)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTab_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        String tabName = "Configuration";
        editModal.getTabs().clickTabAndWait(tabName);
        
        softly.assertThat(editModal.getPreTxWait().getInputValue()).isEqualTo("25");
        softly.assertThat(editModal.getRtsToTxWait().getInputValue()).isEqualTo("0");
        softly.assertThat(editModal.getPostTxWait().getInputValue()).isEqualTo("10");
        softly.assertThat(editModal.getReceiveDataWait().getInputValue()).isEqualTo("0");
        softly.assertThat(editModal.getAdditionalTimeOut().getInputValue()).isEqualTo("0");
        softly.assertAll();       
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_PreTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Pre Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPreTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getPreTxWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MinValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getRtsToTxWait().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_RtsToTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "RTS To Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getRtsToTxWait().setInputValue("10000001");
        editModal.clickOk();

        String errorMsg = editModal.getRtsToTxWait().getValidationError();
        
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWait_MinValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getPostTxWait().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_PostTxWait_MaxValueValidation() {
        String EXPECTED_MSG = "Post Tx Wait must be between 0 and 10,000,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getPostTxWait().setInputValue("10000001");
        editModal.clickOk();
        
        String errorMsg = editModal.getPostTxWait().getValidationError(); 

        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MinValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("-1");
        editModal.clickOk();
        
        String errorMsg = editModal.getReceiveDataWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_ReceiveDataWait_MaxValueValidation() {
        String EXPECTED_MSG = "Receive Data Wait must be between 0 and 1,000.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getReceiveDataWait().setInputValue("1001");
        editModal.clickOk();

        String errorMsg = editModal.getReceiveDataWait().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MinValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("-1");
        editModal.clickOk();

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError();
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_AdditionalTimeOut_MaxValueValidation() {
        String EXPECTED_MSG = "Additional Time Out must be between 0 and 999.";
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);
        editModal.getAdditionalTimeOut().setInputValue("1000");
        editModal.clickOk();;

        String errorMsg = editModal.getAdditionalTimeOut().getValidationError(); 
                
        assertThat(EXPECTED_MSG).isEqualTo(errorMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_InfoTab_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        String tabName = "Info";
        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();

        editModal.getTabs().clickTabAndWait(tabName);

        softly.assertThat(editModal.getName().getInputValue()).isEqualTo(commChannelName);
        softly.assertThat(editModal.getBaudRate().getSelectedValue()).isEqualTo("1200");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();        
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_ConfigTabTimingSection_Displayed() {
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
        editModal.getTabs().clickTabAndWait(tabName);

        Section timing = editModal.getTimingSection();
        
        assertThat(timing.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelTcpEdit_AllFields_Success() {
        SoftAssertions softly = new SoftAssertions();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "TCP  " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", name);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        Integer id = createResponse.path("id");
        
        navigate(Urls.Assets.COMM_CHANNEL_DETAIL + id);
        
        String editName = "Edit TCP " + timeStamp;
        String baudRate = "BAUD_4800";
        String configFieldsValues[] = { "55", "10", "20", "15", "500" };
        String tabName = "Configuration";

        EditTcpCommChannelModal editModal = detailPage.showTcpCommChannelEditModal();
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
        
        softly.assertThat(editName + " saved successfully.").isEqualTo(userMsg);
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

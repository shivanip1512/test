package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.commchannel.CreateTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelsListPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;

public class CommChannelCreateCommonTests extends SeleniumTestSetup {

    private CommChannelsListPage listPage;
    private DriverExtensions driverExt;
    private String commChannelName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        new SoftAssertions();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        commChannelName = "Unique Name " + timeStamp;

        // Creating one UDP port comm channel using hard coded json file.
        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", commChannelName);
        AssetsCreateRequestAPI.createCommChannel(body);
        
        navigate(Urls.Assets.COMM_CHANNELS_LIST);
        listPage = new CommChannelsListPage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(listPage);    
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannel_Name_RequiredValidation() {
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        final String EXPECTED_MSG = "Name is required.";

        createModal.clickOk();

        String errorMsg = createModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannel_Name_InvalidCharValidation() {
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        final String name = "Comm Channel / \\ , ' ";

        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        createModal.getName().setInputValue(name);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannel_Name_AlreadyExistsValidation() {
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        final String EXPECTED_MSG = "Name already exists";

        createModal.getName().setInputValue(commChannelName);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.COMM_CHANNELS })
    public void createCommChannel_Cancel_NavigatesToCorrectUrl() {
        String EXPECTED_TITLE = "Comm Channels";
        CreateTcpCommChannelModal createModal = listPage.showAndWaitCreateTcpCommChannelModal();

        createModal.commChannelClickCancelAndWait();
        
        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}

package com.eaton.tests.demandresponse.loadprogram;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterMethod;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.gears.EcobeeCycleGearBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;

public class LoadProgramEcobeeDetailsTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private String ldPrgmName;
    private String timeStamp;
    private LoadProgramDetailPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
    	driverExt = getDriverExt();
        setRefreshPage(false);
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldPrgmName = "EcobeeLoadProgram" + timeStamp;
        List<JSONObject> gears = new ArrayList<JSONObject>();
        gears.add(EcobeeCycleGearBuilder.gearBuilder().build());

        Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
												.withKwCapacity(Optional.empty()).create();
		JSONObject responseLdGrp = pairLdGrp.getValue1();
		int ldGrpId = responseLdGrp.getInt("id");

		List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));
        Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds).withGears(gears)
                        .withName(Optional.of(ldPrgmName))
                        .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                        .create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("programId");

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        detailPage = new LoadProgramDetailPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeDetail_Delete_Success() {
    	 setRefreshPage(true);
    	 timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
         String ldPrgmName = "EcobeeLoadProgram" + timeStamp;
         final String expected_msg = ldPrgmName + " deleted successfully.";
         
         List<JSONObject> gears = new ArrayList<JSONObject>();
         gears.add(EcobeeCycleGearBuilder.gearBuilder().build());

         Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
 												.withKwCapacity(Optional.empty()).create();
 		 JSONObject responseLdGrp = pairLdGrp.getValue1();
 		 int ldGrpId = responseLdGrp.getInt("id");

 		 List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));
         Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds).withGears(gears)
                         .withName(Optional.of(ldPrgmName))
                         .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                         .create();
         JSONObject response = pair.getValue1();
         int id = response.getInt("programId");

         navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);
    	 
    	 ConfirmModal  confirmModal = detailPage.showDeleteLoadProgramModal(); 
	     confirmModal.clickOkAndWaitForModalToClose();
	     
	     waitForPageToLoad("Setup", Optional.empty());
	     DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
	     String userMsg = setupPage.getUserMessage();
	     
	     assertThat(userMsg).isEqualTo(expected_msg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeDetail_Copy_Success() {
    	 setRefreshPage(true);
    	 timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
         String ldPrgmName = "EcobeeLoadProgram" + timeStamp;
         String copyName = "Copy Ecobee" + timeStamp;
         final String expected_msg = copyName + " copied successfully.";
         
         List<JSONObject> gears = new ArrayList<JSONObject>();
         gears.add(EcobeeCycleGearBuilder.gearBuilder().build());

         Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
 												.withKwCapacity(Optional.empty()).create();
 		 JSONObject responseLdGrp = pairLdGrp.getValue1();
 		 int ldGrpId = responseLdGrp.getInt("id");

 		 List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));
         Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds).withGears(gears)
                         .withName(Optional.of(ldPrgmName))
                         .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                         .create();
         JSONObject response = pair.getValue1();
         int id = response.getInt("programId");

         navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);
         
    	 CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();
         modal.getName().setInputValue(copyName);
         modal.clickOkAndWaitForModalToClose();
        
         waitForPageToLoad("Load Program: " + copyName, Optional.empty());
         String userMsg = detailPage.getUserMessage();
        
         assertThat(userMsg).isEqualTo(expected_msg);
    }       
}
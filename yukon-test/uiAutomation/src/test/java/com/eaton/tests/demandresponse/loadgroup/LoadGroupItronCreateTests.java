package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupItronCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupItronCreateTests extends SeleniumTestSetup {

	private DriverExtensions driverExt;
	private LoadGroupCreatePage createPage;
	private static final String TYPE = "Itron Group";
	private static final String BASE = "LM_GROUP_ITRON";
	
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
    }
   
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_AllFieldsDisableTrueSuccessfully() {
    	JSONObject jo = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
		    			.withKwCapacity(Optional.empty())
		    			.withRelay(Optional.empty())
		    			.build();
    	
    	String name = jo.getJSONObject(BASE).getString("name");
     
    	final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText(TYPE);
        
        waitForLoadingSpinner();
        
        createPage.getRelay().selectItemByText(String.valueOf(jo.getJSONObject(BASE).getInt("virtualRelayId")));
        createPage.getkWCapacity().setInputValue(String.valueOf(jo.getJSONObject(BASE).getDouble("kWCapacity")));
        createPage.getDisableGroup().setValue(jo.getJSONObject(BASE).getBoolean("disableGroup"));
        createPage.getDisableControl().setValue(jo.getJSONObject(BASE).getBoolean("disableControl"));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);   	
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_AllFieldsDisableTrueFalseSuccessfully() {
    	JSONObject jo = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
		    			.withKwCapacity(Optional.empty())
		    			.withRelay(Optional.empty())
		    			.withDisableGroup(Optional.empty())
		    			.build();
    	
		String name = jo.getJSONObject(BASE).getString("name");
		
		final String EXPECTED_MSG = name + " saved successfully.";

		createPage.getName().setInputValue(name);
		createPage.getType().selectItemByText(TYPE);
		
		waitForLoadingSpinner();
		
		createPage.getRelay().selectItemByText(String.valueOf(jo.getJSONObject(BASE).getInt("virtualRelayId")));
		createPage.getkWCapacity().setInputValue(String.valueOf(jo.getJSONObject(BASE).getDouble("kWCapacity")));
		createPage.getDisableGroup().setValue(jo.getJSONObject(BASE).getBoolean("disableGroup"));
		
		createPage.getDisableControl().setValue(true);
		
		createPage.getSaveBtn().click();
		
		waitForPageToLoad("Load Group: " + name, Optional.empty());
		
		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();
		
		assertThat(userMsg).isEqualTo(EXPECTED_MSG);   	
    }
  
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_DefaultRelaySavedSuccessfully() {
    	JSONObject jo = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
    					.build();
    	
		String name = jo.getJSONObject(BASE).getString("name");
		
		final String EXPECTED_MSG = name + " saved successfully.";
		
		createPage.getName().setInputValue(name);
		createPage.getType().selectItemByText(TYPE);
		
		waitForLoadingSpinner();
		
		createPage.getSaveBtn().click();
		
		waitForPageToLoad("Load Group: " + name, Optional.empty());
		
		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();
		
		assertThat(userMsg).isEqualTo(EXPECTED_MSG);  
		
		List<String> row = detailsPage.getTable().getDataRowsTextByCellIndex(2);
		
		assertThat(row.get(0)).isEqualTo("1");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_RelayDropDownContainsExpectedValues() {
    	List<String> expectedRelayValues = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8"));
    	
    	createPage.getType().selectItemByText(TYPE);
        
    	List<String> actualRelayValues = createPage.getRelay().getOptionValues();
        
        assertThat(expectedRelayValues).containsExactlyElementsOf(actualRelayValues);
    }
}

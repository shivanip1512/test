package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.TestEmailModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.ConfigSettingsPage;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class ConfigSettingsTests extends SeleniumTestSetup {
    
    private ConfigSettingsPage page;
    private String baseUrl;
    private String adminUrl;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        setRefreshPage(false);
        
        baseUrl = SeleniumTestSetup.getBaseUrl();
        adminUrl = "/admin/config/";
        
        navigate(Urls.Admin.CONFIGURATION);        
        page = new ConfigSettingsPage(driverExt, Urls.Admin.CONFIGURATION);        
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(page);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SystemSetupSection_Displayed() {
        setRefreshPage(false);
        Section section = page.getSystemSetupSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ApplicationSection_Displayed() {
        setRefreshPage(false);
        Section section = page.getApplicationSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_IntegrationSection_Displayed() {
        setRefreshPage(false);
        Section section = page.getIntegrationSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OtherSection_Displayed() {
        setRefreshPage(false);
        Section section = page.getOtherSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AttributesTitle_Correct() {
        setRefreshPage(false);
        String title = page.getAttributesBtn().getTitle();
        
        assertThat(title).isEqualTo("Attributes");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AttributesDetails_Correct() {
        setRefreshPage(false);
        String details = page.getAttributesBtn().getDetails();
        
        assertThat(details).isEqualTo("Assign custom attributes to devices");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AttributesIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getAttributesBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=ATTRIBUTES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AttributesTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getAttributesBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=ATTRIBUTES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AuthenticationTitle_Correct() {
        setRefreshPage(false);
        String title = page.getAuthenticationBtn().getTitle();
        
        assertThat(title).isEqualTo("Authentication");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AuthenticationDetails_Correct() {
        setRefreshPage(false);
        String details = page.getAuthenticationBtn().getDetails();
        
        assertThat(details).isEqualTo("Authentication, Active Directory, LDAP server settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AuthenticationIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getAuthenticationBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=AUTHENTICATION");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_AuthenticationTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getAuthenticationBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=AUTHENTICATION");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardAdminTitle_Correct() {
        setRefreshPage(false);
        String title = page.getDashboardAdminBtn().getTitle();
        
        assertThat(title).isEqualTo("Dashboard Administration");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardAdminDetails_Correct() {
        setRefreshPage(false);
        String details = page.getDashboardAdminBtn().getDetails();
        
        assertThat(details).isEqualTo("Administration page for Dashboards");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardAdminIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getDashboardAdminBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=DASHBOARD_ADMIN");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardAdminTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getDashboardAdminBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=DASHBOARD_ADMIN");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DemandResponseTitle_Correct() {
        setRefreshPage(false);
        String title = page.getDemandResponseBtn().getTitle();
        
        assertThat(title).isEqualTo("Demand Response");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DemandResponseDetails_Correct() {
        setRefreshPage(false);
        String details = page.getDemandResponseBtn().getDetails();
        
        assertThat(details).isEqualTo("Opt outs, importing, switch command settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DemandResponseIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getDemandResponseBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=DR");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DemandResponseTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getDemandResponseBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=DR");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_EndpointTitle_Correct() {
        setRefreshPage(false);
        String title = page.getEndpointBtn().getTitle();
        
        assertThat(title).isEqualTo("Endpoint");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_EndpointDetails_Correct() {
        setRefreshPage(false);
        String details = page.getEndpointBtn().getDetails();
        
        assertThat(details).isEqualTo("Display templates, importer settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_EndpointIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getEndpointBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=AMI");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_EndpointTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getEndpointBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=AMI");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ThemesTitle_Correct() {
        setRefreshPage(false);
        String title = page.getThemesBtn().getTitle();
        
        assertThat(title).isEqualTo("Themes");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ThemesDetails_Correct() {
        setRefreshPage(false);
        String details = page.getThemesBtn().getDetails();
        
        assertThat(details).isEqualTo("Change the way Yukon looks");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ThemesIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getThemesBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=THEMES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ThemesTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getThemesBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=THEMES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WebServerTitle_Correct() {
        setRefreshPage(false);
        String title = page.getWebServerBtn().getTitle();
        
        assertThat(title).isEqualTo("Web Server");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WebServerDetails_Correct() {
        setRefreshPage(false);
        String details = page.getWebServerBtn().getDetails();
        
        assertThat(details).isEqualTo("Banner logo");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WebServerIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getWebServerBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=WEB_SERVER");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WebServerTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getWebServerBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=WEB_SERVER");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_YukonServicesTitle_Correct() {
        setRefreshPage(false);
        String title = page.getYukonServicesBtn().getTitle();
        
        assertThat(title).isEqualTo("Yukon Services");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_YukonServicesDetails_Correct() {
        setRefreshPage(false);
        String details = page.getYukonServicesBtn().getDetails();
        
        assertThat(details).isEqualTo("Yukon services ip address, ports");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_YukonServicesIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getYukonServicesBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=YUKON_SERVICES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_YukonServicesTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getYukonServicesBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=YUKON_SERVICES");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardWidgetsTitle_Correct() {
        setRefreshPage(false);
        String title = page.getDashboardWidgetsBtn().getTitle();
        
        assertThat(title).isEqualTo("Dashboard Widgets");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardWidgetsDetails_Correct() {
        setRefreshPage(false);
        String details = page.getDashboardWidgetsBtn().getDetails();
        
        assertThat(details).isEqualTo("System-wide dashboard widget settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardWidgetsIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getDashboardWidgetsBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=DASHBOARD_WIDGET");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DashboardWidgetsTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getDashboardWidgetsBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=DASHBOARD_WIDGET");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DataImporExportTitle_Correct() {
        setRefreshPage(false);
        String title = page.getDataImportExportBtn().getTitle();
        
        assertThat(title).isEqualTo("Data Import/Export");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DataImporExportDetails_Correct() {
        setRefreshPage(false);
        String details = page.getDataImportExportBtn().getDetails();
        
        assertThat(details).isEqualTo("Data import and export settings, schedule options, and billing format settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DataImporExportIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getDataImportExportBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=DATA_IMPORT_EXPORT");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_DataImporExportTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getDataImportExportBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=DATA_IMPORT_EXPORT");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_TrendsTitle_Correct() {
        setRefreshPage(false);
        String title = page.getTrendsBtn().getTitle();
        
        assertThat(title).isEqualTo("Trends");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_TrendsDetails_Correct() {
        setRefreshPage(false);
        String details = page.getTrendsBtn().getDetails();
        
        assertThat(details).isEqualTo("Web trends settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_TrendsIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getTrendsBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=GRAPHING");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_TrendsTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getTrendsBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=GRAPHING");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MultispeakTitle_Correct() {
        setRefreshPage(false);
        String title = page.getMultispeakBtn().getTitle();
        
        assertThat(title).isEqualTo("Multispeak");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MultispeakDetails_Correct() {
        setRefreshPage(false);
        String details = page.getMultispeakBtn().getDetails();
        
        assertThat(details).isEqualTo("Vendor info, device mapping, billing groups");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MultispeakIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getMultispeakBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=MULTISPEAK");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MultispeakTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getMultispeakBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=MULTISPEAK");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OpenAdrTitle_Correct() {
        setRefreshPage(false);
        String title = page.getOpenAdrBtn().getTitle();
        
        assertThat(title).isEqualTo("OpenADR");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OpenAdrDetails_Correct() {
        setRefreshPage(false);
        String details = page.getOpenAdrBtn().getDetails();
        
        assertThat(details).isEqualTo("OpenADR connection settings");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OpenAdrIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getOpenAdrBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=OPEN_ADR");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OpenAdrTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getOpenAdrBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=OPEN_ADR");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_VoiceTitle_Correct() {
        setRefreshPage(false);
        String title = page.getVoiceBtn().getTitle();
        
        assertThat(title).isEqualTo("Voice");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_VoiceDetails_Correct() {
        setRefreshPage(false);
        String details = page.getVoiceBtn().getDetails();
        
        assertThat(details).isEqualTo("Voice server call prefix, call response time");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_VoiceIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getVoiceBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=VOICE");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_VoiceTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getVoiceBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=VOICE");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WeatherTitle_Correct() {
        setRefreshPage(false);
        String title = page.getWeatherBtn().getTitle();
        
        assertThat(title).isEqualTo("Weather");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WeatherDetails_Correct() {
        setRefreshPage(false);
        String details = page.getWeatherBtn().getDetails();
        
        assertThat(details).isEqualTo("Add weather tracking locations to Yukon");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WeatherIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getWeatherBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=WEATHER");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_WeatherTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getWeatherBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=WEATHER");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MiscellaneousTitle_Correct() {
        setRefreshPage(false);
        String title = page.getMiscBtn().getTitle();
        
        assertThat(title).isEqualTo("Miscellaneous");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MiscellaneousDetails_Correct() {
        setRefreshPage(false);
        String details = page.getMiscBtn().getDetails();
        
        assertThat(details).isEqualTo("Everything else");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MiscellaneousIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getMiscBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=MISC");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_MiscellaneousTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getMiscBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=MISC");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SecurityTitle_Correct() {
        setRefreshPage(false);
        String title = page.getSecurityBtn().getTitle();
        
        assertThat(title).isEqualTo("Security");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SecurityDetails_Correct() {
        setRefreshPage(false);
        String details = page.getSecurityBtn().getDetails();
        
        assertThat(details).isEqualTo("Demand Response Encrypted Routes and Keys");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SecurityIconLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String btnLink = page.getSecurityBtn().getButtonLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(baseUrl + adminUrl + btnLink);
        
        softly.assertThat(btnLink).isEqualTo("edit?category=SECURITY");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SecurityTitleLink_Correct() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        String link = page.getSecurityBtn().getTitleLink();
        
        ExtractableResponse<?> response = ApiCallHelper.get(link);
        
        softly.assertThat(link).isEqualTo(baseUrl + adminUrl + "edit?category=SECURITY");
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_TestEmail_OpensCorrectModal() {
        setRefreshPage(true);
        String expectedModalTitle = "Test Email";

        TestEmailModal testEmailModal = page.showTestEmailModal();

        String actualModalTitle = testEmailModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void configSettings_TestEmail_ConfirmMessageCorrect() {
        setRefreshPage(true);
        String expectedModalMessage = "Enter email address and click Send. A test email notification will be sent to the email address specified from yukon@eaton.com.";

        TestEmailModal testEmailModal = page.showTestEmailModal();

        String actualModalMessage = testEmailModal.getMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void configSettings_TestEmailAddress_RequiredValidation() {
        setRefreshPage(true);

        TestEmailModal testEmailModal = page.showTestEmailModal();

        testEmailModal.clickOk();
        
        String actualModalMessage = testEmailModal.getEmailAddress().getValidationError();

        assertThat(actualModalMessage).isEqualTo("Email is required.");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void configSettings_TestEmailAddress_InvalidValidation() {
        setRefreshPage(true);

        TestEmailModal testEmailModal = page.showTestEmailModal();
        
        testEmailModal.getEmailAddress().setInputValue("aaaa");
        testEmailModal.clickOk();
        
        String actualModalMessage = testEmailModal.getEmailAddress().getValidationError();

        assertThat(actualModalMessage).isEqualTo("Not a valid email address.");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void configSettings_TestEmailAddress_MaxLengthValidation() {
        setRefreshPage(true);

        TestEmailModal testEmailModal = page.showTestEmailModal();
        
        String maxLength = testEmailModal.getEmailAddress().getMaxLength();

        assertThat(maxLength).isEqualTo("100");
    }
    
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
//    public void configSettings_TestEmail_CouldNotBeSent() {
//        setRefreshPage(true);
//
//        TestEmailModal testEmailModal = page.showTestEmailModal();
//        
//        String email = faker.internet().emailAddress();
//        
//        testEmailModal.getEmailAddress().setInputValue(email);
//        testEmailModal.clickOkAndWaitForModalToClose();
//        
//        String msg = page.getUserMessage();
//
//        assertThat(msg).isEqualTo("An email could not be sent to " + email + " via SMTP host relay.cooperpowereas.net");
//    }
}

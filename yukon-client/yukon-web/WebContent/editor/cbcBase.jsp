<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<jsp:directive.page import="com.cannontech.core.dao.DaoFactory"/>
<jsp:directive.page import="com.cannontech.database.data.lite.LiteYukonUser"/>
<jsp:directive.page import="com.cannontech.common.constants.LoginController"/>
<jsp:directive.page import="com.cannontech.roles.application.WebClientRole"/>
<jsp:directive.page import="com.cannontech.servlet.nav.CBCNavigationUtil"/>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>

<cti:standardPage title="CapControl Wizard" module="capcontrol">
<cti:includeScript link="/JavaScript/itemPicker.js"/>
<cti:includeScript link="/JavaScript/pointPicker.js"/>
<cti:includeScript link="/JavaScript/tableCreation.js"/>
<cti:includeScript link="/JavaScript/scrollDiv.js"/>
<cti:includeScript link="/capcontrol/js/cbc_funcs.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css"/>
<cti:includeScript link="/JavaScript/GreyBox/AmiJS.js"/>
<cti:includeScript link="/JavaScript/GreyBox/greybox.js"/>
<cti:includeCss link="/editor/css/greybox/greybox.css"/>
<%
    //****
    // Entry point file for all operations that edit a PAObject
    //****
    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);
    CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
    if( id != PAOGroups.INVALID ) {
        JSFUtil.resetBackingBean("capControlForm");
        JSFUtil.resetBackingBean("capBankEditor");
        JSFUtil.resetBackingBean("cbcCopyForm");
        capControlForm.initItem( id, type );
    }
%>
<f:verbatim>
<script type="text/JavaScript">
	addLockButtonForButtonGroup("hdr_buttons");
	addLockButtonForButtonGroup("foot_buttons");
</script>
</f:verbatim>
    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader" navigationClass="pageNavigation" bodyClass="pageBody" footerClass="pageFooter" >
        <f:facet name="body">
            <h:form id="editorForm">
            <f:verbatim><br/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Editor:" /> 
            <x:outputText styleClass="bigFont" value="#{capControlForm.paoName}"/>
            <f:verbatim><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true" styleClass="smallResults" errorClass="errorResults" layout="table"/>
            <x:panelGroup id="hdr_buttons" forceId="true">
                <f:verbatim><br/></f:verbatim>
                <x:commandButton id="hdr_submit_button_1" value="Submit" action="#{capControlForm.update}" styleClass="stdButton" title="Writes this item to the database"  rendered = "#{!capControlForm.visibleTabs['CBCCapBank']}"/>
                <x:commandButton id="hdr_submit_button_2" value="Submit" action="#{capBankEditor.update}" styleClass="stdButton" title="Writes this item to the database"  rendered = "#{capControlForm.visibleTabs['CBCCapBank']}"/>
                <x:commandButton id="hdr_reset_button"  value="Reset" action="#{capControlForm.resetForm}" styleClass="stdButton" title="Resets all the data to the original settings" />
                <x:commandButton id="hdr_return_button" value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
                	<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
              	</x:commandButton>
            </x:panelGroup>
            <h:panelGrid id="body" columns="1" styleClass="pageBody">
                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>
                <x:panelTabbedPane id="tabPane" activeTabStyleClass="activeTab" style="width: 100%; vertical-align: top;" selectedIndex="#{capControlForm.selectedPanelIndex}" serverSideTabSwitch="#{true}">
                
                	<x:tabChangeListener type="com.cannontech.web.editor.model.CCTabChangeListener"/>
                	
                    <x:panelTab id="tabGen" label="General" rendered="#{capControlForm.visibleTabs['General']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcGeneralEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSubSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcSubSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSubSchedSetup" label="Schedule" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcSubSchedule.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabFeederSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcFeederSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabStrategySetup" label="Control Strategy Setup" rendered="#{capControlForm.visibleTabs['CBCArea'] || capControlForm.visibleTabs['CBCSpecialArea'] || capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcStrategySetup.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabChild" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcChildList.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabCapBank" label="Setup" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcCapBank.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabController" label="Setup" rendered="#{capControlForm.visibleTabs['CBCController']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcCBController.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSchedule" label="Schedule" rendered="#{capControlForm.visibleTabs['CBCSchedule']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcSchedule.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabDualBus" label="Advanced" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
                    	<jsp:include page="/WEB-INF/pages/cbc/cbcDualBus.jsp"/>
                    </x:panelTab>		
		
		    		<x:panelTab id="tabAdvCapBank" label="Advanced" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
                    	<jsp:include page="/WEB-INF/pages/cbc/cbcCapBankAdvanced.jsp"/>                   
                    </x:panelTab>

		    		<x:panelTab id="tabCBAddInfo" label="Additional Info" rendered="#{capControlForm.visibleTabs['CBAddInfo']}">
                    	<jsp:include page="/WEB-INF/pages/cbc/capBankAddInfo.jsp"/>                   
                    </x:panelTab>
                    
					<x:panelTab id="tabAreaSubs" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCArea']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcAreaSubs.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabSpecialAreaSubs" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcSpecialAreaSubs.jsp"/>
                    </x:panelTab>
	
	                <x:panelTab id="tabStrategyEditor" label="Control Strategy Editor" rendered="#{capControlForm.visibleTabs['CBCStrategy']}">
						<jsp:include page="/WEB-INF/pages/cbc/cbcStrategyEditor.jsp"/>
                    </x:panelTab>
                </x:panelTabbedPane>

                <f:facet name="footer">
                    <x:panelGroup id="foot_buttons" forceId="true">
                        <f:verbatim><br/></f:verbatim>
                        <x:commandButton id="submit_button1" value="Submit" action="#{capControlForm.update}" styleClass="stdButton" title="Writes this item to the database"  rendered = "#{!capControlForm.visibleTabs['CBCCapBank']}"/>
                        <x:commandButton id="submit_button2" value="Submit" action="#{capBankEditor.update}" styleClass="stdButton" title="Writes this item to the database"  rendered = "#{capControlForm.visibleTabs['CBCCapBank']}"/>
                        <x:commandButton  id="reset_button"  value="Reset" action="#{capControlForm.resetForm}" styleClass="stdButton"  title="Resets all the data to the original settings" />
                        <x:commandButton 	id="return_button" value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
                            <f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
                        </x:commandButton>
                    </x:panelGroup>
                </f:facet>
            </h:panelGrid>
            </h:form>
        </f:facet>
    </x:panelLayout>
</cti:standardPage>
</f:view>
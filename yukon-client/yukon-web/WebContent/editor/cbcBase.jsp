<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>

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
        
		//This is needed because this was handled in the CBCSerlvet before entering faces pages.
		//Since the servlet bypass, this static method will need to be called entering any faces page.
        CapControlForm.setupFacesNavigation();

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
            <x:outputText rendered="#{!capControlForm.editingAStrategy}" styleClass="bigFont" value="#{capControlForm.paoName}"/>
            <x:outputText rendered="#{capControlForm.editingAStrategy}" styleClass="bigFont" value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].strategyName}"/>
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
                        <jsp:include page="/WEB-INF/pages/capcontrol/generalEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSubBusSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/subBusSetup.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabSubstationSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/substationSetup.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabAreaSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCArea']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/areaSetup.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabSpecialAreaSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/specialAreaSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSubSchedSetup" label="Schedule" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/subBusSchedule.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabFeederSetup" label="Setup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/feederSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabStrategySetup" label="Control Strategy Setup" rendered="#{capControlForm.visibleTabs['CBCArea'] || capControlForm.visibleTabs['CBCSpecialArea'] || capControlForm.visibleTabs['CBCSubstationBus'] || capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/strategySetup.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabChild" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCSubstationBus'] || capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/childList.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabCapBank" label="Setup" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/capBankSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabController" label="Setup" rendered="#{capControlForm.visibleTabs['CBCController']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/capBankControllerSetup.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tabSchedule" label="Schedule" rendered="#{capControlForm.visibleTabs['CBCSchedule']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/cbcSchedule.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabDualBus" label="Advanced" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
                    	<jsp:include page="/WEB-INF/pages/capcontrol/subBusDualBus.jsp"/>
                    </x:panelTab>		
		
		    		<x:panelTab id="tabAdvCapBank" label="Advanced" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
                    	<jsp:include page="/WEB-INF/pages/capcontrol/capBankAdvanced.jsp"/>                   
                    </x:panelTab>

		    		<x:panelTab id="tabCBAddInfo" label="Additional Info" rendered="#{capControlForm.visibleTabs['CBAddInfo']}">
                    	<jsp:include page="/WEB-INF/pages/capcontrol/capBankAddInfo.jsp"/>                   
                    </x:panelTab>
                    
					<x:panelTab id="tabAreaSubs" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCArea']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/areaSubs.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="tabSpecialAreaSubs" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCSpecialArea']}">
                        <jsp:include page="/WEB-INF/pages/capcontrol/specialAreaSubs.jsp"/>
                    </x:panelTab>
	
	                <x:panelTab id="tabStrategyEditor" label="Control Strategy Editor" rendered="#{capControlForm.visibleTabs['CBCStrategy']}">
						<jsp:include page="/WEB-INF/pages/capcontrol/strategyEditor.jsp"/>
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
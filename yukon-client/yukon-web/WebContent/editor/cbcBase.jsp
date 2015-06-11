<%@ page import="com.cannontech.database.data.pao.*"%>
<%@ page import="com.cannontech.util.*"%>
<%@ page import="com.cannontech.web.util.*"%>
<%@ page import="com.cannontech.web.editor.*"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="javax.faces.application.FacesMessage"%>
<%@ page import="com.cannontech.cbc.exceptions.CBCExceptionMessages"%>
<%@ page import="javax.faces.context.FacesContext"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="x" uri="http://myfaces.apache.org/tomahawk" %>

<f:view>

<cti:standardPage title="CapControl Wizard" module="capcontrol">

<cti:includeScript link="/resources/js/pages/yukon.da.common.js"/>
<cti:includeScript link="/resources/js/lib/tomahawk.hacks/popcalendar.js"/>

<cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>

<%
    /* Entry point file for all operations that edit a PAObject */
    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);
    String copySuccess = ParamUtil.getString(request, "copySuccess", null);
    CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
    if( id != PAOGroups.INVALID ) {
        CapControlForm.setupFacesNavigation();

        capControlForm.initItem( id, type );
        
        if(!StringUtils.isBlank(copySuccess)){
            FacesMessage message = new FacesMessage();
            message.setDetail(CBCExceptionMessages.DB_UPDATE_SUCCESS);
            FacesContext.getCurrentInstance().addMessage("copy_object", message);
        }
    }
%>

<f:verbatim>
<script type="text/JavaScript">
	yukon.da.common.addLockButtonForButtonGroup("hdr_buttons");
	yukon.da.common.addLockButtonForButtonGroup("foot_buttons");
</script>
</f:verbatim>
    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader" navigationClass="pageNavigation" bodyClass="pageBody" footerClass="pageFooter" >
        <f:facet name="body">
            
            <h:form id="editorForm">
                <f:verbatim><cti:csrfToken/></f:verbatim>
                <x:inputHidden value="#{capControlForm.itemId}"/>
                <h:inputHidden value="#{capControlForm.editorType}"/>
                <x:htmlTag value="br"/>
                
                <x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Editor:" /> 
                <x:outputText rendered="#{!capControlForm.editingAStrategy}" styleClass="bigFont" value="#{capControlForm.paoName}"/>
                <x:outputText rendered="#{capControlForm.editingAStrategy}" styleClass="bigFont" value="#{capControlForm.strategy.strategyName}"/>
                
                <x:htmlTag value="br"/>
                
                <x:messages id="messageList" showSummary="true" showDetail="true" styleClass="smallResults" errorClass="errorResults" layout="table"/>
                
                <x:panelGroup id="hdr_buttons" forceId="true">
                    <x:htmlTag value="br"/>
                    
                    <x:commandButton id="hdr_submit_button_1" value="Submit" action="#{capControlForm.update}" styleClass="stdButton" 
                        title="Writes this item to the database"  rendered = "#{!capControlForm.visibleTabs['CBCCapBank'] && capControlForm.editingAuthorized}"/>
                    
                    <x:commandButton id="hdr_submit_button_2" value="Submit" action="#{capBankEditor.update}" styleClass="stdButton" 
                        title="Writes this item to the database"  rendered = "#{capControlForm.visibleTabs['CBCCapBank'] && capControlForm.editingAuthorized}"/>
                    
                    <f:verbatim>
                        <cti:msgScope paths="capcontrol.cbcBase">
                            <%-- Delete Buttons --%>
                            <c:set var="paoDeletion" value="true" />

                            <c:if test="${capControlForm.visibleTabs['CBCStrategy']}">
                                <c:set var="paoDeletion" value="false"/>
                                <cti:url var="deleteStrategyUrl" value="/capcontrol/strategy/deleteStrategy">
                                    <cti:param name="strategyId" value="${capControlForm.itemId}" />
                                </cti:url>
                                <cti:button id="strategy-delete-header" nameKey="delete"  href="${deleteStrategyUrl}"/>
                                <d:confirm on="#strategy-delete-header"
                                    nameKey="confirmDelete" argument="${capControlForm.paoName}"/>
                            </c:if>
                            <c:if test="${paoDeletion}">
                                <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                                    <cti:param name="value" value="${capControlForm.itemId}"/>
                                </cti:url>
                                <cti:button nameKey="delete" href="${deleteUrl}"/>
                            </c:if>

                            <%-- Copy CBC Button --%>
                            <c:if test="${capControlForm.visibleTabs['CBCController']}">
                                <cti:url var="copyUrl" value="/editor/copyBase.jsf">
                                    <cti:param name="itemid" value="${capControlForm.itemId}"/>
                                    <cti:param name="type" value="1"/>
                                </cti:url>
                                <cti:button nameKey="copy" href="${copyUrl}"/>
                            </c:if>
                        </cti:msgScope>
                    </f:verbatim>

                    <x:commandButton id="hdr_return_button" value="Return" action="none" styleClass="stdButton" immediate="true" 
                        title="Returns to the last module page that was used to enter this editor" >
                    	<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
                  	</x:commandButton>
                    
                    <x:htmlTag value="br"/>
                    <x:htmlTag value="br"/>
                </x:panelGroup>
                <h:panelGrid id="body" columns="1" styleClass="pageBody">
                    <f:facet name="header">
                        <x:panelGroup>
                        </x:panelGroup>
                    </f:facet>
                    <x:panelTabbedPane id="tabPane" selectedIndex="#{capControlForm.selectedPanelIndex}" serverSideTabSwitch="#{false}">
                        
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
                        
                            <x:htmlTag value="br"/>
                            
                            <x:commandButton id="submit_button1" value="Submit" action="#{capControlForm.update}" styleClass="stdButton" 
                                title="Writes this item to the database"  rendered = "#{!capControlForm.visibleTabs['CBCCapBank'] && capControlForm.editingAuthorized}"/>
                            
                            <x:commandButton id="submit_button2" value="Submit" action="#{capBankEditor.update}" styleClass="stdButton" 
                                title="Writes this item to the database" rendered = "#{capControlForm.visibleTabs['CBCCapBank'] && capControlForm.editingAuthorized}"/>
                            
                            <f:verbatim>
                                <cti:msgScope paths="capcontrol.cbcBase">

                                    <%-- Delete Buttons --%>
                                    <c:set var="paoDeletion" value="true" />

                                    <c:if test="${capControlForm.visibleTabs['CBCStrategy']}">
                                        <c:set var="paoDeletion" value="false"/>
                                        <cti:url var="deleteStrategyUrl" value="/capcontrol/strategy/deleteStrategy">
                                            <cti:param name="strategyId" value="${capControlForm.itemId}" />
                                        </cti:url>
                                        <cti:button id="strategy-delete-footer" nameKey="delete"  href="${deleteStrategyUrl}"/>
                                        <d:confirm on="#strategy-delete-footer"
                                            nameKey="confirmDelete" argument="${capControlForm.paoName}"/>
                                    </c:if>
                                    <c:if test="${paoDeletion}">
                                        <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                                            <cti:param name="value" value="${capControlForm.itemId}"/>
                                        </cti:url>
                                        <cti:button nameKey="delete" href="${deleteUrl}"/>
                                    </c:if>

                                    <%-- Copy CBC Button --%>
                                    <c:if test="${capControlForm.visibleTabs['CBCController']}">
                                        <cti:url var="copyUrl" value="/editor/copyBase.jsf">
                                            <cti:param name="itemid" value="${capControlForm.itemId}"/>
                                            <cti:param name="type" value="1"/>
                                        </cti:url>
                                        <cti:button nameKey="copy" href="${copyUrl}"/>
                                    </c:if>
                                </cti:msgScope>
                            </f:verbatim>

                            <x:commandButton id="return_button" value="Return" action="none" styleClass="stdButton" immediate="true" 
                                title="Returns to the last module page that was used to enter this editor" >
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
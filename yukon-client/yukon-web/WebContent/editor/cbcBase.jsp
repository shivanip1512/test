<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">
<cti:standardMenu/>

<%
    //****
    // Entry point file for all operations that edit a PAObject
    //****

    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);

    if( id != PAOGroups.INVALID ) {
        CapControlForm capControlForm =
            (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );

        capControlForm.initItem( id, type );
    }
%>


    <x:saveState id="capControlForm" value="#{capControlForm}" />


    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="editorForm">

            <f:verbatim><br/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Editor"/>
            <f:verbatim><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>



            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>


                <x:panelTabbedPane id="tabPane" style="width: 100%; vertical-align: top;" >
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

                    <x:panelTab id="tabStrategy" label="Control Strategy" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
                        <jsp:include page="/WEB-INF/pages/cbc/cbcStrategyEditor.jsp"/>
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



                </x:panelTabbedPane>


                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/></f:verbatim>
                        <x:commandButton value="Submit" action="#{capControlForm.update}" styleClass="stdButton" title="Writes this item to the database" />
                        <x:commandButton value="Reset" action="#{capControlForm.resetForm}" styleClass="stdButton" title="Resets all the data to the original settings" />
                        <x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
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
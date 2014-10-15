<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.web.editor.point.PointForm" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="Editor" module="capcontrol">
    <cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>
<%
    //****
    // Entry point file for all operations that edit a point
    //****

    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);
    String parentId = JSFParamUtil.getJSFReqParam("parentId");

    PointForm ptEditorForm = (PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );

    if (parentId != null) {
        ptEditorForm.initWizard(new Integer(parentId));
    }
    if( id != PAOGroups.INVALID ) {
        ptEditorForm.initItem( id );
    }
    //This is needed because this was handled in the CBCSerlvet before entering faces pages.
    //Since the servlet bypass, this static method will need to be called entering any faces page.
    CapControlForm.setupFacesNavigation();
%>



    <x:panelLayout id="page"
            styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="editorForm">
            <f:verbatim><cti:csrfToken/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="Point Editor"/>
            <f:verbatim><br/><hr/><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>


                <x:panelTabbedPane id="tabPane" style="width: 100%; vertical-align: top;" >
                    <x:panelTab id="generalTab" label="General" rendered="#{ptEditorForm.visibleTabs['General']}">
                        <jsp:include page="/WEB-INF/pages/point/pointBaseEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="physicalAnaTab" label="Physical Setup" rendered="#{ptEditorForm.visibleTabs['PointAnalog']}">
                        <jsp:include page="/WEB-INF/pages/point/pointAnalogEditor.jsp"/>
                    </x:panelTab>
                    
                    <x:panelTab id="physicalAccmuTab" label="Physical Setup" rendered="#{ptEditorForm.visibleTabs['PointAccum']}">
                        <jsp:include page="/WEB-INF/pages/point/pointAccumulatorEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="physicalStatTab" label="Physical Setup" rendered="#{ptEditorForm.visibleTabs['PointStatus']}">
                        <jsp:include page="/WEB-INF/pages/point/pointStatusEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="arlarmingTab" label="Alarming" rendered="#{ptEditorForm.visibleTabs['Alarming']}">
                        <jsp:include page="/WEB-INF/pages/point/pointAlarmEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="fdrTab" label="Foreign Data" rendered="#{ptEditorForm.visibleTabs['FDR']}">
                        <jsp:include page="/WEB-INF/pages/point/pointFDREditor.jsp"/>
                    </x:panelTab>


                </x:panelTabbedPane>


                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Submit" action="#{ptEditorForm.update}" styleClass="stdButton" rendered="#{capControlForm.editingAuthorized}"/>
                        <x:commandButton value="Reset" action="#{ptEditorForm.resetForm}" styleClass="stdButton" rendered="#{capControlForm.editingAuthorized}"/>
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
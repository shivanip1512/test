<%@ page import="com.cannontech.database.data.point.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<%
	//****
	// Entry point file for all operations that edit a point
	//****

	int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);


	if( id != PAOGroups.INVALID ) {
		PointForm ptEditorForm =
			(PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );

		ptEditorForm.initItem( id );
	}
%>

<f:view>

    <x:panelLayout id="page"
            styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
			<h:form id="editorForm">

			<x:outputText styleClass="editorHeader" value="Point Editor"/>
            <f:verbatim><br/><hr/><br/></f:verbatim>
		    <x:messages id="messageList" showSummary="true" showDetail="true"
		    		styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >

				<f:facet name="header">
					<x:panelGroup>
	                </x:panelGroup>
				</f:facet>


                <x:panelTabbedPane id="tabPane" styleClass="tabPane">
                    <x:panelTab id="tab1" label="General" rendered="#{capControlForm.visibleTabs['General']}">
		               	<jsp:include page="pointBaseEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tab2" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
		                <jsp:include page="pointAlarmEditor.jsp"/>
                    </x:panelTab>



	            </x:panelTabbedPane>


				<f:facet name="footer">
					<x:panelGroup>
				        <f:verbatim><br/><hr/><br/></f:verbatim>
				        <x:commandButton value="Submit" action="#{ptEditorForm.update}" styleClass="stdButton" />
				        <x:commandButton value="Reset" action="#{ptEditorForm.resetForm}" styleClass="stdButton" />
				        <x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
							<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
						</x:commandButton>
				    </x:panelGroup>
				</f:facet>

            </h:panelGrid>

			</h:form>
        </f:facet>
    </x:panelLayout>


</f:view>
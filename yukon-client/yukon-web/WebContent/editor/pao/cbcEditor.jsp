<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>

<%
	//****
	// Entry point file for all operations that edit a PAObject
	//****

	int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);

	if( id != PAOGroups.INVALID ) {
		CapControlForm capControlForm =
			(CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );

		capControlForm.initItem( id );
	}
%>

<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8" />
  <title>Editor</title>
  <link rel="stylesheet" type="text/css" href="/editor/css/base.css" />
  
</head>


<body>

<f:view>

    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
			<h:form id="editorForm">

	        <x:commandButton value="Submit" action="#{capControlForm.update}" styleClass="stdButton" title="Writes this item to the database" />
	        <x:commandButton value="Reset" action="#{capControlForm.resetForm}" styleClass="stdButton" title="Resets all the data to the original settings" />
	        <x:commandButton value="Cancel" action="none" styleClass="stdButton" immediate="true" title="Cancels editing and returns to the last module that was used" >
				<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
			</x:commandButton>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

				<f:facet name="header">
					<x:panelGroup>
						<x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle}"/>
		                <f:verbatim><br/><hr/><br/></f:verbatim>
					    <x:messages id="messageList" showSummary="true" showDetail="true"
					    		styleClass="smallResults" errorClass="errorResults" layout="table"/>
	                </x:panelGroup>
				</f:facet>


                <x:panelTabbedPane id="tabPane" styleClass="tabPane">
                    <x:panelTab id="tab1" label="General" rendered="#{capControlForm.visibleTabs['General']}">
		               	<jsp:include page="cbcBaseEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tab2" label="Setup" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
		               	<jsp:include page="cbcFeederSubEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tab3" label="Control Strategy" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
		               	<jsp:include page="cbcStrategyEditor.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tab4" label="#{capControlForm.childLabel}" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}">
		               	<jsp:include page="cbcChildList.jsp"/>
                    </x:panelTab>

                    <x:panelTab id="tab5" label="Setup" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
		               	<jsp:include page="cbcCapBank.jsp"/>
                    </x:panelTab>





	            </x:panelTabbedPane>


				<f:facet name="footer">
					<x:panelGroup>
				        <f:verbatim><br/><hr/><br/></f:verbatim>
				        <x:commandButton value="Submit" action="#{capControlForm.update}" styleClass="stdButton" title="Writes this item to the database" />
				        <x:commandButton value="Reset" action="#{capControlForm.resetForm}" styleClass="stdButton" title="Resets all the data to the original settings" />
				        <x:commandButton value="Cancel" action="none" styleClass="stdButton" immediate="true" title="Cancels editing and returns to the last module that was used" >
							<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
						</x:commandButton>
				    </x:panelGroup>
				</f:facet>

            </h:panelGrid>

			</h:form>
        </f:facet>
    </x:panelLayout>


</f:view>


</body>
</html>
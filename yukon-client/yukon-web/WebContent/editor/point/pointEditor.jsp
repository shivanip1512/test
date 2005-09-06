<%@ page import="com.cannontech.database.data.point.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>

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

<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8" />
  <title>Point Editor</title>
  <link rel="stylesheet" type="text/css" href="/editor/css/base.css" />
</head>


<body>

<f:view>

    <x:panelLayout id="page"
            styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
			<h:form id="editorForm">

	        <x:commandButton value="Submit" action="#{ptEditorForm.update}" styleClass="stdButton" />
	        <x:commandButton value="Reset" action="#{ptEditorForm.resetForm}" styleClass="stdButton" />
	        <x:commandButton value="Cancel" action="none" styleClass="stdButton" immediate="true">
				<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
			</x:commandButton>			

            <h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >

				<f:facet name="header">
					<x:panelGroup>
						<x:outputText styleClass="editorHeader" value="Point Editor"/>
		                <f:verbatim><br/><hr/><br/></f:verbatim>
					    <x:messages id="messageList" showSummary="true" showDetail="true"
					    		styleClass="smallResults" errorClass="errorResults" layout="table"/>
	                </x:panelGroup>
				</f:facet>


				<h:column>
	               	<jsp:include page="pointBaseEditor.jsp"/>
				</h:column>

				<h:column>
	                <jsp:include page="pointAlarmEditor.jsp"/>
				</h:column>



                <x:panelTabbedPane bgcolor="#FFFFCC" rendered="false">
                    <x:panelTab id="tab2" label="Tab 2" rendered="#{ptEditorForm.visibleTabs['Tab2']}">
   	                    <h:selectBooleanCheckbox id="testCheckBox" value="#{testCheckBox.checked}"/>
   	                    <x:outputLabel for="testCheckBox" value="A checkbox"/>
	                    <f:verbatim><br/><br/></f:verbatim>
                        <x:inputText id="inp1"/><f:verbatim><br></f:verbatim>
                        <x:inputText id="inp2"/>
                    </x:panelTab>

                    <x:panelTab id="tab3" label="Tab 3" rendered="#{ptEditorForm.visibleTabs['Tab3']}">
						<x:selectOneRadio value="#{testRadioButton.choice}">
							<f:selectItem itemValue="0" itemLabel="First Choice" />
							<f:selectItem itemValue="1" itemLabel="Second Choice" />
                        </x:selectOneRadio>
		                
						<f:verbatim><br/><br/></f:verbatim>

                        <x:inputText id="inp3"/><f:verbatim><br/></f:verbatim>
                    </x:panelTab>

                    <f:verbatim><br/><hr/><br/></f:verbatim>
	            </x:panelTabbedPane>

				<f:facet name="footer">
					<x:panelGroup>
				        <f:verbatim><br/><hr/><br/></f:verbatim>
				        <x:commandButton value="Submit" action="#{ptEditorForm.update}" styleClass="stdButton" />
				        <x:commandButton value="Reset" action="#{ptEditorForm.resetForm}" styleClass="stdButton" />
				        <x:commandButton value="Cancel" action="none" styleClass="stdButton" immediate="true">
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
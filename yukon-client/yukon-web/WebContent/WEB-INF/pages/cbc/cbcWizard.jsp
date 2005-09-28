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
	// Entry point file for all operations that create a CBC PAObject
	//****

	int type = ParamUtil.getInteger(request, "type", DBEditorForm.INVALID);

	if( type != DBEditorForm.INVALID ) {
		CapControlForm capControlForm =
			(CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );

		capControlForm.initWizard( type );
	}

%>

<f:view>

    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
			<h:form id="wizardForm">

	        <f:verbatim><br/></f:verbatim>
			<x:outputText styleClass="editorHeader" value="#{capControlForm.editorTitle} Wizard"/>
            <f:verbatim><br/></f:verbatim>
		    <x:messages id="messageList" showSummary="true" showDetail="true"
		    		styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

				<f:facet name="header">
					<x:panelGroup>
	                </x:panelGroup>
				</f:facet>

				<x:panelGroup>
				    <f:verbatim><br/><fieldset><legend>General</legend></f:verbatim>

					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="cbcName" value="Name: " title="A label for the item in the system"/>
					<x:inputText id="cbcName" required="true" maxlength="32" styleClass="char32Label"
							value="#{capControlForm.wizData.name}" />
	
			        <f:verbatim><br/></f:verbatim>
					<h:selectBooleanCheckbox id="disabledCheck" value="#{capControlForm.wizData.disabled}" />
					<x:outputLabel for="disabledCheck" value="Disabled" title="Is the item disabled"/>
					<f:verbatim></fieldset></f:verbatim>
				</x:panelGroup>


				<x:panelGroup rendered="#{capControlForm.visibleTabs['CBCController']}" >
			        <f:verbatim><br/><br/></f:verbatim>
				    <f:verbatim><fieldset><legend>Item Type</legend></f:verbatim>
					<x:selectOneRadio id="itemType" value="#{capControlForm.wizData.secondaryType}" layout="pageDirection" 
							required="true" onclick="submit();" >
						<f:selectItems value="#{selLists.CBCTypes}" />
					</x:selectOneRadio>
					
			        <f:verbatim><br/></f:verbatim>
					<x:outputLabel for="cbcComm" value="Comm. Channel: "
							title="The communication channel this CBC will use (only applies to a subset of CBC types)"/>
					<x:selectOneMenu id="cbcComm" disabled="#{!capControlForm.wizData.portNeeded}"
							value="#{capControlForm.wizData.portID}" >
						<f:selectItems value="#{selLists.commChannels}"/>
					</x:selectOneMenu>
					
					<f:verbatim></fieldset></f:verbatim>

				</x:panelGroup>				



				<f:facet name="footer">
					<x:panelGroup>
				        <f:verbatim><br/><hr/><br/></f:verbatim>
				        <x:commandButton value="Create" action="#{capControlForm.create}" styleClass="stdButton" title="Saves this item to the database" />
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
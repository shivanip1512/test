
<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.web.editor.point.PointForm" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">
    <cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>
<%
    //****
    // Entry point file for all operations that create a CBC PAObject
    //****


     int paoId = ParamUtil.getInteger(request, "parentId", PAOGroups.INVALID);

    if( paoId != PAOGroups.INVALID ) {
        PointForm ptEditorForm = (PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );
        ptEditorForm.initWizard( new Integer ( paoId ) );
    }

      

%>



    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="wizardForm">
            <f:verbatim><cti:csrfToken/></f:verbatim>
            <f:verbatim><br/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="Point Wizard"/>
            <f:verbatim><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>

                <x:panelGroup>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
	                    <x:htmlTag value="legend"><x:outputText value="General"/></x:htmlTag>
	
	                    <f:verbatim><br/></f:verbatim>
	                    <x:outputLabel for="Name" value="Name: " title="A label for the item in the system"/>
	                    <x:inputText id="Name" required="true" maxlength="32" styleClass="char32Label"
	                            value="#{ptEditorForm.wizData.name}" />
	    
	                    <f:verbatim><br/></f:verbatim>
	                    <h:selectBooleanCheckbox id="disabledCheck" value="#{ptEditorForm.wizData.disabled}" />
	                    <x:outputLabel for="disabledCheck" value="Disabled" title="Is the item disabled"/>
                    </x:htmlTag>
                </x:panelGroup>


                <x:panelGroup>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Item Type"/></x:htmlTag>
	                    <x:outputLabel for="Point_Type" value="Point Type: "
	                            title="Type of Point this object will be"/>
	                    <x:selectOneMenu id="Point_Type" value="#{ptEditorForm.wizData.pointType}"
	                            required="true" onchange="submit();" >
	                        <f:selectItems value="#{selLists.pointTypes}" />
	                    </x:selectOneMenu>
                    </x:htmlTag>

                </x:panelGroup>             
            <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Create" action="#{ptEditorForm.create}" styleClass="stdButton" title="Saves this item to the database" />
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
<%@ page import="com.cannontech.web.copy.DBCopyForm" %>
<%@ page import="com.cannontech.web.util.ParamUtil" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.web.util.JSFParamUtil" %>
<%@ page import="com.cannontech.web.editor.*" %>
<jsp:directive.page import="com.cannontech.web.util.JSFUtil"/>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
    <cti:standardPage title="CapControl Copy Object" module="capcontrol">
	    <cti:includeScript link="/JavaScript/da/capcontrolGeneral.js"/>
	    <cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>
		<%
		    //****
		    // Entry point file for all operations that edit a PAObject
		    //****
		
		    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);
		    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
		    if( id != PAOGroups.INVALID ) {
		        DBCopyForm cbcCopyForm = (DBCopyForm)JSFParamUtil.getJSFVar( "cbcCopyForm" );
		        cbcCopyForm.init(id, type);
		        
		        //This is needed because this was handled in the CBCSerlvet before entering faces pages.
		        //Since the servlet bypass, this static method will need to be called entering any faces page.
		        CapControlForm.setupFacesNavigation();
		    }
		%>
	
        <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader" navigationClass="pageNavigation" bodyClass="pageBody" footerClass="pageFooter" >
            <f:facet name="body">
                <h:form id="editorForm">
                    <x:outputText styleClass="editorHeader" value="Database Copy Editor"/>
                    <x:messages id="messageList" showSummary="true" showDetail="true" styleClass="smallResults" errorClass="errorResults" layout="table"/>

					<x:htmlTag value="fieldset" styleClass="fieldSet">
					    <x:htmlTag value="legend"><x:outputText value="Object To Copy: #{cbcCopyForm.paoName}"/></x:htmlTag>
	                        <x:panelGrid columns="1" styleClass="gridLayout" columnClasses="gridCell">
                                <x:panelGroup>
									<x:outputText value="New Name" />	
									<x:inputText id="paoNameInput" value="#{cbcCopyForm.paoName}"/>
								</x:panelGroup>
								<x:panelGroup>
						            <x:selectBooleanCheckbox id="copyPoints" value="#{cbcCopyForm.copyPoints}" rendered="#{cbcCopyForm.showCopyPoints}"/>
						            <x:outputText value="Copy Points" rendered="#{cbcCopyForm.showCopyPoints}"/>	
                                </x:panelGroup>
				            </x:panelGrid>
                    </x:htmlTag>
                    
                    <x:panelGroup id="buttons">
	                    <x:htmlTag value="hr"/>
	                    <x:commandButton id = "copy" value="Submit" action="#{cbcCopyForm.copyDBObject}" styleClass="stdButton" title="Copies this item to the database" />
	                    <x:commandButton id="return" value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
	                        <f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
	                    </x:commandButton>
                    </x:panelGroup>
                </h:form>
            </f:facet>
        </x:panelLayout>
    </cti:standardPage>
</f:view>
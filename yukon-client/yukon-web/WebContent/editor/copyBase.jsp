<%@ page import="com.cannontech.web.copy.DBCopyForm" %>
<%@ page import="com.cannontech.util.ParamUtil" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.web.util.JSFParamUtil" %>
<jsp:directive.page import="com.cannontech.web.util.JSFUtil"/>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Copy Object" module="capcontrol">
<cti:includeScript link="/capcontrol/js/cbc_funcs.js"/>
<%
    //****
    // Entry point file for all operations that edit a PAObject
    //****

    int id = ParamUtil.getInteger(request, "itemid", PAOGroups.INVALID);
    int type = ParamUtil.getInteger(request, "type", PAOGroups.INVALID);
	DBCopyForm cbcCopyForm = new DBCopyForm();
    if( id != PAOGroups.INVALID ) {
        JSFUtil.resetBackingBean("cbcCopyForm");
        cbcCopyForm = (DBCopyForm)JSFParamUtil.getJSFVar( "cbcCopyForm" );
        cbcCopyForm.init(id, type);

    }
%>


    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >


        <f:facet name="body">
            <h:form id="editorForm">
            <f:verbatim><br/></f:verbatim>
            <x:outputText styleClass="editorHeader" value="Database Copy Editor"/>
            <f:verbatim><br/></f:verbatim>
            <f:verbatim><br/></f:verbatim>
            <f:verbatim><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

	
	<f:verbatim>
			<br />
			<fieldset class="fieldSet">
				<legend>
				 Object To Copy: ${ cbcCopyForm.paoName }
				</legend>
				<br/> <br/>
	</f:verbatim>
	<x:outputText value="New Name" />	
	<x:inputText id="paoNameInput" value="#{cbcCopyForm.paoName}"/>
	<f:verbatim><br/></f:verbatim>
	<f:verbatim><br/></f:verbatim>
	<f:verbatim><br/></f:verbatim>
    <x:outputText value="Copy Points" rendered="#{cbcCopyForm.showCopyPoints}"/>	
	<x:selectBooleanCheckbox id="copyPoints" value="#{cbcCopyForm.copyPoints}" rendered="#{cbcCopyForm.showCopyPoints}"/>
	             <f:verbatim>
            </fieldset>
            </f:verbatim>
	 <x:panelGroup id="buttons">
                        <f:verbatim><br/><hr/><br/></f:verbatim>
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

<jsp:directive.page import="com.cannontech.database.data.pao.PAOGroups"/>
<jsp:directive.page import="com.cannontech.web.delete.DeleteFormPoint"/>
<jsp:directive.page import="com.cannontech.web.util.JSFUtil"/>
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil"/>
<jsp:directive.page import="com.cannontech.web.util.ParamUtil"/>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">
<cti:includeCss link="/editor/css/CapcontrolEditorStyles.css"/>
<%
//This is needed because this was handled in the CBCSerlvet before entering faces pages.
//Since the servlet bypass, this static method will need to be called entering any faces page.
CapControlForm.setupFacesNavigation();
DeleteFormPoint deleteForm = (DeleteFormPoint)JSFParamUtil.getJSFVar( "pointDeleteForm" );
deleteForm.initItem();
%>

    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="deleteForm">

            <x:outputText styleClass="editorHeader" value="Item Deletion"/>
            <f:verbatim><br/><hr/><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <x:div styleClass="deletionDiv">
                
                    <h:dataTable id="deleteItems" var="dbObj"
                            styleClass="deletionScrollerTable" 
                            headerClass="deletionScrollerTableHeader"
                            footerClass="deletionScrollerTableHeader"
                            rowClasses="altTableRow,tableRow"
                            value="#{pointDeleteForm.deleteItems}"
                            columnClasses="deletionScrollerTableCell,deletionScrollerTableCell,deletionScrollerTableCell,deletionScrollerTableCell" >
                       <h:column>
                            <f:facet name="header">
                                <x:outputText value="Is Deletable?" title="If the particular item will be deleted upon submission"/>
                            </f:facet>
                            <x:outputText value="yes" rendered="#{dbObj.deleteAllowed}"/>
                            <x:outputText value="no" rendered="#{!dbObj.deleteAllowed}" styleClass="alert" />
                       </h:column>
        
                       <h:column>
                            <f:facet name="header">
                                <x:outputText value="Confirm Delete" title="Check if want to delete permanenty"/>
                            </f:facet>
    						<h:selectBooleanCheckbox id="confirmDelete" onclick="submit()" 
    												immediate="true"  value="#{dbObj.checked}" 
    												disabled="#{!dbObj.deleteAllowed}"/>
    						        
                       </h:column>
                       <h:column>
                            <f:facet name="header">
                                <x:outputText value="Selected Item(s)" title="The items that are selected for deletion"/>
                            </f:facet>
                            <x:outputText value="#{dbObj.name}" />
                       </h:column>
        
                       <h:column>
                            <f:facet name="header">
                                <x:outputText value="Status" title="Detailed message describing why this item may or may not be deleted" />
                            </f:facet>
                            <x:outputText value="#{dbObj.warningMsg}" rendered="#{!dbObj.deleteError && !dbObj.wasDeleted}" />
                            <x:outputText value="#{dbObj.warningMsg}" rendered="#{!dbObj.deleteError && dbObj.wasDeleted}" styleClass="complete" />
                            <x:outputText value="#{dbObj.warningMsg}" rendered="#{dbObj.deleteError}" styleClass="alert" />
                           
                       </h:column>
        
                    </h:dataTable>

                </x:div>

                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Submit" action="#{pointDeleteForm.update}" styleClass="stdButton" title="Writes this item to the database" />
                        <x:commandButton value="Return" action="#{pointDeleteForm.reset}" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
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
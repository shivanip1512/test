<jsp:directive.page import="com.cannontech.util.ParamUtil"/>
<jsp:directive.page import="com.cannontech.database.data.pao.PAOGroups"/>
<jsp:directive.page import="com.cannontech.web.util.JSFUtil"/>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">

<%
int value = ParamUtil.getInteger(request, "value", PAOGroups.INVALID);
if (value != PAOGroups.INVALID)
{
    JSFUtil.resetBackingBean("pointDeleteForm");
}
//This is needed because this was handled in the CBCSerlvet before entering faces pages.
//Since the servlet bypass, this static method will need to be called entering any faces page.
CapControlForm.setupFacesNavigation();
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

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>


                <h:dataTable id="deleteItems" var="dbObj"
                        styleClass="fullTable" headerClass="scrollerTableHeader"
                        footerClass="scrollerTableHeader"
                        rowClasses="tableRow,altTableRow"
                        value="#{pointDeleteForm.deleteItems}"
                        columnClasses="gridCellSmall,gridCellSmall,gridCellMedium,gridCellLarge" >
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



                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Submit" action="#{pointDeleteForm.update}" styleClass="stdButton" title="Writes this item to the database" />
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
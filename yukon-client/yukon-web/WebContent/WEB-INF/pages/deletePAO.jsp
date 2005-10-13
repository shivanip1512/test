<%@ page import="com.cannontech.database.data.pao.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>


<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:view>

    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
			<h:form id="paoDeleteForm">

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
	            		value="#{paoDeleteForm.deleteItems}"
						columnClasses="gridCellSmall,gridCellMedium,gridCellLarge" >
	               <h:column>
						<f:facet name="header">
							<x:outputText value="Will Delete" title="If the particular item will be deleted upon submission"/>
						</f:facet>
						<x:outputText value="yes" rendered="#{dbObj.deleteAllowed}"/>
						<x:outputText value="no" rendered="#{!dbObj.deleteAllowed}" styleClass="alert" />
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
				        <x:commandButton value="Submit" action="#{paoDeleteForm.update}" styleClass="stdButton" title="Writes this item to the database" />
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
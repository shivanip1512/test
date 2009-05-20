<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ page import="com.cannontech.web.editor.CapControlForm" %>

<%
//This is needed because this was handled in the CBCSerlvet before entering faces pages.
//Since the servlet bypass, this static method will need to be called entering any faces page.
	CapControlForm.setupFacesNavigation();
%>

<f:view>
	<cti:standardPage title="CapControl Wizard" module="capcontrol">
		<script type="text/javascript">
		    var submitAllowed = true;
		    function submitCheck() {
		        if( !submitAllowed ) {
		            submitAllowed = true;
		            return false;
		        } else {
		            return true;
		        }
		    }
		    
		    var stratNameLookup = ${cti:jsonString(capControlForm.strategyNameMap)};
		
		    function confirmDelete( stratId ) {
		        var stratName = stratNameLookup[stratId];
		        if( confirm("Are you sure you want to delete '" + stratName+ "'")) {
		            submitAllowed = true;
		        } else {
		            submitAllowed = false;
		        }
		    }
		</script>

		<x:panelLayout id="page" styleClass="pageLayout"
			headerClass="pageHeader" navigationClass="pageNavigation"
			bodyClass="pageBody" footerClass="pageFooter">

			<f:facet name="body">
				<h:form id="stratListForm" onsubmit="return submitCheck();">

					<h:outputText styleClass="editorHeader" value="Strategies" />
					<x:htmlTag value="br"/>
					<x:commandButton value="Return" action="none"
						styleClass="stdButton" immediate="true"
						title="Returns to the last module page that was used to enter this editor">
						<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
					</x:commandButton>
					
					<x:htmlTag value="br"/>
					<x:htmlTag value="hr"/>
					<x:htmlTag value="br"/>
										
					<x:messages id="messageList" showSummary="true" showDetail="true"
						styleClass="smallResults" errorClass="errorResults" layout="table" />

					<h:panelGrid id="body" columns="1" styleClass="pageBody">

						<h:dataTable id="deleteItems" var="cbcStrat"
							styleClass="fullTable" headerClass="scrollerTableHeader"
							footerClass="scrollerTableHeader"
							rowClasses="tableRow,altTableRow"
							value="#{capControlForm.allCBCStrats}"
							columnClasses="scrollerLeft">
							<h:column>
								<f:facet name="header">
									<h:outputText value="Strategy Name" title="Name of the strategy" />
								</f:facet>
								
								<x:panelGrid columns="2">
								    
								    <x:panelGroup>
		                                    <h:commandLink id="editAction" style="margin: 2px;"
		                                        title="Edit the schedules attributes"
		                                        actionListener="#{capControlForm.editCBCStrat}">
		                                        <x:graphicImage value="/editor/images/edit_item.gif" height="15" width="15" border="0" rendered="#{capControlForm.editingAuthorized}"/>
		                                        <x:graphicImage value="/editor/images/info_item.gif" height="15" width="15" border="0" rendered="#{!capControlForm.editingAuthorized}"/>
		                                        <f:param name="stratID" value="#{cbcStrat.strategyID}" />
		                                    </h:commandLink>
		                                    <h:commandLink id="delAction" style="margin: 2px;"
                                                rendered="#{capControlForm.editingAuthorized}"
		                                        title="Remove the schedule from the system"
		                                        onclick="confirmDelete( '#{cbcStrat.strategyID}' );"
		                                        actionListener="#{capControlForm.deleteStrat}"
		                                        title=" Delete #{cbcStrat.strategyName}">
		                                        <x:graphicImage value="/editor/images/delete_item.gif" height="15" width="15" border="0" />
		                                        <f:param name="stratID" value="#{cbcStrat.strategyID}" />
		                                    </h:commandLink>
									</x:panelGroup>
									
									<h:outputText value="#{cbcStrat.strategyName}" />
								
								</x:panelGrid>
								
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Control Method" />
								</f:facet>
								<h:outputText value="#{cbcStrat.controlMethod  }" />
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Control Algorithm" />
								</f:facet>
								<h:outputText value="#{cbcStrat.controlUnits}" />
							</h:column>		
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Start/Stop" />
								</f:facet>
								<h:outputText value="#{cbcStrat.peakStartTime}" converter="cti_TimeConverter" />
								<h:outputText value="/"/>
								<h:outputText value="#{cbcStrat.peakStopTime}" converter="cti_TimeConverter"/>
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Interval" />
								</f:facet>
								<h:outputText value="#{cbcStrat.controlIntervalString}"/>
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Confirm Time" />
								</f:facet>
								<h:outputText value="#{cbcStrat.minResponseTimeString}"/>
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Pass/Fail(%)" />
								</f:facet>
								<h:outputText value="#{cbcStrat.passFailPercentString}"/>
							</h:column>

							<h:column>
								<f:facet name="header">
									<h:outputText value="Peak Settings" />
								</f:facet>
								<h:outputText value="#{cbcStrat.peakSettingsString}" />
							</h:column>
							
							<h:column>
								<f:facet name="header">
									<h:outputText value="Off Peak Settings" />
								</f:facet>
								<h:outputText value="#{cbcStrat.offPeakSettingsString}" />
							</h:column>							
						</h:dataTable>

						<f:facet name="footer">
							<x:panelGroup>
								<x:htmlTag value="br"/>
                                <x:htmlTag value="hr"/>
                                <x:htmlTag value="br"/>
								<x:commandButton value="Return" action="none"
									styleClass="stdButton" immediate="true"
									title="Returns to the last module page that was used to enter this editor">
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
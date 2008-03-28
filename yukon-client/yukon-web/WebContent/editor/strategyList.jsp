<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
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
							columnClasses="gridCell">
							<h:column>
								<f:facet name="header">
									<h:outputText value="Strategy Name" title="Name of the strategy" />
								</f:facet>
								
								<x:panelGrid columns="2">
								    
								    <x:panelGroup>
										<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
		                                    <h:commandLink id="editAction" style="margin: 2px;"
		                                        title="Edit the schedules attributes"
		                                        actionListener="#{capControlForm.editCBCStrat}">
		                                        <x:graphicImage value="/editor/images/edit_item.gif" height="15" width="15" border="0" />
		                                        <f:param name="stratID" value="#{cbcStrat.strategyID}" />
		                                    </h:commandLink>
		                                    <h:commandLink id="delAction" style="margin: 2px;"
		                                        title="Remove the schedule from the system"
		                                        onclick="confirmDelete( '#{cbcStrat.strategyID}' );"
		                                        actionListener="#{capControlForm.deleteStrat}"
		                                        title=" Delete #{cbcStrat.strategyName}">
		                                        <x:graphicImage value="/editor/images/delete_item.gif" height="15" width="15" border="0" />
		                                        <f:param name="stratID" value="#{cbcStrat.strategyID}" />
		                                    </h:commandLink>
		                                </cti:checkProperty>
									</x:panelGroup>
									
									<h:outputText value="#{cbcStrat.strategyName}" />
								
								</x:panelGrid>
								
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
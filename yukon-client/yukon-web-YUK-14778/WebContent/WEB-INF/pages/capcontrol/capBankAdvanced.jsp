<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.editor.CapBankEditorForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<f:verbatim>
	<script type="text/javascript">
        function checkAdaptiveCount(el) {
            if (el.value <= 0) {
                var message = "Adaptive Count value must be greater than 0.";
                alert ( message );
            }
        }
    </script>
</f:verbatim>

<f:subview id="paoCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">

	<x:htmlTag value="fieldset" styleClass="fieldSet">
		<x:htmlTag value="legend">
			<x:outputText value="Point Assignment" />
		</x:htmlTag>
        
		<h:panelGrid id="cbBody" columns="2" styleClass="gridLayout" rowClasses="gridCell" columnClasses="gridCell,gridCell">
        
			<x:panelGroup>
                <x:div styleClass="scrollerDiv">
            		<h:dataTable id="pointAvailData" var="point" styleClass="scrollerTable" headerClass="scrollerTableHeader"
    					footerClass="scrollerTableHeader" rowClasses="tableRow,altTableRow"
    					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
    					value="#{capBankEditor.unassignedPoints}" rows="25">
    		
            			<h:column>
    						<f:facet name="header">
    						</f:facet>
    						<h:outputText value="#{point.pointId}" />
    					</h:column>
    
    					<h:column>
    						<f:facet name="header">
    							<x:outputText value="Available Points" />
    						</f:facet>
    						<x:outputText value="#{point.pointName}" />
    					</h:column>
    
    					<h:column>
    						<f:facet name="header">
    						</f:facet>
    						<x:commandLink value="Add >>"
    							action="#{capBankEditor.treeSwapAddAction}"
    							rendered="#{not empty capBankEditor.unassignedPoints && capControlForm.editingAuthorized}">
    							<f:param name="swapType" value="CapBankPoint" />
    							<f:param name="id" value="#{point.pointId}" />
    						</x:commandLink>
    					</h:column>
    
    				</h:dataTable>
    
    				<h:panelGrid columns="1" columnClasses="scrollerCentered" styleClass="scrollerPagingTable">
                    
    					<x:dataScroller id="scrollButtons" for="pointAvailData"
    						fastStep="25" pageCountVar="pageCount" pageIndexVar="pageIndex"
    						styleClass="scrollerCentered" paginator="true" paginatorMaxPages="9"
    						paginatorTableClass="paginator"
    						paginatorActiveColumnStyle="font-weight:bold;">
                            
    						<f:facet name="first">
    							<x:graphicImage url="/WebConfig/yukon/Icons/arrow_first.gif" title="First page" />
    						</f:facet>
    						<f:facet name="last">
    							<x:graphicImage url="/WebConfig/yukon/Icons/arrow_last.gif" title="Last page" />
    						</f:facet>
    						<f:facet name="previous">
    							<x:graphicImage url="/WebConfig/yukon/Icons/resultset_previous.gif" title="Previous page" />
    						</f:facet>
    						<f:facet name="next">
    							<x:graphicImage url="/WebConfig/yukon/Icons/resultset_next.gif" title="Next page" />
    						</f:facet>
    						<f:facet name="fastforward">
    							<x:graphicImage url="/WebConfig/yukon/Icons/arrow_fastforward.gif" title="Next set of pages" />
    						</f:facet>
    						<f:facet name="fastrewind">
    							<x:graphicImage url="/WebConfig/yukon/Icons/arrow_rewind.gif" title="Previous set of pages" />
    						</f:facet>
                            
    					</x:dataScroller>
    
    					<x:dataScroller id="scrollDisplay" for="pointAvailData"
    						rowsCountVar="rowsCount" styleClass="scroller"
    						displayedRowsCountVar="displayedRowsCountVar"
    						firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex"
    						pageCountVar="pageCount" pageIndexVar="pageIndex">
    						<h:outputFormat value="{0} points found">
    							<f:param value="#{rowsCount}" />
    						</h:outputFormat>
    						
    						<x:htmlTag value="br"/>
    						
    						<h:outputFormat value="Page {0} / {1}">
    							<f:param value="#{pageIndex}" />
    							<f:param value="#{pageCount}" />
    						</h:outputFormat>
    					</x:dataScroller>
    				</h:panelGrid>
                </x:div>
			</x:panelGroup>

			<x:panelGroup>
				<h:dataTable id="pointAssignData" var="point"
					styleClass="scrollerTable" headerClass="scrollerTableHeader"
					footerClass="scrollerTableHeader" rowClasses="tableRow,altTableRow"
					rendered="#{capControlForm.visibleTabs['CapBank']}"
					value="#{capBankEditor.assignedPoints}"
					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered">
					<h:column>
						<f:facet name="header">
						</f:facet>
						<x:commandLink value="<< Remove"
							action="#{capBankEditor.treeSwapRemoveAction}"
							rendered="#{not empty capBankEditor.assignedPoints && capControlForm.editingAuthorized}">
							<f:param name="swapType" value="CapBankPoint" />
							<f:param name="id" value="#{point.pointId}" />
						</x:commandLink>
					</h:column>
					<h:column>
						<f:facet name="header">
						</f:facet>
						<h:outputText value="#{point.pointId}" />
					</h:column>
					<h:column>
						<f:facet name="header">
							<x:outputText value="Assigned Points" />
						</f:facet>
						<x:outputText value="#{point.pointName}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Order"
								title="Order used for control (Range: 1 to 1000 )" />
						</f:facet>
						<x:inputText value="#{point.displayOrder}" styleClass="char4Label"
							required="true">
							<f:validateLongRange minimum="1" maximum="1000" />
						</x:inputText>

					</h:column>

				</h:dataTable>
			</x:panelGroup>

		</h:panelGrid>

	</x:htmlTag>
	
	<x:htmlTag value="br"/>

	<x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Point Configuration"/></x:htmlTag>
	   
        <x:div rendered="#{not empty capBankEditor.assignedPoints}">
			<h:dataTable id="pointConfig" var="point" value="#{capBankEditor.assignedPoints}">
            
				<h:column>
                    <h:panelGrid columns="5" styleClass="pointConfigSectionTable">
                        
                        <x:outputText value="#{point.pointName}" styleClass="nameValueLabel"/>
                        <x:outputText value=""/>
                        <x:outputText value=""/>
                        <x:outputText value=""/>
                        <x:outputText value=""/>
                        
                        <x:outputText value="Adaptive Count: "/>
                        <x:inputText value="#{point.NINAvg}" onchange="checkAdaptiveCount(this);" />
                        <x:outputText value=""/>
                        <x:outputLabel for="phaseSelect" value="Phase: "/>
                        <x:selectOneMenu id="phaseSelect"
                            value="#{point.phase}">
                            <f:selectItems id="capControlFormPhases" value="#{selLists.phases}" />
                        </x:selectOneMenu>

                        <h:panelGroup>
                            <x:selectBooleanCheckbox id="scanable1" value="#{point.initScan}" disabled="#{!capControlForm.editingAuthorized}" onclick="submit();" />
                            <x:outputText value="Initiate scan on unsolicited" styleClass="padCheckBoxLabel" />
                        </h:panelGroup>
                        <x:outputText value=""/>
                        <x:outputText value=""/>
                        <x:outputText value="Upper Bandwidth: " />
                        <x:inputText value="#{point.upperBandwidth}" disabled="#{!point.overrideFdrLimits}" />
                        
                        <h:panelGroup>
        					<x:selectBooleanCheckbox id="inheritFdr1" value="#{point.overrideFdrLimits}" onclick="submit();" disabled="#{!capControlForm.editingAuthorized}"/>
        					<x:outputText value="Override feeder limits" styleClass="padCheckBoxLabel" />
                        </h:panelGroup>
                        <x:outputText value=""/>
                        <x:outputText value=""/>
                        <x:outputText value="Lower Bandwidth: " />
                        <x:inputText value="#{point.lowerBandwidth}" disabled="#{!point.overrideFdrLimits}" />
    					
                    </h:panelGrid>
                    
                    <x:htmlTag value="hr"/>
                    
				</h:column>

			</h:dataTable>
        </x:div>
	</x:htmlTag>

</f:subview>
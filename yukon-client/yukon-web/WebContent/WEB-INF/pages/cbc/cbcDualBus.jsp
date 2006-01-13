<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:verbatim>
<script type ="text/javascript">
document.body.onload = init;
<!--
function radioButtonClick(radioObj) {
//make sure that all the radio buttons are unchecked
//radio buttons are inside a table, therefore get the name of the table
var SubBusTable = document.getElementById("editorForm:_feederSetup:_paoFeeder:_fdrAvailData");
var inputElements = SubBusTable.getElementsByTagName("INPUT");
for (var i=0; i < inputElements.length; i++) 
	inputElements[i].checked = false;
radioObj.checked = true;
}

function init() {
//all radio buttons are reset
var SubBusTable = document.getElementById("editorForm:_feederSetup:_paoFeeder:_fdrAvailData");
var inputElements = SubBusTable.getElementsByTagName("INPUT");
for (var i=0; i < inputElements.length; i++) 
	inputElements[i].checked = false;
	//set the selected - for now the first one
	inputElements[0].checked = true;

}
-->
</script>
</f:verbatim>
<f:subview id="_feederSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
	<f:subview id="_paoFeeder" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
		<h:panelGrid id="_subBody" columns="2" styleClass="gridLayout" rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
				<h:dataTable id="_fdrAvailData" var="feeder" styleClass="scrollerTable" 
				headerClass="scrollerTableHeader" footerClass="scrollerTableHeader" 
				rowClasses="tableRow,altTableRow" value="#{capControlForm.subBusList}"
					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" rows="25">
					<h:column>
						<f:facet name="header">
						</f:facet>
						<h:outputText value="#{feeder.value}" />
					</h:column>
					
					<h:column>
						<f:facet name="header">
							<x:outputText value="Alt Sub ID" />
						</f:facet>
						<x:outputText value="#{feeder.label}" title="#{feeder.description}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Select" />
						</f:facet>
						
						<h:selectOneRadio required="false" value="#{feeder.label}" onclick="radioButtonClick(this)">
							<f:selectItem itemValue="#{feeder.label}" itemLabel=""/>
						</h:selectOneRadio>
				
					</h:column>
				</h:dataTable>
				<h:panelGrid columns="1" columnClasses="scrollerCentered">
					<x:dataScroller id="scrollButtonsAltSubId" for="_fdrAvailData" fastStep="25" pageCountVar="pageCount" pageIndexVar="pageIndex" styleClass="scroller" paginator="true" paginatorMaxPages="9" paginatorTableClass="paginator"
						paginatorActiveColumnStyle="font-weight:bold;">
						<f:facet name="first">
							<x:graphicImage url="/editor/images/arrow-first.gif" border="1" title="First page" />
						</f:facet>
						<f:facet name="last">
							<x:graphicImage url="/editor/images/arrow-last.gif" border="1" title="Last page" />
						</f:facet>
						<f:facet name="previous">
							<x:graphicImage url="/editor/images/arrow-previous.gif" border="1" title="Previous page" />
						</f:facet>
						<f:facet name="next">
							<x:graphicImage url="/editor/images/arrow-next.gif" border="1" title="Next page" />
						</f:facet>
						<f:facet name="fastforward">
							<x:graphicImage url="/editor/images/arrow-ff.gif" border="1" title="Next set of pages" />
						</f:facet>
						<f:facet name="fastrewind">
							<x:graphicImage url="/editor/images/arrow-fr.gif" border="1" title="Previous set of pages" />
						</f:facet>
					</x:dataScroller>

					<x:dataScroller id="scrollDisplay" for="_fdrAvailData" rowsCountVar="rowsCount" styleClass="scroller" displayedRowsCountVar="displayedRowsCountVar" firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex" pageCountVar="pageCount"
						pageIndexVar="pageIndex">
						<h:outputFormat value="{0} Alternative Subs found">
							<f:param value="#{rowsCount}" />
						</h:outputFormat>
						<f:verbatim>
							<br />
						</f:verbatim>
						<h:outputFormat value="Page {0} / {1}">
							<f:param value="#{pageIndex}" />
							<f:param value="#{pageCount}" />
						</h:outputFormat>
					</x:dataScroller>
				</h:panelGrid>
			</x:panelGroup>
			<x:panelGroup>
				<h:dataTable id="fdrAvailData" var="feeder" styleClass="scrollerTable" headerClass="scrollerTableHeader" footerClass="scrollerTableHeader" rowClasses="tableRow,altTableRow" value="#{capControlForm.unassignedFeeders}"
					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" rows="25">
					<h:column>
						<f:facet name="header">
						</f:facet>
						<h:outputText value="#{feeder.liteID}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Switch Point ID" />
						</f:facet>
						<x:outputText value="#{feeder.paoName}" title="#{feeder.paoDescription}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Select" />
						</f:facet>
						<h:selectOneRadio required="true" value="#{feeder.paoName}">
							<f:selectItem itemValue="" itemLabel="" />
						</h:selectOneRadio>

					</h:column>
				</h:dataTable>
				<h:panelGrid columns="1" columnClasses="scrollerCentered">
					<x:dataScroller id="scrollButtonsSwitchPID" for="fdrAvailData" fastStep="25" pageCountVar="pageCount" pageIndexVar="pageIndex" styleClass="scroller" paginator="true" paginatorMaxPages="9" paginatorTableClass="paginator"
						paginatorActiveColumnStyle="font-weight:bold;">
						<f:facet name="first">
							<x:graphicImage url="/editor/images/arrow-first.gif" border="1" title="First page" />
						</f:facet>
						<f:facet name="last">
							<x:graphicImage url="/editor/images/arrow-last.gif" border="1" title="Last page" />
						</f:facet>
						<f:facet name="previous">
							<x:graphicImage url="/editor/images/arrow-previous.gif" border="1" title="Previous page" />
						</f:facet>
						<f:facet name="next">
							<x:graphicImage url="/editor/images/arrow-next.gif" border="1" title="Next page" />
						</f:facet>
						<f:facet name="fastforward">
							<x:graphicImage url="/editor/images/arrow-ff.gif" border="1" title="Next set of pages" />
						</f:facet>
						<f:facet name="fastrewind">
							<x:graphicImage url="/editor/images/arrow-fr.gif" border="1" title="Previous set of pages" />
						</f:facet>
					</x:dataScroller>

					<x:dataScroller id="scrollDisplaySwitchPID" for="fdrAvailData" rowsCountVar="rowsCount" styleClass="scroller" displayedRowsCountVar="displayedRowsCountVar" firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex" pageCountVar="pageCount"
						pageIndexVar="pageIndex">
						<h:outputFormat value="{0} Switch Points found">
							<f:param value="#{rowsCount}" />
						</h:outputFormat>
						<f:verbatim>
							<br />
						</f:verbatim>
						<h:outputFormat value="Page {0} / {1}">
							<f:param value="#{pageIndex}" />
							<f:param value="#{pageCount}" />
						</h:outputFormat>
					</x:dataScroller>
				</h:panelGrid>
			</x:panelGroup>



		</h:panelGrid>
		<h:selectBooleanCheckbox title="Disable Dual Bus" value="true" />
		<h:outputText value="Disable Dual Bus" />
	</f:subview>



</f:subview>

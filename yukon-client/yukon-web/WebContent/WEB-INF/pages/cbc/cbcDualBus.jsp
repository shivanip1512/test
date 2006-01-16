<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:verbatim>
	<script type="text/javascript">
document.body.onload = init;
<!--
function radioButtonClick(type, radioObj) {
//make sure that all the radio buttons are unchecked
//radio buttons are inside a table, therefore get the name of the table
var sb_table_id  		= "editorForm:altDualBusSetup:altDualBus:altSubBusData";
var switch_p_table_id  	= "editorForm:altDualBusSetup:altDualBus:altSwitchPointData";

var switch_p_hidden_id 	= "editorForm:altDualBusSetup:selectedSwitchPoint";
var sb_hidden_id 		= "editorForm:altDualBusSetup:selectedSubBus";

var			table_el = null;
var			hidden_el = null;
switch (type) {
		//init the elements based on type of call
		case 'SubBus':			
			table_el = document.getElementById(sb_table_id);
			hidden_el = document.getElementById(sb_hidden_id);
		break;
		case 'SwitchPoint':
			table_el = document.getElementById(switch_p_table_id);
			hidden_el = document.getElementById(switch_p_hidden_id);
		break;	
		}
		var inputElements = table_el.getElementsByTagName("input");
		for (var i=0; i < inputElements.length; i++) {
			inputElements[i].checked = false;
			}
		radioObj.checked = true;
		hidden_el.value = radioObj.value;

	
}

function init() {
//all radio buttons are reset
var sb_table_id  		= "editorForm:altDualBusSetup:altDualBus:altSubBusData";

var switch_p_table_id  	= "editorForm:altDualBusSetup:altDualBus:altSwitchPointData";

var switch_p_hidden_id 	= "editorForm:altDualBusSetup:selectedSwitchPoint";
var sb_hidden_id 		= "editorForm:altDualBusSetup:selectedSubBus";

var tables = [sb_table_id,  switch_p_table_id];
var hidden = [switch_p_hidden_id, sb_hidden_id]; 

for(var i=0; i < tables.length; i++){
	var currenttable =   document.getElementById(tables[i]);	
	var current_hidden = document.getElementById(hidden[i]);
	var inputElements = currenttable.getElementsByTagName("input");
	for (var j=0; j < inputElements.length; j++) {
		curr_radio_button = inputElements[j];
		if (curr_radio_button.value == current_hidden.value)
			curr_radio_button.checked = true;
		}
	
	}
document.getElementById("editorForm:altDualBusSetup:altDualBus:subBody").scrollIntoView(true);	
}
-->
</script>
</f:verbatim>
<f:subview id="altDualBusSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
	<f:subview id="altDualBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
		<h:panelGrid id="subBody" columns="2" styleClass="gridLayout" rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
				<h:dataTable id="altSubBusData" var="subBus" styleClass="scrollerTable" headerClass="scrollerTableHeader" footerClass="scrollerTableHeader" rowClasses="tableRow,altTableRow" value="#{capControlForm.subBusList}"
					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" rows="25">
					<h:column>
						<f:facet name="header">
							<x:outputText value="ID" />
						</f:facet>
						<h:outputText value="#{subBus.value}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Alt Sub Name" />
						</f:facet>
						<x:outputText value="#{subBus.label}" title="#{subBus.description}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Select" />
						</f:facet>

						<h:selectOneRadio required="false" value="#{capControlForm.selectedSubBus}" onclick="radioButtonClick('SubBus', this)">
							<f:selectItem itemValue="#{subBus.label}" itemLabel="" />
						</h:selectOneRadio>
					</h:column>
				</h:dataTable>
				<h:panelGrid columns="1" columnClasses="scrollerCentered">
					<x:dataScroller id="scrollButtonsAltSubId" for="altSubBusData" fastStep="25" pageCountVar="pageCount" pageIndexVar="pageIndex" styleClass="scroller" paginator="true" paginatorMaxPages="9" paginatorTableClass="paginator"
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

					<x:dataScroller id="scrollDisplay" for="altSubBusData" rowsCountVar="rowsCount" styleClass="scroller" displayedRowsCountVar="displayedRowsCountVar" firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex" pageCountVar="pageCount"
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
				<h:dataTable id="altSwitchPointData" var="switchPoint" styleClass="scrollerTable" headerClass="scrollerTableHeader" footerClass="scrollerTableHeader" rowClasses="tableRow,altTableRow" value="#{capControlForm.switchPointList}"
					columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" rows="25">
					<h:column>
						<f:facet name="header">
							<x:outputText value="ID" />
						</f:facet>
						<h:outputText value="#{switchPoint.value}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Alt Switch Point ID" />
						</f:facet>
						<x:outputText value="#{switchPoint.label}" title="#{switchPoint.description}" />
					</h:column>

					<h:column>
						<f:facet name="header">
							<x:outputText value="Select" />
						</f:facet>

						<h:selectOneRadio required="false" value="#{capControlForm.selectedSwitchPoint}" onclick="radioButtonClick('SwitchPoint', this)">
							<f:selectItem itemValue="#{switchPoint.label}" itemLabel="" />
						</h:selectOneRadio>
					</h:column>
				</h:dataTable>
				<h:panelGrid columns="1" columnClasses="scrollerCentered">
					<x:dataScroller id="scrollButtonsAltPointId" for="altSwitchPointData" fastStep="25" pageCountVar="pageCount" pageIndexVar="pageIndex" styleClass="scroller" paginator="true" paginatorMaxPages="9" paginatorTableClass="paginator"
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

					<x:dataScroller id="scrollPointsDisplay" for="altSwitchPointData" rowsCountVar="rowsCount" styleClass="scroller" displayedRowsCountVar="displayedRowsCountVar" firstRowIndexVar="firstRowIndex" lastRowIndexVar="lastRowIndex" pageCountVar="pageCount"
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
		<h:selectBooleanCheckbox id="disableDualBus" title="Disable Dual Bus" value="#{capControlForm.disableDualBus}" />
		<h:outputText value="Disable Dual Bus" />
	</f:subview>
	<h:inputHidden id="selectedSubBus" value="#{capControlForm.selectedSubBus}" />
	<h:inputHidden id="selectedSwitchPoint" value="#{capControlForm.selectedSwitchPoint}" />
</f:subview>

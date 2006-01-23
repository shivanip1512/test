<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:verbatim>
	<script type="text/javascript">

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
//Event.observe(window, 'load', function() { new CtiNonScrollTable('editorForm:altDualBusSetup:altDualBus:altSubBusData','AltSubHTable');    });
//Event.observe(window, 'load', function() { new CtiNonScrollTable('editorForm:altDualBusSetup:altDualBus:altSwitchPointData','SwitchPointHTable');    });

</script>
</f:verbatim>
<f:subview id="altDualBusSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
	<f:subview id="altDualBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
		<f:verbatim>
			<br />
			<fieldset>
				<legend>
					Dual Bus
				</legend>
		</f:verbatim>

		<h:selectBooleanCheckbox styleClass="lAlign" id="enableDualBus" 
		title="Disable Dual Bus" 
		value="#{capControlForm.enableDualBus}" >
			<h:outputText value="Enable Dual Bus" />
		</h:selectBooleanCheckbox>
		<h:panelGrid  id="subBody" columns="2" 
		styleClass="gridLayout" 
		rowClasses="gridCellSmall" 
		columnClasses="gridColumn">
			<x:panelGroup>
				<h:outputText styleClass="tableHeader" value="#{capControlForm.selectedSubBusFormatString}"/>
				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Alternative Substation Bus
						</legend>
				</f:verbatim>
				<x:div styleClass="scrollSmallWidthSet" >
					<x:dataList var="item" value="#{capControlForm.subBusList}" layout="unorderedList" styleClass="ul">
						
							<x:panelGroup>
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.altSubPAOId == item.liteID}" />
								<x:commandLink id="ptLink" value="#{item.paoName}" actionListener="#{capControlForm.subBusPAOsClick}">
									<f:param name="ptID" value="#{item.liteID}" />
								</x:commandLink>
							</x:panelGroup>
						
					</x:dataList>
				</x:div>
			</x:panelGroup>
			<x:panelGroup>
				<h:outputText styleClass="tableHeader" value="#{capControlForm.selectedTwoWayPointsFormatString}"/>
				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Switch Point
						</legend>
				</f:verbatim>
				
				<x:div id="TwoWayPointScrollableDiv" styleClass="scrollSmallWidthSet">

					<x:tree2 id="TwoWayPointsPaoListTree" value="#{capControlForm.switchPointList}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false">

						<f:facet name="root">
							<x:panelGroup>
								<x:outputText id="rootLink" value="#{node.description}" />
							</x:panelGroup>
						</f:facet>
						<f:facet name="paos">
							<x:panelGroup>
								<x:outputText id="paChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
							</x:panelGroup>
						</f:facet>
						<f:facet name="points">
							<x:panelGroup>
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.switchPointID == node.identifier}" />

								<x:commandLink id="ptLink" value="#{node.description}" actionListener="#{capControlForm.twoWayPtsTeeClick}">
									<f:param name="ptID" value="#{node.identifier}" />
								</x:commandLink>
							</x:panelGroup>
						</f:facet>
					</x:tree2>
				</x:div>
				<f:verbatim>
					</fieldset>
				</f:verbatim>
			</x:panelGroup>



		</h:panelGrid>

	</f:subview>
	<h:inputHidden id="selectedSubBus" value="#{capControlForm.selectedSubBus}" />
	<h:inputHidden id="selectedSwitchPoint" value="#{capControlForm.selectedSwitchPoint}" />
</f:subview>

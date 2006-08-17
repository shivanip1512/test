
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<script type="text/javascript">

addSmartScrolling('currentAltSubDivOffset', 'AltSubBusScrollableDiv', 'selectedSubBus', 'AltSubBusList');
addSmartScrolling('currentTwoWayPointDivOffset', 'TwoWayPointScrollableDiv', 'selectedSwitchPoint','editorForm:altDualBusSetup:altDualBus:TwoWayPointsPaoListTree');


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

		<x:selectBooleanCheckbox forceId="true"  styleClass="lAlign" id="enableDualBus" title="Disable Dual Bus" 
		value="#{capControlForm.enableDualBus}" onclick="submit();">
			<h:outputText value="Enable Dual Bus" />
		</x:selectBooleanCheckbox>
	
		<x:panelGrid  forceId="true" id="subBody" columns="2" styleClass="gridLayout" rowClasses="gridCellSmall" columnClasses="gridColumn">
			<x:panelGroup forceId="true" id="altSubBusPanel">
	
				<h:outputText styleClass="tableHeader" value="Selected Alternate SubBus: " rendered="#{capControlForm.enableDualBus}"/>
				<x:commandLink actionListener="#{capControlForm.selectedAltSubBusClick}" rendered="#{capControlForm.enableDualBus}">
				<h:outputText value="#{capControlForm.selectedSubBusFormatString}" rendered="#{capControlForm.enableDualBus}"/>
				</x:commandLink>
			
				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Alternate Substation Bus
						</legend>
				</f:verbatim>
				<x:div forceId="true" id = "AltSubBusScrollableDiv" styleClass="scrollSmallWidthSet" >

				
					<x:dataList forceId="true" id="AltSubBusList" var="item" value="#{capControlForm.subBusList}" layout="unorderedList" styleClass="listWithNoBullets" rendered="#{capControlForm.enableDualBus}">
					
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

				<h:outputText styleClass="tableHeader" value="Selected Disable or Switch Point:" />
				<x:commandLink actionListener="#{capControlForm.selectedTwoWayPointClick}" rendered="#{capControlForm.switchPointEnabled}">
				<h:outputText value="#{capControlForm.selectedTwoWayPointsFormatString}" rendered="#{capControlForm.switchPointEnabled}"/>
				</x:commandLink>
				<h:outputText styleClass="medLabel" value="(none)" rendered="#{!capControlForm.switchPointEnabled}"/>

				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Switch Point
						</legend>
				</f:verbatim>
				<x:div forceId="true" id="TwoWayPointScrollableDiv" styleClass="scrollSmallWidthSet">

					<x:tree2 binding="#{capControlForm.dualBusSwitchPointTree}" id="TwoWayPointsPaoListTree" value="#{capControlForm.switchPointList}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false">

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
						<f:facet name="sublevels">
							<x:panelGroup>
								<x:outputText id="subLvlCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
							</x:panelGroup>
						</f:facet>						
						<f:facet name="points">
							<x:panelGroup>
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.switchPointID == node.identifier}" />

								<x:commandLink  id="ptLink" value="#{node.description}" actionListener="#{capControlForm.twoWayPtsTeeClick}">
									<f:param name="ptID" value="#{node.identifier}" />
								</x:commandLink>
							</x:panelGroup>
						</f:facet>
					</x:tree2>

				</x:div>
				
				<x:commandLink id="switchPoint_set_none" title="Do not use a switch point" value="No Switch Point" actionListener="#{capControlForm.twoWayPtsTeeClick}" styleClass="medStaticLabel">
					<f:param name="ptID" value="0" />
				</x:commandLink>
				<f:verbatim>
					</fieldset>
				</f:verbatim>
			</x:panelGroup>



		</x:panelGrid>

	</f:subview>

	<x:inputHidden forceId="true" id="selectedSubBus" value="#{capControlForm.offsetMap['selectedSubBus']}" />
	<x:inputHidden forceId="true" id="selectedSwitchPoint" value="#{capControlForm.offsetMap['selectedSwitchPoint']}"/>
	<x:inputHidden forceId="true" id="currentTwoWayPointDivOffset" value="#{capControlForm.offsetMap['currentTwoWayPointDivOffset']}" />
	<x:inputHidden forceId="true" id="currentAltSubDivOffset" value="#{capControlForm.offsetMap['currentAltSubDivOffset']}" />

</f:subview>


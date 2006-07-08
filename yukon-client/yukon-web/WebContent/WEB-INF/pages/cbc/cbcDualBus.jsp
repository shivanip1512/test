<%@ page pageEncoding="UTF-8" import="java.util.*"%>
<%@ page import="org.ajaxanywhere.*"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net" prefix="aa" %>
<%

    if (AAUtils.isAjaxRequest(request)){
        AAUtils.addZonesToRefresh(request, "en_DualBus;selected_alt_sub;aaSubList;switchPointTree;selected_sw_pt;dual_hiden");
    }
%>
<f:verbatim>
<script type="text/JavaScript" src="../../../JavaScript/aa.js"></script>
<script>
	ajaxAnywhere.getZonesToReload = function(url, submitButton) {
		
		if ( $("aazone.aaSubList") )
			return "aaSubList";
		if ( $("aazone.en_DualBus") )
			return "en_DualBus";
		if ( $("aazone.selected_alt_sub") )
			return "selected_alt_sub";
		if ( $("aazone.switchPointTree") )
			return "switchPointTree";
		if ( $("aazone.selected_sw_pt") )
			return "selected_sw_pt";
		if ( $("aazone.dual_hiden") )
			return "dual_hiden";
			
	}
	
</script>	
<script type="text/javascript">

addSmartScrolling('currentAltSubDivOffset', 'AltSubBusScrollableDiv', 'selectedSubBus', 'AltSubBusList');
addSmartScrolling('currentTwoWayPointDivOffset', 'TwoWayPointScrollableDiv', 'selectedSwitchPoint','editorForm:altDualBusSetup:altDualBus:TwoWayPointsPaoListTree');

//function that works around aa:zoneJSF inability to preserve state of the x:div element
//basically if the zoning is split like so:
//aa:zoneJSF
//div rendered:#{capcontrolform.enableDualBus}
//datalist rendered:#{capcontrolform.enableDualBus}
//...
//datalist
//div
//aa:zoneJSF
//then it is impossible to store the scrolling offsets in the backing bean because div rendered after the request is done looses
//it's offset basically the state of the x:div tag is lost when it is inside aa:zoneJSF tag
function set_div_visible(div_id, display) {
	var div_elem = document.getElementById (div_id);
	if (!display)
		div_elem.style.display = 'none';
	else
		div_elem.style.display = 'block';
}
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
		<aa:zoneJSF id="en_DualBus">
		<x:selectBooleanCheckbox forceId="true"  styleClass="lAlign" id="enableDualBus" title="Disable Dual Bus" 
		value="#{capControlForm.enableDualBus}" onclick="submit();set_div_visible('AltSubBusScrollableDiv', this.checked)">
			<h:outputText value="Enable Dual Bus" />
		</x:selectBooleanCheckbox>
		</aa:zoneJSF>
		<x:panelGrid  forceId="true" id="subBody" columns="2" styleClass="gridLayout" rowClasses="gridCellSmall" columnClasses="gridColumn">
			<x:panelGroup forceId="true" id="altSubBusPanel">
				<aa:zoneJSF id="selected_alt_sub">				
				<h:outputText styleClass="tableHeader" value="Selected Alternate SubBus: " rendered="#{capControlForm.enableDualBus}"/>
				<x:commandLink actionListener="#{capControlForm.selectedAltSubBusClick}" rendered="#{capControlForm.enableDualBus}">
				<h:outputText value="#{capControlForm.selectedSubBusFormatString}" rendered="#{capControlForm.enableDualBus}"/>
				</x:commandLink>
				</aa:zoneJSF>
				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Alternate Substation Bus
						</legend>
				</f:verbatim>
				<x:div forceId="true" id = "AltSubBusScrollableDiv" styleClass="scrollSmallWidthSet" >
					<aa:zoneJSF id="aaSubList">
				
					<x:dataList forceId="true" id="AltSubBusList" var="item" value="#{capControlForm.subBusList}" layout="unorderedList" styleClass="listWithNoBullets" rendered="#{capControlForm.enableDualBus}">
					
						<x:panelGroup>
							<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.altSubPAOId == item.liteID}" />
							<x:commandLink id="ptLink" value="#{item.paoName}" actionListener="#{capControlForm.subBusPAOsClick}">
								<f:param name="ptID" value="#{item.liteID}" />
							</x:commandLink>
						</x:panelGroup>
					
					</x:dataList>
				</aa:zoneJSF>	
				</x:div>
			</x:panelGroup>
			<x:panelGroup>
				<aa:zoneJSF id="selected_sw_pt">
				<h:outputText styleClass="tableHeader" value="Selected Disable or Switch Point:" />
				<x:commandLink actionListener="#{capControlForm.selectedTwoWayPointClick}" rendered="#{capControlForm.switchPointEnabled}">
				<h:outputText value="#{capControlForm.selectedTwoWayPointsFormatString}" rendered="#{capControlForm.switchPointEnabled}"/>
				</x:commandLink>
				<h:outputText styleClass="medLabel" value="(none)" rendered="#{!capControlForm.switchPointEnabled}"/>
				</aa:zoneJSF>
				<f:verbatim>
					<br />
					<fieldset>
						<legend>
							Switch Point
						</legend>
				</f:verbatim>
				<x:div forceId="true" id="TwoWayPointScrollableDiv" styleClass="scrollSmallWidthSet">
					<aa:zoneJSF id="switchPointTree">
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
				</aa:zoneJSF>
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
<aa:zoneJSF id="dual_hiden">
	<x:inputHidden forceId="true" id="selectedSubBus" value="#{capControlForm.offsetMap['selectedSubBus']}" />
	<x:inputHidden forceId="true" id="selectedSwitchPoint" value="#{capControlForm.offsetMap['selectedSwitchPoint']}"/>
	<x:inputHidden forceId="true" id="currentTwoWayPointDivOffset" value="#{capControlForm.offsetMap['currentTwoWayPointDivOffset']}" />
	<x:inputHidden forceId="true" id="currentAltSubDivOffset" value="#{capControlForm.offsetMap['currentAltSubDivOffset']}" />
</aa:zoneJSF>
</f:subview>


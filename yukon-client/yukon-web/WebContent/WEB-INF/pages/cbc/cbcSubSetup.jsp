<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:verbatim>
<script type="text/JavaScript">
<!--
window.onload = init;

//function that will set the scroll bar offset value to 0 on start up
function init() {

var div_var    = document.getElementById("editorForm:subSetup:paoSubBus:VARscrollable_div"); 
var hidden_var = document.getElementById("editorForm:subSetup:paoSubBus:VARscrollOffsetTop");

var div_watt    = document.getElementById("editorForm:subSetup:paoSubBus:WATTscrollable_div"); 
var hidden_watt = document.getElementById("editorForm:subSetup:paoSubBus:WATTscrollOffsetTop");

var div_volt    = document.getElementById("editorForm:subSetup:paoSubBus:VOLTscrollable_div"); 
var hidden_volt = document.getElementById("editorForm:subSetup:paoSubBus:VOLTscrollOffsetTop");


if (hidden_var.value != 0)
	setScrollBarOffset(div_var, hidden_var);
	
if (hidden_watt.value != 0)
	setScrollBarOffset(div_watt, hidden_watt);
	
if (hidden_volt.value != 0)
	setScrollBarOffset(div_volt, hidden_volt);	
	
div_var.onscroll = 		function () {setHiddenOffsetValue(div_var, hidden_var); };
div_watt.onscroll = 	function () {setHiddenOffsetValue(div_watt, hidden_watt); };
div_volt.onscroll = 	function () {setHiddenOffsetValue(div_volt, hidden_volt); };

//Event.observe(div_var, 'onscroll', function() { new CtiPositionAwareScrollbar(div_var, hidden_var);    }, false);
//Event.observe(div_watt, 'onscroll', function() { new CtiPositionAwareScrollbar(div_watt, hidden_watt);    }, false);
//Event.observe(div_volt, 'onscroll', function() { new CtiPositionAwareScrollbar(div_volt, hidden_volt);    }, false);


//doesn't really belong here but needs to be untill
//figure out a way to call functions from a file
//focus users attention on the table data scroller element
document.getElementById("editorForm:altDualBusSetup:altDualBus:scrollButtonsAltSubIdfirst").scrollIntoView(true);	

}	
//helper functions for initSubBus()
function setHiddenOffsetValue(div_el, hidden_el) {

hidden_el.value = div_el.scrollTop;

}

function setScrollBarOffset(div_el, hidden_el) {
div_el.scrollIntoView(true);
div_el.scrollTop = hidden_el.value;

}
//-->
</script>




</f:verbatim>

<f:subview id="subSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">

	<f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
		<h:panelGrid id="subBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">

			<h:column>
				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							SubBus Info
						</legend>
				</f:verbatim>
				<x:outputLabel for="subAreaName" value="#{capControlForm.PAODescLabel}: " title="Physical location of the Substation Bus" rendered="#{!empty capControlForm.PAODescLabel}" />
				<x:inputText id="subAreaName" value="#{capControlForm.PAOBase.geoAreaName}" required="true" maxlength="60" styleClass="char32Label" rendered="#{!empty capControlForm.PAODescLabel}" />

				<f:verbatim>
					<br />
				</f:verbatim>
				<x:outputLabel for="subMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
				<x:inputText id="subMapLocID" value="#{capControlForm.PAOBase.capControlSubstationBus.mapLocationID}" required="true" maxlength="64" styleClass="char32Label" />

				<f:verbatim>
					</fieldset>
				</f:verbatim>


				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							VAR Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subVarPoint" value="Selected Point: " title="Data point used for the current kVAR value" styleClass="medStaticLabel" />
				<x:outputText id="subVarPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID != 0}"
					value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID].pointName}"
					styleClass="medLabel" />
				<x:outputText id="subVarPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID == 0}" value="(none)" styleClass="medLabel" />



				<x:div id="VARscrollable_div" styleClass="scrollSmall">
							
					<x:tree2 id="subVarPaoListTree" value="#{capControlForm.varTreeData}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false" >
					
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
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID == node.identifier}" />
								
								<x:commandLink id="ptLink" value="#{node.description}" actionListener="#{capControlForm.varPtTeeClick}">
									<f:param name="ptID" value="#{node.identifier}" />
								</x:commandLink>
							</x:panelGroup>
						</f:facet>

					</x:tree2>
				</x:div>


				<x:commandLink id="subVarPoint_setNone" title="Do not use a point for the VAR value" value="No VAR Point" actionListener="#{capControlForm.varPtTeeClick}" styleClass="medStaticLabel">
					<f:param name="ptID" value="0" />
				</x:commandLink>
				<f:verbatim>
					</fieldset>
				</f:verbatim>
			</h:column>



			<h:column>
				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							Watt Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subWattPoint" value="Selected Point: " title="Data point used for the current WATT value" styleClass="medStaticLabel" />
				<x:outputText id="subWattPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID != 0}" styleClass="medStaticLabel"
					value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID].pointName}"
					styleClass="medLabel" />
				<x:outputText id="subWattPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID == 0}" value="(none)" styleClass="medLabel" />

				<x:div id="WATTscrollable_div" styleClass="scrollSmall" >
					<x:tree2 id="subWattListTree" value="#{capControlForm.wattTreeData}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false">

						<f:facet name="root">
							<x:panelGroup>
								<x:outputText id="wRootLink" value="#{node.description}" />
							</x:panelGroup>
						</f:facet>

						<f:facet name="paos">
							<x:panelGroup>
								<x:outputText id="wPAChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
							</x:panelGroup>
						</f:facet>

						<f:facet name="points">
							<x:panelGroup>
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID == node.identifier}" />
								<x:commandLink id="wPtLink" value="#{node.description}" actionListener="#{capControlForm.wattPtTeeClick}">
									<f:param name="ptID" value="#{node.identifier}" />
								</x:commandLink>

							</x:panelGroup>
						</f:facet>

					</x:tree2>
				</x:div>
				<h:inputHidden id="VARscrollOffsetTop" value="#{capControlForm.VARscrollOffsetTop}"/>
				<h:inputHidden id="WATTscrollOffsetTop" value="#{capControlForm.WATTscrollOffsetTop}"/>		
				<h:inputHidden id="VOLTscrollOffsetTop" value="#{capControlForm.VOLTscrollOffsetTop}"/>
				
				<h:inputHidden id="varTreeExp" value="0" />
				<x:commandLink id="subWattPoint_setNone" title="Do not use a point for the Watt value" styleClass="medStaticLabel" value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
					<f:param name="ptID" value="0" />
				</x:commandLink>

				<f:verbatim>
					</fieldset>
				</f:verbatim>


				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							Volt Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subVoltPoint" value="Selected Point: " title="Data point used for the current Volt value" styleClass="medStaticLabel" />
				<x:outputText id="subVoltPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID != 0}" styleClass="medStaticLabel"
					value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID].paobjectID].paoName}
        	/ #{dbCache.allPointsMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID].pointName}"
					styleClass="medLabel" />
				<x:outputText id="subVoltPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID == 0}" value="(none)" styleClass="medLabel" />

				<x:div id="VOLTscrollable_div" styleClass="scrollSmall">
					<x:tree2 id="subVoltPaoListTree" value="#{capControlForm.voltTreeData}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false">

						<f:facet name="root">
							<x:panelGroup>
								<x:outputText id="vltRootLink" value="#{node.description}" />
							</x:panelGroup>
						</f:facet>

						<f:facet name="paos">
							<x:panelGroup>
								<x:outputText id="vltChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
							</x:panelGroup>
						</f:facet>

						<f:facet name="points">
							<x:panelGroup>
								<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID == node.identifier}" />
								<x:commandLink id="ptLink" value="#{node.description}" actionListener="#{capControlForm.voltPtTeeClick}">
									<f:param name="ptID" value="#{node.identifier}" />

								</x:commandLink>

							</x:panelGroup>
						</f:facet>

					</x:tree2>
				</x:div>
				<x:commandLink id="subVoltPoint_setNone" title="Do not use a point for the Volt value" styleClass="medStaticLabel" value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}">
					<f:param name="ptID" value="0" />
				</x:commandLink>

				<f:verbatim>
					</fieldset>
				</f:verbatim>

			</h:column>

		</h:panelGrid>

	</f:subview>

</f:subview>



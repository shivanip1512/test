
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:verbatim>
<script type="text/javascript">

addSmartScrolling('currentAltSubDivOffset', 'AltSubBusScrollableDiv', 'selectedSubBus', 'AltSubBusList');
var switchPointPicker = new PointPicker('switch_point','com.cannontech.common.search.criteria.CCTwoStatePointCriteria','pointName:switchPointName','switchPointPicker','');

</script>
</f:verbatim>
<f:subview id="altDualBusSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
	<f:subview id="altDualBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
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


           <f:verbatim>
                    <br />
                    <fieldset>
                        <legend>
                        Switch Point
                        </legend>
                </f:verbatim>

                 <x:div id="switchPointDiv" forceId="true">
                 <x:inputHidden id="switch_point" forceId="true" 
                 value="#{capControlForm.PAOBase.capControlSubstationBus.switchPointID}" />      
                 <x:outputLabel for="switchPointName" value="Selected Point: " 
                 title="Data Point used for the current VAR value" styleClass="medStaticLabel"/>
                 <x:outputText id="switchPointName" forceId="true" 
                 value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.switchPointID]}" /> 
                <f:verbatim>
                    <br/>
                </f:verbatim>
                    <h:outputLink  value="javascript:switchPointPicker.showPicker()" >
                       <h:outputText value="Select point..."/>
                    </h:outputLink>
                </x:div>
                <f:verbatim>
                    <br/>
                </f:verbatim>
                 <x:commandLink id="switchPoint_setNone" title="Do not a switch point" 
                 styleClass="medStaticLabel"
                    value="No Switch Point" actionListener="#{capControlForm.twoWayPtsTeeClick}">
                        <f:param name="ptID" value="0"/>
                  </x:commandLink>
                
                <f:verbatim>
                    </fieldset>
                </f:verbatim>
			</x:panelGroup>



		</x:panelGrid>

	</f:subview>

	<x:inputHidden forceId="true" id="selectedSubBus" value="#{capControlForm.offsetMap['selectedSubBus']}" />
	<x:inputHidden forceId="true" id="currentAltSubDivOffset" value="#{capControlForm.offsetMap['currentAltSubDivOffset']}" />

</f:subview>


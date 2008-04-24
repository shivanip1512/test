<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:verbatim>
<script type="text/javascript">

addSmartScrolling('currentAltSubDivOffset', 'AltSubBusScrollableDiv', 'selectedSubBus', 'AltSubBusList');
var switchPointPicker = new PointPicker(
    'switch_point',
    'com.cannontech.common.search.criteria.CCTwoStatePointCriteria',
    'pointName:switchPointName;deviceName:switchPointDevice',
    'switchPointPicker',
    '', 
    Prototype.emptyFunction,
    Prototype.emptyFunction);

</script>
</f:verbatim>
<f:subview id="altDualBusSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
    <f:subview id="altDualBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
        <x:htmlTag value="fieldSet" styleClass="fieldSet">
            <x:htmlTag value="legend"><x:outputText value="Dual Bus Setup"/></x:htmlTag>

			<x:selectBooleanCheckbox forceId="true"  styleClass="lAlign" id="enableDualBus" title="Disable Dual Bus" 
                value="#{capControlForm.enableDualBus}" onclick="submit();">
				<h:outputText value="Enable Dual Bus" />
			</x:selectBooleanCheckbox>
		
			<x:panelGrid  forceId="true" id="subBody" columns="2" styleClass="gridLayout" rowClasses="gridCellSmall" columnClasses="gridCell, gridCell">
				<x:panelGroup forceId="true" id="altSubBusPanel">
		
					<h:outputText styleClass="tableHeader" value="Selected Alternate SubBus: " rendered="#{capControlForm.enableDualBus}"/>
					<x:commandLink actionListener="#{capControlForm.selectedAltSubBusClick}" rendered="#{capControlForm.enableDualBus}">
                        <h:outputText value="#{capControlForm.selectedSubBusFormatString}" rendered="#{capControlForm.enableDualBus}"/>
					</x:commandLink>
				
					<x:htmlTag value="fieldSet" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Alternate Substation Bus"/></x:htmlTag>
						<x:div forceId="true" id = "AltSubBusScrollableDiv" styleClass="scrollSmall" >
							<x:dataList forceId="true" id="AltSubBusList" var="item" value="#{capControlForm.subBusList}" layout="unorderedList" styleClass="listWithNoBullets" rendered="#{capControlForm.enableDualBus}">
							
								<x:panelGroup>
									<x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2" rendered="#{capControlForm.PAOBase.capControlSubstationBus.altSubPAOId == item.liteID}" />
									<x:commandLink id="ptLink" value="#{item.paoName}" actionListener="#{capControlForm.subBusPAOsClick}">
										<f:param name="ptID" value="#{item.liteID}" />
									</x:commandLink>
								</x:panelGroup>
							
							</x:dataList>
		
						</x:div>
					</x:htmlTag>
				</x:panelGroup>
				<x:panelGroup>
                    <x:htmlTag value="fieldSet" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Switch Point"/></x:htmlTag>
	
                        <x:div id="switchPointDiv" forceId="true">
                            <x:inputHidden id="switch_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.switchPointID}" />      
                            <x:outputLabel for="switchPointName" value="Selected Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel"/>
                            <x:outputText id="switchPointDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.switchPointID]}"/> 
                            <x:outputText id="switchPointDeviceSeperator" forceId="true" value=" : " />
                            <x:outputText id="switchPointName" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.switchPointID]}" /> 
                            
                            <x:htmlTag value="br"/>
		                    <h:outputLink  value="javascript:switchPointPicker.showPicker()" >
                                <h:outputText value="Select point..."/>
		                    </h:outputLink>
		                </x:div>
		                <x:htmlTag value="br"/>
		                <x:htmlTag value="br"/>
                        <x:commandLink id="switchPoint_setNone" title="Do not use a switch point." styleClass="medStaticLabel" value="No Switch Point" actionListener="#{capControlForm.twoWayPtsTeeClick}">
                            <f:param name="ptID" value="0"/>
                        </x:commandLink>
                    </x:htmlTag>
				</x:panelGroup>
			</x:panelGrid>
	    </x:htmlTag>
	</f:subview>
	<x:inputHidden forceId="true" id="selectedSubBus" value="#{capControlForm.offsetMap['selectedSubBus']}" />
	<x:inputHidden forceId="true" id="currentAltSubDivOffset" value="#{capControlForm.offsetMap['currentAltSubDivOffset']}" />
</f:subview>
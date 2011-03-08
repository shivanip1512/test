<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        var substationBusDisableBusPointPicker = new Picker('voltReductionPointPicker', '', 'substationBusDisableBusPointPicker', 'pointName:disableBusPoint;deviceName:disableBusDevice');
        substationBusDisableBusPointPicker.destinationFieldId = 'subDisablePointValue';
        
        var substationBusVoltReductionPointPicker = new Picker('voltReductionPointPicker', '', 'substationBusVoltReductionPointPicker', 'pointName:substationBusVoltReductionPoint;deviceName:substationBusDevice');
        substationBusVoltReductionPointPicker.destinationFieldId = 'subReductionPointValue';
        
        var sub_Var_PointPicker = new Picker('varPointPicker', '', 'sub_Var_PointPicker', 'pointName:sub_Var_Point;deviceName:sub_Var_Device');
        sub_Var_PointPicker.destinationFieldId = 'var_point';
        
        var sub_Var_PhaseB_PointPicker = new Picker('varPointPicker', '', 'sub_Var_PhaseB_PointPicker', 'pointName:sub_Var_PhaseB_Point;deviceName:sub_Var_PhaseB_Device');
        sub_Var_PhaseB_PointPicker.destinationFieldId = 'var_phase_b_point';
        
        var sub_Var_PhaseC_PointPicker = new Picker('varPointPicker', '', 'sub_Var_PhaseC_PointPicker', 'pointName:sub_Var_PhaseC_Point;deviceName:sub_Var_PhaseC_Device');
        sub_Var_PhaseC_PointPicker.destinationFieldId = 'var_phase_c_point';
        
        var subWattPointPicker = new Picker('wattPointPicker', '', 'subWattPointPicker', 'pointName:subWattPoint;deviceName:subWattDevice');
        subWattPointPicker.destinationFieldId = 'watt_point';
        
        var subVoltPointPicker = new Picker('voltPointPicker', '', 'subVoltPointPicker', 'pointName:subVoltPoint;deviceName:subVoltDevice');
        subVoltPointPicker.destinationFieldId = 'volt_point';
    </script>
</f:verbatim>

<f:subview id="subBusSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
        <x:panelGrid id="subBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
            <h:column>
                <x:htmlTag value="fieldSet" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Subtation Bus Info"/></x:htmlTag>

					<x:panelGrid columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">

						<x:outputLabel for="subAreaName"
							value="#{capControlForm.PAODescLabel}: "
							title="Physical location of the Substation Bus"
							rendered="#{!empty capControlForm.PAODescLabel}" />

						<x:inputText id="subAreaName"
							value="#{capControlForm.PAOBase.geoAreaName}" required="true"
							maxlength="60" styleClass="char32Label"
							rendered="#{!empty capControlForm.PAODescLabel}" />

						<x:outputLabel for="subMapLocID" value="Map Location ID: "
							title="Mapping code/string used for third-party systems" />

						<x:inputText id="subMapLocID"
							value="#{capControlForm.PAOBase.capControlSubstationBus.mapLocationID}"
							required="true" maxlength="64" styleClass="char32Label" />

					</x:panelGrid>

				</x:htmlTag>
                <x:htmlTag value="br"/>
	
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Volt Reduction Control Point Setup"/></x:htmlTag>
    
                    <x:div id="subVoltReductionDiv" forceId="true">
                        <x:inputHidden id="subReductionPointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.voltReductionPointId}"/>
                        <x:outputLabel for="substationBusDevice" value="Selected Point: " title="Point used for control." styleClass="medStaticLabel"/>
                        <x:outputText id="substationBusDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.voltReductionPointId]}"/> 
                        <x:outputText id="substationBusDevicePointSeperator" forceId="true" value=" : " />
                        <x:outputText id="substationBusVoltReductionPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.voltReductionPointId]}" /> 
	    
                        <x:htmlTag value="br"/>
	    
                        <h:outputLink  value="javascript:substationBusVoltReductionPointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point"/>
                        </h:outputLink>
	
                        <x:htmlTag value="br"/>
                        <x:htmlTag value="br"/>
	    
                        <x:commandLink id="substationBusVoltReductionPoint_setNone" 
                            rendered="#{capControlForm.editingAuthorized}"
                            title="Do not use a point for control." 
                            styleClass="medStaticLabel"
                            value="No Volt Reduction Point" 
                            actionListener="#{capControlForm.substationBusNoVoltReductionPointClicked}">
                            <f:param name="ptId" value="0"/>
                        </x:commandLink>
                    </x:div>
                </x:htmlTag>
                
                 <x:htmlTag value="br"/>
	
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Substation Bus Disable Point Setup"/></x:htmlTag>
    
                    <x:div id="subDisablePointDiv" forceId="true">
                        <x:inputHidden id="subDisablePointValue" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.disableBusPointId}"/>
                        <x:outputLabel for="disableBusDevice" value="Selected Point: " title="Point used for auto disable of control." styleClass="medStaticLabel"/>
                        <x:outputText id="disableBusDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.disableBusPointId]}"/> 
                        <x:outputText id="disableBusDevicePointSeperator" forceId="true" value=" : " />
                        <x:outputText id="disableBusPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.disableBusPointId]}" /> 
	    
                        <x:htmlTag value="br"/>
	    
                        <h:outputLink  value="javascript:substationBusDisableBusPointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point"/>
                        </h:outputLink>
	
                        <x:htmlTag value="br"/>
                        <x:htmlTag value="br"/>
	    
                        <x:commandLink id="substationBusDisableBusPoint_setNone" 
                            rendered="#{capControlForm.editingAuthorized}"
                            title="Do not use a point for control." 
                            styleClass="medStaticLabel"
                            value="No Disable Point" 
                            actionListener="#{capControlForm.substationBusNoDisablePointClicked}">
                            <f:param name="disablePtId" value="0"/>
                        </x:commandLink>
                    </x:div>
                </x:htmlTag>
                

                <x:panelGroup>
                    <x:htmlTag value="br"/>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Substation Bus Points"/></x:htmlTag>
                    
                        <x:div forceId="true" id="SubstationBusEditorScrollDiv" styleClass="scrollSmall">
	                        <x:tree2 
	                            id="SubstationBusEditPointTree"
	                            value="#{capControlForm.pointTreeForm.pointList}" var="node"
	                            showRootNode="false" varNodeToggler="t" preserveToggle="true"
	                            clientSideToggle="true" showLines="false">
	
	                            <f:facet name="root">
	                                <x:panelGroup>
	                                    <x:outputText id="rootLink" value="#{node.description}" />
	                                </x:panelGroup>
	                            </f:facet>
					
	                            <f:facet name="pointtype">
	                                <x:panelGroup>
	                                    <x:outputText id="paChCnt"
	                                        value="#{node.description} (#{node.childCount})"
	                                        rendered="#{!empty node.children}" />
	                                </x:panelGroup>
	                            </f:facet>
	
	                            <f:facet name="sublevels">
	                                <x:panelGroup>
	                                    <x:outputText id="subLvlCnt"
	                                        value="#{node.description} (#{node.childCount})"
	                                        rendered="#{!empty node.children}" />
	                                </x:panelGroup>
	                            </f:facet>
	
	                            <f:facet name="points">
	                                <x:panelGroup>
	                                    <x:commandLink id="ptLink" value="#{node.description}"
	                                        actionListener="#{capControlForm.pointTreeForm.pointClick}">
	                                        <f:param name="ptID" value="#{node.identifier}" />
	                                        <f:param name="tabId" value="1"/>
	                                    </x:commandLink>
	                                </x:panelGroup>
	                            </f:facet>
	                        </x:tree2>
                        </x:div>
                    </x:htmlTag>
                </x:panelGroup>
                <x:htmlTag value="br"/>	
	
                <h:outputText styleClass="tableHeader" value="Point Editor: " rendered="#{capControlForm.editingAuthorized}"/>
	
                <x:commandLink id="addPtLnk" value="Add Point" actionListener="#{capControlForm.pointTreeForm.addPointClick}" rendered="#{capControlForm.editingAuthorized}">
                    <f:param name="parentId" value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
                    <f:param name="tabId" value="1"/>
                </x:commandLink>
                
                <x:outputText value=" | " rendered="#{capControlForm.editingAuthorized}"/>	
	
                <x:commandLink id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.pointTreeForm.deletePointClick}" rendered="#{capControlForm.editingAuthorized}">
                    <f:param name="tabId" value="1"/>
                </x:commandLink>
        
            </h:column>
        
            <h:column>
	
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="VAR Point Setup"/></x:htmlTag>
    
                    <x:div id="subVarDiv" forceId="true">
                        <h:selectBooleanCheckbox id="Use_Phase_Data_Checkbox" onclick="submit();" 
                            rendered="#{capControlForm.editingAuthorized}"
                            value="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
                        <x:outputLabel for="Use_Phase_Data_Checkbox" 
                            rendered="#{capControlForm.editingAuthorized}"
                            value="Use Per Phase Var Data" 
                            title="Check this box to use 3 phase var data." 
                            styleClass="smallStaticLabel"/>
                 	
                        <x:htmlTag value="br"/> 
    
                        <h:selectBooleanCheckbox id="Use_Totalized_Value_Checkbox" 
                            onclick="submit();"
                            value="#{capControlForm.PAOBase.capControlSubstationBus.controlFlagBoolean}"
                            rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean && capControlForm.editingAuthorized}"/>
                        <x:outputLabel for="Use_Totalized_Value_Checkbox" 
                            value="Use Totalized Values" 
                            title="Check this box to use totalized phase values for control." 
                            styleClass="smallStaticLabel"
                            rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean && capControlForm.editingAuthorized}"/>
        
                        <x:htmlTag value="br"/> 
                        <x:htmlTag value="br"/> 
    
                        <x:inputHidden id="var_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID }" />
                        <x:outputLabel for="sub_Var_Device" value="Selected Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel"rendered="#{!capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
                        <x:outputLabel for="sub_Var_Device" value="Selected Phase A Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel"rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
                        <x:outputText id="sub_Var_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]}" /> 
                        <x:outputText id="sub_Var_Device_Point_Seperator" forceId="true" value=" : " />
                        <x:outputText id="sub_Var_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]}" /> 
    
                        <x:htmlTag value="br"/> 
    
                        <h:outputLink  value="javascript:sub_Var_PointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point" rendered="#{!capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
                            <h:outputText value="Select point for Phase A" rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
                        </h:outputLink>
                 
                        <x:htmlTag value="br"/> 
                        <x:htmlTag value="br"/> 

                        <x:div id="use_Phase_Data_Div" forceId="true" rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}">
                 
                            <!-- PhaseB Var Point -->
                            <x:inputHidden id="var_phase_b_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.phaseB}"/>
                            <x:outputLabel for="sub_Var_PhaseB_Device" value="Selected Phase B Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
                            <x:outputText id="sub_Var_PhaseB_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseB]}" /> 
                            <x:outputText id="sub_Var_PhaseB_Device_Point_Seperator" forceId="true" value=" : "/>
                            <x:outputText id="sub_Var_PhaseB_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseB]}"/> 
	
                            <x:htmlTag value="br"/> 
	
                            <h:outputLink  value="javascript:sub_Var_PhaseB_PointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                                <h:outputText value="Select point for Phase B"/>
                            </h:outputLink>
	
                            <x:htmlTag value="br"/> 
                            <x:htmlTag value="br"/> 
                 
                            <!-- PhaseC Var Point -->
                            <x:inputHidden id="var_phase_c_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.phaseC}"/>
                            <x:outputLabel for="sub_Var_PhaseC_Device" value="Selected Phase C Point: " title="Data Point used for the current phase C VAR value" styleClass="medStaticLabel"/>
                            <x:outputText id="sub_Var_PhaseC_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseC]}"/> 
                            <x:outputText id="sub_Var_PhaseC_Device_Point_Seperator" forceId="true" value=" : "/>
                            <x:outputText id="sub_Var_PhaseC_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseC]}"/> 
                 
                            <x:htmlTag value="br"/> 
    
                            <h:outputLink  value="javascript:sub_Var_PhaseC_PointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                                <h:outputText value="Select point for Phase C"/>
                            </h:outputLink>
    
                        </x:div>
                    </x:div>
                
                    <x:htmlTag value="br"/> 
                
                    <x:commandLink id="varPoint_setNone" 
                        rendered="#{capControlForm.editingAuthorized}"
                        title="Do not use a point for the VAR value" 
                        styleClass="medStaticLabel"
                        value="No Var Point" 
                        actionListener="#{capControlForm.varPtTeeClick}">
                        <f:param name="ptId" value="0"/>
                    </x:commandLink>
                    
                </x:htmlTag>
                
                <x:htmlTag value="br"/>
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Watt Point Setup"/></x:htmlTag>

                    <x:div id="subWattDiv" forceId="true">
                        <x:inputHidden id="watt_point" forceId="true" 
                            value="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID }" />      
                        <x:outputLabel for="subWattDevice" value="Selected Point: " 
                            title="Data Point used for the current WATT value" 
                            styleClass="medStaticLabel"/>
                        <x:outputText id="subWattDevice" forceId="true" 
                            value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID]}"/> 
                        <x:outputText id="sub_Watt_Device_Point_Seperator" forceId="true" value=" : "/>
                        <x:outputText id="subWattPoint" forceId="true" 
                            value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID]}" /> 
	    
                        <x:htmlTag value="br"/> 
	    
                        <h:outputLink  value="javascript:subWattPointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point..."/>
                        </h:outputLink>
                    </x:div>
    
                    <x:htmlTag value="br"/> 
    
                    <x:commandLink id="wattPoint_setNone" title="Do not use a point for the watt value" 
                        rendered="#{capControlForm.editingAuthorized}"
                        styleClass="medStaticLabel"
                        value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
                        <f:param name="ptId" value="0"/>
                    </x:commandLink>
                
                </x:htmlTag>
                <x:htmlTag value="br"/>
                <x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Volt Point Setup"/></x:htmlTag>

                    <x:div id="subVoltDiv" forceId="true">
                        <x:inputHidden id="volt_point" forceId="true" 
                            value="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID }" />      
                        <x:outputLabel for="subWattDevice" value="Selected Point: " 
                            title="Data Point used for the current VOLT value" styleClass="medStaticLabel"/>
                        <x:outputText id="subVoltDevice" forceId="true" 
                            value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID]}"/> 
                        <x:outputText id="sub_Volt_Device_Point_Seperator" forceId="true" value=" : "/>
                        <x:outputText id="subVoltPoint" forceId="true" 
                            value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID]}" /> 
	    
                        <x:htmlTag value="br"/> 
	                
                        <h:outputLink  value="javascript:subVoltPointPicker.show()" rendered="#{capControlForm.editingAuthorized}">
                            <h:outputText value="Select point..."/>
                        </h:outputLink>
                    </x:div>
    
                    <x:htmlTag value="br"/> 
    
                    <x:commandLink id="voltPoint_setNone" title="Do not use a point for the volt value" 
                        rendered="#{capControlForm.editingAuthorized}"
                        styleClass="medStaticLabel"
                        value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}">
                        <f:param name="ptId" value="0"/>
                    </x:commandLink>
                </x:htmlTag>
            </h:column>
        </x:panelGrid>
	</f:subview>
</f:subview>
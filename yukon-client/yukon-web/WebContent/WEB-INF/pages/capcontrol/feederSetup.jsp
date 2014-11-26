<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
	<script type="text/javascript">
	    var feederVarPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'varPointPicker', '', 'feederVarPointPicker', 'pointName:feederVarPoint;deviceName:feederVarDevice');
	    feederVarPointPicker.destinationFieldId = 'varPoint';
	    feederVarPointPicker.memoryGroup = 'pointPicker';
	    <cti:pickerProperties var="varPickerOutputColumns" property="OUTPUT_COLUMNS" type="varPointPicker"/>
	    feederVarPointPicker.outputColumns = ${varPickerOutputColumns};
	    
	    var feederVarPhaseBPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'varPointPicker', '', 'feederVarPhaseBPointPicker', 'pointName:feederVarPhaseBPoint;deviceName:feederVarPhaseBDevice');
        feederVarPhaseBPointPicker.destinationFieldId = 'varPhaseBPoint';
        feederVarPhaseBPointPicker.memoryGroup = 'pointPicker';
        feederVarPhaseBPointPicker.outputColumns = ${varPickerOutputColumns};
        
        var feederVarPhaseCPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'varPointPicker', '', 'feederVarPhaseCPointPicker', 'pointName:feederVarPhaseCPoint;deviceName:feederVarPhaseCDevice');
        feederVarPhaseCPointPicker.destinationFieldId = 'varPhaseCPoint';
        feederVarPhaseCPointPicker.memoryGroup = 'pointPicker';
        feederVarPhaseCPointPicker.outputColumns = ${varPickerOutputColumns};
        
        var feederWattPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'wattPointPicker', '', 'feederWattPointPicker', 'pointName:feederWattPoint;deviceName:feederWattDevice');
        feederWattPointPicker.destinationFieldId = 'wattPoint';
        feederWattPointPicker.memoryGroup = 'pointPicker';
        <cti:pickerProperties var="wattPickerOutputColumns" property="OUTPUT_COLUMNS" type="wattPointPicker"/>
        feederWattPointPicker.outputColumns = ${wattPickerOutputColumns};
        
        var feederVoltPointPicker = new Picker('OK', 'Cancel', '(none selected)', 'voltPointPicker', '', 'feederVoltPointPicker', 'pointName:feederVoltPoint;deviceName:feederVoltDevice');
        feederVoltPointPicker.destinationFieldId = 'voltPoint';
        feederVoltPointPicker.memoryGroup = 'pointPicker';
        <cti:pickerProperties var="voltPickerOutputColumns" property="OUTPUT_COLUMNS" type="voltPointPicker"/>
        feederVoltPointPicker.outputColumns = ${voltPickerOutputColumns};
        
    </script>
</f:verbatim>
<f:subview id="feederSetup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}">
	<f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}">
		<h:panelGrid id="fdrBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
			<h:column>
				<x:htmlTag value="fieldset" styleClass="fieldSet">
				    <x:htmlTag value="legend"><x:outputText value="Feeder Info"/></x:htmlTag>
					<x:outputLabel for="fdrMapLocID" value="Map Location ID: "
						title="Mapping code/string used for third-party systems" />
					<x:inputText id="fdrMapLocID"
						value="#{capControlForm.PAOBase.capControlFeeder.mapLocationID}"
						required="true" maxlength="64" />
				</x:htmlTag>
				
				<x:htmlTag value="br"/>
				
				<x:panelGroup>
					<x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend"><x:outputText value="Feeder Points"/></x:htmlTag>
						<x:div forceId="true" id="SubstationBusEditorScrollDiv"
							styleClass="scrollSmall">
							<%-- binding="#{capControlForm.pointTreeForm.pointTree}" --%>
							<x:tree2 id="SubstationBusEditPointTree"
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
											<f:param name="tabId" value="6"/>
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
					<f:param name="tabId" value="6"/>
				</x:commandLink>

                <x:outputText value=" | " rendered="#{capControlForm.editingAuthorized}"/>

				<x:commandLink id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.pointTreeForm.deletePointClick}" rendered="#{capControlForm.editingAuthorized}">
				    <f:param name="tabId" value="6"/>
				</x:commandLink>

			</h:column>

			<h:column>

				<x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="VAR Point Setup"/></x:htmlTag>

					<x:div id="feederVarDiv" forceId="true">
						<h:selectBooleanCheckbox id="usePhaseDataCheckbox"
                            rendered="#{capControlForm.editingAuthorized}"
							onclick="submit();"
							valueChangeListener="#{capControlForm.usePhaseDataClicked}"
							value="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" />
						<x:outputLabel for="usePhaseDataCheckbox"
                            rendered="#{capControlForm.editingAuthorized}"
							value="Use Per Phase Var Data"
							title="Check this box to use 3 phase var data."
							styleClass="smallStaticLabel" />
	
						<x:htmlTag value="br"/>
						<x:htmlTag value="br"/>
	
						<h:selectBooleanCheckbox id="Use_Totalized_Value_Checkbox"
							onclick="submit();"
							value="#{capControlForm.PAOBase.capControlFeeder.controlFlagBoolean}"
							rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean && capControlForm.editingAuthorized}" />
						<x:outputLabel for="Use_Totalized_Value_Checkbox"
							value="Use Totalized Values"
							title="Check this box to use totalized phase values for control."
							styleClass="smallStaticLabel"
							rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" />
	
						<x:htmlTag value="br"/>
						<x:htmlTag value="br"/>
	
						<x:inputHidden id="varPoint" forceId="true"
							value="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID }" />
						<x:outputLabel for="feederVarDevice" value="Selected Point: "
							title="Data Point used for the current VAR value"
							rendered="#{!capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"
							styleClass="medStaticLabel" />
						<x:outputLabel for="feederVarDevice"
							value="Selected Phase A Point: "
							title="Data Point used for the current VAR value"
							rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"
							styleClass="medStaticLabel" />
						<x:outputText id="feederVarDevice" forceId="true"
							value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}" />
						<x:outputText id="feederVarPointSeperator" forceId="true"
							value=" : " />
						<x:outputText id="feederVarPoint" forceId="true"
							value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}" />
	
						<x:htmlTag value="br"/>
	
						<h:outputLink value="javascript:feederVarPointPicker.show.call(feederVarPointPicker)" rendered="#{capControlForm.editingAuthorized}">
							<h:outputText value="Select point for Phase A"
								rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" />
							<h:outputText value="Select point"
								rendered="#{!capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" />
						</h:outputLink>
	
						<x:htmlTag value="br"/>
						<x:htmlTag value="br"/>
	
						<x:div id="usePhaseDataDiv" forceId="true"
							rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}">
	
							<x:inputHidden id="varPhaseBPoint" forceId="true"
								value="#{capControlForm.PAOBase.capControlFeeder.phaseB }" />
							<x:outputLabel for="feederVarPhaseBDevice"
								value="Selected Phase B Point: "
								title="Data Point used for the current phase B VAR value"
								styleClass="medStaticLabel" />
							<x:outputText id="feederVarPhaseBDevice" forceId="true"
								value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}" />
							<x:outputText id="feederVarPhaseBPointSeperator" forceId="true"
								value=" : " />
							<x:outputText id="feederVarPhaseBPoint" forceId="true"
								value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}" />
	
							<x:htmlTag value="br"/>
	
							<h:outputLink
                                rendered="#{capControlForm.editingAuthorized}"
								value="javascript:feederVarPhaseBPointPicker.show.call(feederVarPhaseBPointPicker)">
								<h:outputText value="Select point for Phase B" />
							</h:outputLink>
	
							<x:htmlTag value="br"/>
							<x:htmlTag value="br"/>
	
							<x:inputHidden id="varPhaseCPoint" forceId="true"
								value="#{capControlForm.PAOBase.capControlFeeder.phaseC }" />
							<x:outputLabel for="feederVarPhaseCDevice"
								value="Selected Phase C Point: "
								title="Data Point used for the current phase B VAR value"
								styleClass="medStaticLabel" />
							<x:outputText id="feederVarPhaseCDevice" forceId="true"
								value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}" />
							<x:outputText id="feederVarPhaseCPointSeperator" forceId="true"
								value=" : " />
							<x:outputText id="feederVarPhaseCPoint" forceId="true"
								value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}" />
	
							<x:htmlTag value="br"/>
	
							<h:outputLink
                                rendered="#{capControlForm.editingAuthorized}"
								value="javascript:feederVarPhaseCPointPicker.show.call(feederVarPhaseCPointPicker)">
								<h:outputText value="Select point for Phase C" />
							</h:outputLink>
	
						</x:div>
					</x:div>
					
					<x:htmlTag value="br"/>
					
					<x:commandLink id="varPointSetNone"
                        rendered="#{capControlForm.editingAuthorized}"
						title="Do not use a point for the VAR value"
						styleClass="medStaticLabel" value="No Var Point"
						actionListener="#{capControlForm.varPtTeeClick}">
						<f:param name="ptId" value="0" />
					</x:commandLink>
				
				</x:htmlTag>
				
				<x:htmlTag value="br"/>
				
				<x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="WATT Point Setup"/></x:htmlTag>
					<x:div id="feederWattDiv" forceId="true">
						<x:inputHidden id="wattPoint" forceId="true"
							value="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID }" />
						<x:outputLabel for="feederWattDevice" value="Selected Point: "
							title="Data Point used for the current WATT value"
							styleClass="medStaticLabel" />
						<x:outputText id="feederWattDevice" forceId="true"
							value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}" />
						<x:outputText id="feederWattPointSeperator" forceId="true"
							value=" : " />
						<x:outputText id="feederWattPoint" forceId="true"
							value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}" />
						
	                    <x:htmlTag value="br"/>
	
						<h:outputLink value="javascript:feederWattPointPicker.show.call(feederWattPointPicker)" rendered="#{capControlForm.editingAuthorized}">
							<h:outputText value="Select point..." />
						</h:outputLink>
					</x:div>
					
					<x:htmlTag value="br"/>
					
					<x:commandLink id="wattPointSetNone"
                        rendered="#{capControlForm.editingAuthorized}"
						title="Do not use a point for the WATT value"
						styleClass="medStaticLabel" value="No WATT Point"
						actionListener="#{capControlForm.wattPtTeeClick}">
						<f:param name="ptId" value="0" />
					</x:commandLink>
				</x:htmlTag>
				
				<x:htmlTag value="br"/>
				
				<x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="VOLT Point Setup"/></x:htmlTag>
					<x:div id="feederVoltDiv" forceId="true">
						<x:inputHidden id="voltPoint" forceId="true"
							value="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID }" />
						<x:outputLabel for="feederVoltDevice" value="Selected Point: "
							title="Data Point used for the current VOLT value"
							styleClass="medStaticLabel" />
						<x:outputText id="feederVoltDevice" forceId="true"
							value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}" />
						<x:outputText id="feederVoltPointSeperator" forceId="true"
							value=" : " />
						<x:outputText id="feederVoltPoint" forceId="true"
							value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}" />
						
						<x:htmlTag value="br"/>
						
						<h:outputLink value="javascript:feederVoltPointPicker.show.call(feederVoltPointPicker)" rendered="#{capControlForm.editingAuthorized}">
							<h:outputText value="Select point..." />
						</h:outputLink>
					</x:div>
					
					<x:htmlTag value="br"/>
					
					<x:commandLink id="voltPointSetNone"
                        rendered="#{capControlForm.editingAuthorized}"
						title="Do not use a point for the VOLT value"
						styleClass="medStaticLabel" value="No VOLT Point"
						actionListener="#{capControlForm.voltPtTeeClick}">
						<f:param name="ptId" value="0" />
					</x:commandLink>
				</x:htmlTag>
			</h:column>
		</h:panelGrid>
	</f:subview>
</f:subview>
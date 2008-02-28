<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        formatSelectedPoint ('subVarDiv');
        formatSelectedPoint ('subWattDiv');
        formatSelectedPoint ('subVoltDiv');
        
        var sub_Var_PointPicker = new PointPicker('var_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:sub_Var_Point;deviceName:sub_Var_Device','sub_Var_PointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
        var sub_Var_PhaseB_PointPicker = new PointPicker('var_phase_b_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:sub_Var_PhaseB_Point;deviceName:sub_Var_PhaseB_Device','sub_Var_PhaseB_PointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
        var sub_Var_PhaseC_PointPicker = new PointPicker('var_phase_c_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:sub_Var_PhaseC_Point;deviceName:sub_Var_PhaseC_Device','sub_Var_PhaseC_PointPicker','', Prototype.emptyFunction,Prototype.emptyFunction);
		var subWattPointPicker = new PointPicker('watt_point','com.cannontech.common.search.criteria.CCWattCriteria','pointName:subWattPoint;deviceName:subWattDevice','subWattPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var subVoltPointPicker = new PointPicker('volt_point','com.cannontech.common.search.criteria.CCVoltCriteria','pointName:subVoltPoint;deviceName:subVoltDevice','subVoltPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);        
    </script>
</f:verbatim>

<f:subview id="subSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
	<f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">
	<x:panelGrid id="subBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">
	<h:column>
	<f:verbatim>
	<br />
	<br />
	<fieldset>
	<legend>
	SubBus Info
	</legend>
	</f:verbatim>
	<x:outputLabel for="subAreaName" value="#{capControlForm.PAODescLabel}: " 
        title="Physical location of the Substation" 
    	rendered="#{!empty capControlForm.PAODescLabel}" />
	<x:inputText id="subAreaName" value="#{capControlForm.PAOBase.geoAreaName}" 
        required="true" maxlength="60" styleClass="char32Label" 
    	rendered="#{!empty capControlForm.PAODescLabel}" />

	<f:verbatim>
	<br />
	</f:verbatim>
	<x:outputLabel for="subMapLocID" value="Map Location ID: " 
    	title="Mapping code/string used for third-party systems" />
	<x:inputText id="subMapLocID" value="#{capControlForm.PAOBase.capControlSubstationBus.mapLocationID}" 
    	required="true" maxlength="64" styleClass="char32Label" />
	<f:verbatim>
	</fieldset>
	</f:verbatim>
    <f:verbatim>
    <br />
    <br />
    <fieldset>
    <legend>
    Sub Bus Points
    </legend>
    <br/>
    </f:verbatim>
	<x:dataList id="subPoints"
    	var="point"
    	value="#{capControlForm.dataModel.paoPoints}"
    	layout="unorderedList" 
    	styleClass="listWithNoBullets" >
    <x:commandLink  value="#{point.pointName}"  
    	actionListener="#{capControlForm.dataModel.goToPointEditor}">
    <f:param name = "ptID" value="#{point.pointID}"/> 
    </x:commandLink>
	</x:dataList>
    <f:verbatim>
	<br/>
    </f:verbatim>
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
    VAR Point Setup
    </legend>
    </f:verbatim>
    <x:div id="subVarDiv" forceId="true">
    <h:selectBooleanCheckbox id="Use_Phase_Data_Checkbox" 
	   	onclick="submit();"
		valueChangeListener="#{capControlForm.usePhaseDataClicked}"
		value="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
	<x:outputLabel for="Use_Phase_Data_Checkbox" 
		value="Use Per Phase Var Data" 
        title="Check this box to use 3 phase var data." 
        styleClass="smallStaticLabel"/>
                 	
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
	<x:inputHidden id="var_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID }" />      
    <x:outputLabel for="sub_Var_Device" value="Selected Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel" rendered="#{!capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
    <x:outputLabel for="sub_Var_Device" value="Selected PhaseA Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel" rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
    <x:outputText id="sub_Var_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]}"/> 
    <x:outputText id="sub_Var_Device_Point_Seperator" forceId="true" value=" : " />
    <x:outputText id="sub_Var_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]}" /> 
    
    <f:verbatim>
	<br/>
	</f:verbatim>
    
    <h:outputLink  value="javascript:sub_Var_PointPicker.showPicker()" >
    <h:outputText value="Select point..." rendered="#{!capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
    <h:outputText value="Select point for Phase A..." rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}"/>
	</h:outputLink>
                 
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>

	<x:div id="use_Phase_Data_Div" forceId="true" rendered="#{capControlForm.PAOBase.capControlSubstationBus.usePhaseDataBoolean}">
                 
    <!-- PhaseB Var Point -->
    <x:inputHidden id="var_phase_b_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.phaseB}"/>
    <x:outputLabel for="sub_Var_PhaseB_Device" value="Selected Phase B Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="sub_Var_PhaseB_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseB]}"/> 
    <x:outputText id="sub_Var_PhaseB_Device_Point_Seperator" forceId="true" value=" : "/>
    <x:outputText id="sub_Var_PhaseB_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseB]}"/> 
	
	<f:verbatim>
    <br/>
    </f:verbatim>
	
	<h:outputLink  value="javascript:sub_Var_PhaseB_PointPicker.showPicker()">
    <h:outputText value="Select point for Phase B"/>
	</h:outputLink>
	
	<f:verbatim>
	<br/>
    <br/>
	</f:verbatim>
                 
    <!-- PhaseC Var Point -->
    <x:inputHidden id="var_phase_c_point" forceId="true" value="#{capControlForm.PAOBase.capControlSubstationBus.phaseC}"/>
    <x:outputLabel for="sub_Var_PhaseC_Device" value="Selected Phase C Point: " title="Data Point used for the current phase C VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="sub_Var_PhaseC_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseC]}"/> 
    <x:outputText id="sub_Var_PhaseC_Device_Point_Seperator" forceId="true" value=" : "/>
    <x:outputText id="sub_Var_PhaseC_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.phaseC]}"/> 
                 
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:sub_Var_PhaseC_PointPicker.showPicker()">
    <h:outputText value="Select point for Phase C"/>
	</h:outputLink>
    
    </x:div>
    </x:div>
                
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
                
    <x:commandLink id="varPoint_setNone" 
    	title="Do not use a point for the VAR value" 
    	styleClass="medStaticLabel"
        value="No Var Point" 
        actionListener="#{capControlForm.varPtTeeClick}">
    <f:param name="currentVarPointId" value="0"/>
	</x:commandLink>
                
    <f:verbatim>
    </fieldset>
    </f:verbatim>
                
    <f:verbatim>
    <br />
    <br />
    <fieldset>
    <legend>
    Watt Point Setup
    </legend>
    </f:verbatim>

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
    <f:verbatim>
    <br/>
    </f:verbatim>
    <h:outputLink  value="javascript:subWattPointPicker.showPicker()" >
    <h:outputText value="Select point..."/>
    </h:outputLink>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="wattPoint_setNone" title="Do not use a point for the watt value" 
    	styleClass="medStaticLabel"
        value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
    <f:param name="ptID" value="0"/>
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
    <f:verbatim>
    <br/>
    </f:verbatim>
                
    <h:outputLink  value="javascript:subVoltPointPicker.showPicker()" >
	<h:outputText value="Select point..."/>
    </h:outputLink>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="voltPoint_setNone" title="Do not use a point for the volt value" 
    	styleClass="medStaticLabel"
        value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}">
    <f:param name="ptID" value="0"/>
    </x:commandLink>

    <f:verbatim>
    </fieldset>
    </f:verbatim>
	</h:column>
	</x:panelGrid>
	</f:subview>
</f:subview>

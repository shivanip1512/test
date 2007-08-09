<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:subview id="feederSetup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >    
    <f:verbatim>
    <script type="text/javascript">
        formatSelectedPoint ('feederVarDiv');
        formatSelectedPoint ('feederWattDiv');
        formatSelectedPoint ('feederVoltDiv');
		
		var feeder_Var_PointPicker = new PointPicker('var_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPoint;deviceName:feederVarDevice','feederVarPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feeder_Var_PhaseB_PointPicker = new PointPicker('var_phase_b_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feeder_Var_PhaseB_Point;deviceName:feeder_Var_PhaseB_Device','feeder_Var_PhaseB_PointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feeder_Var_PhaseC_PointPicker = new PointPicker('var_phase_c_point','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feeder_Var_PhaseC_Point;deviceName:feeder_Var_PhaseC_Device','feeder_Var_PhaseC_PointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederWattPointPicker = new PointPicker('watt_point','com.cannontech.common.search.criteria.CCWattCriteria','pointName:feederWattPoint;deviceName:feederWattDevice','feederWattPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVoltPointPicker = new PointPicker('volt_point','com.cannontech.common.search.criteria.CCVoltCriteria','pointName:feederVoltPoint;deviceName:feederVoltDevice','feederVoltPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
	</f:verbatim>
    <h:panelGrid id="fdrBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
	<h:column>
	<f:verbatim><br/><br/><fieldset><legend>Feeder Info</legend></f:verbatim>    
	<x:outputLabel for="fdrMapLocID" value="Map Location ID: " 
        title="Mapping code/string used for third-party systems" />
	<x:inputText id="fdrMapLocID" value="#{capControlForm.PAOBase.capControlFeeder.mapLocationID}" 
        required="true" maxlength="64" />
	<f:verbatim></fieldset></f:verbatim>
	<f:verbatim>
    <br />
    <br />
    <fieldset>
    <legend>
    Feeder Points
    <br/>
	</f:verbatim>
	<x:dataList id="subPoints"
    	var="point"
    	value="#{capControlForm.dataModel.paoPoints}"
    	layout="unorderedList" 
    	styleClass="listWithNoBullets" >
    <x:commandLink  value="#{point.pointName}"  actionListener="#{capControlForm.dataModel.goToPointEditor}">
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
	
	<x:div id="feeder_Var_Div" forceId="true">
    <x:inputHidden id="var_point" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID }" />      
    <x:outputLabel for="feeder_Var_Device" value="Selected Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feeder_Var_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}"/> 
    <x:outputText id="feeder_Var_Point_Seperator" forceId="true" value=" : " />
    <x:outputText id="feeder_Var_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feeder_Var_PointPicker.showPicker()" >
    <h:outputText value="Select point..."/>
    </h:outputLink>
    
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:div id="use_Phase_Data_Div" forceId="true" rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}">
    
    <x:inputHidden id="var_phase_b_point" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.phaseB }" />      
    <x:outputLabel for="feeder_Var_PhaseB_Device" value="Selected PhaseB Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feeder_Var_PhaseB_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}"/> 
    <x:outputText id="feeder_Var_PhaseB_Point_Seperator" forceId="true" value=" : "/>
    <x:outputText id="feeder_Var_PhaseB_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}"/> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feeder_Var_PhaseB_PointPicker.showPicker()">
    <h:outputText value="Select point for Phase B"/>
    </h:outputLink>
    
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:inputHidden id="var_phase_c_point" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.phaseC }" />      
    <x:outputLabel for="feeder_Var_PhaseC_Device" value="Selected PhaseC Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feeder_Var_PhaseC_Device" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}"/> 
    <x:outputText id="feeder_Var_PhaseC_Point_Seperator" forceId="true" value=" : "/>
    <x:outputText id="feeder_Var_PhaseC_Point" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}"/> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feeder_Var_PhaseC_PointPicker.showPicker()">
    <h:outputText value="Select point for Phase C"/>
    </h:outputLink>
    
    </x:div>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="varPoint_setNone" title="Do not use a point for the VAR value" 
    	styleClass="medStaticLabel"
        value="No Var Point" actionListener="#{capControlForm.varPtTeeClick}">
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
    WATT Point Setup
    </legend>
    </f:verbatim>
	<x:div id="feederWattDiv" forceId="true">
    <x:inputHidden id="watt_point" forceId="true" 
    	value="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID }" />      
    <x:outputLabel for="feederWattDevice" value="Selected Point: " 
    	title="Data Point used for the current WATT value" 
        styleClass="medStaticLabel"/>
    <x:outputText id="feederWattDevice" forceId="true" 
    	value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}"/> 
   	<x:outputText id="feeder_Watt_Point_Seperator" forceId="true" value=" : " />
    <x:outputText id="feederWattPoint" forceId="true" 
    	value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}" /> 
    <f:verbatim>
    <br/>
    </f:verbatim>
    <h:outputLink  value="javascript:feederWattPointPicker.showPicker()" >
    <h:outputText value="Select point..."/>
    </h:outputLink>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="wattPoint_setNone" title="Do not use a point for the WATT value" 
    	styleClass="medStaticLabel"
        value="No WATT Point" actionListener="#{capControlForm.wattPtTeeClick}">
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
    VOLT Point Setup
    </legend>
    </f:verbatim>
	<x:div id="feederVoltDiv" forceId="true">
    <x:inputHidden id="volt_point" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID }" />      
    <x:outputLabel for="feederVoltDevice" value="Selected Point: " 
    	title="Data Point used for the current VOLT value" styleClass="medStaticLabel"/>
    <x:outputText id="feederVoltDevice" forceId="true" 
    	value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}"/> 
   	<x:outputText id="feeder_Volt_Point_Seperator" forceId="true" value=" : " />
    <x:outputText id="feederVoltPoint" forceId="true" 
    	value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}" /> 
    <f:verbatim>
    <br/>
    </f:verbatim>
    <h:outputLink  value="javascript:feederVoltPointPicker.showPicker()" >
    <h:outputText value="Select point..."/>
    </h:outputLink>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="voltPoint_setNone" title="Do not use a point for the VOLT value" 
    	styleClass="medStaticLabel"
    	value="No VOLT Point" actionListener="#{capControlForm.voltPtTeeClick}">
    <f:param name="ptID" value="0"/>
    </x:commandLink>
    <f:verbatim>
    </fieldset>
    </f:verbatim>
	</h:column>
    </h:panelGrid>
    </f:subview>
</f:subview>
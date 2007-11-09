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
		
		var feederVarPointPicker = new PointPicker('varPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPoint;deviceName:feederVarDevice','feederVarPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVarPhaseBPointPicker = new PointPicker('varPhaseBPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPhaseBPoint;deviceName:feederVarPhaseBDevice','feederVarPhaseBPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVarPhaseCPointPicker = new PointPicker('varPhaseCPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPhaseCPoint;deviceName:feederVarPhaseCDevice','feederVarPhaseCPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederWattPointPicker = new PointPicker('wattPoint','com.cannontech.common.search.criteria.CCWattCriteria','pointName:feederWattPoint;deviceName:feederWattDevice','feederWattPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVoltPointPicker = new PointPicker('voltPoint','com.cannontech.common.search.criteria.CCVoltCriteria','pointName:feederVoltPoint;deviceName:feederVoltDevice','feederVoltPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
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
    </legend>
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
	
	<x:div id="feederVarDiv" forceId="true">
	<h:selectBooleanCheckbox id="usePhaseDataCheckbox" 
	   	onclick="submit();"
		valueChangeListener="#{capControlForm.usePhaseDataClicked}"
		value="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"/>
	<x:outputLabel for="usePhaseDataCheckbox" 
		value="Use Per Phase Var Data" 
        title="Check this box to use 3 phase var data." 
        styleClass="smallStaticLabel"/>
                 	
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    <x:inputHidden id="varPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID }" />      
    <x:outputLabel for="feederVarDevice" value="Selected Point: " title="Data Point used for the current VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feederVarDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}"/> 
    <x:outputText id="feederVarPointSeperator" forceId="true" value=" : " />
    <x:outputText id="feederVarPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feederVarPointPicker.showPicker()" >
    <h:outputText value="Select point..."/>
    </h:outputLink>
    
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:div id="usePhaseDataDiv" forceId="true" rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}">
    
    <x:inputHidden id="varPhaseBPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.phaseB }" />      
    <x:outputLabel for="feederVarPhaseBDevice" value="Selected PhaseB Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feederVarPhaseBDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}"/> 
    <x:outputText id="feederVarPhaseBPointSeperator" forceId="true" value=" : "/>
    <x:outputText id="feederVarPhaseBPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseB]}"/> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feederVarPhaseBPointPicker.showPicker()">
    <h:outputText value="Select point for Phase B"/>
    </h:outputLink>
    
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:inputHidden id="varPhaseCPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.phaseC }" />      
    <x:outputLabel for="feederVarPhaseCDevice" value="Selected PhaseC Point: " title="Data Point used for the current phase B VAR value" styleClass="medStaticLabel"/>
    <x:outputText id="feederVarPhaseCDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}"/> 
    <x:outputText id="feederVarPhaseCPointSeperator" forceId="true" value=" : "/>
    <x:outputText id="feederVarPhaseCPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.phaseC]}"/> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feederVarPhaseCPointPicker.showPicker()">
    <h:outputText value="Select point for Phase C"/>
    </h:outputLink>
    
    </x:div>
    </x:div>
    <f:verbatim>
    <br/>
    </f:verbatim>
    <x:commandLink id="varPointSetNone" title="Do not use a point for the VAR value" 
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
    <x:inputHidden id="wattPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID }" />      
    <x:outputLabel for="feederWattDevice" value="Selected Point: " title="Data Point used for the current WATT value" styleClass="medStaticLabel"/>
    <x:outputText id="feederWattDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}"/> 
   	<x:outputText id="feederWattPointSeperator" forceId="true" value=" : " />
    <x:outputText id="feederWattPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentWattLoadPointID]}" /> 
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
    <x:commandLink id="wattPointSetNone" title="Do not use a point for the WATT value" 
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
    <x:inputHidden id="voltPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID }" />      
    <x:outputLabel for="feederVoltDevice" value="Selected Point: " title="Data Point used for the current VOLT value" styleClass="medStaticLabel"/>
    <x:outputText id="feederVoltDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}"/> 
   	<x:outputText id="feederVoltPointSeperator" forceId="true" value=" : " />
    <x:outputText id="feederVoltPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVoltLoadPointID]}" /> 
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
    <x:commandLink id="voltPointSetNone" title="Do not use a point for the VOLT value" 
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
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:subview id="feederSetup" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >    
    <f:verbatim>
    <script type="text/javascript">
		var feederVarPointPicker = new PointPicker('varPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPoint;deviceName:feederVarDevice','feederVarPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVarPhaseBPointPicker = new PointPicker('varPhaseBPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPhaseBPoint;deviceName:feederVarPhaseBDevice','feederVarPhaseBPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVarPhaseCPointPicker = new PointPicker('varPhaseCPoint','com.cannontech.common.search.criteria.CCVarCriteria','pointName:feederVarPhaseCPoint;deviceName:feederVarPhaseCDevice','feederVarPhaseCPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederWattPointPicker = new PointPicker('wattPoint','com.cannontech.common.search.criteria.CCWattCriteria','pointName:feederWattPoint;deviceName:feederWattDevice','feederWattPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
		var feederVoltPointPicker = new PointPicker('voltPoint','com.cannontech.common.search.criteria.CCVoltCriteria','pointName:feederVoltPoint;deviceName:feederVoltDevice','feederVoltPointPicker','',Prototype.emptyFunction,Prototype.emptyFunction);
    </script>
	</f:verbatim>
    <h:panelGrid id="fdrBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
	<h:column>
	<f:verbatim><br/><br/><fieldset class="fieldSet"><legend>Feeder Info</legend></f:verbatim>    
	<x:outputLabel for="fdrMapLocID" value="Map Location ID: " 
        title="Mapping code/string used for third-party systems" />
	<x:inputText id="fdrMapLocID" value="#{capControlForm.PAOBase.capControlFeeder.mapLocationID}" 
        required="true" maxlength="64" />
	<f:verbatim></fieldset></f:verbatim>
    <x:panelGroup>
        <f:verbatim>
            <br />
            <fieldset class="fieldSet"><legend> Feeder Points </legend>
        </f:verbatim>
        <x:div forceId="true" id="SubstationBusEditorScrollDiv"
            styleClass="scrollSmall">
            <%-- binding="#{capControlForm.pointTreeForm.pointTree}" --%>
            <x:tree2 
                id="SubstationBusEditPointTree"
                value="#{capControlForm.pointTreeForm.pointList}" var="node"
                showRootNode="false" varNodeToggler="t" preserveToggle="true"
                clientSideToggle="false" showLines="false">

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
                        </x:commandLink>
                    </x:panelGroup>
                </f:facet>
            </x:tree2>
        </x:div>
        <f:verbatim>
            </fieldset>
        </f:verbatim>
    </x:panelGroup>
    
    <f:verbatim>
        <br />
    </f:verbatim>
    
    <h:outputText styleClass="tableHeader" value="Point Editor: " />
    
    <x:commandLink id="addPtLnk" value="Add Point"
        actionListener="#{capControlForm.pointTreeForm.addPointClick}">
        <f:param name="parentId"
            value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
    </x:commandLink>
    
    <f:verbatim>
        &nbsp; <bold>|</bold>&nbsp;
    </f:verbatim>
    
    <x:commandLink id="deletePtLnk" value="Delete Point"
        actionListener="#{capControlForm.pointTreeForm.deletePointClick}">
    </x:commandLink>

	</h:column>
	
	<h:column>
    
    <f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
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
    
    <h:selectBooleanCheckbox id="Use_Totalized_Value_Checkbox" 
        onclick="submit();"
        value="#{capControlForm.PAOBase.capControlFeeder.controlFlagBoolean}"
        rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"/>
    <x:outputLabel for="Use_Totalized_Value_Checkbox" 
        value="Use Totalized Values" 
        title="Check this box to use totalized phase values for control." 
        styleClass="smallStaticLabel"
        rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"/>
        
    <f:verbatim>
    <br/>
    <br/>
    </f:verbatim>
    
    <x:inputHidden id="varPoint" forceId="true" value="#{capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID }" />      
    <x:outputLabel for="feederVarDevice" value="Selected Point: " title="Data Point used for the current VAR value" rendered="#{!capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" styleClass="medStaticLabel"/>
    <x:outputLabel for="feederVarDevice" value="Selected Phase A Point: " title="Data Point used for the current VAR value" rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}" styleClass="medStaticLabel"/>
    <x:outputText id="feederVarDevice" forceId="true" value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}"/> 
    <x:outputText id="feederVarPointSeperator" forceId="true" value=" : " />
    <x:outputText id="feederVarPoint" forceId="true" value="#{capControlForm.pointNameMap[capControlForm.PAOBase.capControlFeeder.currentVarLoadPointID]}" /> 
    
    <f:verbatim>
    <br/>
    </f:verbatim>
    
    <h:outputLink  value="javascript:feederVarPointPicker.showPicker()" >
    <h:outputText value="Select point for Phase A" rendered="#{capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"/>
    <h:outputText value="Select point" rendered="#{!capControlForm.PAOBase.capControlFeeder.usePhaseDataBoolean}"/>
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
    <f:param name="ptId" value="0"/>
    </x:commandLink>
	<f:verbatim>
    </fieldset>
    </f:verbatim>
	<f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
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
    <f:param name="ptId" value="0"/>
    </x:commandLink>
    <f:verbatim>
    </fieldset>
    </f:verbatim>
	<f:verbatim>
    <br />
    <br />
    <fieldset class="fieldSet">
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
    <f:param name="ptId" value="0"/>
    </x:commandLink>
    <f:verbatim>
    </fieldset>
    </f:verbatim>
	</h:column>
    </h:panelGrid>
    </f:subview>
</f:subview>
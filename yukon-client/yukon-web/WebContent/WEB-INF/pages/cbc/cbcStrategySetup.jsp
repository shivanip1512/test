
<jsp:directive.page import="com.cannontech.web.editor.CapControlForm"/>
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil"/>
<jsp:directive.page import="com.cannontech.database.data.capcontrol.CapControlSubBus"/>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%
CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
%>
<f:subview id="outerView">

<%/*
<f:verbatim>
</f:verbatim>
	<x:selectBooleanCheckbox title="Use Seasonal Strategies" 
		id="seasonalCheckbox" 
		value="#{capControlForm.seasonalStratFlag}" 
		onclick="submit();"
		valueChangeListener="#{capControlForm.seasonCheckBoxChanged}"/>
	<x:outputLabel for="seasonalCheckbox" value="Use Seasonal Strategies" />
	<f:verbatim><br/></f:verbatim>
	<f:subview id="seasonalStrategies" rendered="#{capControlForm.seasonalStratFlag}" >
		<f:verbatim><br/></f:verbatim>
		<f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}" >
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:outputLabel for="Winter_Sub_Strategy_Selection" value="Winter Strategy: " title="The current control strategy we are using for winter."/>
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:selectOneMenu id="Winter_Sub_Strategy_Selection" 
				onchange="submit();" 
				disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlSubstationBus.winterStrategyID}" 
		        valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="winterStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="winterStratItemid" value="#{capControlForm.PAOBase.capControlSubstationBus.winterStrategyID}"/>
 		   	</x:commandLink>
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:outputLabel for="Summer_Sub_Strategy_Selection" value="Summer Strategy: " title="The current control strategy we are using for summer."/>
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:selectOneMenu id="Summer_Sub_Strategy_Selection" 
				onchange="submit();" 
				disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}" 
		        valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			<f:verbatim></td></f:verbatim>

			<f:verbatim><td></f:verbatim>
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}"/>
 		   	</x:commandLink>
			<f:verbatim></td></f:verbatim>			
			
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:outputLabel for="Spring_Sub_Strategy_Selection" value="Spring Strategy: " title="The current control strategy we are using for spring."/>
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:selectOneMenu id="Spring_Sub_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlSubstationBus.springStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			<f:verbatim></td></f:verbatim>
			
			<f:verbatim><td></f:verbatim>
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="springStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="springStratItemid" value="#{capControlForm.PAOBase.capControlSubstationBus.springStrategyID}"/>
 		   	</x:commandLink>
			<f:verbatim></td></f:verbatim>
			
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:outputLabel for="Fall_Sub_Strategy_Selection" value="Fall Strategy: " title="The current control strategy we are using for fall."/>
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			<x:selectOneMenu id="Fall_Sub_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlSubstationBus.fallStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			<f:verbatim></td></f:verbatim>
			
			<f:verbatim><td></f:verbatim>
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="fallStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="fallStratItemid" value="#{capControlForm.PAOBase.capControlSubstationBus.fallStrategyID}"/>
 		   	</x:commandLink>
			<f:verbatim></td></f:verbatim>
			
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
   		</f:subview>
   		
   		<f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Winter_Area_Strategy_Selection" value="Winter Strategy: " title="The current control strategy we are using for winter."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Winter_Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlArea.winterStrategyID}" 
	                valueChangeListener="#{capControlForm.newStrategySelected}" >
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="winterStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="winterStratItemid" value="#{capControlForm.PAOBase.capControlArea.winterStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Summer_Area_Strategy_Selection" value="Summer Strategy: " title="The current control strategy we are using for summer."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Summer_Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlArea.strategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerSratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlArea.strategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Spring_Area_Strategy_Selection" value="Spring Strategy: " title="The current control strategy we are using for spring."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Spring_Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlArea.springStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="springStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="springStratItemid" value="#{capControlForm.PAOBase.capControlArea.springStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Fall_Area_Strategy_Selection" value="Fall Strategy: " title="The current control strategy we are using for fall."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Fall_Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlArea.fallStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="fallStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="fallStratItemid" value="#{capControlForm.PAOBase.capControlArea.fallStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
   		</f:subview>

		<f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>

			<x:outputLabel for="Winter_Feeder_Strategy_Selection" value="Winter Strategy: " title="The current control strategy we are using for winter."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Winter_Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlFeeder.winterStrategyID}" 
	                valueChangeListener="#{capControlForm.newStrategySelected}" >
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="winterStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="winterStratItemid" value="#{capControlForm.PAOBase.capControlFeeder.winterStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Summer_Feeder_Strategy_Selection" value="Summer Strategy: " title="The current control strategy we are using for summer."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Summer_Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlFeeder.strategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlFeeder.strategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>			
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Spring_Feeder_Strategy_Selection" value="Spring Strategy: " title="The current control strategy we are using for spring."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Spring_Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlFeeder.springStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="springStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="springStratItemid" value="#{capControlForm.PAOBase.capControlFeeder.springStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Fall_Feeder_Strategy_Selection" value="Fall Strategy: " title="The current control strategy we are using for fall."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Fall_Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlFeeder.fallStrategyID}" 
		               valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="fallStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="fallStratItemid" value="#{capControlForm.PAOBase.capControlFeeder.fallStrategyID}"/>
 		   	</x:commandLink>
 		   	
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
   		</f:subview>
   		
  	</f:subview>
	  	
	<f:subview id="singleSeasonStrategy" rendered="#{!capControlForm.seasonalStratFlag}" >
	    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
			<f:verbatim><br/></f:verbatim>
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Area_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using."/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlArea.strategyID}" 
	                valueChangeListener="#{capControlForm.newStrategySelected}" >
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlArea.strategyID}"/>
 		   	</x:commandLink>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
	    </f:subview>
	
	    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}" >
			<f:verbatim><br/></f:verbatim>
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Sub_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Sub_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}" 
	                valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}"/>
 		   	</x:commandLink>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
	    </f:subview>
	
	    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
			<f:verbatim><br/></f:verbatim>
			<f:verbatim><table></f:verbatim>
			<f:verbatim><tr></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:outputLabel for="Feeder_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:selectOneMenu id="Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.PAOBase.capControlFeeder.strategyID}" 
	                valueChangeListener="#{capControlForm.newStrategySelected}">
				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim><td></f:verbatim>
			
			<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="summerStratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="summerStratItemid" value="#{capControlForm.PAOBase.capControlFeeder.strategyID}"/>
 		   	</x:commandLink>
			
			<f:verbatim></td></f:verbatim>
			<f:verbatim></tr></f:verbatim>
			<f:verbatim></table></f:verbatim>
	    </f:subview>
	</f:subview> 
	
	columnClasses="list-column-center, list-column-right, list-column-center, list-column-right" 
	
	*/ %>
	
	<h:outputText value="Season Schedule:"></h:outputText>
	<x:selectOneMenu id="Season_Schedule_Selection" 
  		onchange="submit();" 
  		disabled="#{capControlForm.editingCBCStrategy}"
		value="#{capControlForm.scheduleId}" 
        valueChangeListener="#{capControlForm.newScheduleSelected}">
		<f:selectItems value="#{capControlForm.cbcSchedules}"/>
	</x:selectOneMenu>
	<f:verbatim><br></f:verbatim>
	<f:verbatim><br></f:verbatim>
	<h:dataTable id="seasons"
		value="#{capControlForm.seasonsForSchedule}" 
		var="season">
		<h:column>
    		<f:facet name="header">
      			<h:outputText  value="Season Name"/>
    		</f:facet>
    		<h:outputText  value="#{season.seasonName}"/>
  		</h:column>
  		<h:column>
    		<f:facet name="header">
    			<h:outputText  value="Season Strategy"/>
    		</f:facet>
			<x:selectOneMenu id="Strategy_Selection" 
    				onchange="submit();" 
    				disabled="#{capControlForm.editingCBCStrategy}"
					value="#{capControlForm.assignedStratMap[season]}">

				<f:selectItems value="#{capControlForm.cbcStrategies}"/>
			</x:selectOneMenu>
		</h:column>
  		<h:column>
    		<f:facet name="header">
      			<h:outputText  value=""/>
    		</f:facet>
     		<x:commandLink action="#{capControlForm.dataModel.createEditorLink}" 
				value="Edit" 
				title="Click here to edit this strategy.">
	   			<f:param name="strattype" id="stratType" value="#{selLists.strategyEditorType}"/>
	   			<f:param name="stratitemid" id="stratItemid" value="#{capControlForm.assignedStratMap[season]}"/>
 		   	</x:commandLink>
  		</h:column>
	</h:dataTable> 
</f:subview>
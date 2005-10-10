<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>


<f:subview id="general" rendered="#{ptEditorForm.visibleTabs['General']}" >

	<f:verbatim><fieldset><legend>General</legend></f:verbatim>
	    <x:div id="topDiv">
	        <x:outputText value="Point Type: "/>
	        <x:outputText value="#{ptEditorForm.pointBase.point.pointType} (id: #{ptEditorForm.pointBase.point.pointID})" styleClass="staticLabel"/>
		<f:verbatim><br/></f:verbatim>
	        <x:outputText value="Parent: " />
	        <x:outputText value="#{dbCache.allPAOsMap[ptEditorForm.pointBase.point.paoID]} (id: #{ptEditorForm.pointBase.point.paoID})" styleClass="staticLabel"/>
	    </x:div>
		<f:verbatim><br/></f:verbatim>
		
		<x:outputLabel for="pointName" value="Point Name: "/>
		<x:inputText id="pointName" value="#{ptEditorForm.pointBase.point.pointName}" required="true" maxlength="60" />
	
		<f:verbatim><br/></f:verbatim>	
		<x:outputLabel for="logicalGrp" value="Timing Group: "/>
		<x:selectOneMenu id="logicalGrp" value="#{ptEditorForm.pointBase.point.logicalGroup}" >
			<f:selectItems value="#{ptEditorForm.logicalGroups}"/>
		</x:selectOneMenu>
	
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="outOfServiceCheckBox" value="#{ptEditorForm.pointBase.point.outOfService}"/>
		<x:outputLabel for="outOfServiceCheckBox" value="Disable Point"/>
	<f:verbatim></fieldset></f:verbatim>

	<f:verbatim><br/><br/><fieldset><legend>Archive</legend></f:verbatim>
		<x:outputLabel for="archiveType" value="Archive Data: "/>
		<x:selectOneMenu id="archiveType" value="#{ptEditorForm.pointBase.point.archiveType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.archiveTypeChanged}">
			<f:selectItem itemValue="None" value="None"/>
			<f:selectItem itemValue="On Change" value="On Change"/>
			<f:selectItem itemValue="On Timer" value="On Timer"/>
			<f:selectItem itemValue="On Update" value="On Update"/>
		</x:selectOneMenu>
	
	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="archiveInterval" value="Interval: "/>
		<x:selectOneMenu id="archiveInterval" value="#{ptEditorForm.pointBase.point.archiveInterval}" disabled="#{!ptEditorForm.archiveInterEnabled}">
			<f:selectItems value="#{ptEditorForm.timeInterval}"/>
		</x:selectOneMenu>
	<f:verbatim></fieldset></f:verbatim>


    <f:subview id="pointAnalog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
	    <f:verbatim><br/><br/><fieldset><legend>Analog Summary</legend></f:verbatim>
    
		<x:outputLabel for="uofm" value="Unit of Measure: "/>
		<x:selectOneMenu id="uofm" value="#{ptEditorForm.pointBase.pointUnit.uomID}" >
			<f:selectItems value="#{ptEditorForm.uofMs}"/>
		</x:selectOneMenu>        

	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="decDigits" value="Decimal Digits: "/>
		<x:selectOneMenu id="decDigits" value="#{ptEditorForm.pointBase.pointUnit.decimalPlaces}" >
			<f:selectItem itemValue="0" value="0"/>
			<f:selectItem itemValue="1" value="1"/>
			<f:selectItem itemValue="2" value="2"/>
			<f:selectItem itemValue="3" value="3"/>
			<f:selectItem itemValue="4" value="4"/>
			<f:selectItem itemValue="5" value="5"/>
			<f:selectItem itemValue="6" value="6"/>
			<f:selectItem itemValue="7" value="7"/>
			<f:selectItem itemValue="8" value="8"/>
		</x:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
    

    <f:subview id="pointStatus" rendered="#{ptEditorForm.visibleTabs['PointStatus'] || ptEditorForm.visibleTabs['PointCalcStatus']}" >
		<f:verbatim><br/><br/><fieldset><legend>Status Summary</legend></f:verbatim>
		<x:outputLabel for="stateGroups" value="State Group: "/>
		<x:selectOneMenu id="stateGroups" value="#{ptEditorForm.pointBase.point.stateGroupID}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.stateGroupChanged}">
			<f:selectItems value="#{ptEditorForm.stateGroups}"/>
		</x:selectOneMenu>

	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="initialState" value="Initial State: "/>
		<x:selectOneMenu id="initialState" value="#{ptEditorForm.pointBase.pointStatus.initialState}" >
			<f:selectItems value="#{ptEditorForm.initialStates}"/>
		</x:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
    
    <f:subview id="pointCalc" rendered="#{ptEditorForm.visibleTabs['PointCalcStatus'] || ptEditorForm.visibleTabs['PointCalc']}" >
		<f:verbatim><br/><br/><fieldset><legend>Calculation Summary</legend></f:verbatim>
		<x:outputLabel for="updateType" value="Update Type: "/>
		<x:selectOneMenu id="updateType" value="#{ptEditorForm.pointBase.calcBase.updateType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.updateTypeChanged}" >
			<f:selectItem itemValue="On First Change" value="On First Change"/>
			<f:selectItem itemValue="On All Change" value="On All Change"/>
			<f:selectItem itemValue="On Timer" value="On Timer"/>
			<f:selectItem itemValue="On Timer+Change" value="On Timer+Change"/>
			<f:selectItem itemValue="Historical" value="Historical"/>
		</x:selectOneMenu>

	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="calcStatRate" value="Update Rate: "/>
		<x:selectOneMenu id="calcStatRate" value="#{ptEditorForm.pointBase.calcBase.periodicRate}" disabled="#{!ptEditorForm.calcRateEnabled}" >
			<f:selectItems value="#{ptEditorForm.archiveIntervals}"/>
		</x:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>

</f:subview>
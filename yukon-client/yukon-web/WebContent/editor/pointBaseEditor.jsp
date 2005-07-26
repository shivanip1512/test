<%@ page import="com.cannontech.database.data.point.PointBase" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>


<%

%>
<f:subview id="general" rendered="#{ptEditorForm.visibleTabs['General']}" >

    <x:div id="topDiv">
        <h:outputText value="Point Type: "/>
        <h:outputText value="#{ptEditorForm.pointBase.point.pointType} (id: #{ptEditorForm.pointBase.point.pointID})" styleClass="staticLabel"/>
	<f:verbatim><br/></f:verbatim>
        <h:outputText value="Parent: " />
        <h:outputText value="#{dbCache.allPAOsMap[ptEditorForm.pointBase.point.paoID]} (id: #{ptEditorForm.pointBase.point.paoID})" styleClass="staticLabel"/>
    </x:div>
	<f:verbatim><br/></f:verbatim>
	
	<h:outputLabel for="pointName" value="Point Name: "/>
	<h:inputText id="pointName" value="#{ptEditorForm.pointBase.point.pointName}" required="true"/>
		<h:message for="pointName" showSummary="false" showDetail="false" />

	<f:verbatim><br/></f:verbatim>	
	<h:outputLabel for="logicalGrp" value="Timing Group: "/>
	<h:selectOneMenu id="logicalGrp" value="#{ptEditorForm.pointBase.point.logicalGroup}" >
		<f:selectItems value="#{ptEditorForm.logicalGroups}"/>
	</h:selectOneMenu>

	<f:verbatim><br/></f:verbatim>
	<h:selectBooleanCheckbox id="outOfServiceCheckBox" value="#{ptEditorForm.pointBase.point.outOfService}"/>
	<h:outputLabel for="outOfServiceCheckBox" value="Disable Point"/>

	<f:verbatim><br/><br/><fieldset><legend>Archive</legend></f:verbatim>
		<h:outputLabel for="archiveType" value="Archive Data: "/>
		<h:selectOneMenu id="archiveType" value="#{ptEditorForm.pointBase.point.archiveType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.archiveTypeChanged}">
			<f:selectItem itemValue="None" value="None"/>
			<f:selectItem itemValue="On Change" value="On Change"/>
			<f:selectItem itemValue="On Timer" value="On Timer"/>
			<f:selectItem itemValue="On Update" value="On Update"/>
		</h:selectOneMenu>
	
	    <f:verbatim><br/></f:verbatim>
		<h:outputLabel for="archiveInterval" value="Interval: "/>
		<h:selectOneMenu id="archiveInterval" value="#{ptEditorForm.pointBase.point.archiveInterval}" disabled="#{!ptEditorForm.archiveInterEnabled}">
			<f:selectItems value="#{ptEditorForm.archiveIntervals}"/>
		</h:selectOneMenu>
	<f:verbatim></fieldset></f:verbatim>


    <f:subview id="pointAnalog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
	    <f:verbatim><br/><br/><fieldset><legend>Analog Summary</legend></f:verbatim>
    
		<h:outputLabel for="uofm" value="Unit of Measure: "/>
		<h:selectOneMenu id="uofm" value="#{ptEditorForm.pointBase.pointUnit.uomID}" >
			<f:selectItems value="#{ptEditorForm.uofMs}"/>
		</h:selectOneMenu>        

	    <f:verbatim><br/></f:verbatim>
		<h:outputLabel for="decDigits" value="Decimal Digits: "/>
		<h:selectOneMenu id="decDigits" value="#{ptEditorForm.pointBase.pointUnit.decimalPlaces}" >
			<f:selectItem itemValue="0" value="0"/>
			<f:selectItem itemValue="1" value="1"/>
			<f:selectItem itemValue="2" value="2"/>
			<f:selectItem itemValue="3" value="3"/>
			<f:selectItem itemValue="4" value="4"/>
			<f:selectItem itemValue="5" value="5"/>
			<f:selectItem itemValue="6" value="6"/>
			<f:selectItem itemValue="7" value="7"/>
			<f:selectItem itemValue="8" value="8"/>
		</h:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
    

    <f:subview id="pointStatus" rendered="#{ptEditorForm.visibleTabs['PointStatus'] || ptEditorForm.visibleTabs['PointCalcStatus']}" >
		<f:verbatim><br/><br/><fieldset><legend>Status Summary</legend></f:verbatim>
		<h:outputLabel for="stateGroups" value="State Group: "/>
		<h:selectOneMenu id="stateGroups" value="#{ptEditorForm.pointBase.point.stateGroupID}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.stateGroupChanged}">
			<f:selectItems value="#{ptEditorForm.stateGroups}"/>
		</h:selectOneMenu>

	    <f:verbatim><br/></f:verbatim>
		<h:outputLabel for="initialState" value="Initial State: "/>
		<h:selectOneMenu id="initialState" value="#{ptEditorForm.pointBase.pointStatus.initialState}" >
			<f:selectItems value="#{ptEditorForm.initialStates}"/>
		</h:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
    
    <f:subview id="pointCalc" rendered="#{ptEditorForm.visibleTabs['PointCalcStatus'] || ptEditorForm.visibleTabs['PointCalc']}" >
		<f:verbatim><br/><br/><fieldset><legend>Calculation Summary</legend></f:verbatim>
		<h:outputLabel for="updateType" value="Update Type: "/>
		<h:selectOneMenu id="updateType" value="#{ptEditorForm.pointBase.calcBase.updateType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.updateTypeChanged}" >
			<f:selectItem itemValue="On First Change" value="On First Change"/>
			<f:selectItem itemValue="On All Change" value="On All Change"/>
			<f:selectItem itemValue="On Timer" value="On Timer"/>
			<f:selectItem itemValue="On Timer+Change" value="On Timer+Change"/>
			<f:selectItem itemValue="Historical" value="Historical"/>
		</h:selectOneMenu>

	    <f:verbatim><br/></f:verbatim>
		<h:outputLabel for="calcStatRate" value="Update Rate: "/>
		<h:selectOneMenu id="calcStatRate" value="#{ptEditorForm.pointBase.calcBase.periodicRate}" disabled="#{!ptEditorForm.calcRateEnabled}" >
			<f:selectItems value="#{ptEditorForm.archiveIntervals}"/>
		</h:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>

</f:subview>
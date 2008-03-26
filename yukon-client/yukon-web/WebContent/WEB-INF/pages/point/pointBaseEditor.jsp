<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="general" rendered="#{ptEditorForm.visibleTabs['General']}" >

<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn, gridColumn" >

	<h:column>
	<f:verbatim><fieldset class="fieldSet"><legend>General</legend></f:verbatim>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Point_Type" value="Point Type: "/>
        <x:outputText id="Point_Type" value="#{ptEditorForm.pointBase.point.pointType} (id: #{ptEditorForm.pointBase.point.pointID})" styleClass="staticLabel"/>

		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Parent" value="Parent: "/>
        <x:outputText  id = "Parent" value="#{databaseCache.allPAOsMap[ptEditorForm.pointBase.point.paoID]} (id: #{ptEditorForm.pointBase.point.paoID})" styleClass="staticLabel"/>
		<f:verbatim><br/><br/></f:verbatim>
		<x:outputLabel for="Point_Name" value="Point Name: "/>
		<x:inputText id="Point_Name" value="#{ptEditorForm.pointBase.point.pointName}" required="true" maxlength="60"
				styleClass="char32Label" />
	
		<f:verbatim><br/></f:verbatim>	
		<x:outputLabel for="Logical_Group" value="Timing Group: "/>
		<x:selectOneMenu id="Logical_Group" value="#{ptEditorForm.pointBase.point.logicalGroup}" >
			<f:selectItems value="#{ptEditorForm.logicalGroups}"/>
		</x:selectOneMenu>
	
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="Out_Of_Service" value="#{ptEditorForm.pointBase.point.outOfService}"/>
		<x:outputLabel for="Out_Of_Service" value="Disable Point"/>
	<f:verbatim></fieldset></f:verbatim>
	</h:column>
	
	<h:column >
	<f:subview id="archive_point_analog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
	<f:verbatim ><fieldset class="fieldSet"><legend>Archive</legend></f:verbatim>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Archive_Type" value="Archive Data: "/>
		<x:selectOneMenu id="Archive_Type" value="#{ptEditorForm.pointBase.point.archiveType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.archiveTypeChanged}">
			<f:selectItems value="#{selLists.ptArchiveType}"/>
		</x:selectOneMenu>
	
	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Archive_Interval" value="Interval: "/>
		<x:selectOneMenu id="Archive_Interval" value="#{ptEditorForm.pointBase.point.archiveInterval}" disabled="#{!ptEditorForm.archiveInterEnabled}">
			<f:selectItems value="#{ptEditorForm.archiveInterval}"/>
		</x:selectOneMenu>
	<f:verbatim></fieldset><br/></f:verbatim>
	</f:subview>
	
	<f:subview id="archive_point_status" rendered="#{ptEditorForm.visibleTabs['PointStatus']}" >
	<f:verbatim ><fieldset class="fieldSet"><legend>Archive</legend></f:verbatim>	
		<x:outputLabel for="archive_checkbox" value="Archive: "/>
		<x:selectBooleanCheckbox id="archive_checkbox" value="#{ptEditorForm.pointBase.point.archiveStatusData}"/>
		<f:verbatim></fieldset></f:verbatim>
	</f:subview>	
	

    <f:subview id="pointAnalog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
	    <f:verbatim><br/><fieldset class="fieldSet"><legend>Analog Summary</legend></f:verbatim>
    
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Unit_Of_Measure" value="Unit of Measure: "/>
		<x:selectOneMenu id="Unit_Of_Measure" value="#{ptEditorForm.pointBase.pointUnit.uomID}" >
			<f:selectItems value="#{ptEditorForm.uofMs}"/>
		</x:selectOneMenu>        

	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Decimal_Digits" value="Decimal Digits: "/>
		<x:selectOneMenu id="Decimal_Digits" value="#{ptEditorForm.pointBase.pointUnit.decimalPlaces}" >
			<f:selectItems value="#{ptEditorForm.decimalDigits}"/>
		</x:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
    

    <f:subview id="pointStatus" rendered="#{ptEditorForm.visibleTabs['PointStatus'] || ptEditorForm.visibleTabs['PointCalcStatus']}" >
		<f:verbatim><br/><br/><fieldset class="fieldSet"><legend>Status Summary</legend></f:verbatim>
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
		<f:verbatim><br/><br/><fieldset class="fieldSet"><legend>Calculation Summary</legend></f:verbatim>
		<x:outputLabel for="updateType" value="Update Type: "/>
		<x:selectOneMenu id="updateType" value="#{ptEditorForm.pointBase.calcBase.updateType}"
				onchange="submit();" valueChangeListener="#{ptEditorForm.updateTypeChanged}" >
			<f:selectItems value="#{selLists.ptUpdateType}"/>
		</x:selectOneMenu>

	    <f:verbatim><br/></f:verbatim>
		<x:outputLabel for="calcStatRate" value="Update Rate: "/>
		<x:selectOneMenu id="calcStatRate" value="#{ptEditorForm.pointBase.calcBase.periodicRate}" disabled="#{!ptEditorForm.calcRateEnabled}" >
			<f:selectItems value="#{ptEditorForm.archiveInterval}"/>
		</x:selectOneMenu>
		<f:verbatim></fieldset></f:verbatim>
    </f:subview>
	</h:column>

</h:panelGrid>

</f:subview>
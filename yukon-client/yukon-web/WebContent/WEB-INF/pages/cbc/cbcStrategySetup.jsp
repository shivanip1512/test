
<jsp:directive.page import="com.cannontech.web.editor.CapControlForm" />
<jsp:directive.page import="com.cannontech.web.util.JSFParamUtil" />
<jsp:directive.page
	import="com.cannontech.database.data.capcontrol.CapControlSubBus" />
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%
    CapControlForm capControlForm = (CapControlForm) JSFParamUtil.getJSFVar("capControlForm");
%>
<f:subview id="outerView">
	<x:panelGrid id="subStrategyBody" columns="2" styleClass="gridLayout"
		columnClasses="gridColumn">
		<h:column>
			<h:outputText value="Season Schedule:"></h:outputText>
			<x:selectOneMenu id="Season_Schedule_Selection" onchange="submit();"
				disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.scheduleId}"
				valueChangeListener="#{capControlForm.newScheduleSelected}">
				<f:selectItems value="#{capControlForm.cbcSchedules}" />
			</x:selectOneMenu>
			<f:verbatim>
				<br>
			</f:verbatim>
			<f:verbatim>
				<br>
			</f:verbatim>
			<h:dataTable id="seasons"
				value="#{capControlForm.seasonsForSchedule}" var="season">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Season Name" />
					</f:facet>
					<h:outputText value="#{season.seasonName}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Season Strategy" />
					</f:facet>
					<x:selectOneMenu id="Strategy_Selection" onchange="submit();"
						disabled="#{capControlForm.editingCBCStrategy}"
						value="#{capControlForm.assignedStratMap[season]}">

						<f:selectItems value="#{capControlForm.cbcStrategies}" />
					</x:selectOneMenu>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="" />
					</f:facet>
					<x:commandLink
						action="#{capControlForm.dataModel.createEditorLink}" value="Edit"
						title="Click here to edit this strategy.">
						<f:param name="strattype" id="stratType"
							value="#{selLists.strategyEditorType}" />
						<f:param name="stratitemid" id="stratItemid"
							value="#{capControlForm.assignedStratMap[season]}" />
					</x:commandLink>
				</h:column>
			</h:dataTable>
		</h:column>
		<h:column>
			<f:verbatim><table><tr><td></f:verbatim>
			<h:outputText value="Holiday Schedule:"></h:outputText>
            <f:verbatim></td><td></f:verbatim> 
            <x:selectOneMenu id="Holiday_Schedule_Selection"
				onchange="submit();"
				disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.holidayScheduleId}"
				valueChangeListener="#{capControlForm.newHolidayScheduleSelected}">
		  	   <f:selectItems value="#{capControlForm.cbcHolidaySchedules}" />
			</x:selectOneMenu> 
			<f:verbatim></td></tr><tr><td></f:verbatim> 
			<h:outputText value="Strategy:" />
			<f:verbatim></td><td></f:verbatim> 
			<x:selectOneMenu id="Holiday_Strategy_Selection"
				onchange="submit();"
				disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.holidayStrategyId}">

				<f:selectItems value="#{capControlForm.cbcHolidayStrategies}" />
			</x:selectOneMenu> <x:commandLink
				action="#{capControlForm.dataModel.createEditorLink}"
				value="Edit" title="Click here to edit this strategy.">
				<f:param name="strattype" id="stratType"
					value="#{selLists.strategyEditorType}" />
				<f:param name="stratitemid" id="stratItemid"
					value="#{capControlForm.holidayStrategyId}" />
			</x:commandLink>
			<f:verbatim></td></tr></table></f:verbatim>
		</h:column>
	</x:panelGrid>
</f:subview>
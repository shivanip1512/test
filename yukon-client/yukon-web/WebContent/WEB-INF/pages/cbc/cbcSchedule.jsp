<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="cbcSchedule" rendered="#{capControlForm.visibleTabs['CBCSchedule']}" >

	<h:panelGrid id="cbcBody" columns="1" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

		<h:column>
		    <f:verbatim><br/><fieldset><legend>Schedule</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="interval" value="Repeat Interval: " title="How often this schedule will execute" />
			<x:selectOneMenu id="interval" value="#{capControlForm.PAOBase.intervalRate}" >
				<f:selectItems value="#{capControlForm.timeInterval}"/>
			</x:selectOneMenu>


			<f:verbatim><br/></f:verbatim>
            <h:outputText id="nextRunTime" value="Next Run Time: "/>
            <x:inputDate id="nextRunTimeVal" value="#{capControlForm.PAOBase.nextRunTime}"
            	type="both" popupCalendar="true" />






			<f:verbatim></fieldset></f:verbatim>

		</h:column>

	
	</h:panelGrid>
		
    


</f:subview>
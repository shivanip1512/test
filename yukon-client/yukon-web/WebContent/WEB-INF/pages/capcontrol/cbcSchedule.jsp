<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<f:subview id="cbcSchedule" rendered="#{capControlForm.visibleTabs['CBCSchedule']}" >
    <x:htmlTag value="fieldSet" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Schedule"/></x:htmlTag>
        <x:panelGrid columns="2">
            <x:outputLabel for="interval" value="Repeat Interval: " title="How often this schedule will execute" />
            <x:selectOneMenu id="interval" value="#{capControlForm.PAOBase.intervalRate}" >
                <f:selectItem itemLabel="(none)" itemValue="0" />
                <f:selectItems value="#{capControlForm.scheduleRepeatTime}"/>
                <f:selectItem itemLabel="2 days" itemValue="172800" />
                <f:selectItem itemLabel="5 days" itemValue="432000" />
                <f:selectItem itemLabel="7 days" itemValue="604800" />
                <f:selectItem itemLabel="14 days" itemValue="1209600" />
                <f:selectItem itemLabel="30 days" itemValue="2592000" />
            </x:selectOneMenu>

            <h:outputText id="nextRunTime" value="Next Run Time: " title="The next time this shedule will run" />
            <x:inputDate id="nextRunTimeVal" value="#{capControlForm.PAOBase.nextRunTime}" type="both" popupCalendar="true" />
        
            <h:outputText id="lastRunTime" value="Last Run Time: " title="The last time this schedule ran" />
            <x:panelGroup>
                <h:outputText id="lastRunTimeVal" value="#{capControlForm.PAOBase.lastRunTime}"
                    rendered="#{capControlForm.PAOBase.lastRunTime.time > selLists.startOfTime}"   
                    styleClass="staticLabel" >
                    <f:convertDateTime dateStyle="long" type="both" timeZone="#{selLists.timeZone}"/>
                </h:outputText>
                <h:outputText id="lastRunTimeDash" value="---" styleClass="staticLabel"
                    rendered="#{capControlForm.PAOBase.lastRunTime.time <= selLists.startOfTime}" />
            </x:panelGroup>
        </x:panelGrid>
    </x:htmlTag>
</f:subview>
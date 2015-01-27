<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<f:verbatim>
    <script type="text/javascript">
        function updateSubScheduleTextField (selectElem, input) {
            var str = selectElem.id,
                inputId = str.substring(0,str.lastIndexOf(":")+1),
                selectedId = selectElem.selectedIndex,
                optionsArray = selectElem.options,
                inputElem;
            inputId += input;
            // There are colons in these selectors, which do not work in jQuery, so just use native js to get the element
            inputElem = document.getElementById(inputId);
            $(inputElem).val(optionsArray[selectedId].value);
        }
    </script>
</f:verbatim>

<f:subview id="subSchedSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}">

    <h:panelGrid id="subBody" columns="1" styleClass="gridLayout" rowClasses="gridCell" columnClasses="gridCell">

        <h:column>

            <x:htmlTag value="fieldSet" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Scheduled Tasks" /></x:htmlTag>
                <h:commandButton id="addAction" value="Add" styleClass="submenuLink"
                    rendered="#{capControlForm.editingAuthorized}"
                    action="#{capControlForm.addSchedule}"
                    onclick="yukon.da.common.lockButtonsPerSubmit('hdr_buttons'); yukon.da.common.lockButtonsPerSubmit('foot_buttons')"
                    title="Adds an existing schedule to the list below" />

                <h:dataTable id="schedData" var="paoSched" styleClass="fullTable"
                    headerClass="scrollerTableHeader" footerClass="scrollerTableHeader"
                    rowClasses="tableRow,altTableRow"
                    value="#{capControlForm.PAOBase.schedules}"
                    columnClasses="gridCellMedium,gridCellLarge,gridCellSmall">

                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Schedule"
                                title="A schedule use by this object" />
                        </f:facet>
                        <x:selectOneMenu id="sched" value="#{paoSched.scheduleID}"
                            onchange="yukon.da.common.lockButtonsPerSubmit('hdr_buttons'); yukon.da.common.lockButtonsPerSubmit('foot_buttons');submit();">
                            <f:selectItem itemLabel="(none)" itemValue="-1" />
                            <f:selectItems value="#{capControlForm.PaoScheduleSelectItems}" />
                        </x:selectOneMenu>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Outgoing Command"
                                title="The command this schedule will send" />
                        </f:facet>
                        <x:selectOneMenu id="schedCmd"
                            disabled="#{paoSched.scheduleID == -1}"
                            value="#{paoSched.command}"
                            onchange="updateSubScheduleTextField(this,'schedCmdText');">
                            <f:selectItems value="#{selLists.scheduleCmds}" />
                        </x:selectOneMenu>
                        <x:outputText value=" "></x:outputText>
                        <x:inputText size="70" id="schedCmdText" value="#{paoSched.command}" disabled="#{paoSched.scheduleID == -1}"/>

                    </h:column>
                    <h:column>
                       <f:facet name="header">
                            <x:outputText value="Disable OvUv"/>
                        </f:facet>
                       <h:selectBooleanCheckbox id="DisableOVUVCheckBox" value="#{paoSched.disableOvUvBoolean}"/>
                    </h:column>

                </h:dataTable>

                <x:htmlTag value="br"/>
                <x:outputText id="schedMsg" value="*Set the schedule selection to (none) to remove it from use" />

            </x:htmlTag>

        </h:column>

    </h:panelGrid>

</f:subview>
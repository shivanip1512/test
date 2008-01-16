
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:verbatim>
<script type="text/javascript">
function updateSubScheduleTextField( selectElem, input ) {
	var str = selectElem.id;
	var inputId = str.substring(0,str.lastIndexOf(":")+1);
	inputId += input;
	
	var selectedId = selectElem.selectedIndex;
	var array = selectElem.options;
	
	$(inputId).value = array[selectedId].value;
}
</script>
</f:verbatim>

<f:subview id="subSchedSetup" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}" >


    <h:panelGrid id="subBody" columns="1" styleClass="gridLayout" 
	    		rowClasses="gridCell" columnClasses="gridCell" >

		<h:column>

	    <f:verbatim><br/><br/><fieldset><legend>Scheduled Tasks</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
	       	<h:commandButton id="addAction" value="Add" styleClass="submenuLink"
	       		action="#{capControlForm.addSchedule}" 
                    onclick="lockButtonsPerSubmit('hdr_buttons'); lockButtonsPerSubmit('foot_buttons')"
	       		title="Adds an existing schedule to the list below" />
	                   	
	        <h:dataTable id="schedData" var="paoSched" 
	                styleClass="fullTable" headerClass="scrollerTableHeader"
	                footerClass="scrollerTableHeader"
	                rowClasses="tableRow,altTableRow" 
	        		value="#{capControlForm.PAOBase.schedules}"
					columnClasses="gridCellMedium,gridCellLarge,gridCellSmall" >
	
				<h:column>
					<f:facet name="header">
						<x:outputText value="Schedule" title="A schedule use by this object" />
					</f:facet>
					<x:selectOneMenu id="sched" value="#{paoSched.scheduleID}"
							onchange="lockButtonsPerSubmit('hdr_buttons'); lockButtonsPerSubmit('foot_buttons');submit();" >
						<f:selectItem itemLabel="(none)" itemValue="-1" />
						<f:selectItems value="#{paoScheduleForm.PAOSchedulesSelItems}" />
					</x:selectOneMenu>
				</h:column>
				<h:column>
					<f:facet name="header">
						<x:outputText value="Outgoing Command"
								title="The command this schedule will send" />
					</f:facet>
					<x:selectOneMenu id="schedCmd" disabled="#{paoSched.scheduleID == -1}"
							value="#{paoSched.command}" 
							onchange="updateSubScheduleTextField(this,'schedCmdText');">
						<f:selectItems value="#{selLists.scheduleCmds}"/>
					</x:selectOneMenu>
					<x:outputText value=" "></x:outputText>
					<x:inputText size="70" id="schedCmdText" value="#{paoSched.command}" disabled="#{paoSched.scheduleID == -1}"></x:inputText>
					
				</h:column>               

	        </h:dataTable>

			<f:verbatim><br/></f:verbatim>
			<x:outputText id="schedMsg" value="*Set the schedule selection to (none) to remove it from use" />

		<f:verbatim></fieldset></f:verbatim>



		</h:column>
		
    </h:panelGrid>


</f:subview>
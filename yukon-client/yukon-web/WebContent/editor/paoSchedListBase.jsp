<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">

<%@ page import="com.cannontech.web.editor.CapControlForm" %>

<%
//This is needed because this was handled in the CBCSerlvet before entering faces pages.
//Since the servlet bypass, this static method will need to be called entering any faces page.
	CapControlForm.setupFacesNavigation();
%>
<script type="text/javascript">
<!--
    var submitAllowed = true;
    function submitCheck() {
        if( !submitAllowed ) {
            submitAllowed = true;
            return false;
        }
        else
            return true;
    }

    function confirmDelete( schedName ) {
        if( confirm("Are you sure you want to delete '" + schedName + "'") ) {
            submitAllowed = true;
        }
        else
            submitAllowed = false;
    }
-->
</script>

    <x:panelLayout id="page" styleClass="pageLayout" headerClass="pageHeader"
            navigationClass="pageNavigation" bodyClass="pageBody"
            footerClass="pageFooter" >

        <f:facet name="body">
            <h:form id="schedListForm" onsubmit="return submitCheck();">

            <h:outputText styleClass="editorHeader" value="Schedules"/>
            <f:verbatim><br/><br/></f:verbatim>
            <x:commandButton value="Submit" action="#{paoDeleteForm.update}" styleClass="stdButton" title="Writes this item to the database" />
            <x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
            	<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
            </x:commandButton>
            <f:verbatim><br/><hr/><br/></f:verbatim>
            <x:messages id="messageList" showSummary="true" showDetail="true"
                    styleClass="smallResults" errorClass="errorResults" layout="table"/>

            <h:panelGrid id="body" columns="1" styleClass="pageBody">

                <f:facet name="header">
                    <x:panelGroup>
                    </x:panelGroup>
                </f:facet>


                <h:dataTable id="deleteItems" var="paoSched"
                        styleClass="fullTable" headerClass="scrollerTableHeader"
                        footerClass="scrollerTableHeader"
                        rowClasses="tableRow,altTableRow"
                        value="#{paoScheduleForm.PAOSchedules}"
                        columnClasses="gridCellMedium,gridCellMedium,gridCellMedium,gridCellSmall,gridCellSmall,gridCellSmall,gridCellSmall" >
                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Name" title="Name of the schedule" />
                        </f:facet>
                        <h:outputText value="#{paoSched.scheduleName}" />
                        
                   </h:column>
        
                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Next Run Time" title="The next time this scheduled is to run" />
                        </f:facet>
                        <h:outputText value="#{paoSched.nextRunTime}" rendered="#{paoSched.nextRunTime.time > selLists.startOfTime}" >
                            <f:convertDateTime type="both" timeStyle="full" dateStyle="full" timeZone="#{selLists.timeZone}"/>
                        </h:outputText>
                        <h:outputText value="---" rendered="#{paoSched.nextRunTime.time <= selLists.startOfTime}" />
                   </h:column>

                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Last Run Time" title="The last time this scheduled ran" />
                        </f:facet>
                        <h:outputText value="#{paoSched.lastRunTime}" rendered="#{paoSched.lastRunTime.time > selLists.startOfTime}" >
                            <f:convertDateTime type="both" timeStyle="full" dateStyle="full" timeZone="#{selLists.timeZone}"/>
                        </h:outputText>
                        <h:outputText value="---" rendered="#{paoSched.lastRunTime.time <= selLists.startOfTime}" />
                   </h:column>

                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Interval" title="The number of seconds this schedule will repeat" />
                        </f:facet>
                        <x:selectOneMenu id="interval" value="#{paoSched.intervalRate}" displayValueOnly="true" >
                            <f:selectItem itemLabel="(none)" itemValue="0" />
                            <f:selectItems value="#{capControlForm.scheduleRepeatTime}"/>
                            <f:selectItem itemLabel="2 days" itemValue="172800" />
                            <f:selectItem itemLabel="5 days" itemValue="432000" />
                            <f:selectItem itemLabel="7 days" itemValue="604800" />
                            <f:selectItem itemLabel="14 days" itemValue="1209600" />
                            <f:selectItem itemLabel="30 days" itemValue="2592000" />
                        </x:selectOneMenu>
                        
                   </h:column>

                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Disabled" title="Is the schedule disabled" />
                        </f:facet>
                        <h:outputText value="yes" rendered="#{paoSched.disabled}" styleClass="alert" />
                        <h:outputText value="no" rendered="#{!paoSched.disabled}" />
                   </h:column>

                   <h:column>
                        <h:commandLink id="editAction" styleClass="submenuLink"
                                title="Edit the schedules attributes"
                                actionListener="#{paoScheduleForm.edit}"
                                action="#{paoScheduleForm.goto_edit}" >
                            <x:graphicImage value="/editor/images/edit_item.gif" height="15" width="15" border="0" />
                            <f:param name="schedID" value="#{paoSched.scheduleID}" />
                        </h:commandLink>                        
                   </h:column>

                   <h:column>
                        <h:commandButton id="delAction" value="Delete" styleClass="submenuLink"
                            title="Remove the schedule from the system"
                            onclick="confirmDelete( '#{paoSched.scheduleName}' );" 
                            actionListener="#{paoScheduleForm.delete}"
                            title=" #{paoSched.scheduleID}" >
                                <f:param name="schedID" value="#{paoSched.scheduleID}" />
                        </h:commandButton>

                   </h:column>


                </h:dataTable>



                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
                        <x:commandButton value="Submit" action="#{paoDeleteForm.update}" styleClass="stdButton" title="Writes this item to the database" />
                        <x:commandButton value="Return" action="none" styleClass="stdButton" immediate="true" title="Returns to the last module page that was used to enter this editor" >
                            <f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
                        </x:commandButton>
                    </x:panelGroup>
                </f:facet>

            </h:panelGrid>

            </h:form>
        </f:facet>
    </x:panelLayout>


</cti:standardPage>
</f:view>
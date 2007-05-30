<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<f:view>
<cti:standardPage title="CapControl Wizard" module="capcontrol">


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
            <h:form id="stratListForm" onsubmit="return submitCheck();">

            <h:outputText styleClass="editorHeader" value="Strategies"/>
            <f:verbatim><br/><br/></f:verbatim>
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


                <h:dataTable id="deleteItems" var="cbcStrat"
                        styleClass="fullTable" headerClass="scrollerTableHeader"
                        footerClass="scrollerTableHeader"
                        rowClasses="tableRow,altTableRow"
                        value="#{capControlForm.allCBCStrats}"
                        columnClasses="gridCellMedium,gridCellMedium" >
                   <h:column>
                        <f:facet name="header">
                            <h:outputText value="Strategy Name" title="Name of the strategy" />
                        </f:facet>
                        <h:outputText value="#{cbcStrat.strategyName}" />
                   </h:column>
                   
                   <h:column>     
                        <f:facet name="header">
                            <h:outputText value="Edit/Delete Strategies" title="Edit/Delete Strategies" />
                        </f:facet>
                        <h:commandLink id="editAction" styleClass="submenuLink"
                                title="Edit the schedules attributes"
                                actionListener="#{capControlForm.editCBCStrat}">                            
                                <x:graphicImage value="/editor/images/edit_item.gif" 
                                height="15" width="15" border="0" />
                            <f:param name="stratID" value="#{cbcStrat.strategyID}" />
                        </h:commandLink>                        
                         <h:commandLink id="delAction" value="Delete" styleClass="submenuLink"
                            title="Remove the schedule from the system"
                            onclick="confirmDelete( '#{cbcStrat.strategyName}' );" 
                            actionListener="#{capControlForm.deleteStrat}"
                            title=" Delete #{cbcStrat.strategyName}" >
                                <f:param name="stratID" value="#{cbcStrat.strategyID}" />
                        </h:commandLink>
                   </h:column>


                </h:dataTable>



                <f:facet name="footer">
                    <x:panelGroup>
                        <f:verbatim><br/><hr/><br/></f:verbatim>
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
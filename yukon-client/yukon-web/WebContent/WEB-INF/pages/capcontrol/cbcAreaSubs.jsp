<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="areaSubs" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
    <f:verbatim>
        <br/>
        <fieldset class="fieldSet">
        <legend>Substation Assignment</legend>
        <br/>
    </f:verbatim>

    <h:panelGrid id="areaBody" columns="2" styleClass="gridLayout"
        rowClasses="gridCell" columnClasses="gridCell">
        <x:panelGroup>
            <x:div styleClass="scrollerDivWithBorder">
                <h:dataTable id="subAvailData" var="sub" 
                    styleClass="nonBorderedScrollerTable" headerClass="scrollerTableHeader"
                    footerClass="scrollerTableHeader"
                    rowClasses="tableRow,altTableRow"
                    value="#{capControlForm.dataModel.unassigned}">
                    <h:column>
                        <f:facet name="header"></f:facet>
                        <h:outputText value="#{sub.subID}" />
                    </h:column>
                    
                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Available Substations" />
                        </f:facet>
                        <x:outputText value="#{sub.subName}" title="#{sub.subName}"/>
                    </h:column>
	
                    <h:column>
                        <f:facet name="header"></f:facet>
                        <x:commandLink value="Add >>" action="#{capControlForm.dataModel.add}">
                            <f:param name="id" value="#{sub.subID}"/>
                        </x:commandLink>
                    </h:column>
	
                </h:dataTable>
            </x:div>
        </x:panelGroup>
		<x:panelGroup>
	        <h:dataTable id="subAssignData" var="sub"
	            styleClass="scrollerTable" headerClass="scrollerTableHeader"
	            footerClass="scrollerTableHeader"
	            rowClasses="tableRow,altTableRow"
	            rendered="#{capControlForm.visibleTabs['CBCArea']}"	                    
	            value="#{capControlForm.dataModel.assigned}"
	            columnClasses="scrollerLeft,scrollerLeft,scrollerCentered" >
	            
	            <h:column rendered="#{!capControlForm.dataModel.areaNone}">
	                <f:facet name="header"></f:facet>
	                <x:commandLink value="<< Remove" action="#{capControlForm.dataModel.remove}">
	                    <f:param name="id" value="#{sub.subID}"/>
	                </x:commandLink>
	            </h:column>
		
	            <h:column>
	                <f:facet name="header">
	                    <x:outputText value="Assigned Substations"/>
	                </f:facet>
	                <x:outputText value="#{sub.subName}" />
	            </h:column>
		
	            <h:column>
	                <f:facet name="header">
	                    <x:outputText value="Order" title="Order used for control (Range: 1 to 1000 )" />
	                </f:facet>
	                <x:inputText value="#{sub.displayOrder}" styleClass="char4Label" required="true" ></x:inputText>
	            </h:column>
		
	        </h:dataTable>
	
	        <f:verbatim>
	            <br/>
	            <br/>
	            <fieldset class="fieldSet">
	            <legend>Links</legend>
	            <br/>
	        </f:verbatim>
	        
	        <x:commandLink action="#{capControlForm.dataModel.createWizardLink}" 
	            value="Create Sub" 
	            title="Click here to create a sub. Return after creation to assign the sub.">
	            <f:param  name="type" id="type" value="#{selLists.substationType}"/>
	        </x:commandLink>
	        
	        <f:verbatim>
	            </fieldset>
	        </f:verbatim>
	        
	    </x:panelGroup>
    </h:panelGrid>
</f:subview>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="areaSubs" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Substation Assignment"/></x:htmlTag> 

	    <h:panelGrid id="areaBody" columns="2" styleClass="gridLayout" rowClasses="gridCell" columnClasses="gridCell, gridCell">
	        <x:panelGroup>
	            <x:div styleClass="scrollerDivWithBorder" style="height: expression(this.height > 400 ? '400px' : true);">
	                <h:dataTable id="subAvailData" var="sub" 
	                    styleClass="dataCell"
	                    headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
                        columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
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
	                        <x:commandLink value="Add >>" action="#{capControlForm.dataModel.add}" rendered="#{capControlForm.editingAuthorized}">
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
	                
	                    <x:commandLink value="<< Remove" action="#{capControlForm.dataModel.remove}" rendered="#{capControlForm.editingAuthorized}">
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
	                    <x:inputText value="#{sub.displayOrder}" styleClass="char4Label" required="true" >
	                       <f:validateLongRange maximum="1000" minimum="1"/>
	                    </x:inputText>
	                
	                </h:column>
	
	            </h:dataTable>
	
	            <x:htmlTag value="br"/>
	            <x:htmlTag value="br"/>
	            <x:htmlTag value="fieldset" styleClass="fieldSet" rendered="#{capControlForm.editingAuthorized}">
	               <x:htmlTag value="legend"><x:outputText value="Links"/></x:htmlTag> 
	     
	                <x:commandLink action="#{capControlForm.dataModel.createWizardLink}" 
	                   actionListener="#{capControlForm.setAreaTab}"
	                    value="Create Substation" 
	                    title="Click here to create a sub. Return after creation to assign the sub.">
	                    <f:param  name="type" id="type" value="#{selLists.substationType}"/>
	                    <f:param name="tabId" value="15"/>
	                </x:commandLink>
	     
	            </x:htmlTag>
	        	        
	        </x:panelGroup>
	        
	    </h:panelGrid>
    
    </x:htmlTag>
</f:subview>
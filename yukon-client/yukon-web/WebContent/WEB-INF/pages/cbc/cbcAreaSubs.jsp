<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="areaSubs" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
<f:subview id="areaSubBus" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
	    <f:verbatim><br/><br/>
	    	<fieldset>
	    	<legend>Substation Assignment</legend>
	    </f:verbatim>
	    <f:verbatim><br/></f:verbatim>

	    <h:panelGrid id="areaBody" columns="2" styleClass="gridLayout"
	    		rowClasses="gridCell" columnClasses="gridCell">
			<x:panelGroup>
	            <h:dataTable id="subAvailData" var="sub" 
	                    styleClass="scrollerTable" headerClass="scrollerTableHeader"
	                    footerClass="scrollerTableHeader"
	                    rowClasses="tableRow,altTableRow"
	            		value="#{capControlForm.dataModel.unassigned}"
						columnClasses="scrollerLeft,scrollerLeft,scrollerCentered"
	            		rows="25" >
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <h:outputText value="#{sub}" />
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                      <x:outputText value="Available Feeders" />
	                   </f:facet>
	                   <x:outputText value="Test Pao Name" title="Test Title"/>
	               </h:column>
	
	               <h:column>
	                   <f:facet name="header">
	                   </f:facet>
	                   <x:commandLink value="Add >>" action="#{capControlForm.dataModel.add}">
							<f:param name="id" value="#{sub}"/>
	                   </x:commandLink>
	               </h:column>
	
	            </h:dataTable>
	     
			</x:panelGroup>
	
	    </h:panelGrid>
	    <f:verbatim>
	    	</fieldset>
	    </f:verbatim>
    </f:subview>
</f:subview>
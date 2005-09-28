<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>

<f:subview id="cbcController" rendered="#{capControlForm.visibleTabs['CBCController']}" >

	<h:panelGrid id="cbcBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

		<h:column>
		    <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="serNumber" value="Serial Number: " title="The unique identifier for this CBC in the field"/>
	        <x:outputText id="serNumber"
	        	value="#{capControlForm.PAOBase.address}" styleClass="medLabel" />


			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="commRoute" value="Comm. Route: " title="The route of communication this CBC uses"/>
			<x:selectOneMenu id="bankMan" value="#{capControlForm.PAOBase.commID}" >
				<f:selectItem itemValue="1"/>
				<f:selectItem itemValue="2"/>
				<f:selectItem itemValue="3"/>
			</x:selectOneMenu>


			<f:verbatim></fieldset></f:verbatim>

		</h:column>


		<h:column>
		    <f:verbatim><br/><fieldset><legend>Controller Information</legend></f:verbatim>

			<x:outputText id="warnNoCBC" styleClass="alert"
					value="No CBC information avaible since the controller is not a CBC" />

			<f:verbatim><br/></f:verbatim>

			<f:verbatim></fieldset></f:verbatim>

		</h:column>
	

	
	</h:panelGrid>
		
    


</f:subview>
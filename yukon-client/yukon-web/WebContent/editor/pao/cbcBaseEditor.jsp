<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>


<f:subview id="general" rendered="#{capControlForm.visibleTabs['General']}" >

	<f:verbatim><fieldset><legend>General</legend></f:verbatim>
	    <x:div id="topDiv">

        <x:outputText value="Type: " title="System specific type of CapControl object" />
        <x:outputText value="#{capControlForm.PAOBase.PAOType} (id: #{capControlForm.PAOBase.PAObjectID})" styleClass="staticLabel"/>
		<f:verbatim><br/></f:verbatim>
        <x:outputText value="Class: " title="System specific class of CapControl object" />
        <x:outputText value="#{capControlForm.PAOBase.PAOClass}" styleClass="staticLabel"/>
		<f:verbatim><br/></f:verbatim>
        <x:outputText value="Parent: " title="The parent object this item belongs to" />
        <x:outputText value="#{capControlForm.parent}" styleClass="staticLabel"/>
		<f:verbatim><br/></f:verbatim>

		<x:outputLabel for="paoName" value="Name: " title="System wide label for this object" />
		<x:inputText id="paoName" value="#{capControlForm.PAOBase.PAOName}" required="true" maxlength="60" />
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="disablePao" value="#{capControlForm.PAOBase.disabled}"/>
		<x:outputLabel for="disablePao" value="Disable" title="Removes this item from automatic control" />

	    </x:div>
		<f:verbatim><br/></f:verbatim>

	<f:verbatim></fieldset></f:verbatim>    


</f:subview>
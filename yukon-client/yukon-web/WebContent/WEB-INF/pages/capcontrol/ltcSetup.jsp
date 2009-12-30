<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<f:subview id="ltcView" rendered="#{capControlForm.visibleTabs['LTC']}">
    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="LTC Points"/></x:htmlTag>
        
    </x:htmlTag>            
</f:subview>
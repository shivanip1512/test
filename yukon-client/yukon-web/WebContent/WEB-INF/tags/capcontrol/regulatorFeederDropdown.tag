<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" required="true" type="java.lang.String" description="Path for spring binding"%>
<%@ attribute name="feederList" required="true" type="java.util.List" description="List of Feeders to show in dropdown"%>

<cti:msg2 var="noneSelected" key="yukon.web.components.button.selectionPicker.label"/>
    
<span class="ML50">
    <i:inline key="yukon.web.modules.capcontrol.ivvc.zoneWizard.table.point.feeder"/>:
    <tags:selectWithItems path="${path}" items="${feederList}" itemLabel="ccName" 
        itemValue="ccId" defaultItemLabel="${noneSelected}" inputClass="ML15"/>
</span>
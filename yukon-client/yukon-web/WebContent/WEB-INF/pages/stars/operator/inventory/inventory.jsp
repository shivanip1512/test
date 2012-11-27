<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="inventory.${mode}">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>
<cti:msgScope paths=",modules.operator.hardware">

    <tags:setFormEditMode mode="${mode}"/>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/stars/operator/inventory/update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/stars/operator/inventory/create" var="action"/>
    </cti:displayForPageEditModes>
    
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout hardwareTable">
    
        <%-- LEFT SIDE COLUMN --%>
        <%-- COMMON HARDWARE INFO --%>
        <%@ include file="../hardware/hardwareInfo.jspf" %>
        
        <%-- RIGHT SIDE COLUMN --%>
        <cti:displayForPageEditModes modes="VIEW">
            <cti:dataGridCell>
                
                <%-- COMMON HARDWARE HISTORY --%>
                <%@ include file="../hardware/hardwareHistory.jspf" %>
                
            </cti:dataGridCell>
        </cti:displayForPageEditModes>
    
    </cti:dataGrid>
    
    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="${editingRoleProperty}">
            <cti:url value="/stars/operator/inventory/edit" var="editUrl">
                <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
            </cti:url>
            <cti:button nameKey="edit" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
</cti:msgScope>
</cti:standardPage>
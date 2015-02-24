<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="inventory.${mode}">
<cti:msgScope paths=",modules.operator.hardware">

    <tags:setFormEditMode mode="${mode}"/>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/stars/operator/inventory/update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/stars/operator/inventory/create" var="action"/>
    </cti:displayForPageEditModes>
 <c:if test="${not empty hardware}">
    <div class="column-12-12">
        <div class="column one">
            <%@ include file="../hardware/hardwareInfo.jspf" %>
        </div>

        <cti:displayForPageEditModes modes="VIEW">
            <div class = "column two nogutter">
                <%@ include file="../hardware/hardwareHistory.jspf" %>
            </div>
        </cti:displayForPageEditModes>
    </div>
 </c:if>   

    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="${editingRoleProperty}">
            <cti:url value="/stars/operator/inventory/edit" var="editUrl">
                <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
            </cti:url>
		    <div class="page-action-area clear">
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
		    </div>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
</cti:msgScope>
</cti:standardPage>
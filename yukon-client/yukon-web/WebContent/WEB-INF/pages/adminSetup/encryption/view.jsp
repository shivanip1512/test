<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="adminSetup" page="encryption">
    <script type="text/javascript">
    document.observe("dom:loaded", function() {
        if (${not empty showRouteError}) {
            hideOtherForms(${showRouteError});
        }
    });

    function showDisabled(formId) {
        var formChildren = $(formId).childElements();
        formChildren[2].hide();
        formChildren[0].show();
    }

    function showEnabled(formId) {
        var formChildren = $(formId).childElements();
        formChildren[3].hide();
        formChildren[1].show();
    }

    function showAddKey(formId) {
        var formChildren = $(formId).childElements();
        formChildren[0].hide();
        formChildren[2].show();
        hideOtherForms(formId);
    }

    function addKey(formId) {
        Yukon.ui.blockPage();
        $(formId).action = "save";
        $(formId).submit();
    }

    function showDeleteConfirm(formId) {
        var formChildren = $(formId).childElements();
        formChildren[1].hide();
        formChildren[3].show();
        hideOtherForms(formId);
    }

    function hideOtherForms(formId) {
        $$('form').each(function(item) {
            if (item.id != formId)
                item.disable();
        });
    }
    
    function confirmDelete(formId) {
        $(formId).reset();
        Yukon.ui.blockPage();
        $(formId).action = "delete";
        $(formId).submit();
    }
</script>
    <table class="resultsTable">
        <tr>
            <th><i:inline key=".paoNameLbl" /></th>
            <th><i:inline key=".paoTypeLbl" /></th>
            <th><i:inline key=".CPSkeyLbl" /></th>
        </tr>
        <c:forEach var="route" items="${encryptedRoutes}">
            <tr>
                <td>${fn:escapeXml(route.paoName)}</td>
                <td>${fn:escapeXml(route.type.paoTypeName)}</td>
                <td width="50%">
                    <c:set var="enableDisplay" value="all" /> 
                    <c:set var="disableDisplay" value="none" /> 
                    <c:set var="deleteDisplay" value="none" />
                    <c:set var="saveDisplay" value="none" /> 
                    <c:if test="${route.encrypted}">
                        <c:set var="enableDisplay" value="none" />
                        <c:set var="disableDisplay" value="all" />
                    </c:if> 
                    <c:if test="${route.paobjectId == showRouteError}">
                        <c:set var="saveDisplay" value="all" />
                        <c:set var="disableDisplay" value="none" />
                        <c:set var="enableDisplay" value="none" />
                    </c:if> 
                    <form:form id="${route.paobjectId}" name="allForms" 
                        commandName="encryptedRoute" method="POST" autocomplete="off">
                        <span style="display:${enableDisplay}">
                            <cti:button key="enableEncryptionBtn"
                                onclick="javascript:showAddKey('${route.paobjectId}')" /> 
                        </span>
                        <span style="display:${disableDisplay}"> 
                            <i:inline key=".keyFoundLbl" /> 
                            <cti:button key="deleteBtn" 
                                onclick="javascript:showDeleteConfirm('${route.paobjectId}')" /> 
                        </span>
                        <span style="display:${saveDisplay}"> 
                            <i:inline key=".keyLbl" /> 
                            <tags:input path="value" size="50" /> 
                            <cti:button key="saveBtn" onclick="javascript:addKey('${route.paobjectId}')" /> 
                            <cti:button key="cancelBtn" href="view" /> 
                        </span>
                        <span style="display:${deleteDisplay}"> <i:inline key=".confirmDeleteMsg" />
                        <cti:button key="confirmDeleteBtn" onclick="javascript:confirmDelete('${route.paobjectId}')" /> 
                            <cti:button key="cancelBtn" href="view" /> 
                        </span>
                        <input name="paobjectId" type="hidden" value="${route.paobjectId}" />
                        <input name="type" type="hidden" value="${route.type}" />
                        <input name="paoName" type="hidden" value="${route.paoName}" />
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty encryptedRoutes}">
            <tr>
                <td colspan="4">
                	<i:inline key=".noRoutesMsg" />
                </td>
            </tr>
        </c:if>
    </table>
</cti:standardPage>

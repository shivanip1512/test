<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="notificationGroup.list">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/tools/notificationGroup/create" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="js-create-option" href="${createUrl}"/>
    </div>

    <hr/>
    <div class="js-filtered-results-container">
        <table class="compact-results-table">
            <thead>
                <th><i:inline key="yukon.common.name"/></th>
                <th><i:inline key="yukon.common.status"/></th>
            </thead>
            <tbody>
                <c:forEach var="notificationGroup" items="${notificationGroups}">
                    <c:set var="cssClass" value="error" />
                    <cti:msg2 var="status" key="yukon.common.disabled"/>
                    <c:if test="${notificationGroup.enabled}">
                        <c:set var="cssClass" value="success" />
                        <cti:msg2 var="status" key="yukon.common.enabled"/>
                    </c:if>
                    <tr>
                        <td>
                            <cti:url value="/tools/notificationGroup/${notificationGroup.id}" var="viewUrl"/>
                            <a href="${viewUrl}">${fn:escapeXml(notificationGroup.name)}</a>
                        </td>
                        <td class="${cssClass}">${status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</cti:standardPage>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="routes.list">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/routes/create" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="js-create-option" href="${createUrl}"/>
    </div>

    <hr/>
    
    <div class="js-filtered-results-container">
        <table class="compact-results-table">
            <thead>
                <th><i:inline key="yukon.common.name"/></th>
                <th><i:inline key="yukon.common.type"/></th>
            </thead>
            <tbody>
                <c:forEach var="routes" items="${nonCCUAndMacroRoutes}">
                    <c:set var="cssClass" value="error" />
                    <cti:msg2 var="status" key="yukon.common.disabled"/>
                    <c:if test="${routes.enable}">
                        <c:set var="cssClass" value="success" />
                        <cti:msg2 var="status" key="yukon.common.enabled"/>
                    </c:if>
                    <tr>
                        <td>
                            <cti:url value="/stars/device/routes/${routes.deviceId}" var="viewUrl"/>
                            <a href="${viewUrl}">${fn:escapeXml(routes.deviceName)}</a>
                        </td>
                        <td><i:inline key="${routes.deviceType}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</cti:standardPage>
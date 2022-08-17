<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:msgScope paths="modules.operator.gateways,modules.operator">
<cti:url var="updateUrl"  value="/stars/gateways/firmware-upgrade" />

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<div class="empty-list stacked">
    <i:inline key=".firmwareUpdate.note"/>
</div>

<form:form id="firmware-upgrade-form" modelAttribute="gateways" action="${updateUrl}">
    <cti:csrfToken/>
    <div class="scroll-lg js-resize-with-dialog">
    <table class="compact-results-table with-form-controls js-select-all-container">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".version.current"/></th>
                <th><i:inline key=".version.available"/></th>
                <th><i:inline key=".updateServer"/></th>
                <th class="tar">
                    <span><i:inline key=".firmwareUpdate.send"/></span>
                    <input type="checkbox" class="js-select-all">
                </th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gateway" items="${gateways.list}" varStatus="loop">
                <c:set var="idx" value="${loop.index}" />
                <tags:setFormEditMode mode="VIEW"/>
                <tr>
                    <td>
                        <form:hidden path="list[${idx}].id"/>

                        <tags:input path="list[${idx}].name"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].currentVersion"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].availableVersion"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].updateServerUrl" inputClass="js-update-server "/>
                    </td>
                    <td class="tar">
                        <tags:setFormEditMode mode="EDIT"/>
                        <c:if test="${gateway.updateAvailable and gateway.upgradeable}">
                            <tags:checkbox path="list[${idx}].sendNow" styleClass="js-send-now js-select-all-item"/>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
</form:form>
</cti:msgScope> 
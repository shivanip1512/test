<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:msgScope paths="modules.operator.gateways,modules.operator">
<cti:msg2 var="phUsername" key=".username.ph"/>
<cti:msg2 var="phPassword" key=".password.ph"/>
<cti:url var="updateUrl"  value="/stars/gateways/update-servers" />

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<form:form id="update-servers-form" modelAttribute="allSettings" action="${updateUrl}">
    <cti:csrfToken/>
    <div class="scroll-lg js-resize-with-dialog">
    <table class="compact-results-table with-form-controls">
        <thead>
            <tr>
                <th><i:inline key=".name" /></th>
                <th><i:inline key=".version.current" /></th>
                <th><i:inline key=".version.available" /></th>
                <th><i:inline key=".updateServer" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gateway" items="${allSettings.list}" varStatus="loop">
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
                        <tags:setFormEditMode mode="EDIT"/>
                        <div>
                            <span class="vatb form-control">
                                <i:inline key=".updateServer.default"/>
                            </span>
                            <tags:switchButton path="list[${idx}].useDefault"
                                onClasses="fn" offClasses="fn" onNameKey=".yes.label" offNameKey=".no.label"
                                toggleGroup="toggle-update-server-${idx}" toggleAction="hide" toggleInverse="true"/>
                        </div>
                        <div data-toggle-group="toggle-update-server-${idx}">
                            <i:inline key=".updateServer.server"/>
                            <tags:input path="list[${idx}].updateServerUrl" size="40" toggleGroup="toggle-update-server-${idx}"/>
                        </div>
                        <div data-toggle-group="toggle-update-server-${idx}">
                            <i:inline key=".updateServer.auth"/>
                            <spring:bind path="list[${idx}].updateServerLogin.username">
                                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                                <form:input path="list[${idx}].updateServerLogin.username" cssClass="M0 left ${clazz}" 
                                    placeholder="${phUsername}" size="12" data-toggle-group="toggle-update-server-${idx}"/>
                            </spring:bind>
                            <spring:bind path="list[${idx}].updateServerLogin.password">
                                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                                <form:password path="list[${idx}].updateServerLogin.password" cssClass="M0 ${clazz} right"
                                    placeholder="${phPassword}" showPassword="true" size="12" data-toggle-group="toggle-update-server-${idx}"/>
                            </spring:bind>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
</form:form>
</cti:msgScope>
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

<form:form id="update-servers-form" commandName="allSettings" action="${updateUrl}">
    <cti:csrfToken/>
    <table class="compact-results-table with-form-controls">
        <thead>
            <tr>
                <th><i:inline key=".name" /></th>
                <%--
                <th><i:inline key=".serialNumber" /></th> 
                --%>
                <th><i:inline key=".version.current" /></th>
                <th><i:inline key=".version.available" /></th>
                <th><i:inline key=".updateServer" /></th>
                <th></th>
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
                    <%--
                    <td>
                        <tags:input path="list[${idx}].serialNumber"/>
                    </td>
                    --%>
                    <td>
                        <tags:input path="list[${idx}].currentVersion"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].availableVersion"/>
                    </td>
                    <td>
                        <div>
                            <span data-edit-group="update-server-${idx}">
                                <span class="js-view">
                                    <spring:bind path="list[${idx}].useDefault">
                                        <c:if test="${status.value}">
                                            <span class="empty-list"><i:inline key=".updateServer.default"/></span>
                                        </c:if>
                                    </spring:bind>
                                </span>
                                <span class="js-edit dn">
                                    <div>
                                        <span class="vatb form-control">
                                            <i:inline key=".updateServer.default"/>
                                        </span>
                                        <tags:setFormEditMode mode="EDIT"/>
                                        <tags:switchButton path="list[${idx}].useDefault" 
                                            onClasses="fn" offClasses="fn" onNameKey=".yes.label" offNameKey=".no.label"
                                            toggleGroup="toggle-update-server-${idx}" toggleAction="hide" toggleInverse="true"/>
                                    </div>
                                    <span data-toggle-group="toggle-update-server-${idx}">
                                        <i:inline key=".updateServer.server"/>
                                    </span>
                                </span>
                            </span>
                            <tags:inlineEdit name="update-server-${idx}" hideControls="true">
                                <span data-toggle-group="toggle-update-server-${idx}">
                                    <tags:input path="list[${idx}].updateServerUrl" size="40" toggleGroup="toggle-update-server-${idx}"/>
                                </span>
                            </tags:inlineEdit>

                        </div>
                        <div data-edit-group="update-server-${idx}" data-toggle-group="toggle-update-server-${idx}">
                            <span class="js-edit dn">
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
                            </span>
                        </div>
                    </td>
                    <td>
                        <span data-edit-group="update-server-${idx}">
                            <span class="js-view">
                                <cti:button icon="icon-pencil" renderMode="image" data-edit-toggle="update-server-${idx}" nameKey="edit"/>
                            </span>
                            <span class="js-edit dn">
                                <div>
                                <cti:button icon="icon-cross" renderMode="image" data-edit-toggle="update-server-${idx}" nameKey="cancel"/>
                                </div>
                            </span>
                        </span>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</form:form>
</cti:msgScope>
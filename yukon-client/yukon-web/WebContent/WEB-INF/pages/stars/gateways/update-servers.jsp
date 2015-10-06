<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:msgScope paths="modules.operator.gateways">
<cti:url var="updateUrl"  value="/stars/gateways/update-servers" />

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<form:form id="update-servers-form" commandName="allSettings" action="${updateUrl}">
    <cti:csrfToken/>
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key=".name" /></th>
                <th><i:inline key=".updateServer.url" /></th>
                <th><i:inline key=".updateServer.login.user" /></th>
                <th><i:inline
                        key=".updateServer.login.password" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gateway" items="${allSettings.list}" varStatus="loop">
                <c:set var="idx" value="${loop.index}" />
                <tr>
                    <td>
                        <form:hidden path="list[${idx}].id"/>
                        <form:hidden path="list[${idx}].name"/>
                        <spring:bind path="list[${idx}].name">
                            ${fn:escapeXml(status.value)}
                        </spring:bind>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].updateServerUrl"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].updateServerLogin.username"/>
                    </td>
                    <td>
                        <tags:input path="list[${idx}].updateServerLogin.password"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</form:form>
</cti:msgScope>
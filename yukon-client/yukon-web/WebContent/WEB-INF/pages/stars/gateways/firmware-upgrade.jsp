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

<form:form id="update-servers-form" commandName="gateways" action="${updateUrl}">
    <cti:csrfToken/>
    <table class="compact-results-table with-form-controls js-select-all-container">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".version.current"/></th>
                <th><i:inline key=".version.available"/></th>
                <th>
                    <input type="checkbox" class="js-select-all">
                    <span><i:inline key=".firmwareUpdate.send"/></span> 
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
                        <tags:setFormEditMode mode="EDIT"/>
                        <tags:checkbox path="list[${idx}].sendNow" styleClass="js-select-all-item"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</form:form>
</cti:msgScope> 
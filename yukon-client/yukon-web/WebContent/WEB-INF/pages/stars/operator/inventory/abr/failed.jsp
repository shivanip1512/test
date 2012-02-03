<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.operator.abr">
    <tags:boxContainer2 nameKey="failedItems" id="failedItems">
        <table class="compactResultsTable">
            <tr>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".reason"/></th>
            </tr>
            <c:forEach var="failure" items="${failed}">
                <tr>
                    <td>${failure.serialNumber}</td>
                    <td><i:inline key="${failure.failureReason}"/></td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
</cti:msgScope>
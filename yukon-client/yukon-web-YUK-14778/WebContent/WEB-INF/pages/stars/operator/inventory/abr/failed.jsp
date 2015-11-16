<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.abr">
    <tags:sectionContainer2 nameKey="failedItems" id="failed-items">
        <table class="compact-results-table dashed">
            <thead>
                <tr>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".reason"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="failure" items="${failed}">
                    <tr>
                        <td>${failure.serialNumber}</td>
                        <td><i:inline key="${failure.failureReason}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </tags:sectionContainer2>
</cti:msgScope>
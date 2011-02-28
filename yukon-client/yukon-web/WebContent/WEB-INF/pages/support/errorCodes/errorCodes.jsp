<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="errorCodes">

    <cti:standardMenu menuSelection="information|errorCodes" />

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
        <cti:crumbLink url="/spring/support/" title="Support" />
        <cti:crumbLink><i:inline key=".title"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><i:inline key=".title"/></h2>
    <br>

    <table id="errorCodes" class="resultsTable">
        <tr>
            <th><i:inline key=".header.code"/></th>
            <th><i:inline key=".header.category"/></th>
            <th><i:inline key=".header.description"/></th>
            <th><i:inline key=".header.porter"/></th>
            <th><i:inline key=".header.troubleshoot"/></th>
        </tr>

        <c:forEach items="${allErrors}" var="error">
            <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                <td nowrap="nowrap">${error.errorCode}</td>
                <td nowrap="nowrap">${error.category}</td>
                <td>${error.description}</td>
                <td>${error.porter}</td>
                <td>${error.troubleshooting}</td>
            </tr>
        </c:forEach>
    </table>

</cti:standardPage>
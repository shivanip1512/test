<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="errorCodes">

<style>
#errorCodes td {
    vertical-align: top
}

#errorCodes ul {
    margin: 0 0 0 2em;
    padding: 0;
}

#errorCodes li {
    margin: 0;
    padding: 0;
    list-style-type: disc;
}
</style>

    <table id="errorCodes" class="results-table">
        <thead>
            <tr>
                <th><i:inline key=".header.code"/></th>
                <th><i:inline key=".header.category"/></th>
                <th><i:inline key=".header.description"/></th>
                <th><i:inline key=".header.porter"/></th>
                <th><i:inline key=".header.troubleshoot"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${allErrors}" var="error">
                <tr>
                    <td nowrap="nowrap">${error.errorCode}</td>
                    <td nowrap="nowrap">${error.category}</td>
                    <td>${error.description}</td>
                    <td>${error.porter}</td>
                    <td>${error.troubleshooting}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>
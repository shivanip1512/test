<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:standardPage module="tools" page="configs.summary">

    <table class="compact-results-table">
        <thead>
            <tr>
                <th>Device Name</th>
                <th>Type</th>
                <th>Device Configuration</th>
                <th>Last Action</th>
                <th>Last Action Status</th>
                <th>In Sync</th>
                <th>Last Action Start</th>
                <th>Last Action End</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="detail" items="${results}">
                <tr>
                    <td>${detail.device.name}</td>
                    <td>${detail.device.paoIdentifier.paoType}</td>
                    <td>${detail.deviceConfig.name}</td>
                    <td>${detail.action}</td>
                    <td>${detail.status}</td>
                    <td>${detail.inSync}</td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionStart}"/></td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionEnd}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>
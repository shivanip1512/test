<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div style="max-height: 350px; overflow: auto;">
    <table>
        <tr>
            <td>CBC Name</td>
            <td>Point Id</td>
            <td>Point Name</td>
        </tr>
        <c:forEach var="orphan" items="${orphans}">
            <tr>
                <td>${fn:escapeXml(orphan.deviceName)}</td>
                <td>${orphan.pointId}</td>
                <td>
                    <a href="javascript:void(0);" onclick="closeOrphanedCBCPopup(); setCBC('${orphan.deviceName}', '${orphan.pointId}','${orphan.pointName}');">
                       ${fn:escapeXml(orphan.pointName)}
                    </a>
                </td>
            </tr>    
        </c:forEach>
    </table>
</div>
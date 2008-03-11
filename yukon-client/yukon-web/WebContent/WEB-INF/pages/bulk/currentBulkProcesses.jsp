<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="title" key="yukon.common.device.bulk.currentBulkProcesses.title" />
<cti:standardPage title="${title}" module="amr">
<cti:standardMenu/>

    <c:if test="${fn:length(inProcessList) == 0}">
        No Bulk processes are currently running.
    </c:if>

    <c:forEach var="process" items="${inProcessList}">
        <tags:bulkProcess process="${process}"/><br><br>
    </c:forEach>

</cti:standardPage>
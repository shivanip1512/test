<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Debug Home Page" module="debug">
    <cti:standardMenu menuSelection="scheduler|active" />
    
    <table>
    <c:forEach items="${scheduledJobInfo}" var="jobInfo">
        <tr>
            <td>${jobInfo.job.jobDefinition.title}</td>
            <td>${jobInfo.time}</td>
            <td>${jobInfo.running}</td>
        </tr>            
    </c:forEach>
    </table>
    
</cti:standardPage>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<table id="js-sorted-assigned-programs-table" class="compact-results-table dashed">
    <thead>
        <tr>
            <tags:sort column="${name}" />
            <tags:sort column="${startPriority}" />
            <tags:sort column="${stopPriority}" />
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty controlArea.programAssignment}">
                <tr>
                    <td>
                        <span class="empty-list"><i:inline key=".noProgramsAssigned"/></span>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="assignedProgram" items="${controlArea.programAssignment}">
                    <tr>
                        <td>
                            <cti:url var="viewUrl" value="/dr/setup/loadProgram/${assignedProgram.programId}"/>
                            <a href="${viewUrl}"><cti:deviceName deviceId="${assignedProgram.programId}"/></a>
                        </td>
                        <td>
                            ${assignedProgram.startPriority}
                        </td>
                        <td>
                            ${assignedProgram.stopPriority}
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<script>
    $("#js-sorted-assigned-programs-table").scrollTableBody({rowsToDisplay: 20});
</script>
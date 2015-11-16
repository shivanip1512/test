<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pickerId" value="${widgetParameters.widgetId}_${widgetParameters.pickerType}" scope="page"/>
<c:set var="addPao" value="addPao_${pageScope.pickerId}" scope="page"/>
<c:set var="addPaoSpanId" value="addPaoSpan_${pageScope.pickerId}" scope="page"/>
<c:set var="newPaoId" value="newPao_${pageScope.pickerId}" scope="page"/>

<script type="text/javascript">
${pageScope.addPao} = function() {
    //add the object to the widget
    ${widgetParameters.jsWidget}.doActionRefresh({command: "addPao"});
    return true;
}

</script>
<tags:widgetState paoIdsList="${paoIds}"/>

<div id="paoTable">
   <span>Select items that <b>${instructionText}</b> have this permission.</span>
    <br><br>
    <c:choose>
        <c:when test="${empty paoList}">
            No Existing Permissions
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="compact-results-table row-highlighting">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th class="remove-column tar">Remove</th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="pao" items="${paoList}">
                            <tr>
                                <td>
                                    <c:out value="${pao.paoName}" />
                                </td>
                                <td>
                                    <c:out value="${pao.type}" />
                                </td>
                                <td class="remove-column remove">
                                    <tags:widgetActionRefreshImage nameKey="remove" method="removePao" paoId="${pao.paoId}" icon="icon-cross"/>
                                </td>
                            </tr>
                            
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
    
    <div class="action-area">
        <c:if test="${showSave}">
            <span id="${widgetParameters.widgetId}_results">
                <tags:widgetActionUpdate method="save" nameKey="save" container="${widgetParameters.widgetId}_results"/>
            </span>
        </c:if>
        <tags:pickerDialog extraArgs="${energyCompanyId}"
                            id="${pageScope.pickerId}"
                            type="${widgetParameters.pickerType}"
                            destinationFieldId="${pageScope.newPaoId}"
                            endAction="${pageScope.addPao}"
                            multiSelectMode="true"
                            memoryGroup="${pageScope.pickerId}"
                            linkType="button"
                            icon="icon-add"
                            nameKey="add"/>
        <input id="${pageScope.newPaoId}" name="newPaoId" type="hidden">
        <script type="text/javascript">
            ${pageScope.pickerId}.excludeIds = [
            <c:forEach var="pao" varStatus="status" items="${paoList}">
                ${pao.paoId}<c:if test="${!status.last}">,</c:if>
            </c:forEach> ];
        </script>
    </div>
</div>
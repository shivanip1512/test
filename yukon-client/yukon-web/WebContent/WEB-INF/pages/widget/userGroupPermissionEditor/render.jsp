<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/tableCreation.js" />

<c:set var="pickerId" value="${widgetParameters.widgetId}_${widgetParameters.pickerType}" scope="page"/>
<c:set var="addPao" value="addPao_${pageScope.pickerId}" scope="page"/>
<c:set var="addPaoSpanId" value="addPaoSpan_${pageScope.pickerId}" scope="page"/>
<c:set var="newPaoId" value="newPao_${pageScope.pickerId}" scope="page"/>

<script type="text/javascript">
${pageScope.addPao} = function() {
    ${widgetParameters.jsWidget}.doActionRefresh({
        command:    "addPao",
        buttonID:   "${pageScope.addPaoSpanId}", 
        waitingText:""
     });
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
                <div class="permissionsContainer">
                    <table class="compactResultsTable rowHighlighting">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Type</th>
                                <th class="removeColumn">Remove</th>
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
                                    <td class="removeColumn">
                                        <tags:widgetActionRefreshImage nameKey="remove" method="removePao" paoId="${pao.paoId}"/>
                                    </td>
                                </tr>
                                
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
		
        <div class="actionArea">
            <c:if test="${showSave}">
                <span id="${widgetParameters.widgetId}_results">
                    <tags:widgetActionUpdate method="save" nameKey="save" container="${widgetParameters.widgetId}_results"/>
                </span>
            </c:if>
            <span class="widgetActionLink">
                    <tags:pickerDialog type="${widgetParameters.pickerType}" id="${pageScope.pickerId}"
                       destinationFieldId="${pageScope.newPaoId}" endAction="${pageScope.addPao}"
                       multiSelectMode="true" memoryGroup="${pageScope.pickerId}" linkType="button" nameKey="add"/>
                    <input id="${pageScope.newPaoId}" name="newPaoId" type="hidden">
                    <script type="text/javascript">
                        ${pageScope.pickerId}.excludeIds = [
                        <c:forEach var="pao" varStatus="status" items="${paoList}">
                            ${pao.paoId}<c:if test="${!status.last}">,</c:if>
                        </c:forEach> ];
                    </script>
                </span>
                <span id="${pageScope.addPaoSpanId}">
                    <span class="widgetAction_waiting" style="display:none">
                        <img src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
                    </span>
                </span>
        </div>
    </div>
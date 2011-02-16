<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/tableCreation.js" />

<c:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<c:set var="pickerId" value="${widgetParameters.widgetId}_${widgetParameters.pickerType}" scope="page"/>
<c:set var="addPao" value="addPao_${pageScope.pickerId}" scope="page"/>
<c:set var="addPaoSpanId" value="addPaoSpan_${pageScope.pickerId}" scope="page"/>
<c:set var="newPaoId" value="newPao_${pageScope.pickerId}" scope="page"/>

<script type="text/javascript">
${pageScope.addPao} = function() {
    ${widgetParameters.jsWidget}.doActionRefresh("addPao", "${pageScope.addPaoSpanId}", "Adding");
    return true;
}
</script>
    <tags:widgetState paoIdsList="${paoIds}"/>
    
    <div id="paoTable">
	
		Select Objects that <b>${instructionText}</b> have this permission.
		<br><br>
		<table width="100%" border="0" cellspacing="0" cellpadding="3">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<th>Name</th>
				<th>Type</th>
				<th style="text-align:center">
                <span class="widgetActionLink">
                    <tags:pickerDialog type="${widgetParameters.pickerType}" id="${pageScope.pickerId}"
                       destinationFieldId="${pageScope.newPaoId}" endAction="${pageScope.addPao}"
                       multiSelectMode="true" memoryGroup="${pageScope.pickerId}">Add</tags:pickerDialog>
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
            </th>
			</tr>
			<c:forEach var="pao" items="${paoList}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>
						<c:out value="${pao.paoName}" />
					</td>
					<td>
						<c:out value="${pao.type}" />
					</td>
					<td align="center">
                        <tags:widgetActionRefreshImage paoId="${pao.paoId}" method="removePao" title="Remove this PAO" imgSrc="${delete}" imgSrcHover="${deleteOver}"/>
					</td>
				</tr>
				
			</c:forEach>
		</table>
    </div>
    <br>
<c:if test="${showSave}">
<div id="${widgetParameters.widgetId}_results">
  <tags:widgetActionUpdate method="save" label="Save" labelBusy="Saving" container="${widgetParameters.widgetId}_results"/>
</div>
</c:if>

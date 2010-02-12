<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<c:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<c:set var="pickerId" value="${widgetParameters.widgetId}_${widgetParameters.pickerType}"/>
<c:set var="addPao" value="addPao_${pickerId}"/>
<c:set var="addPaoSpanId" value="addPaoSpan_${pickerId}"/>
<c:set var="newPaoId" value="newPao_${pickerId}"/>

<script type="text/javascript">
${addPao} = function() {
    ${widgetParameters.jsWidget}.doActionRefresh("addPao", "${addPaoSpanId}", "Adding");
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
                    <tags:pickerDialog type="${widgetParameters.pickerType}" id="${pickerId}"
                       destinationFieldId="${newPaoId}" endAction="${addPao}"
                       immediateSelectMode="true">Add</tags:pickerDialog>
                    <input id="${newPaoId}" name="newPaoId" type="hidden">
                </span>
                <span id="${addPaoSpanId}">
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

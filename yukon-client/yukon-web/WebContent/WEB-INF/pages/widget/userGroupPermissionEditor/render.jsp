<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>
<cti:uniqueIdentifier var="newPaoId" prefix="newPao_"/>

<script type="text/javascript">
  ${addPao} = function() {
    ${widgetParameters.jsWidget}.doActionRefresh("addPao", "${addPaoSpanId}", "Adding");
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
                          <span id="${addPaoSpanId}">
                            <span class="widgetActionLink">
            <cti:paoPicker paoIdField="${newPaoId}" constraint="${widgetParameters.constraint}" finalTriggerAction="${addPao}">
                              Add
                        </cti:paoPicker>
                            </span>
                          <span class="widgetAction_waiting" style="display:none">
                          <img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
                          </span>
                          </span>
            <input id="${newPaoId}" name="newPaoId" type="hidden">
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
                        <tags:widgetActionRefreshImage paoId="${pao.paoId}" method="removePao" title="Remove this PAO" imgSrc="/WebConfig/yukon/Icons/cancel.gif" imgSrcHover="/WebConfig/yukon/Icons/cancel.gif"/>
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

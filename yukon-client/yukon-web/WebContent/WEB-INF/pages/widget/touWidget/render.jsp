<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="touTable">
	<div class="widgetInternalSection">
		<c:choose>
			<c:when test="${not empty rateTypes}">

				<table class="miniResultsTable">
					<c:forEach var="rateType" items="${rateTypes}">
						<tr class="">
							<th class="tableHeaders" colspan=2>
								${rateType.key}
							</th>
						</tr>

						<c:forEach var="attributeValuePair"	items="${rateType.attributeValuePairList}">
							<tr>
								<td>
									<c:out value="${attributeValuePair.label}" />
								</td>

								<cti:attributeResolver device="${meter}"
									attribute="${attributeValuePair.attribute}" var="pointId" />
								<td>
									<ct:attributeValue device="${meter}" attribute="${attributeValuePair.attribute}" />
								</td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				Tou Widget not configured.
			</c:otherwise>
		</c:choose>
	</div>
</div>
<br />

<c:if test="${not empty rateTypes}">
	<div id="${widgetParameters.widgetId}_results"></div>
	<div style="text-align: right">
        <ct:widgetActionUpdate hide="${!readable}" method="read" label="Read Now"
			labelBusy="Reading" container="${widgetParameters.widgetId}_results" />
	</div>
</c:if>


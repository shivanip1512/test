<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="touTable">
	<div class="widgetInternalSection">
		<c:choose>
			<c:when test="${rateTypes != null}">

				<table class="miniResultsTable">
					<c:forEach var="rateType" items="${rateTypes}">
						<c:if test="${(rateType.peak != null || rateType.usage != null)}">
							<tr class="">
								<th class="tableHeaders" colspan=3>
									${rateType.displayName}
								</th>
							</tr>
							<tr>
								<td>
									Usage
								</td>
								<c:choose>
									<c:when test="${rateType.usage != null}">
										<cti:attributeResolver device="${meter}"
											attribute="${rateType.usage}" var="pointId" />
										<td>
											<cti:pointValue pointId="${pointId}" format="SHORT" />
										</td>
										<td>
											<cti:pointValue pointId="${pointId}" format="DATE" />
										</td>
									</c:when>
									<c:otherwise>
										<td colspan="2">
											Rate is not configured.
										</td>
									</c:otherwise>
								</c:choose>

							</tr>
							<tr>
								<td>
									Peak Demand
								</td>
								<c:choose>
									<c:when test="${rateType.peak != null}">
										<cti:attributeResolver device="${meter}"
											attribute="${rateType.peak}" var="pointId" />
										<td>
											<cti:pointValue pointId="${pointId}" format="SHORT" />
										</td>
										<td>
											<cti:pointValue pointId="${pointId}" format="DATE" />
										</td>
									</c:when>
									<c:otherwise>
										<td colspan="2">
											Rate is not configured.
										</td>
									</c:otherwise>
								</c:choose>
							</tr>

						</c:if>
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

<c:if test="${rateTypes != null}">
	<div id="${widgetParameters.widgetId}_results"></div>
	<div style="text-align: right">
		<ct:widgetActionUpdate method="read" label="Read Now"
			labelBusy="Reading" container="${widgetParameters.widgetId}_results" />
	</div>
</c:if>


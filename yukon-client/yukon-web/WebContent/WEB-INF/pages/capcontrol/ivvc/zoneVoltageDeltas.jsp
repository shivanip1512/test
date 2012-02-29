<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneVoltageDeltas">
	<cti:includeCss link="/capcontrol/css/ivvc.css" />

	<script>
		jQuery(document).ready(function() {
			jQuery(".viewDelta").click(function(e) {
				jQuery(e.currentTarget).hide();
				jQuery(e.currentTarget).next().show();
				jQuery(e.currentTarget).next().find("input").focus();
				jQuery("#deltaSubmitBtn").slideDown();
			});

			jQuery(".editDelta input").keydown(function(e) {
				if (e.which == 27) {
					/* Escape Key */
					cancelEdit(e);
				}
			});
			
			jQuery("input[type='checkbox'].editableStaticDelta").click(function(e) {
				jQuery(e.currentTarget).toggleClass("staticChanged");
				jQuery(e.currentTarget).parent("label").toggleClass("staticChangedLabel");

				if (jQuery(".editDelta:visible").length == 0 &&
					jQuery("input.staticChanged").length == 0) {
					jQuery("#deltaSubmitBtn").slideUp();
				} else {
					jQuery("#deltaSubmitBtn").slideDown();
				}
			});

			jQuery("a.cancelEdit").click(cancelEdit);
			jQuery("#deltaSubmitBtn").click(function() {
				if (jQuery(".editDelta:visible").length == 0 &&
						jQuery("input.staticChanged").length == 0) {
					jQuery("#deltaSubmitBtn").slideUp();
					return;
				}
	
				var inputs = [];
				var index = 0;
				jQuery("form#deltaForm tr").each(function() {
					var staticChanged = jQuery(this).find("input.staticChanged");
					var editDeltaInput = jQuery(this).find(".editDelta:visible input");
	
					if (staticChanged.length > 0 || editDeltaInput.length > 0) {
	
	            		params = {};
						params.newDelta = editDeltaInput.length > 0 ? editDeltaInput.val() : jQuery(this).find(".viewDelta input[type='hidden']").val();
	            		params.newStaticValue = staticChanged.is(":checked");
	        			params.bankId = jQuery(this).find("td.bankAndPointIds").find(".pointDeltaBankId").val();
	        			params.pointId = jQuery(this).find("td.bankAndPointIds").find(".pointDeltaPointId").val();
	
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].delta" value="'+ params.newDelta +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].staticDelta" value="'+ params.newStaticValue +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].pointId" value="'+ params.pointId +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].bankId" value="'+ params.bankId +'"/>');
						index++;
					}
				});
				
				if (inputs.length > 0) {
					jQuery('form#deltaForm').append(inputs.join(''));
					Yukon.ui.blockPage();
					jQuery('form#deltaForm').submit();
				}
			});
		});

		function cancelEdit(e) {
			jQuery(e.currentTarget).closest("td").find(".editDelta").hide();
			jQuery(e.currentTarget).closest("td").find(".viewDelta").show();
			if (jQuery(".editDelta:visible").length == 0 &&
				jQuery("input.staticChanged").length == 0) {
				jQuery("#deltaSubmitBtn").slideUp();
			}
		}
	</script>

	<cti:url var="baseUrl" value="/spring/capcontrol/ivvc/zone/voltageDeltas" />
	<tags:pagedBox2 nameKey="deltas" searchResult="${searchResults}"
		baseUrl="${baseUrl}" showAllUrl="${baseUrl}">
		<tags:alternateRowReset />

		<form:form id="deltaForm" action="/spring/capcontrol/ivvc/zone/deltaUpdate" method="POST" commandName="zoneVoltageDeltas">
			<input type="hidden" name="zoneId" id="zoneId" value="${zoneId}">

			<table id="deltaTable" class="compactResultsTable">
				<tr>
					<th><i:inline key=".deltas.cbcName" /></th>
					<th><i:inline key=".deltas.bankName" /></th>
					<th><i:inline key=".deltas.deviceName" /></th>
					<th><i:inline key=".deltas.pointName" /></th>
					<th><i:inline key=".deltas.preOp" /></th>
					<th><i:inline key=".deltas.static" /></th>
					<th style="width: 15%"><i:inline key=".deltas.delta" /></th>
				</tr>

				<c:if test="${searchResults.hitCount == 0}">
					<tr>
						<td><i:inline key=".deltas.emptyTable" />
						</td>
					</tr>
				</c:if>

				<c:forEach var="pointDelta" items="${searchResults.resultList}" varStatus="status">

					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td class="dn bankAndPointIds">
							<input class="pointDeltaBankId" type="hidden" value="${pointDelta.bankId}"/>
							<input class="pointDeltaPointId" type="hidden" value="${pointDelta.pointId}"/>
						</td>
						<td>
							<spring:escapeBody htmlEscape="true">${pointDelta.cbcName}</spring:escapeBody>
						</td>
						<td>
							<spring:escapeBody htmlEscape="true">${pointDelta.bankName}</spring:escapeBody>
						</td>
						<td>
							<spring:escapeBody htmlEscape="true">${pointDelta.affectedDeviceName}</spring:escapeBody>
						</td>
						<td>
							<spring:escapeBody htmlEscape="true">${pointDelta.affectedPointName}</spring:escapeBody>
						</td>
						<td>
							<spring:escapeBody htmlEscape="true">${pointDelta.preOpValue}</spring:escapeBody>
						</td>
						<c:choose>
							<c:when test="${hasEditingRole}">
								<td><label class="staticDeltaLabel"><input type="checkbox" class="editableStaticDelta"
									<c:choose>
					                	<c:when test="${pointDelta.staticDelta}">
			                                checked="checked"
			                            </c:when>
			                        </c:choose>></label>
								</td>
								<td class="editable">
									<cti:msg2 var="deltaTitle" key=".deltas.deltaTitle" />
									<div class="viewDelta anchorUnderlineHover" title="${deltaTitle}">
										<input type="hidden" value="${pointDelta.delta}"/>
										<spring:escapeBody htmlEscape="true">${pointDelta.deltaRounded}</spring:escapeBody>
									</div>
									<div class="editDelta" style="display: none;">
										<input type="text" style="margin-right: 5px; width: 30px;"
											name="editDeltaInput"
											value="<spring:escapeBody htmlEscape="true">${pointDelta.delta}</spring:escapeBody>">
										<a href="javascript:void(0);" class="cancelEdit"><i:inline key="yukon.common.cancel"/></a>
									</div>
								</td>
							</c:when>
							<c:otherwise>
								<td><input type="checkbox"
									disabled="disabled"
									<c:choose>
						                	<c:when test="${pointDelta.staticDelta}">
				                                checked="checked"
				                            </c:when>
				                        </c:choose>>
								</td>
								<td>
									<div>
										<spring:escapeBody htmlEscape="true">${pointDelta.deltaRounded}</spring:escapeBody>
									</div>
								</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</table>
			<div id="deltaSubmitBtn" class="actionArea dn">
				<cti:button nameKey="submit" type="submit"/>
				<cti:url var="zoneVoltageDeltasUrl" value="/spring/capcontrol/ivvc/zone/voltageDeltas">
			    	<cti:param name="zoneId" value="${zoneId}"/>
			    </cti:url>
			    <cti:button nameKey="reset" href="${zoneVoltageDeltasUrl}"/>
			</div>
		</form:form>
	</tags:pagedBox2>
</cti:standardPage>

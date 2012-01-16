<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.capcontrol.comments">

<script type="text/javascript">
jQuery("#executeCommand").click(function(event) {
    hideMenu();
    doItemCommand('${paoId}', '${commandId}', event, jQuery('#commentTextArea').val(), 'true');
});
jQuery("#reasonSelect").change(function(event) {
	jQuery('#commentTextArea').val(this.options[this.selectedIndex].value);
});
</script>

<c:set var="maxCommentLength" value="40"/>

<div id="menuPopupBoxContainer">
	<input type="hidden" id="dialogTitle" value="${reasonTitle}">
	<input type="hidden" id="isFinished" value="${finished}">

	<div class="content boxContainer_content">

		<div class="commandReason">
			<div><i:inline key=".enterComment"/></div>
			<div class="dialogReason">
				<textarea id="commentTextArea" rows="3"></textarea>
			</div>
			<div>
				<select id="reasonSelect">
					<option><i:inline key=".previousComment"/></option>
					<c:forEach var="comment" items="${comments}">
						<c:choose>
							<c:when test="${fn:length(comment) > maxCommentLength}">
								<c:set var="subString"
									value="${fn:substring(comment, 0, (maxCommentLength - 3))}"/>
								<c:set var="formattedComment" value="${subString}..."/>
							</c:when>
							<c:otherwise>
								<c:set var="formattedComment" value="${comment}"/>
							</c:otherwise>
						</c:choose>
						<option value="${formattedComment}">
							<spring:escapeBody htmlEscape="true">${formattedComment}</spring:escapeBody>
						</option>
					</c:forEach>
				</select>
			</div>
			<div class="actionArea">
				<cti:button nameKey="submit" id="executeCommand"/>
			</div>
		</div>
	</div>
</div>
</cti:msgScope>
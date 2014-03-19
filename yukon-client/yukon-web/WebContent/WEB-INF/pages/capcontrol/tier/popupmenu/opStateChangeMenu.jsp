<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">

<script type="text/javascript">
$("#reasonSelect").change(function(event) {
	$('#coReason').val(this.options[this.selectedIndex].value);
});
</script>

<c:set var="maxCommentLength" value="35"/>

<div id="menuPopupBoxContainer">
	<input type="hidden" id="dialogTitle" value="${title}">

	<div class="changeOpState">
		<div>
			<span><i:inline key=".opState"/></span>
			<select id="newOpState">
				<c:forEach items="${allowedOperationStates}" var="state">
					<option value="${state}"
						<c:if test="${currentState == state}">selected</c:if>>
						<cti:msg2 key="${state}"/>
					</option>
				</c:forEach>
			</select>
		</div>
		<div class="dialogReason">
			<div><i:inline key=".reason"/></div>
			<textarea id="coReason" rows="3">${reason}</textarea>
		</div>
		<div>
			<select id="reasonSelect">
				<option><cti:msg2 key=".previousComment"/></option>
				<c:forEach var="comment" items="${comments}">
					<c:choose>
						<c:when test="${fn:length(comment) > maxCommentLength}">
							<c:set var="subString" value="${fn:substring(comment, 0, (maxCommentLength - 3))}" />
							<c:set var="formattedComment" value="${subString}..." />
						</c:when>
						<c:otherwise>
							<c:set var="formattedComment" value="${comment}" />
						</c:otherwise>
					</c:choose>
					<option value="${formattedComment}">${formattedComment}</option>
				</c:forEach>
			</select>
		</div>
		<div class="action-area">
			<cti:button nameKey="execute"
				onclick="doChangeOpState(${bankId}, $('#newOpState').val(), $('#coReason').val(), 'true')" />
		</div>
	</div>
</div>
</cti:msgScope>
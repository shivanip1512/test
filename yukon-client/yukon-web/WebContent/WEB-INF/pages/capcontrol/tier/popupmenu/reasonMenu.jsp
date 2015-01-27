<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol.comments">

<script type="text/javascript">
$("#executeCommand").click(function(event) {
    yukon.da.common.hideMenu();
    doItemCommand('${paoId}', '${commandId}', event, $('#commentTextArea').val(), 'true');
});
$("#reasonSelect").change(function(event) {
    $('#commentTextArea').val(this.options[this.selectedIndex].value);
});
</script>

<c:set var="maxCommentLength" value="40"/>

<div id="menuPopupBoxContainer">
    <input type="hidden" id="dialogTitle" value="${reasonTitle}">
    <input type="hidden" id="isFinished" value="${finished}">

    <div class="commandReason">
        <div><i:inline key=".enterComment"/></div>
        <div class="dialogReason stacked">
            <textarea id="commentTextArea" rows="3" class="full-width"></textarea>
        </div>
        <c:if test="${fn:length(comments) > 0}">
            <select id="reasonSelect" class="full-width">
                <option><i:inline key=".previousComment"/></option>
                <c:forEach var="comment" items="${comments}">
                    <c:choose>
                        <c:when test="${fn:length(comment) > maxCommentLength}">
                            <c:set var="subString" value="${fn:substring(comment, 0, (maxCommentLength - 3))}"/>
                            <c:set var="formattedComment" value="${subString}..."/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="formattedComment" value="${comment}"/>
                        </c:otherwise>
                    </c:choose>
                    <option value="${comment}">${fn:escapeXml(formattedComment)}</option>
                </c:forEach>
            </select>
        </c:if>
        <div class="action-area">
            <cti:button nameKey="submit" id="executeCommand" classes="primary action"/>
        </div>
    </div>
</div>
</cti:msgScope>
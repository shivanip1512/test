<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.comments">

<script type="text/javascript">
$('executeCommand').observe('click', function(event) {
    hideMenu();
    doItemCommand('${paoId}', '${commandId}', event, $F('commentTextArea'), 'true');
});
</script>

<c:set var="maxCommentLength" value="40"/>

<div id="menuPopupBoxContainer" class="thinBorder">
    <div class="titledContainer boxContainer">
    
        <div class="titleBar boxContainer_titleBar">
            <div class="controls" onclick="$('menuPopup').hide()">
                <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
            </div>
            <div class="title boxContainer_title">${reasonTitle}</div>
        </div>
        
        <div class="content boxContainer_content">
        
        <div class="commandReason">
            <div><i:inline key=".enterComment"/></div>
            <div>
                <textarea id="commentTextArea" style="width:100%;"></textarea>
            </div>
            <div>
                <select onchange="$('commentTextArea').value = this.options[this.selectedIndex].value;">
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
                        <option value="${formattedComment}"><spring:escapeBody htmlEscape="true">${formattedComment}</spring:escapeBody></option>
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
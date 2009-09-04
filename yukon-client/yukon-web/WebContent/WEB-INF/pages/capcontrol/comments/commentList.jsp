<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<table id="innerTable" width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="columnHeader lAlign">
        <td width="7%">Edit</td>
        <td>Comment</td>
        <td>By User</td>
        <td>Time</td>
        <td>Altered</td>
    </tr>
    <tr id="addCommentRow" >
        <td>
            <c:choose>
                <c:when test="${addPermission}">
                    <img src="/WebConfig/yukon/Icons/pencil.gif" border="0" height="15" width="15" onclick="$('addCommentDiv').show();unHighlightAllRows();"/>
                </c:when>
                <c:otherwise>
                    <img src="/WebConfig/yukon/Icons/pencil.gif" border="0" height="15" width="15" onclick=""/>
                </c:otherwise>
            </c:choose>
        </td>
        <td>Click the edit button to add or edit a comment.</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <!-- Loops for each comment here. -->
    <c:forEach var="comment" items="${comments}">
            <tr id="commentRow_${comment.id}" class="<tags:alternateRow odd="altRow" even=""/>">
                <td>
                    <c:choose>
                        <c:when test="${modifyPermission}">
                            <input type="hidden" id="commentInput_${comment.id}" value="<spring:escapeBody>${comment.comment}</spring:escapeBody>"></input>
                            <img src="/WebConfig/yukon/Icons/pencil.gif" border="0" height="15" width="15"  onclick="selectComment(${comment.id}, $('commentInput_${comment.id}'));highlightRow('commentRow_${comment.id}');"/>
                            <img src="/WebConfig/yukon/Icons/delete.gif" border="0" height="15" width="15" onclick="removeComment(${comment.id});" />
                        </c:when>
                    </c:choose>
                </td>
                <td><spring:escapeBody htmlEscape="true">${comment.comment}</spring:escapeBody></td>
                <td>${comment.userName}</td>
                
                <td><cti:formatDate value="${comment.time}" type="BOTH" /></td>
                <td>
                    <c:choose>
                        <c:when test="${comment.altered}">Yes</c:when>
                        <c:otherwise>No</c:otherwise>
                    </c:choose>
                </td>
            </tr>
    </c:forEach>
</table>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!-- Layout CSS files -->
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/CannonStyle.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/InnerStyles.css" >
<link rel="stylesheet" type="text/css" href="/capcontrol/css/base.css" >

<!-- Consolidated Script Files -->
<script type="text/javascript" src="/JavaScript/prototype.js" ></script>

<cti:url var="commentsURL" value="/spring/capcontrol/comments/"/>

<script type="text/javascript"> 

function addComment(commentText) {
    var url = '${commentsURL}' + 'add';
    var parameters = { 'paoId': ${paoId}, 'comment': commentText };
    executeUrl(url, parameters);  
}

function updateComment(commentId, commentText) {
    var url = '${commentsURL}' + 'update';
    var parameters = { 'commentId': commentId, 'comment': commentText, 'paoId': ${paoId} };
    executeUrl(url, parameters); 
}

function removeComment(commentId) {
    var url = '${commentsURL}' + 'remove';
    var parameters = { 'commentId': commentId, 'paoId': ${paoId} };
    executeUrl(url, parameters);
}

function executeUrl(url, parameters) {
    new Ajax.Updater('commentListDiv', url, {'method': 'POST', 'parameters': parameters});
}

function selectComment(commentId, inputElement) {
    $('updateCommentId').value = commentId;
    $('updateCommentInput').value = inputElement.value;
    $('updateCommentDiv').show();
}

function highlightRow( id ){
    unHighlightAllRows();
    $(id).style.backgroundColor = 'yellow';
}

function unHighlightAllRows(){
    var rows = $$('#innerTable tr');
    for( var i = 2; i < rows.length; i++){
        rows[i].style.backgroundColor = 'white';
    }
}

</script>

<div>
    <div id="addCommentDiv" style="width: auto; display: none;">
        <tags:abstractContainer type="box" title="Add Comment">
            <input id="addCommentInput" type="text" value=""></input>
            <input type="button" value="Add Comment" onclick="addComment($('addCommentInput').value);"></input>
        </tags:abstractContainer>
        <br>  
    </div>
    <div id="updateCommentDiv" style="width: auto; display: none;">
        <tags:abstractContainer type="box" title="Update Comment">
            <input id="updateCommentInput" type="text" value=""></input>
            <input id="updateCommentId" type="hidden" value=""></input>
            <input type="button" value="Update Comment" onclick="updateComment($('updateCommentId').value, $('updateCommentInput').value);"></input>
        </tags:abstractContainer> 
        <br>        
    </div>
    <tags:abstractContainer type="box" title="${name}">
        <div id="commentListDiv">
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
        </div>
    </tags:abstractContainer>
</div>
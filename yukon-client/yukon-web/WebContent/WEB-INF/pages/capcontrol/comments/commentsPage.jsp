<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="capcontrol">
    <cti:url var="commentsURL" value="/spring/capcontrol/comments/"/>
    
    <script type="text/javascript">
    
        addComment = function() {
            var newComment = $('newCommentInput').value;
            if(newComment.strip().length != 0) {
                $('commentForm').action = '${commentsURL}' + 'add';
                $('comment').value = newComment.escapeHTML();
                submitForm();
                $('newCommentInput').disable();
            }
        };

        hideNewRow = function() {
        	$('newCommentInput').value = '';
        	$('newRow').hide();
        };

        showNewRow = function() {
        	var editInputs = $$("input[name='editCommentInput']");
            editInputs.each(function (elem) {
                    var elemsCommentId = elem.id.split('_')[1];
                    $('editCommentSpan_' + elemsCommentId).hide();
                    $('comment_' + elemsCommentId).show();
            });
        	$('newRow').show();
        	$('newCommentInput').enable();
            $('newCommentInput').focus();
            flashYellow($('newRow'));
        };

        addOrCancel = function(event) {
            var key = event.keyCode;
            if(key == 27) {
                /* Escape Key */
                hideNewRow();
            } else if (key == 13) {
                /* Enter Key */
                addComment();
            }
            return (key != 13);
        };

        updateOrCancel = function(event, commentId) {
            var key = event.keyCode;
            if(key == 27) {
                /* Escape Key */
            	cancelUpdate(commentId);
            } else if (key == 13) {
                /* Enter Key */
                updateComment(commentId);
            }
            return (key != 13);
        };

        cancelUpdate = function(commentId) {
        	$('comment_' + commentId).show();
            $('editCommentSpan_' + commentId).hide();
            $('editComment_' + commentId).value = $('comment_' + commentId).innerHTML;
        };
        
        updateComment = function(commentId) {
            var newComment = $('editComment_' + commentId).value;
            if(newComment.strip().length != 0) {
                $('comment').value = newComment.escapeHTML();
                $('commentForm').action = '${commentsURL}' + 'update';
                $('commentId').value = commentId;
                submitForm();
            }
        };

        editComment = function(commentId) {
        	hideNewRow();
            var editInputs = $$("input[name='editCommentInput']");
            editInputs.each(function (elem) {
                if(elem.id != 'editComment_' + commentId) {
                    var elemsCommentId = elem.id.split('_')[1];
                    $('editCommentSpan_' + elemsCommentId).hide();
                    $('comment_' + elemsCommentId).show();
                }
            });
            $('comment_' + commentId).hide();
            $('editCommentSpan_' + commentId).show();
            $('editComment_' + commentId).focus();
        };
        
        deleteComment = function(commentId) {
            $('commentId').value = commentId;
            $('commentForm').action = '${commentsURL}' + 'remove';
            submitForm();
        };
        
        submitForm = function() {
            if (${submitNormal}) {
                $('commentForm').submit();
            } else {
                submitFormViaAjax('tierContentPopup', 'commentForm', null, $('tierContentPopup').getElementsByClassName('boxContainer_title')[0].innerHTML);
            }
        };
        
    </script>
    
    <cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
    <cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
    <cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
    <cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
    
    <div style="max-height:400px;overflow-x:hidden;overflow-y:auto;">
        <form id="commentForm" action="/spring/capcontrol/comments/" method="POST">
            <input type="hidden" name="paoId" value="${paoId}">
            <input type="hidden" name="commentId" id="commentId" value="">
            <input type="hidden" name="comment" id="comment">
            <input type="hidden" name="submitNormal" value="${submitNormal}">
            <input type="hidden" name="redirectToOneline" value="${redirectToOneline}">
            <table id="commentsTable" class="compactResultsTable">
                <thead>
                    <tr>
                        <th>Comment</th>
                        <th>By User</th>
                        <th>Time</th>
                        <th>Altered</th>
                        <th>Delete</th>
                    </tr>
                </thead>
                
                <tbody>
                    <c:choose>
                        <c:when test="${empty comments}">
                            <tr id="noCommentsRow"><td colspan="4">No Comments</td></tr>
                        </c:when>
                        <c:otherwise>
                        
                            <c:forEach var="comment" items="${comments}">
                                <tr id="commentRow_${comment.id}" class="<tags:alternateRow odd="" even="altRow"/>">
                                    <td class="editable">
                                        <div id="editCommentSpan_${comment.id}" style="display: none;">
                                            <input id="editComment_${comment.id}" type="text" 
                                                style="margin-right: 5px;width:350px;" 
                                                name="editCommentInput" onKeyPress="return updateOrCancel(event, ${comment.id})" 
                                                value="<spring:escapeBody htmlEscape="true">${comment.comment}</spring:escapeBody>">
                                            <a href="javascript:updateComment(${comment.id})">Save</a> <a href="javascript:cancelUpdate(${comment.id})">Cancel</a>
                                        </div>
                                        <div id="comment_${comment.id}" title="Click to edit." 
                                        <c:if test="${modifyPermission}">onclick="editComment(${comment.id})"</c:if>><spring:escapeBody htmlEscape="true">${comment.comment}</spring:escapeBody></div>
                                    </td>
                                    <c:choose>
                                    	<c:when test="${comment.userName == null}">
                                    		<td><i:inline key="yukon.web.defaults.dashes"/></td>
                                    	</c:when>
                                    	<c:otherwise>
                                    		<td>${comment.userName}</td>
                                    	</c:otherwise>
                                    </c:choose>
                                    <td><cti:formatDate value="${comment.date}" type="BOTH" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${comment.altered}">Yes</c:when>
                                            <c:otherwise>No</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <cti:img key="remove" href="javascript:deleteComment(${comment.id})"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            
                        </c:otherwise>
                    </c:choose>
                </tbody>
                
            </table>
        </form>
    </div>
    <c:if test="${addPermission}">
        <div id="newRow" style="display: none;">
            <span>Enter Comment Text: </span>
            <span class="textFieldLabel">
                <input type="text" id="newCommentInput" onKeyPress="return addOrCancel(event)">
            </span>
            <span class="textFieldLabel"><a href="javascript:addComment()">Save</a> <a href="javascript:hideNewRow()">Cancel</a></span>
        </div>
        <div class="compactResultsFooter">
            <cti:button nameKey="add" onclick="javascript:showNewRow()"/>
        </div>
    </c:if>
</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.comments">
    <cti:url var="commentsURL" value="/capcontrol/comments/"/>
    
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
                jQuery.ajax({
                    url: jQuery("#commentForm").attr("action"),
                    data: jQuery("#commentForm").serialize(),
                    type: "POST",
                    success: function(data) {
                        jQuery("#contentPopup").html(data);
                    }
                });
            }
        };
        
    </script>
    
    <div id="comment_editor" class="scroll-large stacked">
        <form id="commentForm" action="/capcontrol/comments/" method="POST">
            <input type="hidden" name="paoId" value="${paoId}">
            <input type="hidden" name="commentId" id="commentId" value="">
            <input type="hidden" name="comment" id="comment">
            <input type="hidden" name="submitNormal" value="${submitNormal}">
            <input type="hidden" name="redirectToOneline" value="${redirectToOneline}">
            
            <c:if test="${empty comments}">
                <span class="empty-list"><i:inline key=".noComments"/></span>
            </c:if>
            <c:if test="${not empty comments}">
                <table id="commentsTable" class="compact-results-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".comment"/></th>
                            <th><i:inline key=".user"/></th>
                            <th><i:inline key=".time"/></th>
                            <th><i:inline key=".altered"/></th>
                            <th class="remove-column"><i:inline key=".delete.label"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="comment" items="${comments}">
                            <tr id="commentRow_${comment.id}">
                                <td class="editable">
                                    <div id="editCommentSpan_${comment.id}" style="display: none;">
                                        <input id="editComment_${comment.id}" type="text" 
                                            style="margin-right: 5px;width:350px;" 
                                            name="editCommentInput" onKeyPress="return updateOrCancel(event, ${comment.id})" 
                                            value="${fn:escapeXml(comment.comment)}">
                                        <a href="javascript:updateComment(${comment.id})"><i:inline key=".save"/></a> <a href="javascript:cancelUpdate(${comment.id})"><i:inline key=".cancel"/></a>
                                    </div>
                                    <div id="comment_${comment.id}" title="<cti:msg2 key=".clickToEdit"/>" <c:if test="${modifyPermission}">onclick="editComment(${comment.id})"</c:if> >
                                        ${fn:escapeXml(comment.comment)}
                                    </div>
                                </td>
                                <c:choose>
                                    <c:when test="${comment.userName == null}">
                                        <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>${fn:escapeXml(comment.userName)}</td>
                                	</c:otherwise>
                                </c:choose>
                                <td><cti:formatDate value="${comment.date}" type="BOTH" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${comment.altered}"><i:inline key="yukon.web.defaults.yes"/></c:when>
                                        <c:otherwise><i:inline key="yukon.web.defaults.no"/></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="remove-column">
                                    <cti:button nameKey="remove" renderMode="image" onclick="deleteComment(${comment.id})" classes="center" icon="icon-cross"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </form>
    </div>
    <c:if test="${addPermission}">
        <div id="newRow" style="display: none;">
            <span><i:inline key=".enterText"/></span>
            <span class="textFieldLabel">
                <input type="text" id="newCommentInput" onKeyPress="return addOrCancel(event)">
            </span>
            <span class="textFieldLabel"><a href="javascript:addComment()"><i:inline key=".save"/></a> <a href="javascript:hideNewRow()"><i:inline key=".cancel"/></a></span>
        </div>
        <div class="compactResultsFooter">
            <cti:button nameKey="add" icon="icon-add" onclick="javascript:showNewRow()"/>
        </div>
    </c:if>
</cti:msgScope>
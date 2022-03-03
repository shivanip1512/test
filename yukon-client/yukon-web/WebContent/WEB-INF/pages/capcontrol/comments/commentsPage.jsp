<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.comments">
    <c:if test="${addPermission}">
        <div class="dib">
            <cti:msg2 var="commentText" key=".enterText"/>
            <textarea rows="2" cols="0" id="newCommentInput" placeholder="${commentText}" class="MR10 fl rn taw250" 
                             onKeyPress="return yukon.da.comments.addOrCancel(event)"></textarea>
            <cti:button nameKey="create" icon="icon-plus-green" classes="M0 js-save-comment" busy="true"/>
        </div>
        <br>
        <br>
    </c:if>
    
    <cti:url var="commentsURL" value="/capcontrol/comments/"/>

    <div id="comment_editor" class="scroll-lg stacked">
        <form id="commentForm" action="/capcontrol/comments/" method="POST" data-submit-normal="${submit-normal}" data-comments-url="${commentsURL}">
            <cti:csrfToken/>
            <input type="hidden" name="paoId" value="${paoId}">
            <input type="hidden" name="commentId" id="commentId" value="">
            <input type="hidden" name="comment" id="comment">
            <input type="hidden" name="submitNormal" value="${submitNormal}">
            
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
                            <c:if test="${modifyPermission}">
                                <th class="remove-column"><i:inline key=".delete.label"/></th>
                            </c:if>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="comment" items="${comments}">
                            <tr id="commentRow_${comment.id}">

                                <td class="${modifyPermission ? 'editable' : '' }">
                                    <div id="editCommentSpan_${comment.id}" style="display: none;">
                                        <input id="editComment_${comment.id}" type="text" 
                                            style="margin-right: 5px;width:350px;" 
                                            name="editCommentInput" onKeyPress="return yukon.da.comments.updateOrCancel(event, ${comment.id})" 
                                            value="${fn:escapeXml(comment.comment)}">
                                        <a href="javascript:yukon.da.comments.updateComment(${comment.id})"><i:inline key=".save"/></a> <a href="javascript:yukon.da.comments.cancelUpdate(${comment.id})"><i:inline key=".cancel"/></a>
                                    </div>
                                    <div id="comment_${comment.id}" title="<cti:msg2 key=".clickToEdit"/>" <c:if test="${modifyPermission}">onclick="yukon.da.comments.editComment(${comment.id})"</c:if> >
                                        ${fn:escapeXml(comment.comment)}
                                    </div>
                                </td>
                                <c:choose>
                                    <c:when test="${comment.userName == null}">
                                        <td><i:inline key="yukon.common.dashes"/></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>${fn:escapeXml(comment.userName)}</td>
                                    </c:otherwise>
                                </c:choose>
                                <td><cti:formatDate value="${comment.date}" type="BOTH" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${comment.altered}"><i:inline key="yukon.common.yes"/></c:when>
                                        <c:otherwise><i:inline key="yukon.common.no"/></c:otherwise>
                                    </c:choose>
                                </td>
                                <c:if test="${modifyPermission}">
                                    <td class="remove-column">
                                        <cti:button nameKey="remove" renderMode="image" onclick="yukon.da.comments.deleteComment(${comment.id})" classes="center" icon="icon-delete"/>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </form>
    </div>
</cti:msgScope>
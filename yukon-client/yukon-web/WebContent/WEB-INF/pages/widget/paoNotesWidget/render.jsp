<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="common.paoNote, web.components.ajaxConfirm">
    
    <form:form id="create-note-form" method="POST" modelAttribute="createPaoNote">
        <cti:csrfToken/>
        <form:hidden path="paoId" id="deviceId"/>
        <form:hidden path="createUserName" />
        <table style="width:100%">
            <tr>
                <td width="80%">
                    <cti:msg2 var="noteTextPlaceholder" key=".noteText"/>
                    <tags:textarea rows="3" cols="46" path="noteText" id="createNoteTextarea" isResizable="false"
                                   placeholder="${noteTextPlaceholder}"/>
                </td>
                <td width="20%" class="vam">
                    <cti:button nameKey="create" icon="icon-plus-green" classes="js-create-note M0 fr"/>
                </td>
            </tr>
        </table>
    </form:form>
    
    <br>
    
    <c:choose>
        <c:when test="${empty recentNotes}">
            <span class="empty-list"><i:inline key=".noNotes"/></span>
        </c:when>
        <c:otherwise>
            <table class="row-highlighting striped bordered-div " style="width:100%">
                <c:forEach var="recentNote" items="${recentNotes}" varStatus="status">
                    <c:set var="noteId" value="${recentNote.paoNote.noteId}"/>
                    <tr>
                        <td width="80%">
                            <div id="js-note-${noteId}">
                                <div id="js-note-content-${noteId}">
                                    ${fn:escapeXml(recentNote.paoNote.noteText)}
                                </div>
                                <c:if test="${not empty recentNote.paoNote.editDate}">
                                    <div class="small-font-orange-color-text fr">
                                        <cti:formatDate type="BOTH" value="${recentNote.paoNote.editDate}" var="editDate"/>
                                        <i:inline key=".editedBy"/> ${fn:escapeXml(recentNote.paoNote.editUserName)} - ${editDate}
                                    </div>
                                </c:if>
                                <br>
                                <div class="fr small-font-gray-color-text">
                                    <cti:formatDate type="BOTH" value="${recentNote.paoNote.createDate}" var="createDate"/>
                                    ${fn:escapeXml(recentNote.paoNote.createUserName)} - ${createDate}
                                </div>
                            </div>
                            <div id="js-edit-note-${noteId}" class="dn">
                                <textarea id="js-edit-note-textarea-${noteId}" rows="3" cols="46" style="resize: none;" 
                                          maxlength="255" value="${recentNote.paoNote.noteText}"></textarea>
                            </div>
                        </td>
                    <td width="20%" class="vam js-note-actions">
                        <cti:msg2 var="cancelText" key=".cancel.hoverText"/>
                        <cti:msg2 var="deleteText" key=".delete.hoverText"/>
                        <div id="js-edit-note-btn-group-${noteId}" class="button-group">
                            <cti:button id="js-edit-note-btn-${noteId}" renderMode="buttonImage" icon="icon-pencil" 
                                        data-note-id="${noteId}"/>
                            <cti:button id="js-delete-note-btn-${noteId}" renderMode="buttonImage" icon="icon-cross" 
                                        data-note-id="${noteId}" data-ok-event="yukon:note:delete" title="${deleteText}"/>
                            <d:confirm on="#js-delete-note-btn-${noteId}"  nameKey="noteText.confirmDelete"/>
                        </div>
                        <div id="js-save-note-group-${noteId}" class="button-group dn">
                            <cti:button id="js-save-note-btn-${noteId}" renderMode="buttonImage" icon="icon-disk" 
                                        data-note-id="${noteId}"/>
                            <cti:button id="js-cancel-btn-${noteId}" renderMode="buttonImage" icon="icon-cancel" 
                                        data-note-id="${noteId}" title="${cancelText}"/>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="action-area">
        <a href="#"><i:inline key="yukon.common.viewAll"/></a>
        &nbsp;|&nbsp;
        <cti:url value="/tools/paoNote/search" var="searchUrl"/>
        <a href="${searchUrl}"><i:inline key="yukon.common.search"/></a>
    </div>
    
</cti:msgScope>
<cti:includeScript link="/resources/js/widgets/yukon.paonotes.widget.js"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="common.paoNote, web.components.ajaxConfirm, yukon.common">
    
    <form:form id="create-note-form" method="POST" modelAttribute="createPaoNote">
        <cti:csrfToken/>
        <form:hidden path="paoId" id="deviceId"/>
        <form:hidden path="createUserName" />
        <table style="width:100%">
            <tr>
                <cti:checkRolesAndProperties value="MANAGE_NOTES" level = "OWNER">
                    <td width="80%" class="P0">
                        <cti:msg2 var="noteTextPlaceholder" key=".noteText.placeHolder" argument="${maxCharactersInNote}"/>
                        <tags:textarea rows="3" cols="46" path="noteText" id="createNoteTextarea" isResizable="false"
                                       placeholder="${noteTextPlaceholder}" maxLength="${maxCharactersInNote}"/>
                    </td>
                    <td width="20%" class="vam">
                        <cti:button nameKey="create" icon="icon-plus-green" classes="js-create-note M0 fr"/>
                    </td>
                </cti:checkRolesAndProperties>
            </tr>
        </table>
    </form:form>
    
    <br>
    
    <c:choose>
        <c:when test="${empty recentNotes}">
            <span class="empty-list"><i:inline key=".noNotes"/></span>
        </c:when>
        <c:otherwise>
            <table class="row-highlighting striped bordered-div wrbw" style="width:100%; table-layout:fixed;">
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
                                    <br>
                                </c:if>
                                <div class="fr small-font-gray-color-text">
                                    <cti:formatDate type="BOTH" value="${recentNote.paoNote.createDate}" var="createDate"/>
                                    ${fn:escapeXml(recentNote.paoNote.createUserName)} - ${createDate}
                                </div>
                            </div>
                            <div id="js-edit-note-${noteId}" class="dn">
                                <textarea id="js-edit-note-textarea-${noteId}" rows="3" cols="46" style="resize: none;" 
                                          maxlength="255" value="${recentNote.paoNote.noteText}" placeholder="${noteTextPlaceholder}"></textarea>
                            </div>
                        </td>
                    <td width="20%" class="vam js-note-actions">
                        <cti:msg2 var="cancelText" key=".cancel.hoverText"/>
                        <cti:msg2 var="deleteText" key=".delete.hoverText"/>
                        <cti:msg2 var="editText" key=".edit.hoverText"/>
                        <cti:msg2 var="saveText" key="yukon.common.save"/>
                        <div id="js-edit-note-btn-group-${noteId}" class="button-group">
                            <c:if test="${recentNote.modifiable}">
                                <cti:button id="js-edit-note-btn-${noteId}" renderMode="buttonImage" icon="icon-pencil" 
                                    data-note-id="${noteId}" title="${editText}"/>
                                <cti:button id="js-delete-note-btn-${noteId}" renderMode="buttonImage" icon="icon-cross" 
                                    data-note-id="${noteId}" data-ok-event="yukon:note:delete" title="${deleteText}"/>
                                <d:confirm on="#js-delete-note-btn-${noteId}"  nameKey="confirmDelete"/>
                            </c:if>
                        </div>
                        <div id="js-save-note-group-${noteId}" class="button-group dn">
                            <cti:button id="js-save-note-btn-${noteId}" renderMode="buttonImage" icon="icon-disk" 
                                        data-note-id="${noteId}" title="${saveText}"/>
                            <cti:button id="js-cancel-btn-${noteId}" renderMode="buttonImage" icon="icon-delete" 
                                        data-note-id="${noteId}" title="${cancelText}"/>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="action-area">
        <c:set var="count" value="${noteCount - maxNotesToDisplay}"/>
        <c:if test="${count > 0}">
            ${count} <i:inline key=".more"/><i:inline key=".moreInfoSuffix"/>&nbsp;|&nbsp;
        </c:if>
        <a class="js-view-all-notes" href="javascript:void(0)" data-pao-id="${createPaoNote.paoId}"><i:inline key="yukon.common.viewAll"/></a>
        &nbsp;|&nbsp;
        <cti:url value="/tools/paoNotes/search" var="searchUrl"/>
        <a href="${searchUrl}"><i:inline key="yukon.common.search"/></a>
    </div>
</cti:msgScope>
<div class="dn" id="js-pao-notes-popup"></div>
<cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
<cti:includeScript link="/resources/js/widgets/yukon.widget.paonotes.js"/>

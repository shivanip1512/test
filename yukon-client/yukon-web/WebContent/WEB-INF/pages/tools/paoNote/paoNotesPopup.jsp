<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="common.paoNote, web.components.ajaxConfirm">
    <input type="hidden" id="popupTitle" value="${popupTitle}"/>
    <cti:url var="url" value="/tools/paoNotes/createPaoNote"/>
    <form:form id="create-popup-note-form" method="POST" modelAttribute="paoNote" action="${url}">
        <cti:csrfToken/>
        <form:hidden path="paoId"/>
        <form:hidden path="createUserName"/>
        <div class="column-14-10 clearfix">
            <cti:checkRolesAndProperties value="MANAGE_NOTES" level = "OWNER">
                <div class="column one">
                    <cti:msg2 var="noteTextPlaceholder" key=".noteText"/>
                    <tags:textarea id="createPopupNoteTextarea" path="noteText" rows="3" cols="72" isResizable="false" 
                              maxLength="255" placeholder="${noteTextPlaceholder}"/>
                </div>
            </cti:checkRolesAndProperties>
            <div class="column two nogutter">
                <c:set var="searchStyle" value="margin-right : -147%;"/>
                <cti:checkRolesAndProperties value="MANAGE_NOTES" level = "OWNER">
                    <c:set var="searchStyle" value=""/>
                    <cti:button nameKey="create" icon="icon-plus-green" classes="js-create-popup-note fl"/>
                </cti:checkRolesAndProperties>
                <cti:url value="/tools/paoNotes/search" var="searchUrl"/>
                <div class="fr" style="${searchStyle}"><a href="${searchUrl}"><i:inline key="yukon.common.search"/></a></div>
            </div>
        </div>
    </form:form>
    <br>
    <c:choose>
        <c:when test="${empty searchResults}">
            <span class="empty-list"><i:inline key=".noNotes"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg" id="pao-notes-list-container" data-url="${searchUrl}" data-static>
                <table class="compact-results-table has-actions row-highlighting wrbw" style="width:100%; table-layout:fixed;">
                    <thead>
                        <th width="36%"><i:inline key="yukon.web.common.paoNote.noteText"/></th>
                        <th width="10%"/>
                        <th width="11%"><i:inline key="yukon.web.common.paoNote.createdBy"/></th>
                        <th width="11%"><i:inline key="yukon.web.common.paoNote.createDate"/></th>
                        <th width="11%"><i:inline key="yukon.web.common.paoNote.editedBy"/></th>
                        <th width="11%"><i:inline key="yukon.web.common.paoNote.editDate"/></th>
                        <th width="10%"/>
                    </thead>
                    <tbody>
                        <c:forEach var="paoNoteSearchResult" items="${searchResults}">
                            <c:set var="noteId" value="${paoNoteSearchResult.paoNote.noteId}"/>
                            <c:set var="paoId" value="${paoNoteSearchResult.paoNote.paoId}"/>
                            <tr id="js-popup-note-row-${noteId}">
                                <td class="vam js-popup-note-actions" colspan="2">
                                    <div id="js-popup-note-${noteId}">
                                        <div id="js-popup-note-content-${noteId}">
                                            ${fn:escapeXml(paoNoteSearchResult.paoNote.noteText)}
                                        </div>
                                    </div>
                                    <div id="js-edit-popup-note-${noteId}" class="dn">
                                        <textarea id="js-edit-popup-note-textarea-${noteId}" rows="3" style="resize: none; width:100%" 
                                            maxlength="255" value="${paoNoteSearchResult.paoNote.noteText}"></textarea>
                                    </div>
                                </td>
                                <td class="vam js-popup-note-actions dn">
                                    <cti:msg2 var="cancelText" key=".cancel.hoverText"/>
                                    <cti:msg2 var="saveText" key="yukon.common.save"/>
                                    <div id="js-save-popup-note-group-${noteId}" class="button-group dn">
                                        <cti:button id="js-save-popup-note-btn-${noteId}" renderMode="buttonImage" icon="icon-disk" 
                                                    data-note-id="${noteId}" title="${saveText}" data-pao-id="${paoId}"/>
                                        <cti:button id="js-popup-note-cancel-btn-${noteId}" renderMode="buttonImage" icon="icon-delete" 
                                                    data-note-id="${noteId}" title="${cancelText}"/>
                                    </div>
                                </td>
                                <td>${fn:escapeXml(paoNoteSearchResult.paoNote.createUserName)}</td>
                                <td>
                                    <cti:formatDate type="BOTH" value="${paoNoteSearchResult.paoNote.createDate}" 
                                                    var="createDate"/>
                                    ${createDate}
                                </td>
                                <td>${fn:escapeXml(paoNoteSearchResult.paoNote.editUserName)}</td>
                                <td>
                                    <c:if test="${not empty paoNoteSearchResult.paoNote.editDate}">
                                        <cti:formatDate type="BOTH" value="${paoNoteSearchResult.paoNote.editDate}" 
                                                        var="editDate"/>
                                        ${editDate}
                                    </c:if>
                                </td>
                                <td class="vam js-popup-note-actions">
                                    <cti:msg2 var="editText" key=".edit.hoverText"/>
                                    <cti:msg2 var="deleteText" key=".delete.hoverText"/>
                                    <div id="js-edit-popup-note-btn-group-${noteId}" class="button-group fr">
                                        <c:if test="${paoNoteSearchResult.modifiable}">
                                            <cti:button id="js-edit-popup-note-btn-${noteId}" renderMode="buttonImage" icon="icon-pencil" 
                                                        data-note-id="${noteId}" title="${editText}"/>
                                            <cti:button id="js-delete-popup-note-btn-${noteId}" renderMode="buttonImage" icon="icon-cross" 
                                                        data-ok-event="yukon:popup:note:delete" title="${deleteText}"/>
                                            <d:confirm on="#js-delete-popup-note-btn-${noteId}"  nameKey="confirmDelete"/>
                                        </c:if>
                                        <cti:url var="url" value="/tools/paoNotes/deletePaoNote/${noteId}"/>
                                        <form:form id="delete-popup-note-form" method="DELETE" action="${url}">
                                            <input type="hidden" name="paoId" value="${paoId}"/>
                                            <cti:csrfToken/>
                                        </form:form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="common.paoNote, web.components.ajaxConfirm">
    <form:form id="create-note-form" method="POST" modelAttribute="paoNote">
        <cti:csrfToken/>
        <form:hidden path="paoId"/>
        <form:hidden path="createUserName"/>
        <div class="column-14-10 clearfix">
            <div class="column one">
                <cti:msg2 var="noteTextPlaceholder" key=".noteText"/>
                <tags:textarea id="createNoteTextarea" path="noteText" rows="3" cols="77" isResizable="false" 
                          maxLength="255" placeholder="${noteTextPlaceholder}"/>
            </div>
            <div class="column two nogutter">
                <cti:button nameKey="create" icon="icon-plus-green" classes="js-create-note fl"/>
                <cti:url value="/tools/paoNotes/search" var="searchUrl"/>
                <div class="fr"><a href="${searchUrl}"><i:inline key="common.paoNotesSearch.pageName"/></a></div>
            </div>
        </div>
    </form:form>
    <div class="scroll-lg" id="pao-notes-list-container" data-url="${searchUrl}" data-static>
        <br>
        <table class="compact-results-table has-actions row-highlighting">
            <thead>
                    <th colspan="2"><i:inline key="yukon.web.common.paoNote.noteText"/></th>
                    <th><i:inline key="yukon.web.common.paoNote.createdBy"/></th>
                    <th><i:inline key="yukon.web.common.paoNote.createDate"/></th>
                    <th><i:inline key="yukon.web.common.paoNote.editedBy"/></th>
                    <th colspan="2"><i:inline key="yukon.web.common.paoNote.editDate"/></th>
            </thead>
            <tbody>
                <c:forEach var="paoNoteSearchResult" items="${searchResults}">
                    <c:set var="noteId" value="${paoNoteSearchResult.paoNote.noteId}"/>
                    <tr id="js-note-row-${noteId}">
                        <td width="30%" class="vam js-note-actions">
                            <div id="js-note-${noteId}">
                                <div id="js-note-content-${noteId}">
                                    ${fn:escapeXml(paoNoteSearchResult.paoNote.noteText)}
                                </div>
                            </div>
                            <div id="js-edit-note-${noteId}" class="dn">
                                <textarea id="js-edit-note-textarea-${noteId}" rows="3" cols="40" style="resize: none;" 
                                    maxlength="255" value="${paoNoteSearchResult.paoNote.noteText}"></textarea>
                            </div>
                        </td>
                        <td width="10%" class="js-note-actions">
                            <cti:msg2 var="cancelText" key=".cancel.hoverText"/>
                            <cti:msg2 var="saveText" key="yukon.common.save"/>
                            <div id="js-save-note-group-${noteId}" class="button-group dn">
                                <cti:button id="js-save-note-btn-${noteId}" renderMode="buttonImage" icon="icon-disk" 
                                            data-note-id="${noteId}" title="${saveText}" data-pao-id="${paoNoteSearchResult.paoNote.paoId}"/>
                                <cti:button id="js-cancel-btn-${noteId}" renderMode="buttonImage" icon="icon-delete" 
                                            data-note-id="${noteId}" title="${cancelText}"/>
                            </div>
                        </td>
                        <td width="10%">${fn:escapeXml(paoNoteSearchResult.paoNote.createUserName)}</td>
                        <td width="15%">
                            <cti:formatDate type="BOTH" value="${paoNoteSearchResult.paoNote.createDate}" 
                                            var="createDate"/>
                            ${createDate}
                        </td>
                        <td width="10%">${fn:escapeXml(paoNoteSearchResult.paoNote.editUserName)}</td>
                        <td width="15%">
                            <c:if test="${not empty paoNoteSearchResult.paoNote.editDate}">
                                <cti:formatDate type="BOTH" value="${paoNoteSearchResult.paoNote.editDate}" 
                                                var="editDate"/>
                                ${editDate}
                            </c:if>
                        </td>
                        <td width="10%" class="vam js-note-actions">
                            <cti:msg2 var="editText" key=".edit.hoverText"/>
                            <cti:msg2 var="deleteText" key=".delete.hoverText"/>
                            <div id="js-edit-note-btn-group-${noteId}" class="button-group fr">
                                <cti:button id="js-edit-note-btn-${noteId}" renderMode="buttonImage" icon="icon-pencil" 
                                            data-note-id="${noteId}" title="${editText}"/>
                                <cti:button id="js-delete-note-btn-${noteId}" renderMode="buttonImage" icon="icon-cross" 
                                            data-note-id="${noteId}" data-ok-event="yukon:note:delete" title="${deleteText}"
                                            data-pao-id="${paoNoteSearchResult.paoNote.paoId}"/>
                                <d:confirm on="#js-delete-note-btn-${noteId}"  nameKey="confirmDelete"/>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</cti:msgScope>
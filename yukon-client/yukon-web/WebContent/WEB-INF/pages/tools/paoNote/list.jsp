<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="paoNotesSearch">
    <cti:msgScope paths="common.paoNote, common.paoNotesSearch, menu.tools, yukon.common">
        <hr>
        <div class="filter-section dib">
            <cti:msg2 var="noteTextPlaceholder" key=".noteText"/>
            <cti:url var="url" value="/tools/paoNotes/search"/>
            <form:form id="filter-pao-notes-form" action="${url}" modelAttribute="paoNoteFilter" method="GET">
                <i:inline key="yukon.common.filterBy"/>
                <tags:textarea rows="2" cols="46" path="text" isResizable="false" placeholder="${noteTextPlaceholder}"/>
                &nbsp;
                <form:select id="js-select-devices" path="paoSelectionMethod">
                    <c:forEach var="paoSelectionMethod" items="${paoSelectionMethods}">
                        <form:option id="js-${paoSelectionMethod}" value="${paoSelectionMethod}">
                            <i:inline key=".${paoSelectionMethod}"/>
                        </form:option>
                    </c:forEach>
                </form:select>
                <div id="js-picker-dialog" class="dib dn">
                    <tags:bind path="paoIds">
                        <tags:pickerDialog type="paoPicker" 
                                       id="paoPicker"
                                       linkType="selection"
                                       selectionProperty="paoName"
                                       destinationFieldName="paoIds"
                                       allowEmptySelection="true"
                                       multiSelectMode="true"
                                       initialIds="${paoNoteFilter.paoIds}"/>
                    </tags:bind>
                </div>
                <div id="js-device-group-picker" class="dib dn">
                    <cti:list var="groups">
                        <c:forEach var="subGroup" items="${deviceGroupNames}">
                            <cti:item value="${subGroup}"/>
                        </c:forEach>
                    </cti:list>
                    <tags:deviceGroupPicker inputName="deviceGroupNames" inputValue="${groups}" multi="true"/>
                </div>
                <div id="js-note-create-dates" class="dib">
                    <i:inline key=".createDate"/> :
                    <dt:date path="startDate" wrapperClass="fn vam" displayValidationToRight="true"/>
                    <i:inline key="yukon.common.to"/>
                    <dt:date path="endDate" wrapperClass="fn vam" displayValidationToRight="true"/>
                </div>
                <div id="js-note-create-by" class="dib">
                    <cti:yukonUser var="currentUser"/>
                    <i:inline key=".createdBy"/> :
                    <form:select path="user">
                        <form:option value=""><i:inline key=".anyUser"/></form:option>
                        <form:option value="${currentUser.username}"><i:inline key=".currentUser"/></form:option>
                    </form:select>
                </div>
                <cti:button nameKey="filter" classes="primary action fr vab" type="submit"/>
            </form:form>
        </div>
        <hr>
        <br>
        <c:choose>
            <c:when test="${searchResults.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noResults"/></span>
            </c:when>
            <c:otherwise>
                <span class="fwn"><i:inline key=".filteredResults"/> : </span>
                <span class="badge">${searchResults.hitCount}</span>&nbsp;<i:inline key=".notes"/>
                <span class="js-cog-menu">
                    <cm:dropdown icon="icon-cog">
                        <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" 
                                           newTab="true"/> 
                        <cm:dropdownOption icon="icon-csv" key=".download" classes="js-download"/>
                        <cti:url var="mapUrl" value="/tools/map">
                            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}" newTab="true"/>
                        <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}" newTab="true"/>
                        <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}" newTab="true"/>
                    </cm:dropdown>
                </span>
                <cti:url var="searchUrl" value="/tools/paoNotes/search">
                    <cti:param name="text" value="${paoNoteFilter.text}"/>
                    <cti:param name="user" value="${paoNoteFilter.user}"/>
                    <cti:formatDate type="DATE" value="${paoNoteFilter.startDate}" var="startDate"/>
                    <cti:param name="startDate" value="${startDate}"/>
                    <cti:formatDate type="DATE" value="${paoNoteFilter.endDate}" var="endDate"/>
                    <cti:param name="endDate" value="${endDate}"/>
                    <cti:param name="deviceGroupNames" value="${deviceGroupNames}"/>
                    <c:if test="${paoNoteFilter.paoSelectionMethod == 'selectIndividually'}">
                        <c:forEach var="paoId" items="${paoNoteFilter.paoIds}">
                            <cti:param name="paoIds" value="${paoId}"/>
                        </c:forEach>
                    </c:if>
                    <cti:param name="paoSelectionMethod" value="${paoNoteFilter.paoSelectionMethod}"/>
                </cti:url>
                <div id="pao-notes-list-container" data-url="${searchUrl}" data-static>
                    <br>
                    <table class="compact-results-table has-actions row-highlighting">
                        <thead>
                            <tr>
                                <tags:sort column="${deviceName}" />
                                <tags:sort column="${deviceType}" />
                                <tags:sort column="${noteText}" />
                                <tags:sort column="${createdBy}" />
                                <tags:sort column="${createDate}" />
                                <tags:sort column="${modifiedBy}" />
                                <tags:sort column="${modifiedDate}" />
                                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="paoNoteSearchResult" items="${searchResults.resultList}">
                                <tr>
                                    <td>
                                        <cti:paoDetailUrl paoId="${paoNoteSearchResult.paoNote.paoId}">
                                            <c:if test="${!empty paoNoteSearchResult.paoName}">
                                                ${fn:escapeXml(paoNoteSearchResult.paoName)}
                                            </c:if>
                                        </cti:paoDetailUrl>
                                    </td>
                                    <td style="width:10%"><i:inline key="${paoNoteSearchResult.paoType}"/></td>
                                    <td>${fn:escapeXml(paoNoteSearchResult.paoNote.noteText)}</td>
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
                                    <td>
                                        <cm:dropdown>
                                            <cm:dropdownOption icon="icon-notes-pin" key=".viewAllNotes" classes="js-gw-connect js-view-all-notes" 
                                                data-pao-id="${paoNoteSearchResult.paoNote.paoId}" 
                                                data-pao-name="${paoNoteSearchResult.paoName}"/>
                                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions">
                                                <cti:param name="collectionType" value="idList"/>
                                                <cti:param name="idList.ids" value="${paoNoteSearchResult.paoNote.paoId}"/>
                                            </cti:url>
                                            <cm:dropdownOption icon="icon-cog-go" key=".collectionActions" 
                                                               classes="js-gw-disconnect" href="${collectionActionUrl}" 
                                                               newTab="true" />
                                        </cm:dropdown>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" />
                </div>
            </c:otherwise>
        </c:choose>
    </cti:msgScope>
    <div class="dn" id="js-pao-notes-popup"
        data-pao-id="${paoNoteSearchResult.paoNote.paoId}">
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotessearch.js"/>
</cti:standardPage>

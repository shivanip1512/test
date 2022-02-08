<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="paoNotesSearch">
    <style>
        .MT1 {
            margin-top: 1%;
        }
    </style>
    <cti:msgScope paths="common.paoNote, common.paoNotesSearch, menu.tools, yukon.common">
        <hr>
        <div class="two-column-table filter-section dib">
            <cti:msg2 var="noteTextPlaceholder" key=".noteText"/>
            <cti:url var="url" value="/tools/paoNotes/search"/>
            <form:form id="filter-pao-notes-form" action="${url}" modelAttribute="paoNoteFilter" method="GET">
                <i:inline key="yukon.common.filterBy"/>
                <form:input path="text" placeholder="${noteTextPlaceholder}" size="35" maxlength="40" cssStyle="${marignTopDeviceFilters}"/>
                <div id="notes-search-help-popup" class="dn" data-title='<i:inline key="yukon.web.modules.tools.paoNotesSearch.pageName"/>' 
                     data-width="600">
                    <div class="scroll-lg">
                        <i:inline key="yukon.web.widgets.paoNotesSearchWidget.helpText"/>
                    </div>
                </div>
                <cti:button renderMode="image" icon="icon-help" classes="widget-controls fr" data-popup="#notes-search-help-popup"/>
                
                <i:inline key=".devices"/>:
                <%@ include file="../../common/paoNotes/selectPaos.jsp" %>
                
                <c:if test="${hasDateFilterErrors}">
                    <c:set var="marginTop" value="margin-top: -5%;"/>
                </c:if>
                <c:if test="${hasDeviceFilterErrors}">
                    <c:set var="marignTopDeviceFilters" value="margin-top: -5%;"/>
                </c:if>
                <div class="db">
                    <div id="js-note-create-dates" class="dib MT1" style="margin-left: 6%;">
                        <i:inline key=".createDate"/>:
                        <dt:date path="dateRange.min" wrapperClass="fn vam" displayValidationToRight="true"/>
                        <i:inline key="yukon.common.to"/>
                        <dt:date path="dateRange.max" wrapperClass="fn vam"/>
                    </div>
                    <div id="js-note-create-by" class="dib vam" style="${marginTop}">
                        <cti:yukonUser var="currentUser"/>
                        <i:inline key=".createdBy"/>:
                        <form:select path="user">
                            <form:option value=""><i:inline key=".anyUser"/></form:option>
                            <form:option value="${currentUser.username}"><i:inline key=".currentUser"/></form:option>
                        </form:select>
                    </div>
                    <cti:button nameKey="filter" classes="primary action fr MT1" type="submit"/>
                </div>
            </form:form>
        </div>
        <hr>
        <c:choose>
            <c:when test="${searchResults.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noResults"/></span>
            </c:when>
            <c:otherwise>
                <span class="fwn"><i:inline key=".filteredResults"/>: </span>
                <span class="badge">${searchResults.hitCount}</span>&nbsp;<i:inline key=".notes"/>
                <span class="js-cog-menu">
                    <cm:dropdown icon="icon-cog">
                        <!-- Collection Actions -->
                        <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" 
                                           newTab="true"/>
                        
                        <!-- Download -->
                        <cm:dropdownOption icon="icon-csv" key=".download" classes="js-download"/>
                       
                        <!-- Map Devices -->
                        <cti:url var="mapUrl" value="/tools/map">
                            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map" key=".mapDevices" href="${mapUrl}" newTab="true"/>
                            
                        <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                            <!-- Read Attribute -->
                            <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                    <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}" newTab="true"/>
                            
                            <!-- Send Command -->
                            <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                                <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                                    <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                        <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                                    </c:forEach>
                                </cti:url>
                                <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}" newTab="true"/>
                            </cti:checkRolesAndProperties>
                        </cti:checkRolesAndProperties>
                    </cm:dropdown>
                </span>
                <cti:url var="searchUrl" value="/tools/paoNotes/search">
                    <cti:param name="text" value="${paoNoteFilter.text}"/>
                    <cti:param name="user" value="${paoNoteFilter.user}"/>
                    <cti:formatDate type="DATE" value="${paoNoteFilter.dateRange.min}" var="startDate"/>
                    <cti:formatDate type="DATE" value="${paoNoteFilter.dateRange.max}" var="endDate"/>
                    <cti:param name="dateRange.min" value="${startDate}"/>
                    <cti:param name="dateRange.max" value="${endDate}"/>
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
                    <table class="compact-results-table has-actions row-highlighting wrbw" style="table-layout:fixed;">
                        <thead>
                            <tr>
                                <tags:sort width="12%" column="${deviceName}" />
                                <tags:sort width="12%" column="${deviceType}" />
                                <tags:sort width="31%" column="${noteText}" />
                                <tags:sort width="12%" column="${createdBy}" />
                                <tags:sort width="12%" column="${createDate}" />
                                <tags:sort width="10%" column="${editedBy}" />
                                <tags:sort width="10%" column="${editDate}" />
                                <th width="1%" class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
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
                                    <td><i:inline key="${paoNoteSearchResult.paoType}"/></td>
                                    <td>
                                        <!-- This needs to be on a single line. Any whitespace will be visible to the user, due to wsp style-->
                                        <span class="wspr">${fn:escapeXml(paoNoteSearchResult.paoNote.noteText)}</span>
                                    </td>
                                    <td>${fn:escapeXml(paoNoteSearchResult.paoNote.createUserName)}</td>
                                    <td>
                                        <cti:formatDate type="BOTH" value="${paoNoteSearchResult.paoNote.createDate}" 
                                                        var="createdDate"/>
                                        ${createdDate}
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
                                                data-pao-id="${paoNoteSearchResult.paoNote.paoId}"/>
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
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotessearch.js"/>
</cti:standardPage>
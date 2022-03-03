<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.config.loggers,yukon.common">
    
    <div class="scroll-md">
        <table class="compact-results-table row-highlighting has-actions ">
            <thead>
                <tr>
                    <th class="row-icon"/>
                    <tags:sort column="${loggerName}"/>
                    <tags:sort column="${loggerLevel}"/>
                    <tags:sort column="${expirationDate}"/>
                    <th class="action-column"><cti:icon icon="icon-cog" /></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="logger" items="${userLoggers}">
                    <tr>
                        <c:set var="loggerId" value="${logger.loggerId}"/>
                        <td>
                            <c:if test="${not empty fn:trim(logger.notes)}">
                                <cti:msg2 var="viewNoteTitle" key=".viewNote"/>
                                <cti:icon icon="icon-notes-pin" title="${viewNoteTitle}" data-logger-id="${loggerId}" data-popup="#logger-note-${loggerId}"/>
                                <div id="logger-note-${loggerId}" class="dn" data-title="Notes" data-width="300" data-height="200"
                                    data-destroy-dialog-on-close="true">${fn:escapeXml(logger.notes)}</div>
                                <br/>
                            </c:if>
                        </td>

                        <td class="wbba">${fn:escapeXml(logger.loggerName)}</td>
                        <td><i:inline key="${logger.level}"/></td>
                        <td>
                            <cti:msg2 var="neverText" key="yukon.common.never"/>
                            <cti:formatDate type="DATE" value="${logger.expirationDate}" nullText="${neverText}"/>
                        </td>
                        <td>
                            <cm:dropdown icon="icon-cog">
                                <cti:msg2 var="editLoggerTitle" key=".editUserLoggerTitle"/>
                                <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-logger-popup" data-logger-id="${loggerId}" data-title="${editLoggerTitle}"/>
                                
                                <cm:dropdownOption key=".delete" icon="icon-delete" data-ok-event="yukon:logger:delete" 
                                     classes="js-hide-dropdown js-delete-logger-${loggerId}" data-logger-id="${loggerId}"/>

                                <d:confirm on=".js-delete-logger-${loggerId}" nameKey="confirmDelete" argument="${logger.loggerName}"/>
                                <cti:url var="deleteUrl" value="/admin/config/loggers/${loggerId}"/>
                                <form:form id="delete-logger-form-${loggerId}" action="${deleteUrl}" method="DELETE">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="loggerName" value="${fn:escapeXml(logger.loggerName)}"/>
                                </form:form>
                            </cm:dropdown>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <c:if test="${empty userLoggers}">
        <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>
    <div class="dn js-edit-logger-popup"
             data-popup
             data-dialog
             data-title="${editLoggerTitle}">
    </div>

</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />
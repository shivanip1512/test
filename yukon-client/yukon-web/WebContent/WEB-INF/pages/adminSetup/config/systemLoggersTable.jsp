<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.config.loggers,yukon.common">
    
    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <tr>
                <th class="row-icon"/>
                <tags:sort column="${loggerName}"/>
                <tags:sort column="${loggerLevel}"/>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="logger" items="${systemLoggers}">
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
                        <cm:dropdown icon="icon-cog">
                            <cti:msg2 var="editLoggerTitle" key=".editSystemLoggerTitle"/>
                            <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-logger-popup" data-logger-id="${loggerId}" data-title="${editLoggerTitle}"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty systemLoggers}">
        <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>

    <div class="dn js-edit-logger-popup"
             data-popup
             data-dialog
             data-title="${editLoggerTitle}">
    </div>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />
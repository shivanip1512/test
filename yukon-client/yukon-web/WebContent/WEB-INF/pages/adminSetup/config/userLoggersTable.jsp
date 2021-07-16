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
    
    <c:set var="errorClass" value="${not empty errorMessage ? '' : 'dn'}"/>
    <tags:alertBox classes="js-error-msg ${errorClass}" includeCloseButton="true">${fn:escapeXml(errorMessage)}</tags:alertBox>
    <c:set var="successClass" value="${not empty successMessage ? '' : 'dn'}"/>
    <tags:alertBox type="success" classes="js-success-msg ${successClass}" includeCloseButton="true">${fn:escapeXml(successMessage)}</tags:alertBox>

    <table class="compact-results-table row-highlighting has-actions">
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
                        <c:if test="${not empty logger.notes}">
                            <cti:msg2 var="viewNoteTitle" key=".viewNote"/>
                            <cti:icon icon="icon-notes-pin" title="${viewNoteTitle}" data-logger-id="${loggerId}" data-popup="#logger-note-${loggerId}"/>
                            <div id="logger-note-${loggerId}" class="dn" data-title="Notes" data-width="300" data-height="200">${logger.notes}</div>
                            <br/>
                        </c:if>
                    </td>

                    <td>${fn:escapeXml(logger.loggerName)}</td>
                    <td><i:inline key="${logger.level}"/></td>
                    <td>
                        <cti:msg2 var="neverText" key="yukon.common.never"/>
                        <cti:formatDate type="DATE" value="${logger.expirationDate}" nullText="${neverText}"/>
                    </td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-logger" data-logger-id="${loggerId}"/>

                            <cm:dropdownOption key=".delete" icon="icon-cross" id="delete-logger-${loggerId}" 
                            data-ok-event="yukon:logger:delete" classes="js-hide-dropdown" data-logger-id="${loggerId}"/>

                            <d:confirm on="#delete-logger-${loggerId}" nameKey="confirmDelete" argument="${logger.loggerName}"/>
                            <cti:url var="deleteUrl" value="/admin/config/loggers/${loggerId}"/>
                            <form:form id="delete-logger-form-${loggerId}" action="${deleteUrl}" method="DELETE">
                                <cti:csrfToken/>
                            </form:form>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty userLoggers}">
        <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>
    <cti:msg2 var="editUserLoggerTitle" key=".editUserLoggerTitle"/>
    <div class="dn js-edit-logger-popup"
             data-popup
             data-dialog
             data-title="${editUserLoggerTitle}">
    </div>

</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />
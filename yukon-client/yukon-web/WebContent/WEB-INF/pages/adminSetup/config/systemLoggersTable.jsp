<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
                <th><i:inline key=".loggerName"></i:inline></th>
                <th><i:inline key=".loggerLevel"></i:inline></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="logger" items="${systemLoggers}">
                <tr>
                    <c:set var="loggerName" value="${logger.loggerName}"/>
                    <td>${fn:escapeXml(logger.loggerName)}</td>
                    <td><i:inline key="${logger.level}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".edit" icon="icon-pencil" data-assignment-id="${logger.loggerName}"/>
                                <input type="hidden" name="name" value="${fn:escapeXml(logger.loggerName)}"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty systemLoggers}">
        <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>

    <cti:msg2 var="editSystemLoggerTitle" key=".editSystemLoggerTitle"/>
    <div class="dn js-edit-logger-popup"
             data-popup
             data-dialog
             data-title="${editSystemLoggerTitle}">
    </div>
</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
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
                <tags:sort column="${loggerName}"/>
                <tags:sort column="${level}"/>
                <tags:sort column="${expiration}"/>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="logger" items="${userLoggers}">
                <tr>
                    <c:set var="loggerId" value="${logger.loggerId}"/>
                    <td>
                        <c:if test="${not empty logger.notes}">
                            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                            <cti:icon icon="icon-notes-pin" title="${viewAllNotesTitle}" />
                        </c:if>
                    </td>

                    <td>${fn:escapeXml(logger.loggerName)}</td>
                    <td><i:inline key="${logger.level}"/></td>
                    <td><cti:formatDate type="BOTH" value="${logger.expirationDate}" /></td>
                    
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".edit" icon="icon-pencil"/>
                            <cm:dropdownOption key=".delete" icon="icon-cross"/>
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
    <div class="dn js-edit-assignment-popup"
             data-popup
             data-dialog
             data-title="${editUserLoggerTitle}">
    </div>

</cti:msgScope>
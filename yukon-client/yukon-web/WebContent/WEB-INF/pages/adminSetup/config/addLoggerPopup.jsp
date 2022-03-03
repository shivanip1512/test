<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${!empty errorMessage}">
	<tags:alertBox type="error" includeCloseButton="true">${errorMessage}</tags:alertBox>
</c:if>

<cti:msgScope paths="modules.adminSetup.config.loggers,yukon.common">

    <cti:url var="saveUrl" value="/admin/config/loggers"/>
    <form:form id="logger-form" action="${saveUrl}" method="POST" modelAttribute="logger">

        <cti:csrfToken/>
        <tags:hidden path="loggerId"/>
        <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".loggerName">
        <c:choose>
             <c:when test="${isEditMode}">
                 <span class="wbba">${fn:escapeXml(logger.loggerName)}</span>
                 <input type="hidden" name="loggerName" value="${fn:escapeXml(logger.loggerName)}"/>
            </c:when>
            <c:otherwise>
                <tags:input path="loggerName" size="60" maxlength="200"/>
             </c:otherwise>
       </c:choose>

       </tags:nameValue2>
            <tags:nameValue2 nameKey=".loggerLevel">
                <tags:selectWithItems path="level" items="${loggerLevels}"/>
            </tags:nameValue2>

            <c:if test="${allowDateTimeSelection}">
                <tags:nameValue2 nameKey=".expirationDate">
                <tags:switchButton name="specifiedDateTime" toggleGroup="js-date-time" toggleAction="hide" color="true"
                       onNameKey="yukon.common.specified" offNameKey="yukon.common.never" checked="${specifiedDateTime}"/>
                <c:set var="specifiedClass" value="${specifiedDateTime ? '' : 'dn'}"/>
                <span data-toggle-group="js-date-time" class="${specifiedClass}">
                    <dt:date path="expirationDate" minDate="${now}" hideErrors="${invalidDateError}"
                             cssClass="${status.error ? 'error' : ''}" displayValidationToRight="${!isEditMode}"/>
                    <c:if test="${invalidDateError}">
                        <div class="error">
                            <i:inline key=".invalidDate"/>
                        </div>
                    </c:if>
                </span>
                </tags:nameValue2>
            </c:if>

            <tags:nameValue2 nameKey=".notes">
                <cti:msg2 var="noteTextPlaceholder" key=".noteText.placeHolder"/>
                    <tags:textarea rows="3" cols="0" path="notes" isResizable="false" classes="tadw js-notes"
                                   placeholder="${fn:escapeXml(noteTextPlaceholder)}" maxLength="255" forceDisplayTextarea="true"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />

</cti:msgScope>
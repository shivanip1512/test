<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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
                ${fn:escapeXml(logger.loggerName)}
                 <input type="hidden" name="loggerName" value="${fn:escapeXml(logger.loggerName)}" maxlength="200"/>
            </c:when>
            <c:otherwise>
                <tags:input path="loggerName" size="60"/>
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
                    <c:set var="expiration" value="${not empty logger.expirationDate ? logger.expirationDate : now}"/>
                    <dt:date value="${expiration}" minDate="${now}" path="expirationDate" cssClass="${specifiedDateTimeError ? 'error' : ''}"/>
                    <c:if test="${not empty specifiedDateTimeError}">
                    <br/></br>
                        <div class="error">
                            <cti:msg2 var="expirationDate" key=".inValidDate"/>
                            <i:inline key="yukon.web.modules.dr.setup.error.required" arguments="${expirationDate}"/>
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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:flashScopeMessages/>

<cti:msgScope paths="modules.adminSetup.config.loggers,yukon.common">

    <cti:url var="saveUrl" value="/admin/config/loggers"/>
    <form:form id="logger-form" action="${saveUrl}" method="POST" modelAttribute="logger">

        <cti:csrfToken/>
        <tags:hidden path="loggerId"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".loggerName">
                <tags:input path="loggerName" inputClass="js-logger-name" size="60"/>
            </tags:nameValue2>
                
            <tags:nameValue2 nameKey=".loggerLevel">
                <tags:selectWithItems inputClass= "js-levels" path="level" items="${loggerLevels}"/>
            </tags:nameValue2>

            <c:if test="${allowDateTimeSelection}">
                <tags:nameValue2 nameKey=".expiration">
                    <tags:switchButton name="specifiedDateTime" toggleGroup="js-date-time" toggleAction="hide" color="false"
                       onNameKey="yukon.common.specified" offNameKey="yukon.common.now" checked="${specifiedDateTime}"/>
                    <c:set var="specifiedClass" value="${specifiedDateTime ? '' : 'dn'}"/>
                    <span data-toggle-group="js-date-time" class="${specifiedClass}">
                        <dt:dateTime path="timestamp"/>
                    </span>
                </tags:nameValue2>
            </c:if>
                
            <tags:nameValue2 nameKey=".notes">
                <cti:msg2 var="noteTextPlaceholder" key=".noteText.placeHolder"/>
                    <tags:textarea rows="3" cols="0" path="noteText" id="createNoteTextarea" isResizable="false" classes="tadw"
                                   placeholder="${noteTextPlaceholder}" maxLength="255" forceDisplayTextarea="true" inputClass="js-notes"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />

</cti:msgScope>
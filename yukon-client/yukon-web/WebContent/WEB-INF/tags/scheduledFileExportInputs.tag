<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="cronExpressionTagState" required="true" rtexprvalue="true" 
    type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" %>
<%@ attribute name="exportData" required="true" type="java.lang.Object" %>

<cti:includeScript link="/JavaScript/yukon.tag.scheduled.file.export.inputs.js"/>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.inputs">
    <cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>

    <tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>

    <tags:nameValue2 nameKey=".exportFileName">
        <c:if test="${exportData.scheduleName == exportData.exportFileName}">
             <c:set var="checked" value=" checked=\"true\""/>
         </c:if>
        <label class="db">
            <input id="sameAsSchedName" type="checkbox" ${checked}/><cti:msg2 key=".sameAsScheduleName"/>
        </label>
        <tags:input path="exportFileName" id="exportFileName" maxlength="100"/>
        <img src="${infoImg}" title="<cti:msg2 key=".exportFileNameInfo"/>"/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox path="appendDateToFileName" id="appendDateToFileName"/>
            <i:inline key=".appendDateToFileName"/>
        </label>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".timestampPatternField">
        <tags:input id="timestampPatternField" path="timestampPatternField" maxlength="20" />
        <img src="${infoImg}" title="<cti:msg2 key=".exportTimestampInfo"/>"/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox id="overrideFileExtension" path="overrideFileExtension"/>
            <i:inline key=".overrideFileExtension"/>
        </label>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".exportFileExtension">
        <tags:bind path="exportFileExtension">
            <form:select id="exportFileExtension" path="exportFileExtension" items="${fileExtensionChoices}" />
        </tags:bind>
        <img src="${infoImg}" title="<cti:msg2 key=".exportFileExtensionInfo"/>"/>
    </tags:nameValue2>


    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox id="includeExportCopy" path="includeExportCopy"/>
            <i:inline key=".includeExportCopy"/>
        </label>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".exportPath">
        <tags:bind path="exportPath">
            <form:select id="exportPath" path="exportPath" items="${exportPathChoices}" />
        </tags:bind>
        <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".scheduleCronString" nameClass="vat">
        <tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
    </tags:nameValue2>

    <tags:inputNameValue nameKey=".notificationEmailAddresses" path="notificationEmailAddresses"/>

</cti:msgScope>
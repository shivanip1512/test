<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="cronExpressionTagState" required="true"
        type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" %>
<%@ attribute name="exportData" required="true" type="java.lang.Object" %>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.inputs">

    <tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>

    <tags:nameValue2 nameKey=".exportFileName">
        <c:if test="${exportData.scheduleName == exportData.exportFileName}">
             <c:set var="checked" value=" checked=\"true\""/>
         </c:if>
        <label class="db">
            <input id="sameAsSchedName" type="checkbox" ${checked}/><cti:msg2 key=".sameAsScheduleName"/>
        </label>
        <tags:input path="exportFileName" id="exportFileName" maxlength="100"/>
        <div id="export-file-name-info" class="dn" data-title="<cti:msg2 key=".exportFileName"/>" data-width="500">
            <i:inline key=".exportFileNameInfo"/>
        </div>
        <cti:icon icon="icon-information" classes="fn cp" data-popup="#export-file-name-info" data-popup-toggle=""/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox path="appendDateToFileName" id="appendDateToFileName"/>
            <i:inline key=".appendDateToFileName"/>
        </label>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".timestampPatternField">
        <tags:input id="timestampPatternField" path="timestampPatternField" maxlength="20" />
        <div id="export-timestamp-info" class="dn" data-title="<cti:msg2 key=".appendDateToFileName"/>" data-width="500">
            <i:inline key=".exportTimestampInfo"/>
        </div>
        <cti:icon icon="icon-information" classes="fn cp" data-popup="#export-timestamp-info" data-popup-toggle=""/>
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
        <div id="export-file-extension-info" class="dn" data-title="<cti:msg2 key=".exportFileExtension"/>" data-width="500">
            <i:inline key=".exportFileExtensionInfo"/>
        </div>
        <cti:icon icon="icon-information" classes="fn cp" data-popup="#export-file-extension-info" data-popup-toggle=""/>
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
        <div id="export-path-info" class="dn" data-title="<cti:msg2 key=".exportPath"/>" data-width="500">
            <i:inline key=".exportPathInfo"/>
        </div>
        <cti:icon icon="icon-information" classes="fn cp" data-popup="#export-path-info" data-popup-toggle=""/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".scheduleCronString" nameClass="vat">
        <tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
    </tags:nameValue2>

    <c:if test="${!isSmtpConfigured}">    
        <tags:nameValue2 nameKey="yukon.common.email.address">
            <tags:switchButton name="sendEmail" toggleGroup="email-address" offClasses="M0"/>
            <tags:input id="emailNotificationAddress" path="notificationEmailAddresses" disabled="true" toggleGroup="email-address" />
        </tags:nameValue2>
    </c:if>
</cti:msgScope>
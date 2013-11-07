<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="cronExpressionTagState" required="true" rtexprvalue="true"
    type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" %>
<%@ attribute name="exportData" required="true" type="java.lang.Object" %>

<script type="text/javascript">
jQuery(function() {
    
    function toggleField(checkBoxId, changeItemId) {
        if (jQuery(checkBoxId).is(":checked")) {
            jQuery(changeItemId).removeAttr("disabled").closest("tr").show(250);
        } else {
            jQuery(changeItemId).attr("disabled","disabled").closest("tr").hide(250);
        }
    };

    toggleTimestampPatternField = function() {
        toggleField("#appendDateToFileName", "#timestampPatternField");
    };

    toggleFileExtensionField = function() {
        toggleField("#overrideFileExtension", "#exportFileExtension");
    };

    toggleExportPathField = function() {
        toggleField("#includeExportCopy", "#exportPath");
    };

    var lastDisplayName = false;
    sameAsNameClicked = function() {
        if (jQuery("#sameAsSchedName").is(":checked")) {
            lastDisplayName = jQuery("#exportFileName").val();
            jQuery("#exportFileName").val(jQuery("#scheduleName").val());
            jQuery("#exportFileName").attr("disabled","disabled");
        } else {
            if (lastDisplayName) {
                jQuery("#exportFileName").val(lastDisplayName);
            }
            jQuery("#exportFileName").removeAttr("disabled");
        }
    };
    nameChanged = function() {
        if (jQuery("#sameAsSchedName").is(":checked")) {
            jQuery("#exportFileName").val(jQuery("#scheduleName").val());
        }
    };

    jQuery("#appendDateToFileName").click(toggleTimestampPatternField);
    jQuery("#overrideFileExtension").click(toggleFileExtensionField);
    jQuery("#includeExportCopy").click(toggleExportPathField);
    jQuery("#scheduleName").keyup(nameChanged);
    jQuery("#scheduleName").change(nameChanged);
    jQuery("#sameAsSchedName").click(sameAsNameClicked);

    toggleTimestampPatternField();
    toggleFileExtensionField();
    toggleExportPathField();
    sameAsNameClicked();
    
});
</script>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.inputs">
<cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>

	<tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>
	
    <tags:nameValue2 nameKey=".exportFileName">
        <c:if test="${exportData.scheduleName == exportData.exportFileName}">
            <c:set var="checked" value=" checked=\"true\""/>
        </c:if>
        <input id="sameAsSchedName" type="checkbox" ${checked}/>
        <label for="sameAsSchedName"><cti:msg2 key=".sameAsScheduleName"/></label>
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
        <tags:input path="timestampPatternField" maxlength="20" />
        <img src="${infoImg}" title="<cti:msg2 key=".exportTimestampInfo"/>"/>
    </tags:nameValue2>

    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox path="overrideFileExtension" id="overrideFileExtension"/>
            <i:inline key=".overrideFileExtension"/>
        </label>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".exportFileExtension">
        <tags:bind path="exportFileExtension">
            <form:select path="exportFileExtension" items="${fileExtensionChoices}" />
        </tags:bind>
        <img src="${infoImg}" title="<cti:msg2 key=".exportFileExtensionInfo"/>"/>
    </tags:nameValue2>


    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox path="includeExportCopy" id="includeExportCopy"/>
            <i:inline key=".includeExportCopy"/>
        </label>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".exportPath">
        <tags:bind  path="exportPath">
            <form:select path="exportPath" items="${exportPathChoices}" />
        </tags:bind>
        <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
    </tags:nameValue2>


	<tags:nameValue2 nameKey=".scheduleCronString" nameClass="vat">
		<tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
	</tags:nameValue2>

	<tags:inputNameValue nameKey=".notificationEmailAddresses" path="notificationEmailAddresses"/>

</cti:msgScope>
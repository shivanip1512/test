<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="cronExpressionTagState" required="true" rtexprvalue="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" %>

<script type="text/javascript">
jQuery(function() {
    
    toggleTimestampPatternField = function() {
        if (jQuery("#appendDateToFileName").is(":checked")) {
            jQuery("#timestampPatternField").removeAttr("disabled").closest("tr").show(250);
        } else {
            jQuery("#timestampPatternField").attr("disabled","disabled").closest("tr").hide(250);
        }
    }

    toggleFileExtensionField = function() {
        if (jQuery("#overrideFileExtension").is(":checked")) {
            jQuery("#exportFileExtension").removeAttr("disabled").closest("tr").show(250);
        } else {
            jQuery("#exportFileExtension").attr("disabled","disabled").closest("tr").hide(250);
        }
    }

    toggleExportPathField = function() {
        if (jQuery("#includeExportCopy").is(":checked")) {
            jQuery("#exportPath").removeAttr("disabled").closest("tr").show(250);
        } else {
            jQuery("#exportPath").attr("disabled","disabled").closest("tr").hide(250);
        }
    }
    
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
    }
    nameChanged = function() {
        if (jQuery("#sameAsSchedName").is(":checked")) {
            jQuery("#exportFileName").val(jQuery("#scheduleName").val());
        }
    }

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
    
})
</script>


<cti:msgScope paths="yukon.web.modules.amr.scheduledFileExport.inputs">
<cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>

	<tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>
	
    <tags:nameValue2 nameKey=".exportFileName">
        <tags:input path="exportFileName" id="exportFileName" maxlength="100"/>
        <img src="${infoImg}" title="<cti:msg2 key=".exportFileNameInfo"/>"/>
        <c:if test="${scheduleName == exportFileName}">
            <c:set var="checked" value=" checked=\"true\""/>
        </c:if>
        <input id="sameAsSchedName" type="checkbox" ${checked} }/>
        <label for="sameAsSchedName"><cti:msg2 key=".sameAsScheduleName"/></label>
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
        <form:select path="exportFileExtension" items="${fileExtensionChoices}" />
        <img src="${infoImg}" title="<cti:msg2 key=".exportFileExtensionInfo"/>"/>
    </tags:nameValue2>


    <tags:nameValue2 nameKey="yukon.web.defaults.blank" excludeColon="true">
        <label>
            <form:checkbox path="includeExportCopy" id="includeExportCopy"/>
            <i:inline key=".includeExportCopy"/>
        </label>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".exportPath">
        <form:select path="exportPath" items="${exportPathChoices}" />
        <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
    </tags:nameValue2>


	<tags:nameValue2 nameKey=".scheduleCronString">
		<tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
	</tags:nameValue2>

	<tags:inputNameValue nameKey=".notificationEmailAddresses" path="notificationEmailAddresses"/>

</cti:msgScope>
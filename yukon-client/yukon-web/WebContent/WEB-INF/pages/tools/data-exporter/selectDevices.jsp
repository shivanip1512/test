<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage page="bulk.archivedValueExporter.selectDevices" module="tools">
    <tags:sectionContainer2 nameKey="selectionContainer">
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
        <tags:deviceSelection action="selected" groupDataJson="${groupDataJson}" pickerType="dataExportPicker"/>
    </tags:sectionContainer2>

    <div id="copyForm" style="display:none">
        <input type="hidden" name="formatId" value="${archivedValuesExporter.formatId}"/>
        <input type="hidden" name="archivedValuesExportFormatType" value="${archivedValuesExporter.archivedValuesExportFormatType}"/>
        <c:forEach var="attribute" items="${archivedValuesExporter.attributes}">
                <input type="hidden" name="attributes" value="${attribute.key}"/>
        </c:forEach>
    </div>

<script>
    $(function() {
        $("form").each(function(index) {
            var formInputs = $("#copyForm").clone().html();
            $(this).append(formInputs);
        });
    });
</script>
</cti:standardPage>
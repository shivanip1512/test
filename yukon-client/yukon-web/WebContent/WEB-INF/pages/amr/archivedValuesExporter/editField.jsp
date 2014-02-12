<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<d:ajaxPage nameKey="${backingBean.pageNameKey}" module="tools" page="bulk.archivedValueExporter.${mode}" okEvent="editFieldOkPressed" id="fieldDialog">
    <%@ include file="editField.jspf" %>
</d:ajaxPage>

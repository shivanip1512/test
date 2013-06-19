<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<dialog:ajaxPage nameKey="${backingBean.pageNameKey}" module="tools" page="bulk.archivedValueExporter.${mode}" okEvent="editFieldOkPressed" id="fieldDialog">
    <%@ include file="editField.jspf" %>
</dialog:ajaxPage>

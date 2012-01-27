<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<dialog:ajaxPage nameKey="${backingBean.pageNameKey}" module="amr" page="archivedValueExporter.${mode}" okEvent="editFieldOkPressed" id="fieldDialog">
    <%@ include file="editField.jspf" %>
</dialog:ajaxPage>

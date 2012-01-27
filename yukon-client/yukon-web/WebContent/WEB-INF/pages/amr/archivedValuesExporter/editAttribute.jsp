<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<dialog:ajaxPage nameKey="${backingBean.pageNameKey}" module="amr" page="archivedValueExporter.${mode}" okEvent="editAttributeOkPressed" id="attributeDialog">
    <%@ include file="editAttribute.jspf" %>
</dialog:ajaxPage>

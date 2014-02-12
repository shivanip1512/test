<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<d:ajaxPage nameKey="${backingBean.pageNameKey}" module="tools" page="bulk.archivedValueExporter.${mode}" okEvent="editAttributeOkPressed" id="attributeDialog">
    <%@ include file="editAttribute.jspf" %>
</d:ajaxPage>

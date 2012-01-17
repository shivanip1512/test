<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<dialog:ajaxPage nameKey="${backingBean.pageNameKey}" module="amr" page="archivedValueExporter.${mode}" okEvent="editAttributeOkPressed" id="attributeDialog">
    <%@ include file="editAttribute.jspf" %>
</dialog:ajaxPage>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<cti:msgScope paths="modules.operator,modules.operator.inventory.controlAudit">
<dr:controlAuditResult result="${result}" type="${type}" auditId="${auditId}"/>
</cti:msgScope>
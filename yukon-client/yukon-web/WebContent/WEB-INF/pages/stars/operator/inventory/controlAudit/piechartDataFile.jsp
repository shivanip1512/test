<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<pie>
    <slice title="<cti:msg2 key="yukon.web.modules.operator.controlAudit.controlled"/>">${controlled}</slice>
    <slice title="<cti:msg2 key="yukon.web.modules.operator.controlAudit.uncontrolled"/>">${uncontrolled}</slice>
    <slice title="<cti:msg2 key="yukon.web.modules.operator.controlAudit.unknown"/>">${unknown}</slice>
    <slice title="<cti:msg2 key="yukon.web.modules.operator.controlAudit.unsupported"/>">${unsupported}</slice>
</pie>
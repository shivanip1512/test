<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.billing.timestamp">
    <p><i:inline key=".description"/></p>
    <p><i:inline key=".warning"/></p>
    <p><i:inline key=".default"/></p>
    <h3><i:inline key="yukon.common.formatting"/></h3>
    <tags:timestampFormat classes="full-width striped"/>
</cti:msgScope>
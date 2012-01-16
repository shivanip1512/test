<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">
    <tags:boxContainer2 nameKey="tests" styleClass="rfnTestContainer">
        <div class="rfnTests">
            <cti:button nameKey="meterReadArchiveRequest" href="viewMeterReadArchiveRequest" />
            <cti:button nameKey="eventArchiveRequest" href="viewEventArchiveRequest"/>
        </div>
    </tags:boxContainer2>
</cti:standardPage>
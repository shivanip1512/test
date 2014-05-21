<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest.viewDataSimulator">

<c:if test="${isRunning}">
    <p>The simulator is running.</p>
</c:if>

<form action="startDataSimulator" method="post">
<cti:csrfToken/>
<tags:sectionContainer2 nameKey="lcrDataSimulator">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6200">
            <input name="lcr6200serialFrom" type="text" value="${currentSettings.lcr6200serialFrom}">
            <i:inline key="yukon.common.to"/>
            <input name="lcr6200serialTo" type="text" value="${currentSettings.lcr6200serialTo}">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6600">
            <input name="lcr6600serialFrom" type="text" value="${currentSettings.lcr6600serialFrom}">
            <i:inline key="yukon.common.to"/>
            <input name="lcr6600serialTo" type="text" value="${currentSettings.lcr6600serialTo}">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageId">
            <input name="messageId" type="text" value="${currentSettings.messageId}">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageIdTimestamp">
            <input name="messageIdTimestamp" type="text" value="${currentSettings.messageIdTimestamp}">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div>
        <cti:button nameKey="start" type="submit"/>
        <cti:button nameKey="stop" href="stopDataSimulator"/>
    </div>
</tags:sectionContainer2>
</form>
</cti:standardPage>

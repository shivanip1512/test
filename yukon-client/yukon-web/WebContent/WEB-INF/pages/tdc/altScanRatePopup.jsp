<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.tools.tdc">
    <form:form id="altScanRateForm" commandName="backingBean" action="/tdc/altScanRateSend">
        <form:hidden path="deviceId" />
        <tags:nameValueContainer2>
        <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".altScan.duration">
                <form:select path="altScanRate">
                    <c:forEach var="altScanRate" items="${altScanRates}">
                        <form:option value="${altScanRate}">
                            <cti:msg2 key="${altScanRate}" />
                        </form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary f-altScanRate-send" />
            <cti:button nameKey="close" onclick="jQuery('#tdc-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>
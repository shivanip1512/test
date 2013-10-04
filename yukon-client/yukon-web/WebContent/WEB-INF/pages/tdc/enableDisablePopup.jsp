<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.tools.tdc">
    <form:form id="enableDisableForm" commandName="backingBean" action="/tdc/enableDisableSend">
        <form:hidden path="pointId" />
        <tags:nameValueContainer2>
           <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
           <tags:nameValue2 nameKey="yukon.common.point">${fn:escapeXml(pointName)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".enableDisable.pointStatus">
                <form:select path="pointEnabledStatus">
                    <c:forEach var="enableDisable" items="${enableDisable}">
                        <form:option value="${enableDisable}">
                            <cti:msg2 key="${enableDisable}" />
                        </form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".enableDisable.deviceStatus">
                <form:select path="deviceEnabledStatus">
                    <c:forEach var="enableDisable" items="${enableDisable}">
                        <form:option value="${enableDisable}">
                            <cti:msg2 key="${enableDisable}" />
                        </form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="actionArea">
            <cti:button nameKey="ok" classes="primary f-enableDisable-send" />
            <cti:button nameKey="close" onclick="jQuery('#tdc-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>

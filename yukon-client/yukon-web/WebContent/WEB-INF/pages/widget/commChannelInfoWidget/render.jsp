<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.commChannelInfoWidget">
    <cti:tabs>
        <cti:msg2 var="infoTab" key=".info" />
        <cti:tab title="${infoTab}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">${fn:escapeXml(commChannel.name)}</tags:nameValue2>
                <tags:nameValue2 nameKey=".type"><i:inline key="${commChannel.type}"/></tags:nameValue2>
                <tags:nameValue2 nameKey=".baudRate"><i:inline key="${commChannel.baudRate}"/></tags:nameValue2>
                <c:set var="cssClass" value="error" />
                <cti:msg2 var="commChannelStatus" key="yukon.common.disabled"/>
                <c:if test="${commChannel.enable}">
                    <c:set var="cssClass" value="success" />
                    <cti:msg2 var="commChannelStatus" key="yukon.common.enabled"/>
                </c:if>
                <tags:nameValue2 nameKey=".status" valueClass="${cssClass}">
                    ${commChannelStatus}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </cti:tab>
        <cti:msg2 var="configTab" key=".config" />
        <cti:tab title="${configTab}">
        </cti:tab>
    </cti:tabs>
</cti:msgScope>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.commChannelInfoWidget">
    <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
    <cti:tabs>
        <!-- Info Tab -->
        <cti:msg2 var="infoTab" key=".info" />
        <cti:tab title="${infoTab}">
            <c:choose>
                <c:when test="${not empty commChannel}">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">${fn:escapeXml(commChannel.name)}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".type"><i:inline key="${commChannel.type}"/></tags:nameValue2>
                        <c:if test="${isIpAddressSupported}">
                            <tags:nameValue2 nameKey=".ipAddress">${commChannel.ipAddress}</tags:nameValue2>
                        </c:if>
                        <c:if test="${isPortNumberSupported}">
                            <tags:nameValue2 nameKey=".portNumber">${commChannel.portNumber}</tags:nameValue2>
                        </c:if>
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
                </c:when>
                <c:otherwise>
                    <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                </c:otherwise>
            </c:choose>
        </cti:tab>
        <!-- Configuration Tab -->
        <cti:msg2 var="configTab" key=".config" />
        <cti:tab title="${configTab}">
            <c:choose>
                <c:when test="${not empty commChannel}">
                    <c:if test="${isAdditionalConfigSupported}">
                        <tags:sectionContainer2 nameKey="general">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".protocolWrap">
                                    ${commChannel.protocolWrap}
                                </tags:nameValue2>
                                <c:set var="cssClass" value="" />
                                <c:if test="${commChannel.carrierDetectWaitInMilliseconds == 0}">
                                    <c:set var="cssClass" value="error" />
                                </c:if>
                                <tags:nameValue2 nameKey=".carrierDetectWait" valueClass="${cssClass}">
                                    <c:choose>
                                        <c:when test="${commChannel.carrierDetectWaitInMilliseconds > 0}">
                                            ${commChannel.carrierDetectWaitInMilliseconds}
                                            <i:inline key="yukon.common.units.ms"/>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key="yukon.common.no"/>
                                        </c:otherwise>
                                    </c:choose>
                                </tags:nameValue2>
                                <c:if test="${isEncyptionSupported}">
                                    <c:set var="cssClass" value="" />
                                    <c:if test="${empty commChannel.keyInHex}">
                                        <c:set var="cssClass" value="error" />
                                    </c:if>
                                    <tags:nameValue2 nameKey=".encyptionKey" valueClass="${cssClass}">
                                        <c:choose>
                                            <c:when test="${not empty commChannel.keyInHex}">
                                                ${commChannel.keyInHex}
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key="yukon.common.no"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </tags:nameValue2>
                                </c:if>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                    </c:if>
                    <!--  Timing Section -->
                    <tags:sectionContainer2 nameKey="timing">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".preTxWait">
                                ${commChannel.timing.preTxWait}
                                <i:inline key="yukon.common.units.ms"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".rtsToTxWait">
                                ${commChannel.timing.rtsToTxWait}
                                <i:inline key="yukon.common.units.ms"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".postTxWait">
                                ${commChannel.timing.postTxWait}
                                <i:inline key="yukon.common.units.ms"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".receiveDataWait">
                                ${commChannel.timing.receiveDataWait}
                                <i:inline key="yukon.common.units.ms"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".additionalTimeOut">
                                ${commChannel.timing.extraTimeOut}
                                <i:inline key="yukon.common.units.sec"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    <!--  Shared Section -->
                    <c:if test="${isAdditionalConfigSupported}">
                        <tags:sectionContainer2 nameKey="shared">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".sharedPortType">
                                    <i:inline key="${commChannel.sharing.sharedPortType}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".socketNumber">
                                    ${commChannel.sharing.sharedSocketNumber}
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                </c:otherwise>
            </c:choose>
        </cti:tab>
    </cti:tabs>
</cti:msgScope>

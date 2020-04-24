<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.commChannelInfoWidget">
    <tags:setFormEditMode mode="${mode}" />
    <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
    <form:form modelAttribute="commChannel">
        <cti:csrfToken />
        <cti:tabs>
        <!-- Info Tab -->
            <cti:msg2 var="infoTab" key=".info" />
            <cti:tab title="${infoTab}">
                <c:choose>
                    <c:when test="${not empty commChannel}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="name" maxlength="60"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".type">
                                <i:inline key="${commChannel.type}"/>
                            </tags:nameValue2>
                            <c:if test="${isIpAddressSupported}">
                                <tags:nameValue2 nameKey=".ipAddress">
                                    <tags:input path="ipAddress"/>
                                </tags:nameValue2>
                            </c:if>
                            <c:if test="${isPortNumberSupported}">
                                <tags:nameValue2 nameKey=".portNumber">
                                    <tags:input path="portNumber"/>
                                </tags:nameValue2>
                            </c:if>
                            <tags:nameValue2 nameKey=".baudRate">
                                <tags:selectWithItems items="${baudRateList}" path="baudRate"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".status">
                                <tags:switchButton path="enable" offNameKey=".disabled.label" onNameKey=".enabled.label"/>
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
                <cti:msg2 var="milliseconds" key="yukon.common.units.ms"/>
                <c:choose>
                    <c:when test="${not empty commChannel}">
                        <c:if test="${isAdditionalConfigSupported}">
                            <tags:sectionContainer2 nameKey="general">
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".protocolWrap">
                                        <tags:radio path="protocolWrap" value="IDLC" classes="left yes ML0" key=".IDLC"/>
                                        <tags:radio path="protocolWrap" value="None" classes="right yes" key=".NONE"/>
                                    </tags:nameValue2>
                                    <c:set var="cssClass" value="" />
                                    <c:if test="${commChannel.carrierDetectWaitInMilliseconds == 0}">
                                        <c:set var="cssClass" value="error" />
                                    </c:if>
                                    <tags:nameValue2 nameKey=".carrierDetectWait" valueClass="${cssClass}">
                                        <!--  TODO - Change the UI Component -->
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
                                            <!--  TODO - Change the UI Component -->
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
                                    <tags:input path="timing.preTxWait" units="${milliseconds}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".rtsToTxWait">
                                    <tags:input path="timing.rtsToTxWait" units="${milliseconds}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".postTxWait">
                                    <tags:input path="timing.postTxWait" units="${milliseconds}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".receiveDataWait">
                                    <tags:input path="timing.receiveDataWait" units="${milliseconds}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".additionalTimeOut">
                                    <cti:msg2 var="seconds" key="yukon.common.units.sec"/>
                                    <tags:input path="timing.extraTimeOut" units="${milliseconds}"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <!--  Shared Section -->
                        <c:if test="${isAdditionalConfigSupported}">
                            <tags:sectionContainer2 nameKey="shared">
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".sharedPortType">
                                        <tags:radio path="sharing.sharedPortType" value="NONE" classes="left yes ML0" key=".NONE"/>
                                        <tags:radio path="sharing.sharedPortType" value="ACS" classes="middle yes" key=".ACS"/>
                                        <tags:radio path="sharing.sharedPortType" value="ILEX" classes="right yes" key=".ILEX"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".socketNumber">
                                        <tags:input path="sharing.sharedSocketNumber"/>
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
    </form:form>
</cti:msgScope>

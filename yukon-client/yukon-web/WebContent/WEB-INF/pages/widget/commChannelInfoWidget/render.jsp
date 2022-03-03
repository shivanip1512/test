<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,yukon.web.modules.operator.commChannelInfoWidget,yukon.web.modules.operator.commChannel.sharedPortType">
    <tags:setFormEditMode mode="${mode}" />
    <c:if test="${not empty errorMsg}"><tags:alertBox>${fn:escapeXml(errorMsg)}</tags:alertBox></c:if>
    <div class="js-global-error">
        <c:if test="${not empty uniqueErrorMsg}">
            <c:forEach var="globalErrorMsg" items="${uniqueErrorMsg}">
                <tags:alertBox>${fn:escapeXml(globalErrorMsg)}</tags:alertBox>
            </c:forEach>
        </c:if>
    </div>
    <form:form modelAttribute="commChannel" method="post" id="commChannel-info-form">
        <cti:csrfToken />
        <cti:msg2 var="milliseconds" key="yukon.common.units.ms"/>
        <cti:tabs>
        <!-- Info Tab -->
            <cti:msg2 var="infoTab" key=".info" />
            <cti:tab title="${infoTab}">
                <c:choose>
                    <c:when test="${not empty commChannel}">
                        <tags:hidden path="deviceId"/>
                        <tags:hidden path="deviceType"/>
                        <input type="hidden" name="commChannel" value="${commChannel.deviceType}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="deviceName" maxlength="60" inputClass="w300 wrbw dib"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".type">
                                <i:inline key="${commChannel.deviceType}"/>
                            </tags:nameValue2>
                            <%@ include file="configuration.jsp" %>
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
                                <tags:nameValueContainer2 tableClass="js-general-tbl">
                                    <tags:nameValue2 nameKey=".protocolWrap">
                                        <tags:radio path="protocolWrap" value="IDLC" classes="left yes ML0" key="yukon.web.modules.operator.commChannel.protocolWrap.IDLC"/>
                                        <tags:radio path="protocolWrap" value="None" classes="right yes" key="yukon.web.modules.operator.commChannel.protocolWrap.NONE"/>
                                    </tags:nameValue2>
                                    <cti:displayForPageEditModes modes="EDIT">
                                        <c:set var="carrierDetectWaitError">
                                            <form:errors path="carrierDetectWaitInMilliseconds"/>
                                        </c:set>
                                        <c:set var="carrierDetectWaitEnabled" value="${commChannel.carrierDetectWaitInMilliseconds > 0 || not empty carrierDetectWaitError}"/>
                                        <tags:nameValue2 nameKey=".carrierDetectWait" rowClass="js-carrier-detect-wait">
                                            <tags:switchButton name="carrierDetectWait" toggleGroup="carrierDetectWait" toggleAction="hide"
                                                               onNameKey=".yes.label" offNameKey=".no.label" checked="${carrierDetectWaitEnabled}"
                                                               classes="js-carrier-detect-wait-switch"/>
                                            <tags:input inputClass="js-carrierDetectWait" units="${milliseconds}" path="carrierDetectWaitInMilliseconds" toggleGroup="carrierDetectWait"/>
                                        </tags:nameValue2>
                                    </cti:displayForPageEditModes>
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <tags:nameValue2 nameKey=".carrierDetectWait">
                                            <c:choose>
                                                <c:when test="${commChannel.carrierDetectWaitInMilliseconds > 0}">
                                                    ${commChannel.carrierDetectWaitInMilliseconds}
                                                    <i:inline key="yukon.common.units.ms"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <span><i:inline key="yukon.common.no"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </tags:nameValue2>
                                    </cti:displayForPageEditModes>
                                    <c:if test="${isEncyptionSupported}">
                                        <cti:displayForPageEditModes modes="EDIT">
                                            <c:set var="encryptionKeyError">
                                                <form:errors path="keyInHex"/>
                                            </c:set>
                                            <c:set var="encryptionKeyEnabled" value="${not empty commChannel.keyInHex || not empty encryptionKeyError}"/>
                                            <tags:nameValue2 nameKey=".encryptionKey" rowClass="js-encryption-key">
                                                <tags:switchButton name="encryptionKey" toggleGroup="encryptionKey" toggleAction="hide"
                                                                   onNameKey=".yes.label" offNameKey=".no.label" checked="${encryptionKeyEnabled}"
                                                                   classes="js-encryption-key-switch"/>
                                                <tags:input inputClass="js-encryptionKey" path="keyInHex" toggleGroup="encryptionKey" maxlength="32"/>
                                            </tags:nameValue2>
                                        </cti:displayForPageEditModes>
                                        <cti:displayForPageEditModes modes="VIEW">
                                            <tags:nameValue2 nameKey=".encryptionKey">
                                                <c:choose>
                                                    <c:when test="${not empty commChannel.keyInHex}">
                                                        <span class="w300 wrbw dib">
                                                            ${commChannel.keyInHex}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span><i:inline key="yukon.common.no"/></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tags:nameValue2>
                                        </cti:displayForPageEditModes>
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
                                    <tags:input path="timing.extraTimeOut" units="${seconds}"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".postCommWait">
                                    <tags:input path="timing.postCommWait" units="${milliseconds}"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <!--  Shared Section -->
                        <c:if test="${isAdditionalConfigSupported}">
                            <tags:sectionContainer2 nameKey="shared">
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".sharedPortType" rowId="js-socket-type">
                                        <tags:radioButtonGroup items="${sharedPortTypes}" path="sharing.sharedPortType" viewModeKey="${commChannel.sharing.sharedPortType}" inputCssClass="js-socket-type-val"/>
                                    </tags:nameValue2>
                                    <input type="hidden" id="socketTypeNone" value="${sharedPortNone}">
                                    <c:set var="socketNumberCssClass" value=""/>
                                    <c:set var="socketNumberValueCssClass" value=""/>
                                    <cti:displayForPageEditModes modes="EDIT">
                                        <c:set var="socketNumberCssClass" value="js-socket-number"/>
                                        <c:set var="socketNumberValueCssClass" value="js-socket-number-val"/>
                                    </cti:displayForPageEditModes>
                                    <c:set var="cssClassForSocketType" value="${isSharedPortTypeNone ? 'dn' : ''}"/>
                                    <tags:nameValue2 nameKey=".socketNumber" rowClass="${cssClassForSocketType} ${socketNumberCssClass}">
                                       <tags:input path="sharing.sharedSocketNumber" id="${socketNumberValueCssClass}"/>
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
    <cti:displayForPageEditModes modes="VIEW">
        <div class="action-area">
             <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE"> 
                <cti:url var="editUrl" value="/widget/commChannelInfoWidget/${commChannel.deviceId}/edit"/>
                <cti:msg2 var="saveText" key="components.button.save.label"/>
                <cti:msg2 var="editPopupTitle" key="yukon.web.modules.operator.commChannelInfoWidget.edit" argument="${commChannel.deviceName}"/>
                <c:if test="${not empty commChannel}">
                    <cti:button icon="icon-pencil"
                                nameKey="edit"
                                data-popup="#js-edit-comm-channel-popup"
                                id="edit-btn"/>
                </c:if>
             </cti:checkRolesAndProperties> 
        </div>
        <div class="dn" 
             id="js-edit-comm-channel-popup" 
             data-title="${editPopupTitle}" 
             data-dialog
             data-load-event="yukon:assets:commChannel:load"
             data-ok-text="${saveText}" 
             data-width="555"
             data-height="450"
             data-event="yukon:assets:commChannel:save" 
             data-url="${editUrl}"/>
        </div>
    </cti:displayForPageEditModes>
    <cti:includeScript link="/resources/js/common/yukon.comm.channel.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.commChannel.info.js"/>
</cti:msgScope>

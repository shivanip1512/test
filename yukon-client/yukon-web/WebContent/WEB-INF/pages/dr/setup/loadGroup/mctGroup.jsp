<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:sectionContainer2 nameKey="address" styleClass="js-mct-load-group-container">
    <input type="hidden" class="js-addr-level-mct-addr-val" value="${mctAddressEnumVal}"/>
    <cti:uniqueIdentifier var="uniqueId"/>
    <input type="hidden" class="js-unique-value" value="${uniqueId}"/>
    <c:set var="displayMctPicker" value="${isMctAddressSelected ? '' : 'dn'}"/>
    <c:set var="displayAddressTxtField" value="${isMctAddressSelected ? 'dn' : ''}"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".addressLevel">
            <tags:selectWithItems items="${addressLevels}" path="level" inputClass="js-address-level" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".address" rowClass="js-address ${displayAddressTxtField}">
            <tags:input path="address" inputClass="js-address-txt"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".mctAddress" rowClass="js-mct-address ${displayMctPicker} spaced-form-controls">
            <c:choose>
                <c:when test="${not isViewMode}">
                    <div class="${mctAddressHasError ? 'MT5 MB5' : ''}">
                        <form:hidden id="js-mct-meter-selected-${uniqueId}" path="mctDeviceId" cssClass="js-mct-meter-id"/>
                        <tags:pickerDialog id="mctMeterPicker_${uniqueId}"
                                                         type="mctMeterPicker" 
                                                         linkType="selection"
                                                         selectionProperty="paoName"
                                                         destinationFieldId="js-mct-meter-selected-${uniqueId}"/>
                        <br>
                        <form:errors path="mctDeviceId" cssClass="error"/>
                     </div>
                </c:when>
                <c:otherwise>
                    <cti:paoDetailUrl newTab="true" paoId="${loadGroup.mctDeviceId}">
                        <cti:deviceName deviceId="${loadGroup.mctDeviceId}"/>
                    </cti:paoDetailUrl>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".relayUsage">
            <c:choose>
                <c:when test="${not isViewMode}">
                    <div class="button-group stacked relayUsage" style="margin-left: -5px;">
                        <c:forEach var="relayUsageValue" items="${relayUsageList}">
                            <tags:check path="relayUsage" value="${relayUsageValue}" key="${relayUsageValue}"/>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${fn:length(loadGroup.relayUsage) < 1}">
                            <i:inline key="yukon.common.none.choice"/>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="relayUsageValue" items="${loadGroup.relayUsage}" varStatus="status">
                                <c:if test="${!status.first}">
                                    <i:inline key="yukon.common.comma"/>&nbsp;
                                </c:if>
                                <i:inline key="${relayUsageValue}"/>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
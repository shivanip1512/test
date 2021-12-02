<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.tools.trend">
    <cti:uniqueIdentifier var="uniqueId"/>
    <input type="hidden" class="js-date-type-enum-value" value="${graphTypeDateEnumValue}"/>
    <c:forEach items="${colors}" var="color">
        <cti:msg var="colorName" key="${color.formatKey}"/>
        <input type="hidden" class="js-color-item" value="${color.hexValue}" data-color-name="${colorName}"/>
    </c:forEach>
    
    <cti:url var="addPointUrl" value="/tools/trend/addPointOrMarker"/>
    <form:form class="js-point-setup-form" modelAttribute="trendSeries" method="POST" action="${addPointUrl}">
        <cti:csrfToken/>
        <input type="hidden" class="js-unique-identifier" value="${uniqueId}"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.common.point" nameClass="vam" valueClass="fl MB5">
                <tags:pickerDialog id="trendPointPicker_${uniqueId}"
                                                 type="pointPicker"
                                                 destinationFieldId="js-point-id-${uniqueId}"
                                                 linkType="selection"
                                                 selectionProperty="pointName"
                                                 endEvent="yukon:trend:setup:pointSelection:complete"
                                                 allowEmptySelection="false"
                                                 initialId="${trendSeries.pointId}" />
                <tags:hidden path="pointId" id="js-point-id-${uniqueId}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.device" nameClass="vam">
                <span class="js-device-name-span dib mw300 wbba">
                    <c:choose>
                        <c:when test="${empty deviceName}">
                            <i:inline key="yukon.common.none" />
                        </c:when>
                        <c:otherwise>
                            ${fn:escapeXml(deviceName)}
                        </c:otherwise>
                    </c:choose>
                </span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".label" nameClass="vam">
                <tags:input path="label" maxlength="40" inputClass="js-point-label-input w300" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".color" nameClass="vam">
                <input type="hidden" class="js-color-input" name="color" value="${trendSeries.color.hexValue}"/>
                <input type="text" class="js-color-picker" value="${trendSeries.color.hexValue}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".style" nameClass="vam">
                <tags:selectWithItems items="${styles}" path="style" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.type" nameClass="vam">
                <tags:selectWithItems items="${graphTypes}" path="type" inputClass="js-graph-type"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.date" rowClass="js-date-picker-row ${isDateTypeSelected ? '' : 'dn' }" nameClass="vam">
                <div class="dib">
                    <dt:date path="date" value="${trendSeries.date}" maxDate="${now}" id="js-date-picker_${uniqueId}" />
                </div>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".axis" nameClass="vam">
                <tags:radioButtonGroup items="${axes}" path="axis"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".multiplier" nameClass="vam">
                <tags:input path="multiplier" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.tools.trend.setup.js" />
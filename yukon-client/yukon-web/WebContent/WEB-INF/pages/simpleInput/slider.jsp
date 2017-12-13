<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:includeScript link="/resources/js/pages/yukon.admin.slider.js"/>
<c:set var="clazz" value="10px;"/>
<c:if test="${not empty tasks}">
    <c:set var="clazz" value="7.5px;"/>
</c:if>
    <span class="js-time-label fwb"></span>
    <input id="val1" <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" size="30" name="${status.expression}" 
           value="${inputType.optionList[0].value}" class="sliderValue" data-index="0">

    <input id="val2" <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" size="30" name="${status.expression}" 
           value="${inputType.optionList[1].value}" class="sliderValue" data-index="1">
<br/>
<div class="slider-range" style="margin-top: 10px; margin-bottom: 10px;" data-hours="${inputType.hours}"></div>
<div class="steps-2 wsnw" style="font-size:${clazz}" id="ticks">
    <c:choose>
        <c:when test="${inputType.hours == 24}">
            <span><i:inline key="yukon.web.modules.adminSetup.config.slider.twelveAm"/></span>
            <span style="padding-left:17%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.eightAm"/></span>
            <span style="padding-left:25%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.fourPm"/></span>
            <span style="padding-left:22%"><i:inline key="yukon.web.modules.adminSetup.config.slider.elevenFiftyNinePm"/></span>
        </c:when>
        <c:otherwise>
            <span><i:inline key="yukon.web.modules.adminSetup.config.slider.twelveAm"/></span>
            <span style="padding-left:4%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.eightAm"/></span>
            <span style="padding-left:7%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.fourPm"/></span>
            <span style="padding-left:7%"><i:inline key="yukon.web.modules.adminSetup.config.slider.twelveAm"/></span>
            <span style="padding-left:9%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.eightAm"/></span>
            <span style="padding-left:8%;"><i:inline key="yukon.web.modules.adminSetup.config.slider.fourPm"/></span>
            <span style="padding-left:3%"><i:inline key="yukon.web.modules.adminSetup.config.slider.twelveAm"/></span>
        </c:otherwise>
    </c:choose>
</div>

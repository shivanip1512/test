<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<cti:includeScript link="/resources/js/pages/yukon.admin.slider.js"/>
    <span class="js-time-label fwb"></span>
    <input id="val1" <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" size="30" name="${status.expression}" 
           value="${inputType.optionList[0].value}" class="sliderValue" data-index="0">

    <input id="val2" <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" size="30" name="${status.expression}" 
           value="${inputType.optionList[1].value}" class="sliderValue" data-index="1">
<br/>
<div class="slider-range"></div>

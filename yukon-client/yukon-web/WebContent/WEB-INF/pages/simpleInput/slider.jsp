<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:set var="maxValue" value="${inputType.hours == 48 ? '2880' : '1440'}"/>
<tags:timeSlider startName="${status.expression}" startValue="${fn:escapeXml(inputType.optionList[0].value)}" rangeEnabled="true"
    endName="${status.expression}" endValue="${fn:escapeXml(inputType.optionList[1].value)}" showLabels="true" maxValue="${maxValue}"/>
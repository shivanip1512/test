<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:set var="maxValue" value="${inputType.hours == 48 ? '2880' : '1440'}"/>
<tags:timeSlider startName="${status.expression}" startValue="${inputType.optionList[0].value}" rangeEnabled="true"
    endName="${status.expression}" endValue="${inputType.optionList[1].value}" showLabels="true" maxValue="${maxValue}"/>
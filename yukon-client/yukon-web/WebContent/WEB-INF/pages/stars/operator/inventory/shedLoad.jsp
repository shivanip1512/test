<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.operator.hardware.shedLoad">

<form:form id="shed-load-form" method="post" commandName="shedLoad" onsubmit="return false;">
  <cti:csrfToken/>
  <form:hidden path="inventoryId" value="${inventoryId}"/>
  <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="true">
    <tags:nameValue2 nameKey=".duration">
        <c:choose>
            <c:when test="${isAllowDRControl}">
                <tags:intervalDropdown path="duration" intervals="${duration}" id="duration" /> 
            </c:when>
            <c:otherwise>
                <form:hidden path="duration" value="${interval.seconds}"/>
                <cti:formatDuration type="DHMS_REDUCED" value="${interval.duration}"/>
            </c:otherwise>
        </c:choose>
    </tags:nameValue2>
    <tags:selectNameValue nameKey=".relayNo" items="${relayNo}" path="relayNo" id="relayNo"/>
  </tags:nameValueContainer2>
</form:form>

</cti:msgScope>


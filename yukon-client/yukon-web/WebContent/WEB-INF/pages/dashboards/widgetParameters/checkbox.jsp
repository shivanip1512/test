<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:uniqueIdentifier var="id" />

<script>
$(document).on('change', '.js-checkbox', function() {
    var checkbox = $(this);
        checkboxValue = checkbox.prev();
        checkboxValue.val($(this).is(":checked"));
});

</script>
<div class="js-checkbox-div">
    <form:hidden path="${param.path}.parameters['${param.parameterName}']" cssClass="js-checkbox-value"/>
    <input type="checkbox" name="${id}" class="js-checkbox" style="cursor: default;" value="${fn:escapeXml(param.parameterValue)}" <c:if test="${param.parameterValue == 'true'}">checked="checked"</c:if>>
    <cti:msg2 key="${param.parameterKey}"/>
</div>

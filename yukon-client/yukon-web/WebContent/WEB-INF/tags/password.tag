<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ attribute name="path" required="true" %>
<%@ attribute name="disabled" type="java.lang.Boolean" %>
<%@ attribute name="readonly" type="java.lang.Boolean" description="If true, the field will look disabled but still submit the value." %>
<%@ attribute name="size" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="autocomplete" 
        description="HTML input autocomplete attribute. Possible values: 'on|off'. Default: 'off'." %>
<%@ attribute name="cssClass" %>
<%@ attribute name="includeShowHideButton" type="java.lang.Boolean" %>
<%@ attribute name="includeClearButton" type="java.lang.Boolean" description="If true, display a clear button next to the field to clear the value"%>
<%@ attribute name="maskValue" type="java.lang.Boolean" description="If true, mask the password value so it is not visible in the DOM."%>
<%@ attribute name="showPassword" type="java.lang.Boolean" %>
<%@ attribute name="tabindex" %>
<%@ attribute name="placeholder" %>


<cti:default var="autocomplete" value="off"/>

<script type="text/javascript">
    function showHideData (isSelected, path) {
        var sensitiveField = document.getElementsByName(path)[0];
        sensitiveField.type = isSelected ? 'text' : 'password';
    }
    
    $(document).on('click', '.js-clear-data', function (ev) {
    	var path = $(this).data('fieldPath'),
    		input = $('input[name=' + $.escapeSelector(path) + ']');
    	input.val("");
    	input.prop('readonly', false);
    	input.prop('disabled', false);
    });
    
    $(document).ready(function() {
	    <c:if test="${maskValue}">
			var sensitiveField = $('input[name=' + $.escapeSelector('${path}') + ']');
	    	if (sensitiveField.val() != '') {
	    		var maskedValue = sensitiveField.val().replace(/./g, '*');
	    		sensitiveField.attr("value", maskedValue);
	    	}
	    </c:if>
    });
</script>

<spring:bind path="${path}">

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
    <c:set var="inputClass" value="error"/>
</c:if>

<div class="dib M0">
    <form:password path="${path}" disabled="${pageScope.disabled}" readonly="${pageScope.readonly}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" 
        autocomplete="${autocomplete}" cssClass="${inputClass} ${pageScope.cssClass}" showPassword="${showPassword}" tabindex="${tabindex}" placeholder="${placeholder}"/>
    <c:if test="${includeShowHideButton}">
         <tags:check classes="fr M0" onclick="showHideData(this.checked, '${path}');" name="showHideDataField">
             <i title="<i:inline key="yukon.web.modules.adminSetup.config.showHideData"/>" class="icon icon-eye"></i>
         </tags:check>
    </c:if>
    <c:if test="${includeClearButton}">
    	<cti:msg2 var="clearButtonText" key="yukon.web.modules.adminSetup.config.clearData"/>
    	<cti:button renderMode="buttonImage" icon="icon-cancel" classes="fr M0 js-clear-data" title="${clearButtonText}" data-field-path="${path}"/>
    </c:if>
</div>
<c:if test="${status.error}"><br><form:errors path="${path}" cssClass="error"/><br/></c:if>

</cti:displayForPageEditModes>

</spring:bind>
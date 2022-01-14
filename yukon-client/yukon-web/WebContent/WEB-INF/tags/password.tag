<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ attribute name="path" required="true" %>
<%@ attribute name="disabled" type="java.lang.Boolean" %>
<%@ attribute name="size" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="autocomplete" 
        description="HTML input autocomplete attribute. Possible values: 'on|off'. Default: 'off'." %>
<%@ attribute name="cssClass" %>
<%@ attribute name="includeShowHideButton" type="java.lang.Boolean" %>
<%@ attribute name="showPassword" type="java.lang.Boolean" %>
<%@ attribute name="tabindex" %>
<%@ attribute name="placeholder" %>


<cti:default var="autocomplete" value="off"/>

<script type="text/javascript">
    function showHideData (isSelected, path) {
        var sensitiveField = document.getElementsByName(path)[0];
        sensitiveField.type = isSelected ? 'text' : 'password';
    }
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
    <form:password path="${path}" disabled="${pageScope.disabled}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" 
        autocomplete="${autocomplete}" cssClass="${inputClass} ${pageScope.cssClass}" showPassword="${showPassword}" tabindex="${tabindex}" placeholder="${placeholder}"/>
			<c:if test="${includeShowHideButton}">
				<cti:msg2 var="showHideDataMsg" key="yukon.web.modules.adminSetup.config.showHideData" />
				<tags:check classes="fr M0" onclick="showHideData(this.checked, '${path}');" name="showHideDataField">
             	<cti:icon icon="icon-eye" title="${showHideDataMsg}"/>
         </tags:check>
    </c:if>

		</div>
<c:if test="${status.error}"><br><form:errors path="${path}" cssClass="error"/><br/></c:if>

</cti:displayForPageEditModes>

</spring:bind>
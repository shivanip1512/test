<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="value" required="true" description="The filter value" %>
<%@ attribute name="labelKey" description="The label text i18n key" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="isClearable" type="java.lang.Boolean" description="Whether or not this filter value is removable (showing a clear icon after the value). Default is true" %>
<%@ attribute name="clearClass" description="The class(s) to be added to the clear icon" %>
<%@ attribute name="clearHref" description="The href to be used on the clear icon anchor" %>
<%@ attribute name="isReset" type="java.lang.Boolean" description="Whether or not this filter value is the reset element. There should only ever be one of these in a set of filteredBy tags. Default is false." %>

<script>
jQuery(function() {
    jQuery(".filter_container .icon_small_clear").bind("click", function(e) {
        var clear_icon = jQuery(e.target);
        var clear_href = clear_icon.attr("data-href");
        if (typeof(clear_href) !== 'undefined' && clear_href !== "") {
            window.location.href = clear_href;
        } else {
            clear_icon.parent("a").fadeOut();
        }
    });
});
</script>

<c:choose>
    <c:when test="${filteredByContainer}">
		<a href="javascript:void(0);" class="${pageScope.cssClass} <c:if test="${pageScope.isReset}"> reset</c:if>">
			<span class="label"><i:inline key="${pageScope.labelKey}"/></span>
			<span class="value">${pageScope.value}</span>
			<c:if test="${empty pageScope.isClearable || pageScope.isClearable == 'true'}">
		        <i data-href="${pageScope.clearHref}" class="icon_small_clear ${pageScope.clearClass}" title="<cti:msg2 key="yukon.web.defaults.remove"/>"></i>
			</c:if>
		</a>
	</c:when>
    <c:otherwise>
        <div class="errorRed">ERROR: The &lt;filteredBy&gt; tag must be enclosed in a &lt;filteredByContainer&gt; tag</div>
    </c:otherwise>
</c:choose>
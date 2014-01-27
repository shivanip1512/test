<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="value" required="true" description="The filter value" %>
<%@ attribute name="labelKey" description="The label text i18n key" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="clearClass" description="The class(s) to be added to the clear button." %>
<%@ attribute name="isClearable" type="java.lang.Boolean" description="Whether or not this filter value is removable (showing a clear icon after the value). Default is true" %>
<%@ attribute name="isReset" type="java.lang.Boolean" description="Whether or not this filter value is the reset element. There should only ever be one of these in a set of filteredBy tags. Default is false." %>

<c:if test="${empty pageScope.isClearable || pageScope.isClearable == 'true'}">
    <c:set var="showClear" value="${true}"/>
</c:if>

<c:choose>
    <c:when test="${filteredByContainer}">
        <div class="dib wsnw">
		<button class="button ${pageScope.cssClass}<c:if test="${pageScope.isReset}"> reset</c:if><c:if test="${pageScope.showClear}"> left</c:if>" style="margin-left:0;">
			<span class="b-label" style="font-weight: normal;"><i:inline key="${pageScope.labelKey}"/></span>
			<span class="b-label">${pageScope.value}</span>
		</button>
		<c:if test="${pageScope.showClear}">
            <button class="f-clear-filter-option right ${pageScope.clearClass}">
                <i class="icon icon-cross"></i>
            </button>
        </c:if>
        </div>
	</c:when>
    <c:otherwise>
        <div class="error">ERROR: The &lt;filteredBy&gt; tag must be enclosed in a &lt;filteredByContainer&gt; tag</div>
    </c:otherwise>
</c:choose>
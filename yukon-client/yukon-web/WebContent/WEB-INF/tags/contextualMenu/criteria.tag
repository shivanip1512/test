<%@ tag trimDirectiveWhitespaces="true" description="Use this tag to create a button whose menu has criteria options...see criteriaOpiton.tag." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="containerCssClass" description="Class names applied to the outer container element." %>
<%@ attribute name="id" %>
<%@ attribute name="key" description="Required when 'label' attribute is not used." %>
<%@ attribute name="label" description="Text representing the type of criteria, ie: 'Device Type:'" %>
<%@ attribute name="value" description="Text representing the criteria currently chosen, ie: 'RFN-420fL, RFN-420fD'" %>
<%@ attribute name="menuCssClass" description="Class names applied to the menu container element." %>

<cti:msg2 key="yukon.web.defaults.all" var="allText"/>
<cti:msg2 key="yukon.web.defaults.none" var="noneText"/>

<div class="f-dropdown_outer_container fl ${pageScope.containerCssClass}" <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <div class="dropdown-container usn fl">
        <button type="button" class="criteria-button" data-all-text="${allText}" data-none-text="${noneText}">
            <div class="criteria-wrap">
                <c:choose>
                    <c:when test="${not empty pageScope.label}">
                        <span class="criteria-label">${fn:escapeXml(pageScope.label)}</span>
                    </c:when>
                    <c:otherwise><span class="criteria-label"><cti:msg2 key="${pageScope.key}"/>:&nbsp;</span></c:otherwise>
                </c:choose>
                <span class="criteria-value">${fn:escapeXml(pageScope.value)}</span>
            </div>
            <i class="icon icon-bullet-arrow-down">&nbsp;</i>
        </button>
        <ul class="dropdown-menu criteria-menu dn ${pageScope.menuCssClass}"><jsp:doBody/></ul>
    </div>
</div>
<%@ tag trimDirectiveWhitespaces="true" description="Use this tag to create a button whose menu has criteria options...see criteriaOpiton.tag." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="triggerClasses" description="Class names applied to the outer container element." %>
<%@ attribute name="id" %>
<%@ attribute name="key" description="Required when 'label' attribute is not used." %>
<%@ attribute name="label" description="Text representing the type of criteria, ie: 'Device Type:'" %>
<%@ attribute name="value" description="Text representing the criteria currently chosen, ie: 'RFN-420fL, RFN-420fD'" %>
<%@ attribute name="menuClasses" description="Class names applied to the menu container element." %>
<%@ attribute name="labelWidth" description="Max width to apply to the label." %>

<cti:msg2 key="yukon.common.all" var="allText"/>
<cti:msg2 key="yukon.common.none.choice" var="noneText"/>

<div class="dropdown-trigger usn ${pageScope.triggerClasses}" <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <button type="button" class="criteria-button" data-all-text="${allText}" data-none-text="${noneText}">
        <div class="criteria-wrap" style="max-width:${pageScope.labelWidth}">
            <span class="criteria-label">
                <c:choose>
                    <c:when test="${not empty pageScope.label}">${fn:escapeXml(pageScope.label)}:&nbsp;</c:when>
                    <c:otherwise><cti:msg2 key="${pageScope.key}"/>:&nbsp;</c:otherwise>
                </c:choose>
            </span>
            <span class="criteria-value">${fn:escapeXml(pageScope.value)}</span>
        </div>
        <i class="icon icon-bullet-arrow-down">&nbsp;</i>
    </button>
    <ul class="dropdown-menu criteria-menu dn ${pageScope.menuClasses}"><jsp:doBody/></ul>
</div>

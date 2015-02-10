<%@ tag dynamic-attributes="attrs" description="Creates a dropdown menu option inside a dropdown.tag element." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="classes" description="Class attribute applied to the outer 'li' element." %>
<%@ attribute name="icon" description="Icon class name.  Defaut: icon-blank" %>
<%@ attribute name="id" description="Id attribute applied to the 'li' element." %>
<%@ attribute name="disabled" %>
<%@ attribute name="href" description="Href attribute applied to the 'a' element." %>
<%@ attribute name="key" description="Required when 'label' or 'displayable' attributes are not used." %>
<%@ attribute name="label" description="Text for the option. Text is html escaped internally." %>
<%@ attribute name="newTab" type="java.lang.Boolean" description="If true, the link will open in a new browser tab. Default: false." %>

<cti:default var="icon" value="icon-blank"/>
<cti:default var="newTab" value="${false}"/>
<c:set var="classes" value="dropdown-option ${pageScope.classes}"/>
<c:set var="labelClasses" value="dropdown-option-label"/>
<c:set var="disabled" value="${not empty pageScope.disabled && disabled == 'true' ? 'disabled' : ''}"/>

<li class="${classes}" <c:if test="${not empty pageScope.id}">id="${id}"</c:if> <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    <a <c:if test="${not empty pageScope.href}">href="${href}"</c:if> <c:if test="${newTab}">target="_blank"</c:if> class="clearfix">
        <c:choose>
            <c:when test="${not empty pageScope.key}">
                <cti:icon icon="${icon}" classes="${disabled}"/>
                <span class="${labelClasses} ${disabled}"><cti:msg2 key="${key}"/></span>
            </c:when>
            <c:when test="${not empty pageScope.label}">
                <cti:icon icon="${icon}" classes="${disabled}"/>
                <span class="${labelClasses} ${disabled}">${fn:escapeXml(label)}</span>
            </c:when>
            <c:otherwise>
                <cti:icon icon="${icon}" classes="${disabled}"/>
                <span class="${labelClasses} ${disabled}"><jsp:doBody/></span>
            </c:otherwise>
        </c:choose>
    </a>
</li>
<%@ tag trimDirectiveWhitespaces="true" description="Use this tag to create a criteria menu option inside a criteria.tag element." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="classes" description="Class attribute applied to the 'a' element." %>
<%@ attribute name="id" description="Id attribute applied to the 'li' element." %>
<%@ attribute name="href" description="Href attribute applied to the 'a' element." %>
<%@ attribute name="key" description="An i18n key code or a formattable object. (see ObjectFormattingServiceImpl#formatObjectAsResolvable)." %>
<%@ attribute name="label" description="Text for the criteria name. Text will be html escaped internally." %>
<%@ attribute name="checked" description="When 'true', the checkbox will be checked." type="java.lang.Boolean" %>
<%@ attribute name="value" description="The value attribute of the checkbox input." %>

<c:if test="${pageScope.checked}"><c:set var="checkedAttr">checked="checked"</c:set></c:if>
<c:if test="${not empty pageScope.href}"><c:set var="hrefAttr">href="${href}"</c:set></c:if>
<c:if test="${not empty pageScope.classes}"><c:set var="hrefAttr">class="${classes}"</c:set></c:if>

<li <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <a ${classesAttr} ${hrefAttr}>
        <label class="criteria-option"><input type="checkbox" class="fl" value="${value}" ${checkedAttr}>
            <c:choose>
                <c:when test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:when>
                <c:when test="${not empty pageScope.label}">${fn:escapeXml(label)}</c:when>
                <c:otherwise><jsp:doBody/></c:otherwise>
            </c:choose>
        </label>
    </a>
</li>
<%@ tag trimDirectiveWhitespaces="true" description="Use this tag to create a criteria menu option inside a criteria.tag element." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="classes" description="Class attribute applied to the 'a' element." %>
<%@ attribute name="displayable" description="Object that implements Displayable or DisplayableEnum. Used in place of 'value' and 'label' attributes." %>
<%@ attribute name="id" description="Id attribute applied to the 'li' element." %>
<%@ attribute name="href" description="Href attribute applied to the 'a' element." %>
<%@ attribute name="key" description="Required when 'label' or displayable attributes are not used." %>
<%@ attribute name="label" description="Text for the criteria name, ie'RFN-420 FL'" %>
<%@ attribute name="checked" description="When 'true', the checkbox will be checked." %>
<%@ attribute name="value" description="Text criteria value. Used as value attribute of checkbox input." %>

<li <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <a <c:if test="${not empty pageScope.href}">href="${href}"</c:if> <c:if test="${not empty pageScope.classes}">class="${classes}"</c:if>>
        <c:choose>
            <c:when test="${pageScope.displayable}">
                <label class="criteria-option"><input type="checkbox" class="fl" value="${displayable}" <c:if test="${checked}">checked="checked"</c:if>><cti:formatObject value="${displayable}"/></label>
            </c:when>
            <c:when test="${pageScope.key}">
                <label class="criteria-option"><input type="checkbox" class="fl" value="${value}" <c:if test="${checked}">checked="checked"</c:if>><cti:msg2 key="${key}"/></label>
            </c:when>
            <c:when test="${pageScope.label}">
                <label class="criteria-option"><input type="checkbox" class="fl" value="${value}" <c:if test="${checked}">checked="checked"</c:if>>${fn:escapeXml(label)}</label>
            </c:when>
            <c:otherwise>
                <label class="criteria-option"><input type="checkbox" class="fl" value="${value}" <c:if test="${checked}">checked="checked"</c:if>><jsp:doBody/></label>
            </c:otherwise>
        </c:choose>
    </a>
</li>
<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="attrs" description="Use this tag to create a dropdown menu option inside a dropdown.tag element." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="classes" description="Class attribute applied to the 'a' element. Classes will already have 'clearfix'." %>
<%@ attribute name="displayable" description="Object that implements Displayable or DisplayableEnum. Used in place of 'label' and 'key' attributes." %>
<%@ attribute name="icon" description="Icon class name.  Defaut: icon-blank" %>
<%@ attribute name="id" description="Id attribute applied to the 'li' element." %>
<%@ attribute name="disabled" %>
<%@ attribute name="href" description="Href attribute applied to the 'a' element." %>
<%@ attribute name="key" description="Required when 'label' or 'displayable' attributes are not used." %>
<%@ attribute name="label" description="Text for the criteria name, ie'RFN-420 FL'" %>

<cti:default var="icon" value="icon-blank"/>
<c:set var="classes" value="clearfix ${pageScope.classes}"/>

<li <c:if test="${not empty pageScope.id}">id="${id}"</c:if> <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    <a <c:if test="${not empty pageScope.href}">href="${href}"</c:if> class="${classes}">
        <c:choose>
            <c:when test="${not empty pageScope.displayable}">
                <c:choose>
                    <c:when test="${not empty pageScope.disabled && disabled == 'true'}">
                        <cti:icon icon="${icon}" classes="disabled"/>
                    </c:when>
                    <c:otherwise>
                        <cti:icon icon="${icon}"/>
                    </c:otherwise>
                </c:choose>
                <span class="fl dib <c:if test="${not empty pageScope.disabled && disabled == 'true'}">disabled</c:if>">
                    <cti:formatObject value="${displayable}"/>
                </span>
            </c:when>
            <c:when test="${not empty pageScope.key}">
                <c:choose>
                    <c:when test="${not empty pageScope.disabled && disabled == 'true'}">
                        <cti:icon icon="${icon}" classes="disabled"/>
                    </c:when>
                    <c:otherwise>
                        <cti:icon icon="${icon}"/>
                    </c:otherwise>
                </c:choose>
                <span class="fl dib <c:if test="${not empty pageScope.disabled && disabled == 'true'}">disabled</c:if>">
                    <cti:msg2 key="${key}"/>
                </span>
            </c:when>
            <c:when test="${not empty pageScope.label}">
                <c:choose>
                    <c:when test="${not empty pageScope.disabled && disabled == 'true'}">
                        <cti:icon icon="${icon}" classes="disabled"/>
                    </c:when>
                    <c:otherwise>
                        <cti:icon icon="${icon}"/>
                    </c:otherwise>
                </c:choose>
                <span class="fl dib <c:if test="${not empty pageScope.disabled && disabled == 'true'}">disabled</c:if>">${fn:escapeXml(label)}</span>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${not empty pageScope.disabled && disabled == 'true'}">
                        <cti:icon icon="${icon}" classes="disabled"/>
                    </c:when>
                    <c:otherwise>
                        <cti:icon icon="${icon}"/>
                    </c:otherwise>
                </c:choose>
                <span class="fl dib <c:if test="${not empty pageScope.disabled && disabled == 'true'}">disabled</c:if>"><jsp:doBody/></span>
            </c:otherwise>
        </c:choose>
    </a>
</li>
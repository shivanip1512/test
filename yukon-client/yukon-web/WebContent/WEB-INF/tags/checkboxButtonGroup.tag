<%@ tag description="A component that looks like a toggle group button and drives an html checkbox input." trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="buttonGroupContainerCssClasses" type="java.lang.String" description="The css class to be applied to the button group container div." %>
<%@ attribute name="items" type="java.util.Collection" description="Items to be rendered as button group." required="true"%>
<%@ attribute name="keyPrefix" type="java.lang.String" description="String to be prepended to from the complete the i18n key." %>
<%@ attribute name="keySuffix" type="java.lang.String" description="String to be prepended to from the complete the i18n key." %>
<%@ attribute name="path" description="Spring path." %> 

<c:set var="placeHolder" value="{0}"/>

<c:choose>
    <c:when test="${not empty keyPrefix && not empty keySuffix}">
        <c:set var="key" value="${keyPrefix}${placeHolder}${keySuffix}"/>
    </c:when>
    <c:when test="${empty keyPrefix && not empty keySuffix}">
        <c:set var="key" value="${placeHolder}${keySuffix}"/>
    </c:when>
    <c:when test="${not empty keyPrefix && empty keySuffix}">
        <c:set var="key" value="${keyPrefix}${placeHolder}"/>
    </c:when>
    <c:otherwise>
        <c:set var="key" value=".${placeHolder}"/>
    </c:otherwise>
</c:choose>
    
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <div class="button-group stacked ${buttonGroupContainerCssClasses}">
        <c:forEach var="item" items="${items}">
            <tags:check id="${item}_chk" path="${path}" value="${item}" key="${fn:replace(key, placeHolder, item)}"/>
        </c:forEach>
    </div>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="VIEW">
    <c:choose>
        <c:when test="${empty items}">
            <i:inline key="yukon.common.none"/>
        </c:when>
        <c:otherwise>
            <c:forEach var="item" items="${items}" varStatus="status">
                <c:if test="${!status.first}">
                    <i:inline key="yukon.common.comma"/>&nbsp;
                </c:if>
                <i:inline key="${fn:replace(key, placeHolder, item)}"/>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</cti:displayForPageEditModes>
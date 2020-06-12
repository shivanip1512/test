<%@ tag description="A component that looks like a toggle group button." trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="inputCssClass" description="The css class to be applied to the button group container div." %>
<%@ attribute name="items" type="java.util.Collection" description="Items to be rendered as button group." required="true" %>
<%@ attribute name="path" description="Spring path." %>
<%@ attribute name="viewModeKey" type="java.lang.String" description="The key for view mode." %>

<cti:displayForPageEditModes modes="CREATE,EDIT">
    <c:forEach var="item" items="${items}" varStatus="status">
        <c:choose>
            <c:when test="${status.index == 0}">
                <c:set var="css" value="left yes ML0"/>
            </c:when>
            <c:when test="${status.index == fn:length(units)-1}">
                <c:set var="css" value="right yes"/>
            </c:when>
            <c:otherwise>
                <c:set var="css" value="middle yes"/>
            </c:otherwise>
        </c:choose>
        <tags:radio path="${path}" value="${item}" classes="${css}" key="${item}" inputClass="${inputCssClass}"/>
    </c:forEach>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="VIEW">
    <i:inline key=".${viewModeKey}"/>
</cti:displayForPageEditModes>
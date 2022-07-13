<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="statusBackgroundColorClass" type="java.lang.String" description="The class to use that specifies the background color for the Status Stripe." %>
<%@ attribute name="dateTime" type="java.lang.Object" description="The Date/Time to display." %>
<%@ attribute name="title" type="java.lang.String" description="The title to display." %>
<%@ attribute name="subTitle" type="java.lang.String" description="The subtitle to display." %>
<%@ attribute name="tagText" type="java.lang.String" description="The text to display in the tag." %>
<%@ attribute name="tagColorClass" type="java.lang.String" description="The class to use that specifies the background color for the tag." %>
<%@ attribute name="icon" type="java.lang.String" description="The css class name of the icon to use. Example: icon-resultset-next-gray" %>
<%@ attribute name="iconLink" type="java.lang.String" description="The link to go to when the user clicks on the icon." %>
<%@ attribute name="iconTooltip" type="java.lang.String" description="The text to display in the tooltip on hover of the icon." %>

<c:set var="minHeight" value="40px"/>
<c:if test="${title != null && subTitle != null}">
    <c:set var="minHeight" value="55px"/>
</c:if>

<div class="info-list-item" style="min-height:${minHeight}">
    <span class="status-stripe ${statusBackgroundColorClass}">&nbsp;</span>
    <c:if test="${dateTime != null}">
        <span class="MR10 notes">
            <b><cti:formatDate value="${dateTime}" type="TIME"/></b><br/>
            <cti:formatDate value="${dateTime}" type="DATE"/>
        </span>
    </c:if>
    <span>
        <div><b>${title}</b></div>
        <div style="font-size:13px">${subTitle}</div>
    </span>
    <span class="ma fr dif MR0">
        <span class="ML10">
            <tags:infoListTag text="${tagText}" backgroundColor="${tagColorClass}"/>
        </span>
        <c:if test="${icon != null}">
            <span class="ML5"><cti:icon icon="${icon}" href="${iconLink}" title="${iconTooltip}"/></span>
        </c:if>
    </span>


</div>

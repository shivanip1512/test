<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="styleClass"%>
<%@ attribute name="moreAttributes"%>

<%@ attribute name="sortParam" description="name of paramater for sort-by. Defaults to 'sort'"%>
<%@ attribute name="descendingParam" description="name of parameter to hold descending boolean. Defaults to 'descending'."%>

<%@ attribute name="isDefault" type="java.lang.Boolean" %>
<%@ attribute name="descendingByDefault" type="java.lang.Boolean" %>
<%@ attribute name="overrideParams" type="java.lang.Boolean" description="Ignores params from the previous request. Set to true if they are specified in the baseUrl"%>

<%--
If the default sort field is anything other than "NAME", set the isDefault
attribute to true on the field which is the default sort field.
--%>

<cti:msgScope paths=".${nameKey}">
    <cti:msg2 var="linkTextMsg" key=".linkText"/>
</cti:msgScope>
<cti:msgScope paths=".${nameKey},components.sortLink">
    <cti:msg2 var="ascendingMsg" key=".ascending"/>
    <cti:msg2 var="descendingMsg" key=".descending"/>
</cti:msgScope>

<cti:default var="descendingByDefault" value="false"/>
<cti:default var="sortParam" value="sort"/>
<cti:default var="descendingParam" value="descending"/>

<c:set var="currentSort" value="${param[sortParam]}"/>
<c:set var="currentDescending" value="${param[descendingParam]}"/>
<cti:default var="currentSort" value="${empty pageScope.isDefault ? 'NAME' : pageScope.isDefault ? fieldName : ''}"/>
<c:set var="isSorted" value="${currentSort == fieldName}"/>

<cti:url var="sortUrl" value="${baseUrl}">
    <c:if test="${not pageScope.overrideParams}">
        <%-- keep all parameters except sort and page number --%>
        <c:forEach var="aParam" items="${paramValues}">
            <c:if test="${aParam.key != sortParam && aParam.key != descendingParam && aParam.key != 'page'}">
                <c:forEach var="theValue" items="${aParam.value}">
                    <cti:param name="${aParam.key}" value="${theValue}"/>
                </c:forEach>
            </c:if>
        </c:forEach>
    </c:if>
    <cti:param name="${sortParam}" value="${fieldName}"/>
    <c:set var="sortIcon" value="icon-bullet-arrow-up" />
    <c:if test="${isSorted && !currentDescending || !isSorted && descendingByDefault}">
        <c:set var="sortIcon" value="icon-bullet-arrow-down" />
        <cti:param name="${descendingParam}" value="true"/>
    </c:if>
</cti:url>

<c:if test="${isSorted}">
    <c:set var="sortMsg" value="${currentDescending ? descendingMsg : ascendingMsg}"/>
    <c:set var="sortClass" value="sorted ${currentDescending ? 'desc' : 'asc'}"/>
    <a href="${sortUrl}" class="${sortClass} ${styleClass}" ${moreAttributes}>
        <span title="${sortMsg}" class="fl">${linkTextMsg}</span>

        <i title="" class="icon ${sortIcon}"></i>
    </a>
</c:if>

<c:if test="${!isSorted}">
    <a href="${sortUrl}" class="${styleClass}" ${moreAttributes}>
        <span class="fl">${linkTextMsg}</span>
    </a>
</c:if>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="nameKeys" required="true"  type="java.util.List" description="Base i18n keys. Available settings: .title (required)" rtexprvalue="true" %>
<%@ attribute name="id" rtexprvalue="true" %>
<%@ attribute name="styleClass" rtexprvalue="true" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" rtexprvalue="true" %>

<cti:uniqueIdentifier prefix="titledContainer_" var="tabContainerId" />
<c:if test="${!empty pageScope.id}">
    <c:set var="tabContainerId" value="${pageScope.id}" />
</c:if>

<c:set var="tabContainer" value="true" scope="request"/>
<div class="titledContainer boxContainer greyGradientBox ${pageScope.styleClass}" id="${tabContainerId}">

    <ul class="f_tabs pr">
        <c:forEach var="nameKey" items="${nameKeys}" varStatus="status">
            
            <cti:msgScope paths=".${nameKey}">
                <cti:msg2 var="title" key=".title" />
            </cti:msgScope>
            
            <li class="title boxContainer_title">
                <c:choose>
                    <c:when test="${pageScope.escapeTitle}">
                        <spring:escapeBody>${pageScope.title}</spring:escapeBody>
                    </c:when>
                    <c:otherwise>
                        ${pageScope.title}
                    </c:otherwise>
                </c:choose>
            </li>
        </c:forEach>
        <c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
            <div class="controls" id="${tabContainerId}_control">
                <img class="minMax" id="${tabContainerId}_minusImg" alt="hide"
                    src="<c:url value="/WebConfig/yukon/Icons/collapse.gif"/>">
                <img class="minMax" id="${tabContainerId}_plusImg" alt="show"
                    src="<c:url value="/WebConfig/yukon/Icons/expand.gif"/>">
            </div>
        </c:if>
    </ul>

    <div id="${tabContainerId}_content" class="content boxContainer_content f_tabbed">
        <jsp:doBody />
    </div>

</div>
<c:set var="tabContainer" value="false" scope="request"/>

<c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
    <script type="text/javascript">
        hideRevealSectionSetup('${tabContainerId}_plusImg', '${tabContainerId}_minusImg', 
                '${tabContainerId}_control', '${tabContainerId}_content', ${pageScope.showInitially ? true : false});
    </script>
</c:if>
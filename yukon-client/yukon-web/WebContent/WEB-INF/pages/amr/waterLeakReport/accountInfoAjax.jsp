<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ page import="com.cannontech.multispeak.client.MultiSpeakVersion" %>
<cti:msgScope paths="widgets.accountInformationWidget">
<c:set var="V3" value="<%= MultiSpeakVersion.V3.getVersion()%>"/>
    <c:choose>
        <c:when test="${!hasVendorId}">
            <i:inline key=".noVendor" />
        </c:when>
        <c:otherwise>
           <c:choose>
               <c:when test="${multiSpeakVersion.version == V3}">
                   <%@ include file="../../widget/accountInformationWidget/accountInfoPartial.jspf"%>
               </c:when>
               <c:otherwise>
                   <%@ include file="../../widget/accountInformationWidget/accountInfoPartialV5.jspf"%>
               </c:otherwise>
           </c:choose>
        </c:otherwise>
    </c:choose>
</cti:msgScope>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ page import="com.cannontech.multispeak.client.MultiSpeakVersion" %>
<script type="text/javascript">
	$(function() {
		$("#cisDetailsLink").click(function(event) {
			$("#cisDetails").dialog({
				width : "auto",
				minWidth : 400
			});
		});
	});
</script>
<c:set var="V3" value="<%= MultiSpeakVersion.V3.getVersion()%>"/>
<c:set var="V4" value="<%= MultiSpeakVersion.V4.getVersion()%>"/>
<c:choose>
    <c:when test="${!hasVendorId}">
        <i:inline key=".noVendor" />
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${multiSpeakVersion.version == V3}">
                <%@ include file="accountInfoPartial.jspf"%>
            </c:when>
            <c:when test="${multiSpeakVersion.version == V4}">
                <%@ include file="accountInfoPartialV4.jspf"%>
            </c:when>
            <c:otherwise>
                <%@ include file="accountInfoPartialV5.jspf"%>
            </c:otherwise>
        </c:choose>
        <div class="action-area">
            <c:choose>
                <c:when test="${multiSpeakVersion.version == V3}">
                    <a href="javascript:void(0);" id="cisDetailsLink" class="fl"><i:inline key=".viewDetails" /></a>
                </c:when>
                <c:when test="${multiSpeakVersion.version == V4}">
                    <cti:url var="cisDetailsUrlV4" value="/multispeak/viewCISDetailsV4/${deviceId}" />
                    <a target="_blank" href="${cisDetailsUrlV4}" id="cisDetailsLink" class="fl"><i:inline
                            key=".viewDetails" /></a>
                </c:when>
                <c:otherwise>
                    <cti:url var="cisDetailsUrl" value="/multispeak/viewCISDetails/${deviceId}" />
                    <a target="_blank" href="${cisDetailsUrl}" id="cisDetailsLink" class="fl"><i:inline
                            key=".viewDetails" /></a>
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${multiSpeakVersion.version == V3}">
            <div id="cisDetails" class="dn scroll-lg dialog-no-buttons" title="<cti:msg2 key=".cisDetails.title"/>"
                style="min-width: 400px;">
                <jsp:include page="/WEB-INF/pages/widget/accountInformationWidget/moreInfo.jsp" />
            </div>
        </c:if>
    </c:otherwise>
</c:choose>
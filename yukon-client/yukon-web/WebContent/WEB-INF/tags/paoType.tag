<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="yukonPao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ attribute name="showLink" %>

<cti:default var="showLink" value="true"/>

<c:choose>
	<c:when test="${empty yukonPao.paoIdentifier.paoType}">
		<cti:paoTypeIcon yukonPao="${yukonPao}"/>&nbsp;
	</c:when>
	<c:otherwise>
		<cti:url var="meterSearchUrl" value="/meter/search">
			<cti:param name="deviceType">${yukonPao.paoIdentifier.paoType.paoTypeName}</cti:param>
		</cti:url>
        <c:choose>
            <c:when test="${showLink == 'true' }">
        		<a href="${meterSearchUrl}">
                    <cti:paoTypeIcon yukonPao="${yukonPao}"/>&nbsp;${yukonPao.paoIdentifier.paoType.paoTypeName}
                </a>
            </c:when>
            <c:otherwise>
                <cti:paoTypeIcon yukonPao="${yukonPao}"/>&nbsp;${yukonPao.paoIdentifier.paoType.paoTypeName}
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
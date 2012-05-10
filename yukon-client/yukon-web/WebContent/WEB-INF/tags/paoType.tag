<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag body-content="empty"%>

<%@ attribute name="yukonPao" required="true" type="java.lang.Object"%>

<c:choose>
	<c:when test="${empty yukonPao.paoType}">
		<cti:paoTypeIcon yukonPao="${yukonPao}"/>&nbsp;
	</c:when>
	<c:otherwise>
		<cti:url var="meterSearchUrl" value="/spring/meter/search">
			<cti:param name="Device Type">${yukonPao.paoType.dbString}</cti:param>
		</cti:url>
		<a href="${meterSearchUrl}">
            <cti:paoTypeIcon yukonPao="${yukonPao}"/>&nbsp;${yukonPao.paoType.dbString}
        </a>
    </c:otherwise>
</c:choose>
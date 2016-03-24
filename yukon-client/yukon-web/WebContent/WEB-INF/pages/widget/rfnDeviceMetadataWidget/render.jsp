<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<d:inline id="metadata_showAll" nameKey="showAll" okEvent="none" on="#showAll">
    <tags:nameValueContainer altRowOn="true">
        <c:forEach var="pair" items="${metadata}">
            <%@ include file="metadataRow.jspf" %>
        </c:forEach>
    </tags:nameValueContainer>
</d:inline>

<c:choose>
    <c:when test="${not empty error}">
        <i:inline key="${error}"/>
    </c:when>
    <c:otherwise>
        <div id="metadata_csrView">
            <tags:nameValueContainer altRowOn="true">
                <c:forEach var="pair" items="${csrMetadata}">
                    <%@ include file="metadataRow.jspf" %>
                </c:forEach>
            </tags:nameValueContainer>
        </div>
    </c:otherwise>
</c:choose>

<div class="action-area">
    <c:if test="${showAll}">
        <a href="" class="showAll fl" id="showAll"><i:inline key=".showAll.label"/></a>
    </c:if>
    <tags:widgetActionRefresh method="render" nameKey="refresh" icon="icon-arrow-refresh"/>
</div>
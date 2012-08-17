<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<div id="metadata_content">

<dialog:inline id="metadata_showAll" nameKey="showAll" okEvent="none" on="#showAll" options="{'modal' : false}">
    <tags:nameValueContainer altRowOn="true">
        <c:forEach var="pair" items="${metadata}">
            <%@ include file="metadataRow.jspf" %>
        </c:forEach>
    </tags:nameValueContainer>
</dialog:inline>

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
    
    <div class="actionArea">
        <c:if test="${showAll}">
            <a class="showAll" id="showAll"><i:inline key=".showAll.label"/></a>
        </c:if>
        <tags:widgetActionUpdate hide="false" method="render" nameKey="refresh" container="metadata_content"/>
    </div>
</div>
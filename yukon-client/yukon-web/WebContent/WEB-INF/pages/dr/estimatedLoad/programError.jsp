<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.dr.estimatedLoad">

<input class="js-popup-title" type="hidden" value="${fn:escapeXml(title)}">
<div>
    <c:if test="${not empty initialIcon}">
        <cti:icon icon="${initialIcon}" classes="M0"/>&nbsp;
    </c:if>
    ${initialMessage}
</div>
<div class="separated-sections">
    <c:forEach var="error" items="${errors}" varStatus="status">
        <div class="section">
            <c:choose>
                <c:when test="${error.error}">
                    <div class="stacked">
                        <cti:icon icon="${error.icon}" classes="M0"/>&nbsp;
                        <strong>${fn:escapeXml(error.programName)}</strong>
                    </div>
                    <div>${fn:escapeXml(error.errorMessage)}</div>
                    <div>
                        <c:if test="${not empty error.linkValue}">
                            <cti:url var="url" value="${error.linkValue}"/>
                            <a href="${url}" target="_blank">${fn:escapeXml(error.linkText)}</a>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <i:inline key=".error.program.resolved" arguments="${error.programName}"/>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>
</div>
</cti:msgScope>
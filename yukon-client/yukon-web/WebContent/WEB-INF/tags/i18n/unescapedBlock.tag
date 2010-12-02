<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ tag body-content="empty" %>
<%@ attribute name="key" required="true" type="java.lang.Object"%>
<%@ attribute name="arguments" required="false" type="java.lang.Object"%>
<%@ attribute name="argumentSeparator" required="false" type="java.lang.String"%>
<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
<div class="i18nBlock">
<c:choose>
<c:when test="${not empty argumentSeparator}">
<cti:msg2 key="${key}" arguments="${arguments}" argumentSeparator="${argumentSeparator}" debug="true" fallback="true" htmlEscape="false"/>
</c:when>
<c:otherwise>
<cti:msg2 key="${key}" arguments="${arguments}" debug="true" fallback="true" htmlEscape="false"/>
</c:otherwise>
</c:choose>
<div class="i18nBlockDebug">
<c:forEach items="${msg2TagDebugMap}" var="entry">${entry.key}=${entry.value}<br>
</c:forEach>
</div>
</div>
</cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE"><div>
<c:choose>
<c:when test="${not empty argumentSeparator}">
<cti:msg2 key="${key}" arguments="${arguments}" argumentSeparator="${argumentSeparator}" htmlEscape="false"/>
</c:when>
<c:otherwise>
<cti:msg2 key="${key}" arguments="${arguments}" htmlEscape="false"/>
</c:otherwise>
</c:choose>
</div></cti:checkGlobalRolesAndProperties>
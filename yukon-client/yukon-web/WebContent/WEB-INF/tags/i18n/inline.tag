<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ attribute name="key" required="true" type="java.lang.Object" %>
<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="argumentSeparator" %>

<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
	<span class="i18nInline">
		<c:choose>
			<c:when test="${not empty argumentSeparator}">
				<cti:msg2 key="${key}" arguments="${arguments}"
					argumentSeparator="${argumentSeparator}" debug="true" fallback="true"/>
			</c:when>
			<c:otherwise>
				<cti:msg2 key="${key}" arguments="${arguments}" debug="true" fallback="true"/>
			</c:otherwise>
		</c:choose>
		<span class="i18nInlineDebug">
			<c:forEach var="entry" items="${msg2TagDebugMap}">${entry.key}=${entry.value}<br></c:forEach>
		</span>
	</span>
</cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE">
	<c:choose>
		<c:when test="${not empty argumentSeparator}">
			<cti:msg2 key="${key}" arguments="${arguments}" argumentSeparator="${argumentSeparator}"/>
		</c:when>
		<c:otherwise>
			<cti:msg2 key="${key}" arguments="${arguments}"/>
		</c:otherwise>
	</c:choose>
</cti:checkGlobalRolesAndProperties>
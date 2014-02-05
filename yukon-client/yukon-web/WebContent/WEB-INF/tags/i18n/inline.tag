<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ attribute name="key" required="true" type="java.lang.Object" %>
<%@ attribute name="arguments" type="java.lang.Object" %>

<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
	<span class="i18n-inline">
		<cti:msg2 key="${key}" arguments="${arguments}" debug="true" fallback="true"/>
	    <span class="i18n-inline-debug">
			<c:forEach var="entry" items="${msg2TagDebugMap}">${entry.key}=${entry.value}<br></c:forEach>
		</span>
	</span>
</cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE">
<cti:msg2 key="${key}" arguments="${arguments}"/>
</cti:checkGlobalRolesAndProperties>
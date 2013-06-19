<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="labelKey" required="true" type="java.lang.String" description="Key for the i18n string to be displayed" %>
<%@ attribute name="showPlainText" required="true" type="java.lang.Boolean" description="If true, the text is shown.  If false, the href is displayed." %>
<%@ attribute name="href" required="false" type="java.lang.String" description="The URL to use for the link (if displayed)" %>

<c:if test="${showPlainText}">
    <span><i:inline key="${labelKey}"/></span>
</c:if>
<c:if test="${not showPlainText}">
    <a href="${href}"><i:inline key="${labelKey}"/></a>
</c:if>

<%@ tag trimDirectiveWhitespaces="true" description="Creates an icon that will show a popup on click.  NOTE: Do NOT put javascript in the body of the popup since it would be executed twice." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="classes" description="CSS class names to add to the help icon element." %>
<%@ attribute name="nameKey" description="If provided, will use '.[nameKey.title]' as the popups title and .[nameKey] as the popup message." %>
<%@ attribute name="title"  description="Will be the title of the popup. Required unless using nameKey." %>
<%@ attribute name="width"  description="Width of the popup, see api.jqueryui.com/dialog/ for values. Default: 'auto'." %>

<cti:default var="width" value="auto"/>

<cti:uniqueIdentifier var="id" prefix="help-popup-"/>
<cti:icon icon="icon-help" classes="cp fn ${pageScope.classes}" data-popup="#${id}"/>
<c:if test="${not empty pageScope.nameKey}"><c:set var="title"><cti:msg2 key="${nameKey}.title"/></c:set></c:if>
<div id="${id}" class="dn" data-width="${width}" data-title="${fn:escapeXml(title)}">
    <c:if test="${not empty pageScope.nameKey}"><i:inline key="${nameKey}"/></c:if>
    <c:if test="${empty pageScope.nameKey}"><jsp:doBody/></c:if>
</div>
<%@ attribute name="method" required="true"%>
<%@ attribute name="container" required="true"%>
<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!pageScope.hide}">
	<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
	<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
	
	<script type="text/javascript">
		${widgetParameters.jsWidget}.setupLink('${uniqueId}', 
		                                        ${cti:jsonString(pageScope.linkParameters)});
	</script>
	
	<span id="${thisId}">
        <cti:button nameKey="${nameKey}" 
                    onclick="${widgetParameters.jsWidget}.doActionUpdate('${method}', '${container}', 
                                                                         '${thisId}', '${uniqueId}')"/>
	</span>
</c:if>
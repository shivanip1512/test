<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="hideReveal_" var="thisId"/>

<span id="${thisId}_span" class="${styleClass}" style="cursor: pointer">
<img id="${thisId}_plusImg" src="/WebConfig/yukon/Icons/triangle-right.gif">
<img id="${thisId}_minusImg" src="/WebConfig/yukon/Icons/triangle-down.gif">
${title}
</span>

<div id="${thisId}_container">
<jsp:doBody/>
</div>

<script type="text/javascript">
hideRevealSectionSetup('${thisId}', '${thisId}_container', ${showInitially ? true : false});
</script>
	
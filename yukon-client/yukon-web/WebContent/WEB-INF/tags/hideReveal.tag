<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="hideReveal_" var="thisId"/>

<div class="titledContainer triangleContainer ${styleClass}" <c:if test="${!empty id}" >id="${id}"</c:if>>
<div class="titleBar">
<span id="${thisId}_control" class="controls">
<img id="${thisId}_plusImg" src="/WebConfig/yukon/Icons/triangle-right.gif">
<img id="${thisId}_minusImg" src="/WebConfig/yukon/Icons/triangle-down.gif">
<span class="title">${title}</span>
</span>
</div>
<div id="${thisId}_content" class="content">
<jsp:doBody/>
</div>

</div>

<script type="text/javascript">
hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${showInitially ? true : false}, '${cti:jsSafe(title)}');
</script>
	
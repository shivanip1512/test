<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/boxContainer.js"/>

<cti:uniqueIdentifier prefix="boxContainer_" var="thisId"/>

<div class="boxContainer ${styleClass}" <c:if test="${!empty id}" >id="${id}"</c:if>>

	<div class="titleBar">
		<span class="title">
			${title}
		</span>
		<div class="controls">
			<c:if test="${(hideEnabled == null) || hideEnabled}">
				<img 
					class="minMax" 
					id="${thisId}_minusImg" 
					src="<c:url value="/WebConfig/yukon/Icons/clearbits/subtract.gif"/>"></img> 	
				<img 
					class="minMax" 
					id="${thisId}_plusImg" 
					src="<c:url value="/WebConfig/yukon/Icons/clearbits/add.gif"/>"></img>
			</c:if>
		</div>
	</div>
	
	<div id="${thisId}_content" class="content">
		<jsp:doBody/>
	</div>    
	            
</div>

<c:if test="${(hideEnabled == null) || hideEnabled}">
	<script type="text/javascript">
		boxContainerSetup('${thisId}', ${showInitially ? true : false}, '${title}');
	</script>
</c:if>

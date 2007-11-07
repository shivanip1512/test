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
		<div class="controls">
			<c:if test="${(hideEnabled == null) || hideEnabled}">
				<img 
					class="minMax" 
					id="${thisId}_minusImg" 
                    alt="hide"
					src="<c:url value="/WebConfig/yukon/Icons/clearbits/subtract.gif"/>">	
				<img 
					class="minMax" 
					id="${thisId}_plusImg" 
                    alt="show"
					src="<c:url value="/WebConfig/yukon/Icons/clearbits/add.gif"/>">
			</c:if>
		</div>
		<div class="title">
			${title}
		</div>
	</div>
	
	<div id="${thisId}_content" class="content">
		<jsp:doBody/>
	</div>    
	            
</div>
<c:if test="${empty showInitially}">
  <c:set var="showInitially" value="${true}"/> <%-- show by default --%>
</c:if>

<c:if test="${(hideEnabled == null) || hideEnabled}">
	<script type="text/javascript">
		boxContainerSetup('${thisId}', ${showInitially ? 'true' : 'false'}, '${cti:jsSafe(title)}');
	</script>
</c:if>

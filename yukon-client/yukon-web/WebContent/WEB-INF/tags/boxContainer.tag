<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="titledContainer_" var="thisId"/>

<div class="titledContainer boxContainer ${styleClass}" <c:if test="${!empty id}" >id="${id}"</c:if>>

	<div class="titleBar boxContainer_titleBar">
			<c:if test="${(hideEnabled == null) || hideEnabled}">
		      <div class="controls" id="${thisId}_control">
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
		      </div>
			</c:if>
		<div class="title boxContainer_title">
			${title}
		</div>
	</div>
	
	<div id="${thisId}_content" class="content boxContainer_content">
		<jsp:doBody/>
	</div>    
	            
</div>
<c:if test="${empty showInitially}">
  <c:set var="showInitially" value="${true}"/> <%-- show by default --%>
</c:if>

<c:if test="${(hideEnabled == null) || hideEnabled}">
	<script type="text/javascript">
        hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${showInitially ? true : false}, '${cti:jsSafe(title)}');
	</script>
</c:if>

<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="titledContainer_" var="thisId"/>

<div class="titledContainer boxContainer ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}" >id="${pageScope.id}"</c:if>>

	<div class="titleBar boxContainer_titleBar">
			<c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
		      <div class="controls" id="${thisId}_control">
				<img 
					class="minMax" 
					id="${thisId}_minusImg" 
                    alt="hide"
					src="<c:url value="/WebConfig/yukon/Icons/collapse.gif"/>">	
				<img 
					class="minMax" 
					id="${thisId}_plusImg" 
                    alt="show"
					src="<c:url value="/WebConfig/yukon/Icons/expand.gif"/>">
		      </div>
			</c:if>
		<div class="title boxContainer_title">
			${pageScope.title}
			
			<c:if test="${not empty pageScope.helpText}">
				<a href="javascript:void(0);" onclick="$('boxContainerInfoPopup_${thisId}').toggle();" >
            	<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
            	</a>
        	</c:if>
        	
		</div>
	</div>
	
	<div id="${thisId}_content" class="content boxContainer_content">
		<jsp:doBody/>
	</div>    
	            
</div>
<c:if test="${empty pageScope.showInitially}">
  <c:set var="showInitially" value="${true}"/> <%-- show by default --%>
</c:if>

<c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
	<script type="text/javascript">
        hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${pageScope.showInitially ? true : false}, '${cti:jsSafe(pageScope.title)}');
	</script>
</c:if>

<c:if test="${not empty pageScope.helpText}">
	<tags:simplePopup id="boxContainerInfoPopup_${thisId}" title="${pageScope.title}">
     	${helpText}
	</tags:simplePopup>	
</c:if>

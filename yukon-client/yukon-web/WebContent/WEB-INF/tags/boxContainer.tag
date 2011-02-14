<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

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
            <c:choose>
              <c:when test="${pageScope.escapeTitle}">
                <spring:escapeBody htmlEscape="true">${pageScope.title}</spring:escapeBody>
              </c:when>
              <c:otherwise>
                ${pageScope.title} 
              </c:otherwise>
            </c:choose>

			<c:if test="${not empty pageScope.helpText}">
				<a href="javascript:void(0);" onclick="$('boxContainerInfoPopup_${thisId}').toggle();" >
                    <cti:img key="help"/>
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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:uniqueIdentifier prefix="sectionContainer_" var="thisId"/>

<div class="titledContainer sectionContainer ${styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>>
    
    <div class="titleBar sectionContainer_titleBar">
        <div class="titleBar sectionContainer_title">
            ${title} 
            
            <c:if test="${not empty helpText}">
            	<img onclick="$('sectionContainerInfoPopup_${thisId}').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
        	</c:if>
        	
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content sectionContainer sectionContainer_content">
        <jsp:doBody/>
    </div>    

</div>

<c:if test="${not empty pageScope.helpText}">
	<tags:simplePopup id="sectionContainerInfoPopup_${thisId}" title="${pageScope.title}" onClose="$('sectionContainerInfoPopup_${thisId}').toggle();">
     	${pageScope.helpText}
	</tags:simplePopup>	
</c:if>
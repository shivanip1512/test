<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="width" required="false" type="java.lang.String"%>
<%@ attribute name="height" required="false" type="java.lang.String"%>

<cti:uniqueIdentifier prefix="sectionContainer_" var="thisId"/>

<div class="titledContainer sectionContainer ${styleClass}" <c:if test="${not empty id}">id="${id}"</c:if>>
    
    <%-- TITLE BAR AND TITLE --%>
    <div class="titleBar sectionContainer_titleBar">
        <div class="titleBar sectionContainer_title">
            ${title}
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content sectionContainer sectionContainer_content">
        <jsp:doBody/>
    </div>    

</div>
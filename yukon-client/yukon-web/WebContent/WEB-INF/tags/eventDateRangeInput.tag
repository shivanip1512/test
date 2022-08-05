<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="startDatePath" type="java.lang.Object" %>
<%@ attribute name="stopDatePath" type="java.lang.Object" %>

<spring:bind path="${startDatePath}">
   <c:if test="${status.error}">
      <c:set var="startCssClass">error</c:set>
      <c:set var="startWrapperClass">date-time-error</c:set>
   </c:if>
</spring:bind>
<spring:bind path="${stopDatePath}">
   <c:if test="${status.error}">
      <c:set var="stopCssClass">error</c:set>
      <c:set var="stopWrapperClass">date-time-error</c:set>
   </c:if>
</spring:bind>
<dt:dateRange startPath="${startDatePath}" endPath="${stopDatePath}" hideErrors="true" 
   startCssClass="${startCssClass}" startWrapperClass="${startWrapperClass}"
   endCssClass="${stopCssClass}" endWrapperClass="${stopWrapperClass}"/>
<span class="fl" style="width:170px;min-height:18px">
   <spring:bind path="${startDatePath}">
      <c:if test="${status.error}"><form:errors path="${startDatePath}" cssClass="error" /></c:if>
   </spring:bind>
</span>
<spring:bind path="${stopDatePath}">
   <c:if test="${status.error}"><form:errors path="${stopDatePath}" cssClass="error" /></c:if>
</spring:bind> 

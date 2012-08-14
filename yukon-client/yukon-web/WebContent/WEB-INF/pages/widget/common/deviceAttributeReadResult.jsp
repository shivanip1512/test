<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:if test="${not result.success}">
<div style="max-height: 240px; overflow: auto; padding: 1px">
  <div class="errorMessage"><i:inline key="yukon.common.device.attributeRead.general.errorHeading"/></div>
  <c:forEach items="${result.messages}" var="message">
  <c:if test="${not empty message.detail}">
    <tags:hideReveal2 titleKey="${message.summary}" showInitially="false">
    <i:inline key="${message.detail}"/>
    </tags:hideReveal2>
  </c:if>  
  <c:if test="${empty message.detail}">
    <div><i:inline key="${message.summary}"/></div>
  </c:if>
  </c:forEach>
</div>
</c:if>

<c:if test="${result.success}">
<div class="successMessage">
<i:inline key="yukon.common.device.attributeRead.general.success"/>
</div>
</c:if>
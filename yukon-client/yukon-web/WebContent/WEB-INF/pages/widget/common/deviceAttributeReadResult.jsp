<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i18n" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:if test="${not result.success}">
<div style="max-height: 240px; overflow: auto; padding: 1px">
  <div class="errorRed"><i18n:inline key="yukon.common.device.attributeRead.general.errorHeading"/></div>
  <c:forEach items="${result.messages}" var="message">
  <c:if test="${not empty message.detail}">
    <tags:hideReveal2 titleKey="${message.summary}" showInitially="false">
    <i18n:unescapedBlock key="${message.detail}"/>
    </tags:hideReveal2>
  </c:if>  
  <c:if test="${empty message.detail}">
    <i18n:block key="${message.summary}"/>
  </c:if>
  </c:forEach>
</div>
</c:if>

<c:if test="${result.success}">
<div>
<i18n:inline key="yukon.common.device.attributeRead.general.success"/>
</div>
</c:if>
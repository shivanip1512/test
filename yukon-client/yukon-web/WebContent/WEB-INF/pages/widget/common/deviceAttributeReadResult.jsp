<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${result != null}">
	<c:choose>
		<c:when test="${result.timeout}">
			<div class="warning"><i:inline key="yukon.common.device.attributeRead.timeoutWarning"/></div>
		</c:when>
		<c:when test="${result.success}">
		    <div class="success"><i:inline key="yukon.common.device.attributeRead.general.success"/></div>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty result.errors}">
			    <div style="max-height: 240px; overflow: auto; padding: 1px">
			        <div class="error"><i:inline key="yukon.common.device.attributeRead.general.errorHeading"/></div>
			        <c:forEach items="${result.errors}" var="error">
			            <c:if test="${not empty error.detail}">
			                <tags:hideReveal2 titleKey="${error.summary}" showInitially="false"><i:inline key="${error.detail}"/></tags:hideReveal2>
			            </c:if>  
			            <c:if test="${empty error.detail}">
			                <div><i:inline key="${error.summary}"/></div>
			            </c:if>
			        </c:forEach>
			    </div>
		    </c:if>
		</c:otherwise>
	</c:choose>
</c:if>
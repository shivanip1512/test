<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="detail stacked form-control">
    <cti:msg2 key=".attributeMapping.helpText" />
</div>

<cti:displayForPageEditModes modes="EDIT,CREATE">
    <spring:bind path="attributeMappingInputs">
        <c:if test="${status.error}">
            <form:errors path="attributeMappingInputs" cssClass="error" element="div" />
        </c:if>
    </spring:bind>
</cti:displayForPageEditModes>

<tags:nameValueContainer2 tableClass="with-form-controls">
    <c:forEach var="input" items="${categoryEditBean.attributeMappingInputs}" varStatus="loopStatus">
        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${not empty input.pointName}">
                <tags:nameValue2 nameKey="${input.attribute.formatKey}">${fn:escapeXml(input.pointName)}</tags:nameValue2>
            </c:if>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
	        <form:hidden path="attributeMappingInputs[${loopStatus.index}].attribute"/>
	        <tags:nameValue2 nameKey="${input.attribute.formatKey}">
	            <tags:input path="attributeMappingInputs[${loopStatus.index}].pointName" size="35" maxlength="60" autofocus="autofocus"/>
	        </tags:nameValue2>
        </cti:displayForPageEditModes>
    </c:forEach>
</tags:nameValueContainer2>

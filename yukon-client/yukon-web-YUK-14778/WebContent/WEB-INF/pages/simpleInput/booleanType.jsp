<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"  %>

<cti:displayForPageEditModes modes="VIEW">
    <cti:msg2 key="yukon.common.${status.value}"/>
    <input type="hidden" name="${status.expression}" value="${status.value}">
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
    <div class="button-group button-group-toggle">
        <cti:msg2 var="trueText" key="yukon.common.true"/>
        <cti:msg2 var="falseText" key="yukon.common.false"/>
        <c:choose>
            <c:when test="${status.value}">
                <c:set var="trueClass" value="on" />
                <c:set var="falseClass" value="" />
            </c:when>
            <c:otherwise>
                <c:set var="trueClass" value="" />
                <c:set var="falseClass" value="on" />
            </c:otherwise>
        </c:choose>

        <cti:button label="${trueText}" classes="yes ${trueClass}" data-value="true"/>
        <cti:button label="${falseText}" classes="no ${falseClass}" data-value="false"/>

        <input type="hidden" name="${status.expression}" value="${status.value}" data-input>

    </div>
</cti:displayForPageEditModes>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:displayForPageEditModes modes="VIEW">
    <c:forEach var="option" items="${inputType.optionList}">
        <c:if test="${status.value == option.value}">
            <cti:msg2 key="${option}"/>
            <input type="hidden" name="${status.expression}" value="${option.value}"/>
        </c:if>
    </c:forEach>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
    <div class="button-group button-group-toggle">
        <input type="hidden" name="${status.expression}" value="${status.value}" data-input>

        <c:forEach var="option" items="${inputType.optionList}">
            <cti:msg2 var="optionText" key="${option}"/>
            <c:set var="on" value=""/>
            <c:if test="${status.value == option.value}">
                <c:set var="on" value="on"/>
            </c:if>
            <cti:button label="${optionText}" classes="yes ${on}" data-value="${option.value}"/>
        </c:forEach>
    </div>
</cti:displayForPageEditModes>
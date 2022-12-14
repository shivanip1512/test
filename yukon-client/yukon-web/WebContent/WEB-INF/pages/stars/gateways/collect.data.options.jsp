<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<div class="column-12-12 clearfix js-select-all-container">
<c:forEach var="type" items="${dataTypes}" varStatus="status">
    <c:choose>
        <c:when test="${status.index % 2 == 0}">
            <div class="column one">
                <label>
                    <input class="js-select-all-item" type="checkbox" name="${type}" checked>
                    <i:inline key=".sequenceType.${type}"/>
                </label>
            </div>
        </c:when>
        <c:otherwise>
            <div class="column two nogutter">
                <label>
                    <input class="js-select-all-item" type="checkbox" name="${type}" checked>
                    <i:inline key=".sequenceType.${type}"/>
                </label>
            </div>
        </c:otherwise>
    </c:choose>
</c:forEach>
<label><input class="js-select-all" type="checkbox" checked><i:inline key="yukon.common.selectall"/></label>
</div>

</cti:msgScope>
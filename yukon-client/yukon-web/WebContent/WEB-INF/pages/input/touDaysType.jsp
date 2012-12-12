<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<!-- Iterate through each of the TOU days -->

<c:forEach var="item" items="${input.inputMap}">
    <c:set var="key" value="${item.key}" />
        <div style="margin-bottom: 5px;">
            <cti:renderInput input="${input.inputMap[key]}" /><br/>
        </div>
</c:forEach>
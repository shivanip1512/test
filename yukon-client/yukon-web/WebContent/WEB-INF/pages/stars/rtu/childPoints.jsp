<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<div class="js-child-points">
    <b><cti:msg2 key="yukon.web.modules.operator.rtuDetail.childPoints"/></b>
    <div class="bordered-div scroll-md" style="width:60%;">
        <c:if test="${empty points}">
            <cti:msg2 key="yukon.web.modules.operator.rtuDetail.noChildPoints"/>
        </c:if>
        <%@ include file="/WEB-INF/pages/capcontrol/pointsTable.jsp" %>
    </div>
</div>

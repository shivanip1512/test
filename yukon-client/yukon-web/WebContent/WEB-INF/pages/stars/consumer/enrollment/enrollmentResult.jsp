<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="enrollmentResult">
    <cti:standardMenu/>

    <h3>
        <cti:msg key="yukon.dr.consumer.enrollment.header" /><br>
    </h3>
    <br>
    <br>
    <div align="center">
        <c:choose>
            <c:when test="${!empty errorMessage}">
                <div class="user-message error"><cti:msg key="${errorMessage}"/></div>
            </c:when>
            <c:otherwise>
                <div><cti:msg key="${enrollmentResult}"/></div>
            </c:otherwise>
        </c:choose>
        <br>
        <cti:url var="okUrl" value="/stars/consumer/enrollment"/>
        <cti:msg key="yukon.dr.consumer.enrollmentResult.ok" var="ok"/>
        <cti:button label="${ok}" onclick="location.href='${okUrl}';"/>
    </div>    

</cti:standardPage>
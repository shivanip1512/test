<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dr" page="cc.customerList">

<div class="column-24">
    <div class="column one nogutter">
        <div>
            <ul>
            <c:forEach var="customer" items="${customerList}">
                <cti:url var="url" value="customerDetail/${customer.id}"/>
                <li><a href="${url}">${fn:escapeXml(customer.companyName)}</a></li>
            </c:forEach>
            </ul>
        </div>
    </div>
</div>

</cti:standardPage>
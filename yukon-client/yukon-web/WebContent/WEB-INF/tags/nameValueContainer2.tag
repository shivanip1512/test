<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="id" %>
<%@ attribute name="naturalWidth" type="java.lang.Boolean" description="Default is true. Causes table have a width:auto style (as opposed to 100%)."%>
<%@ attribute name="tableClass" %>

<c:set var="nameValueContainer2" value="true" scope="request"/>
<table class="name-value-table ${pageScope.tableClass}<c:if test="${empty pageScope.naturalWidth || pageScope.naturalWidth == true}"> natural-width</c:if>" <c:if test="${!empty pageScope.id}">id="${pageScope.id}"</c:if>>
    <thead></thead>
    <tfoot></tfoot>
    <tbody>
        <jsp:doBody/>
    </tbody>
</table>
<c:set var="nameValueContainer2" value="false" scope="request"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<h3><cti:msg key="yukon.dr.operator.optoutError.header"/></h3>

    <cti:msg key="${error}"/>

    <br>
    <br>

    <cti:url var="optOutUrl" value="/operator/Consumer/OptOut.jsp"/>
    <input type="button" value='<cti:msg key="yukon.dr.operator.optoutError.ok"/>'
           onclick="location.href='<c:out value="${optOutUrl}"/>';"></input>

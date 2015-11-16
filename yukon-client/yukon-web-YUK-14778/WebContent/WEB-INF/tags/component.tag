<%@ tag  dynamic-attributes="componentAttributes" %>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="paramMap" required="false" type="java.util.Map"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<!--  Component: ${name}  -->
<c:set var="componentParameters" value="${cti:combineWidgetParams(pageScope.paramMap,null)}" scope="request"/>
<c:set var="componentParameters" value="${cti:combineWidgetParams(componentParameters,pageScope.componentAttributes)}" scope="request"/>
<cti:uniqueIdentifier var="componentId" prefix="component_"/>
<c:set target="${componentParameters}" property="componentId" value="${componentId}"/>

<jsp:include flush="false" page="/component/${name}"/>


<%@ tag  dynamic-attributes="widgetAttributes" %>
<%@ attribute name="bean" required="true" type="java.lang.String"%>
<%@ attribute name="paramMap" required="false" type="java.util.Map"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/widgetObject.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>
<cti:beanLookup bean="${bean}" var="beanInst"/>

<!--  Widget: ${beanInst.shortName}  -->
<c:set var="widgetParameters" value="${cti:combineWidgetParams(paramMap,null)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,widgetContainerParams)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,widgetAttributes)}" scope="request"/>
<cti:uniqueIdentifier var="widgetId" prefix="widget_"/>
<c:set target="${widgetParameters}" property="widgetId" value="${widgetId}"/>
<c:set target="${widgetParameters}" property="jsWidget" value="jsobj_${widgetParameters.widgetId}"/>

<script type="text/javascript">
var ${widgetParameters.jsWidget} = new JsWidgetObject("${beanInst.shortName}", ${cti:jsonString(widgetParameters)});
<c:if test="${beanInst.lazyLoad}">
Event.observe(window,'load', function() {${widgetParameters.jsWidget}.render()});
</c:if>
</script>

<div id="widgetWrapper_${widgetParameters.widgetId}" style="width: ${widgetParameters.width}px;">

<c:set var="showIdentity" value="${widgetParameters.identify and beanInst.hasIdentity}"/>
<c:if test="${showIdentity}">
<c:import var="widgetIdentity" url="/spring/widget/${beanInst.shortName}/identity" scope="page"/>
<c:set var="containerTitle" value="${beanInst.title}: ${widgetIdentity}"/>
</c:if>
<c:if test="${not showIdentity}">
<c:set var="containerTitle" value="${beanInst.title}"/>
</c:if>

<ct:boxContainer title="${containerTitle}" id="widgetTitledContainer_${widgetParameters.widgetId}" styleClass="widgetContainer" hideEnabled="${empty hideEnabled ? true : hideEnabled}">

<div id="widgetContainer_${widgetParameters.widgetId}" style="height: ${widgetParameters.height}; width: ${widgetParameters.width - 10}px;">
<c:choose>
<c:when test="${beanInst.lazyLoad}">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
</c:when>
<c:otherwise>

<jsp:include flush="false" page="/spring/widget/${beanInst.shortName}/render"/>

</c:otherwise>
</c:choose>
</div>

</ct:boxContainer>

</div>
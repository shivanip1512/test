<%@ tag  dynamic-attributes="widgetAttributes" %>

<%@ attribute name="bean" required="true" %>
<%@ attribute name="paramMap" type="java.util.Map" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="title" %>
<%@ attribute name="helpText" %>
<%@ attribute name="container" description="container type: 'box' or 'section'. Default:'box'" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<cti:includeScript link="/JavaScript/widgetObject.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:beanLookup bean="${bean}" var="beanInst"/>

<%-- AUTHORIZE VISIBLITY BASED ON ROLE/ROLE PROPERTY --%>
<cti:checkUserChecker userChecker="${beanInst.roleAndPropertiesChecker}">

<!--  Widget: ${beanInst.shortName}  -->
<c:set var="widgetParameters" value="${cti:combineWidgetParams(pageScope.paramMap,null)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,widgetContainerParams)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,pageScope.widgetAttributes)}" scope="request"/>
<cti:uniqueIdentifier var="widgetId" prefix="widget_"/>
<c:set target="${widgetParameters}" property="widgetId" value="${widgetId}"/>
<c:set target="${widgetParameters}" property="shortName" value="${beanInst.shortName}"/>
<c:set target="${widgetParameters}" property="jsWidget" value="jsobj_${widgetParameters.widgetId}"/>

<script type="text/javascript">
    var ${widgetParameters.jsWidget} = new JsWidgetObject("${beanInst.shortName}", ${cti:jsonString(widgetParameters)});
    <c:if test="${beanInst.lazyLoad}">
        jQuery(function() {${widgetParameters.jsWidget}.render()});
    </c:if>
</script>

<c:if test="${empty widgetParameters.width}">
    <c:set target="${widgetParameters}" property="width" value="100%"/>
</c:if>

<div id="widgetWrapper_${widgetParameters.widgetId}" class="widgetWrapper" style="width: ${widgetParameters.width};">
    <c:set var="showIdentity" value="${widgetParameters.identify and beanInst.hasIdentity}"/>
    <c:if test="${showIdentity}">
        <c:import var="widgetIdentity" url="/widget/${beanInst.shortName}/identity" scope="page"/>
        <cti:msg2 var="containerTitle" key="${beanInst.titleKey}.widgetIdentity" argument="${widgetIdentity}"/>
    </c:if>
    <c:if test="${not showIdentity}">
        <cti:msg2 var="containerTitle" key="${beanInst.titleKey}"/>
    </c:if>
    <c:if test="${not empty pageScope.title}">
        <c:set var="containerTitle" value="${pageScope.title}"/>
    </c:if>

    <c:if test="${empty container or container eq 'box' }">
        <tags:boxContainer title="${containerTitle}" id="widget-titled-container-${widgetParameters.widgetId}" styleClass="widget-container" showInitially="true" hideEnabled="${empty pageScope.hideEnabled ? true : pageScope.hideEnabled}" helpText="${pageScope.helpText}">
            <div id="widget-container-${widgetParameters.widgetId}" style="height: ${widgetParameters.height};">
                <c:choose>
                <c:when test="${beanInst.lazyLoad}">
                    <img src="<c:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </c:when>
                <c:otherwise>
                    <jsp:include flush="false" page="/widget/${beanInst.shortName}/render"/>
                </c:otherwise>
                </c:choose>
            </div>
        </tags:boxContainer>
    </c:if>
    <c:if test="${container eq 'section'}">
        <tags:sectionContainer title="${containerTitle}" id="widget-titled-container-${widgetParameters.widgetId}" styleClass="widget-container" helpText="${pageScope.helpText}">
            <div id="widget-container-${widgetParameters.widgetId}" style="height: ${widgetParameters.height};">
                <c:choose>
                <c:when test="${beanInst.lazyLoad}">
                    <img src="<c:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </c:when>
                <c:otherwise>
                    <jsp:include flush="false" page="/widget/${beanInst.shortName}/render"/>
                </c:otherwise>
                </c:choose>
            </div>
        </tags:sectionContainer>
    </c:if>
</div>
</cti:checkUserChecker>
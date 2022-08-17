<%@ tag dynamic-attributes="widgetAttributes" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="bean" required="true" %>
<%@ attribute name="paramMap" type="java.util.Map" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="displayUnauthorizedMessage" type="java.lang.Boolean" %>
<%@ attribute name="title" %>
<%@ attribute name="id" %>

<%@ attribute name="helpText" description="The text to put inside of a help popup." %>
<%@ attribute name="helpUrl" description="A url used to load a help popup with content before showing." %>
<%@ attribute name="showHelpIcon" required="false" type="java.lang.Boolean" 
              description="Show help icon even if the helpText or helpUrl is empty. The help text has to be loaded using some js code in this case." %>

<%@ attribute name="container" description="container type: 'box' or 'section'. Default:'box'" %>
<%@ attribute name="classes" description="CSS class names applied to the container." %>

<cti:default var="classes" value=""/>

<cti:includeScript link="/resources/js/widgets/yukon.widget.js"/>

<cti:beanLookup bean="${bean}" var="beanInst"/>

<%-- AUTHORIZE VISIBILITY BASED ON ROLE/ROLE PROPERTY --%>
<cti:checkUserChecker var="authorized" userChecker="${beanInst.roleAndPropertiesChecker}"/>

<%-- Do not affect how widgets currently display on pages other than dashboards...widgets should still be hidden in this case --%>
<c:if test="${displayUnauthorizedMessage || authorized}">

<!--  Widget: ${beanInst.shortName}  -->
<c:set var="widgetParameters" value="${cti:combineWidgetParams(pageScope.paramMap,null)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,widgetContainerParams)}" scope="request"/>
<c:set var="widgetParameters" value="${cti:combineWidgetParams(widgetParameters,pageScope.widgetAttributes)}" scope="request"/>

<cti:uniqueIdentifier var="widgetId" prefix="widget_"/>
<c:if test="${!empty pageScope.id && pageScope.id > 0}">
    <c:set var="widgetId" value="widget_${pageScope.id}"/>
    <c:set var="useId" value="${true}"/>
</c:if>
<c:set target="${widgetParameters}" property="widgetId" value="${widgetId}"/>
<c:set target="${widgetParameters}" property="shortName" value="${beanInst.shortName}"/>
<c:set target="${widgetParameters}" property="jsWidget" value="jsobj_${widgetParameters.widgetId}"/>

<script type="text/javascript">
    var ${widgetParameters.jsWidget} = new YukonWidget("${beanInst.shortName}", ${cti:jsonString(widgetParameters)});
    if (!yukon.widgets) yukon.widgets = {};
    yukon.widgets['${widgetParameters.widgetId}'] = ${widgetParameters.jsWidget};
    <c:if test="${authorized && beanInst.lazyLoad}">
        $(function() {${widgetParameters.jsWidget}.render()});
    </c:if>
    $(document).ready(function() {
        var widgetContainer = $('#widget-container-${widgetParameters.widgetId}'),
            titleContainer = $('#widget-titled-container-${widgetParameters.widgetId}_content');
        widgetContainer.prependTo(titleContainer);
        widgetContainer.removeClass('dn');
    });
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

    <c:if test="${empty container or container eq 'box'}">
        <div id="widget-container-${widgetParameters.widgetId}" style="height: ${widgetParameters.height};" class="dn">
            <c:choose>
                <c:when test="${!authorized}">
                    <cti:msg2 key="widgets.notAuthorized"/>
                </c:when>
                <c:when test="${beanInst.lazyLoad}">
                    <img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </c:when>
                <c:otherwise>
                    <jsp:include flush="false" page="/widget/${beanInst.shortName}/render"/>
                </c:otherwise>
            </c:choose>
        </div>
        <tags:boxContainer title="${containerTitle}"
                id="widget-titled-container-${widgetParameters.widgetId}" 
                styleClass="widget-container ${classes}" 
                showInitially="true" 
                hideEnabled="${empty pageScope.hideEnabled ? true : pageScope.hideEnabled}" 
                helpText="${pageScope.helpText}"
                helpUrl="${pageScope.helpUrl}"
                showHelpIcon="${pageScope.showHelpIcon}"
                smartNotificationsEvent="${beanInst.smartNotificationsEvent}"
                useIdForCookie="${useId}">
        </tags:boxContainer>
    </c:if>
    <c:if test="${container eq 'section'}">
        <tags:sectionContainer title="${containerTitle}" 
                id="widget-titled-container-${widgetParameters.widgetId}" 
                styleClass="widget-container ${classes}" 
                helpText="${pageScope.helpText}"
                helpUrl="${pageScope.helpUrl}"
                showHelpIcon="${pageScope.showHelpIcon}">
            <div id="widget-container-${widgetParameters.widgetId}" style="height: ${widgetParameters.height};">
                <c:choose>
                    <c:when test="${!authorized}">
                        <cti:msg2 key="widgets.notAuthorized"/>
                    </c:when>
                    <c:when test="${beanInst.lazyLoad}">
                        <img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                    </c:when>
                    <c:otherwise>
                        <jsp:include flush="false" page="/widget/${beanInst.shortName}/render"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </tags:sectionContainer>
    </c:if>
</div>
</c:if>

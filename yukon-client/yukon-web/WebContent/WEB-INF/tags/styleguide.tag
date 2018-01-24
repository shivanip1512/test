<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="page" required="true" %>

<cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
<cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>

<div class="column-4-20">
    <div class="column one side-nav">
        <h2 class="buffered">Basic</h2>
        <ul>
            <c:set var="clazz" value="${page == 'grids' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/grids"/>">Grids</a></li>
            <c:set var="clazz" value="${page == 'tables' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/tables"/>">Tables</a></li>
            <c:set var="clazz" value="${page == 'containers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/containers"/>">Containers</a></li>
            <c:set var="clazz" value="${page == 'icons' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/icons"/>">Icons</a></li>
            <c:set var="clazz" value="${page == 'labels.badges' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/labels-badges"/>">Labels / Badges</a></li>
            <c:set var="clazz" value="${page == 'progressbars' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/progressbars"/>">Progress Bars</a></li>
            <c:set var="clazz" value="${page == 'buttons' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/buttons"/>">Buttons</a></li>
            <c:set var="clazz" value="${page == 'switches' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/switches"/>">Switches</a></li>
            <c:set var="clazz" value="${page == 'alerts' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/alerts"/>">Alerts</a></li>
        </ul>
        <h2 class="buffered">Advanced</h2>
        <ul>
            <c:set var="clazz" value="${page == 'inputs' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/fun-with-inputs"/>">Fun with Inputs</a></li>
            <c:set var="clazz" value="${page == 'blocking' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/blocking"/>">Blocking</a></li>
            <c:set var="clazz" value="${page == 'dialogs' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/dialogs"/>">Dialogs</a></li>
            <c:set var="clazz" value="${page == 'pickers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/pickers"/>">Pickers</a></li>
            <c:set var="clazz" value="${page == 'timelines' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/timelines"/>">Time Lines</a></li>
            <c:set var="clazz" value="${page == 'sliders' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/sliders"/>">Sliders</a></li>
            <c:set var="clazz" value="${page == 'steppers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/steppers"/>">Steppers</a></li>
            <c:set var="clazz" value="${page == 'date-pickers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/date-pickers"/>">Date & Time Pickers</a></li>
            <c:set var="clazz" value="${page == 'group-pickers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/group-pickers"/>">Group Pickers</a></li>
            <c:set var="clazz" value="${page == 'device-collections' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/device-collections"/>">Device Collections</a></li>
        </ul>
    </div>
    <div class="column two nogutter"><jsp:doBody/></div>
</div>
<script>$(function () { prettyPrint(); });</script>
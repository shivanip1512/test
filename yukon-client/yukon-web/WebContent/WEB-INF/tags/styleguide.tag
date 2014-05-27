<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="page" required="true" %>

<cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
<cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>

<div class="column-4-20">
    <div class="column one side-nav">
        <ul>
            <c:set var="clazz" value="${page == 'grids' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/grids"/>">Grids</a></li>
            <c:set var="clazz" value="${page == 'tables' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/tables"/>">Tables</a></li>
            <c:set var="clazz" value="${page == 'containers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/containers"/>">Containers</a></li>
            <c:set var="clazz" value="${page == 'buttons' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/buttons"/>">Buttons</a></li>
            <c:set var="clazz" value="${page == 'icons' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/icons"/>">Icons</a></li>
            <c:set var="clazz" value="${page == 'pickers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/pickers"/>">Pickers</a></li>
            <c:set var="clazz" value="${page == 'dialogs' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/dialogs"/>">Dialogs</a></li>
            <c:set var="clazz" value="${page == 'date-pickers' ? 'selected' : ''}"/>
            <li class="${clazz}"><a href="<cti:url value="/dev/styleguide/date-pickers"/>">Date Pickers</a></li>
        </ul>
    </div>
    <div class="column two nogutter"><jsp:doBody/></div>
</div>
<script>$(function () { prettyPrint(); });</script>
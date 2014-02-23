<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="suffix" required="true" %>
<%@ attribute name="updaterString" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:set var="dynamicChoose" value="true" scope="request"/>
<cti:uniqueIdentifier var="dynamicChooseId" prefix="dynamicChooseSpan_${suffix}_"/>
<c:set var="dynamicChooseId" value="${dynamicChooseId}" scope="request"/>

<span id="${dynamicChooseId}"><jsp:doBody/></span>

<cti:dataUpdaterCallback function="yukon.ui.util.updateDynamicChoose('${dynamicChooseId}')" initialize="true" state="${updaterString}" />

<c:set var="dynamicChoose" value="false" scope="request"/>
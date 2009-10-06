<%@ attribute name="suffix" required="true" type="java.lang.String"%>
<%@ attribute name="updaterString" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<cti:includeScript link="/JavaScript/dynamicChoose.js"/>

<c:set var="dynamicChoose" value="true" scope="request"/>
<cti:uniqueIdentifier var="dynamicChooseId" prefix="dynamicChooseSpan_${suffix}_"/>
<c:set var="dynamicChooseId" value="${dynamicChooseId}" scope="request"/>

<span id="${dynamicChooseId}">
    <jsp:doBody/>
</span>

<cti:dataUpdaterCallback function="updateDynamicChoose('${dynamicChooseId}')" initialize="true" state="${updaterString}" />

<c:set var="dynamicChoose" value="false" scope="request"/>

<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="onClose" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
  Until the script inclusion feature gets fixed, you might have to include the scripts and CSS by hand.
<cti:includeScript link="/JavaScript/javaWebStartLauncher.js"/>
--%>

<div id="${id}" class="simplePopup ${styleClass}" style="font-size:.8em;display:none;">
<div style="float:right;cursor:pointer" <c:if test="${not empty onClose}">onclick="${onClose}"</c:if>>
<img class="cssicon" alt="close" src="/WebConfig/yukon/Icons/clearbits/close.gif">
</div>
<h2>${title}</h2>
<jsp:doBody/>
</div>


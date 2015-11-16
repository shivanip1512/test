<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="arguments" type="java.lang.Object"%>
<%@ attribute name="id" required="true"%>
<%@ attribute name="on" description="Registers a click event on the element with this CSS selector to open the popup."%>
<%@ attribute name="onClose"%>
<%@ attribute name="options" description="JQUI dialog options. See http://api.jqueryui.com/dialog/"%>
<%@ attribute name="showImmediately" type="java.lang.Boolean"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="titleKey" required="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msg2 key="${titleKey}" arguments="${arguments}" var="theTitle" javaScriptEscape="true"/>
<tags:simplePopup title="${theTitle}" id="${id}" onClose="${pageScope.onClose}" on="${pageScope.on}" options="${pageScope.options}" styleClass="${pageScope.styleClass}" showImmediately="${pageScope.showImmediately}">
    <jsp:doBody/>
</tags:simplePopup>
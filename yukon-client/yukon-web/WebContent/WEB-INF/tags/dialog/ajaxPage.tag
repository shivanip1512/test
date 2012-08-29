<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag language="java" pageEncoding="UTF-8" description="Use this tag to wrap a JSP which is to be an AJAX dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="module" required="true" %>
<%@ attribute name="page" required="true" %>
<%@ attribute name="okEvent" required="true" description="Use 'yukonDialogSubmit' to submit a form, 'none' to skip the ok button or an event name to trigger that event on the dialog." %>
<%@ attribute name="id" description="The id of the div to put the dialog in.  This div should exist on the page using the dialog.  Defaults to 'ajaxDialog'." %>
<%@ attribute name="options" description="Options to use for the dialog.  See http://jqueryui.com/demos/dialog/#options" %>
<%@ attribute name="title" description="Dialog Title--use this if you need a dynamic title." %>

<cti:includeScript link="/JavaScript/ajaxDialog.js"/>

<c:if test="${empty pageScope.id}">
    <c:set var="id" value="ajaxDialog" />
</c:if>

<c:set var="pageParts" value="${fn:split(page, '.')}"/>
<c:set var="pathSep" value=""/>
<c:set var="pagePartSep" value=""/>
<c:set var="modulePaths" value=""/>
<c:forEach var="pagePart" items="${pageParts}">
    <c:set var="modulePaths" value="${modulePaths}${pathSep}modules.${module}.${prevPageParts}${pagePartSep}${pagePart}"/>
    <c:set var="prevPageParts" value="${prevPageParts}${pagePartSep}${pagePart}"/>
    <c:set var="pathSep" value=","/>
    <c:set var="pagePartSep" value="."/>
</c:forEach>

<cti:msgScope paths="${modulePaths}">

<cti:msgScope paths=".${nameKey},components.dialog.${nameKey},components.dialog">
    <c:if test="${!empty pageScope.title}">
        <c:set var="titleMsg" value="${title}"/>
    </c:if>
    <c:if test="${empty pageScope.title}">
        <cti:msg2 var="titleMsg" key=".title"/>
    </c:if>
    <cti:msg2 var="okBtnMsg" key=".ok"/>
    <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
    <cti:msg2 var="closeBtnMsg" key=".close"/>
</cti:msgScope>


<script type="text/javascript">
jQuery(document).ready(function() {
    var dialogDiv = jQuery('#${id}');
    var buttons = [];
    <c:if test="${okEvent != 'none'}">
        var okButton = {'text' : '${okBtnMsg}'};
        okButton.click = function() { dialogDiv.trigger('${okEvent}'); }
        buttons.push(okButton);
    </c:if>
    <c:if test="${okEvent == 'none'}">
        buttons.push({'text' : '${closeBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
    </c:if>
    var dialogOpts = {
            'title' : '${titleMsg}',
            'position' : 'center',
            'width' : 'auto',
            'height' : 'auto',
            'modal' : true,
            'buttons' : buttons };
    <c:if test="${!empty pageScope.options}">
        jQuery.extend(dialogOpts, ${options});
    </c:if>
    dialogDiv.dialog(dialogOpts);
    Yukon.ui.unblockPage();
});
</script>

<cti:flashScopeMessages/>
<jsp:doBody/>

</cti:msgScope>

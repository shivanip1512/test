<%@ tag language="java" pageEncoding="UTF-8" description="Use this tag to wrap a JSP which is to be an AJAX dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="module" required="true"%>
<%@ attribute name="page" required="true"%>
<%@ attribute name="okAction" required="true" description="Use 'submit' to submit a form, 'none' to skip the ok button or an event name to trigger that event on the dialog."%>
<%@ attribute name="dialogId" description="The id of the div to put the dialog in.  This div should exist on the page using the dialog.  Defaults to 'ajaxDialog'."%>

<c:if test="${empty pageScope.dialogId}">
    <c:set var="dialogId" value="ajaxDialog"/>
</c:if>

<c:set var="pageParts" value="${fn:split(page, '.')}"/>
<c:set var="pathSep" value=""/>
<c:set var="pagePartSep" value=""/>
<c:set var="modulePaths" value=""/>
<c:forEach var="pagePart" items="${pageParts}">
    <c:set var="modulePaths" value="${modulePaths}${pathSep}modules.${module}.${prevPageParts}${pagePart}"/>
    <c:set var="prevPageParts" value="${prevPageParts}${pagePartSep}${pagePart}"/>
    <c:set var="pathSep" value=","/>
    <c:set var="pagePartSep" value="."/>
</c:forEach>

<cti:msgScope paths="${modulePaths}">

<cti:msgScope paths=".${nameKey},components.dialog.${nameKey},components.dialog">
    <cti:msg2 var="titleMsg" key=".title"/>
    <cti:msg2 var="okBtnMsg" key=".ok"/>
    <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
</cti:msgScope>

<script type="text/javascript">
jQuery(document).ready(function() {
    var buttons = [];
    <c:if test="${okAction != 'none'}">
        var okButton = {'text' : '${okBtnMsg}'};
        <c:if test="${okAction == 'submit'}">
            okButton.click = function() { jQuery(this).find('form').submit(); }
        </c:if>
        <c:if test="${okAction != 'submit'}">
            okButton.click = function() { jQuery('#${dialogId}').trigger('${okAction}'); }
        </c:if>
        buttons.push(okButton);
    </c:if>
    buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
    var dialogOpts = {
            'title' : '${titleMsg}',
            'position' : 'center',
            'width' : 'auto',
            'height' : 'auto',
            'buttons' : buttons };
    jQuery('#${dialogId}').dialog(dialogOpts);
    Yukon.ui.unblockPage();
});
</script>

<cti:flashScopeMessages/>
<jsp:doBody/>

</cti:msgScope>

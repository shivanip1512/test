<%@ tag trimDirectiveWhitespaces="true" language="java" pageEncoding="UTF-8" description="Use this tag to wrap a JSP which is to be an AJAX dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="title" description="The raw text to use for the title, if supplied it is overrides any i18n title that may be specified."%>
<%@ attribute name="id" description="The id of the div to put the dialog in.  This div is created in this tag.  This is also used to name the function to open the dialog."%>
<%@ attribute name="okEvent" required="true" description="Use 'submit' to submit a form, 'none' to skip the ok button or an event name to trigger that event on the dialog."%>
<%@ attribute name="on" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="options" description="Options to use for the dialog.  See http://jqueryui.com/demos/dialog/#options" %>
<%@ attribute name="arguments" required="false" type="java.lang.Object"%>
<%@ attribute name="argumentSeparator" required="false" type="java.lang.String"%>

<cti:includeScript link="/JavaScript/ajaxDialog.js"/>

<cti:msgScope paths=".${nameKey},components.dialog.${nameKey},components.dialog">
    <c:choose>
        <c:when test="${not empty pageScope.title}">
            <c:set var="titleMsg" value="${title}"/>
        </c:when>
        <c:when test="${not empty argumentSeparator}">
            <cti:msg2 var="titleMsg" key=".title" arguments="${arguments}" argumentSeparator="${argumentSeparator}"/>
        </c:when>
        <c:otherwise>
            <cti:msg2 var="titleMsg" key=".title" arguments="${arguments}"/>
        </c:otherwise>
    </c:choose>
    <cti:msg2 var="okBtnMsg" key=".ok"/>
    <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
    <cti:msg2 var="closeBtnMsg" key=".close"/>
</cti:msgScope>

<c:if test="${empty pageScope.id}">
    <cti:uniqueIdentifier var="id" prefix="inlineDialog"/>
</c:if>

<script type="text/javascript">
function open_${id}() {
    var dialogDiv = jQuery('#${id}');
    var buttons = [];
    
    <c:if test="${okEvent == 'none'}">
        buttons.push({'text' : '${closeBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
    </c:if>
    <c:if test="${okEvent != 'none'}">
        var okButton = {'text' : '${okBtnMsg}', 'class': 'primary action'};
        <c:if test="${!empty pageScope.on}">
        dialogDiv.data('on', '${pageScope.on}');
        </c:if>
        buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
        okButton.click = function() { dialogDiv.trigger('${okEvent}'); }
        buttons.push(okButton);
    </c:if>
    
    var dialogOpts = {
            'title' : '${titleMsg}',
            'position' : 'center',
            'width' : 'auto',
            'height' : 'auto',
            'modal' : false,
            'buttons' : buttons };
    <c:if test="${!empty pageScope.options}">
        jQuery.extend(dialogOpts, ${options});
    </c:if>
    dialogDiv.dialog(dialogOpts);
}
<c:if test="${!empty pageScope.on}">
jQuery(function() {
    jQuery(document).on('click', '${on}', function() {
        open_${id}();
    });
});
</c:if>
</script>

<div id="${id}" style="display: none">
    <cti:flashScopeMessages/>
    <jsp:doBody/>
</div>

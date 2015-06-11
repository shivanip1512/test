<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="title" description="The raw text to use for the title, if supplied it is overrides any i18n title that may be specified." %>
<%@ attribute name="id" description="The id of the dialog element. This is also used to name the function to open the dialog: 'open_\${id}'" %>
<%@ attribute name="okEvent" required="true" description="Use 'submit' to submit a form, 'none' to skip the ok button or an event name to trigger that event on the dialog." %>
<%@ attribute name="on" description="registers click event on the element with this CSS selector" %>
<%@ attribute name="options" description="Options to use for the dialog.  See http://jqueryui.com/demos/dialog/#options" %>
<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="classes" %>

<cti:includeScript link="/resources/js/pages/yukon.dialog.ajax.js"/>

<cti:msgScope paths=".${nameKey},components.dialog.${nameKey},components.dialog">
    <c:choose>
        <c:when test="${not empty pageScope.title}">
            <c:set var="titleMsg" value="${title}"/>
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
window['open_${id}'] = function () {
    
    var dialog = $('#${id}'),
        buttons = [],
        okEvent = '${okEvent}',
        on = '${pageScope.on}';
    
    if (okEvent === 'none') {
        buttons.push({'text' : '${closeBtnMsg}', 'click' : function() {$(this).dialog('close');}});
    } else {
        var okButton = {'text' : '${okBtnMsg}', 'class': 'primary action'};
        
        if (on != '') {
            dialog.data('on', '${pageScope.on}');
        }
        
        buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() {$(this).dialog('close');}});
        
        if (okEvent === 'submit') {
            okButton.click = function() {
                dialog.find('form').submit();
            };
        } else {
            okButton.click = function() {
                dialog.trigger('${okEvent}');
            };
        }
        buttons.push(okButton);
    }
    
    var defaults = {
            'title': '${titleMsg}',
            'width': 'auto',
            'height': 'auto',
            'modal': false,
            'buttons': buttons};
    
    <c:if test="${not empty pageScope.options}">
        $.extend(defaults, ${options});
    </c:if>
    dialog.dialog(defaults);
};
if ('${pageScope.on}' != '') {
    $(function() {
        $(document).on('click', '${on}', function() {
            window['open_${id}']();
        });
    });
}
</script>

<div id="${id}" class="dn ${pageScope.classes}">
    <cti:flashScopeMessages/>
    <jsp:doBody/>
</div>

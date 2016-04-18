<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="on" description="Registers a click event on the element with this CSS selector to open the popup." %>
<%@ attribute name="onClose" %>
<%@ attribute name="options" description="JQUI dialog options. See http://api.jqueryui.com/dialog/" %>
<%@ attribute name="showImmediately" %>
<%@ attribute name="style" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="title" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<div id="${id}" class="${pageScope.styleClass} dn" style="${pageScope.style}" role="dialog">
    <jsp:doBody/>
</div>
<script type="text/javascript">
var defaults = { width: 'auto',
             autoOpen: false,
             modal: true,
             title: '${cti:escapeJavaScript(title)}'
};

<c:if test="${not empty pageScope.options}">
$.extend(defaults, ${options});
</c:if>

<c:if test="${not empty pageScope.showImmediately and showImmediately == true}">
defaults.autoOpen = true;
</c:if>

<c:if test="${not empty pageScope.onClose}">
defaults.close = function(event, ui) {${onClose}};
</c:if>
$('#${id}').dialog(defaults);

<c:if test="${not empty pageScope.on}">
$(function() {
    $('${pageScope.on}').click(function(event) {
        $('#${id}').dialog('open');
    });
});
</c:if>
</script>
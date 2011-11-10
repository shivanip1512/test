<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="onClose" required="false" type="java.lang.String"%>
<%@ attribute name="on" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="showImmediately" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty pageScope.onClose}">
    <c:set var="onClose" value="$('${id}').hide();"/>
</c:if>
<div id="${id}" class="popUpDiv simplePopup ${pageScope.styleClass}" <c:if test="${not pageScope.showImmediately}">style="display:none;"</c:if>>
<div class="titledContainer boxContainer ${pageScope.styleClass}">
    <div class="titleBar boxContainer_titleBar">
        <div class="controls" onclick="${pageScope.onClose}"><img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif"></div>
        <div id="${id}_title" class="title boxContainer_title">${title}</div>
    </div>
    <div id="${id}_body" class="content boxContainer_content"><jsp:doBody/></div>
</div>
</div>
<c:if test="${!empty pageScope.on}">
<script type="text/javascript">
YEvent.observeSelectorClick('${pageScope.on}', function() {
    $('${id}').show();
});
</script>
</c:if>
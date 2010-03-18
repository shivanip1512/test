<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="titleKey" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="onClose" required="false" type="java.lang.String"%>
<%@ attribute name="arguments" required="false" type="java.lang.Object"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<c:if test="${empty pageScope.onClose}">
    <c:set var="onClose" value="$('${id}').hide();"/>
</c:if>

<div id="${id}" class="popUpDiv simplePopup ${pageScope.styleClass}" style="display:none;">
<!--  fix for IE6 bug (see YukonGeneralStyles.css ".simplePopup iframe" for more info) -->
<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
<div class="titledContainer boxContainer ${pageScope.styleClass}">

    <div class="titleBar boxContainer_titleBar">
              <div class="controls" onclick="${pageScope.onClose}">
                <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
              </div>
        <div id="${id}_title" class="title boxContainer_title">
            <i:inline key="${titleKey}" arguments="${arguments}"/>
        </div>
    </div>
    
    <div id="${id}_body" class="content boxContainer_content">
        <jsp:doBody/>
    </div>    
</div>
</div>
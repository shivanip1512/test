<%@ attribute name="paoId" required="true" type="java.lang.Integer" %>
<%@ attribute name="includeText" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:dynamicChoose updaterString="DR_FAVORITE/${paoId}" suffix="${paoId}">
    <tags:dynamicChooseOption optionId="true">
        <a href="javascript:void(0)" onclick="removeFavorite(${paoId})" class="simpleLink">
            <cti:logo key="yukon.web.modules.dr.actions.removeFavoriteIcon"/>
            <c:if test="${includeText}">
                <cti:msg key="yukon.web.modules.dr.actions.removeFavorite"/>
            </c:if>
        </a>
    </tags:dynamicChooseOption>
    <tags:dynamicChooseOption optionId="false">
        <a href="javascript:void(0)" onclick="addFavorite(${paoId})" class="simpleLink">
            <cti:logo key="yukon.web.modules.dr.actions.addFavoriteIcon"/>
            <c:if test="${includeText}">
                <cti:msg key="yukon.web.modules.dr.actions.addFavorite"/>
            </c:if>
        </a>
    </tags:dynamicChooseOption>
</tags:dynamicChoose>

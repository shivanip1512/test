<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fancyTree" tagdir="/WEB-INF/tags/fancyTree" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

    <div>
        <cti:url var="moveUrl" value="/capcontrol/command/bankMove"/>
        <form:form modelAttribute="bankMoveBean" action="${moveUrl}">
            <cti:csrfToken/>
            <input type="hidden" name="substationId" value="${substationId}">
            <input type="hidden" name="selectedFeeder" value="${oldFeederId}" id="selectedFeeder">
            <form:hidden path="bankId"/>
            <form:hidden path="oldFeederId"/>
            <form:hidden path="newFeederId" id="newFeederId"/>
            
            <tags:nameValueContainer2 tableClass="stacked">
                <tags:nameValue2 nameKey=".capbank">
                    <span>${fn:escapeXml(bankName)}</span>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".currentLocation">
                    <span>${fn:escapeXml(path)}</span>
                </tags:nameValue2>
            </tags:nameValueContainer2>
    
            <h3><i:inline key=".feedersContainer.title"/></h3>
    
            <fancyTree:inlineTree id="feederId" maxHeight="250" styleClass="stacked" dataJson="${areaJson}" 
                includeControlBar="true" treeParameters='{"minExpandLevel": "1", "icon": false}'/>
    
            <div id="controlOrders"></div>
            
            <div class="action-area">
                <c:if test="${!isIVVC}">
                    <cti:button nameKey="tempMove" type="submit" name="tempMove"/>
                </c:if>
                <cti:button nameKey="move" type="submit" name="move"/>
                <span><i:inline key=".controlOrder"/>&nbsp;<form:input path="displayOrder" size="1" maxlength="3"/></span>
                <span><i:inline key=".closeOrder"/>&nbsp;<form:input path="closeOrder" size="1" maxlength="3"/></span>
                <span><i:inline key=".tripOrder"/>&nbsp;<form:input path="tripOrder" size="1" maxlength="3"/></span>
            </div>
        </form:form>
    </div>
</cti:msgScope>
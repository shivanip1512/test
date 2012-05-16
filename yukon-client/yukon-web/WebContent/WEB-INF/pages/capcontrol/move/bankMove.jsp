<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

<cti:url var="controlOrderPage" value="/spring/capcontrol/move/feederBankInfo"/>

<script type="text/javascript"> 
updateFeederBankInfo = function () {
    var params = {'feederId': $F("selectedFeeder")};
    new Ajax.Updater('controlOrders', '${controlOrderPage}', {method: 'post', parameters: params});
}

selectFeeder = function (fid) {
    $("selectedFeeder").value = fid;
    updateFeederBankInfo();
}
</script>

<div style="padding: 5px;">
    <form:form commandName="bankMoveBean" action="/spring/capcontrol/command/bankMove">
        <input type="hidden" name="oneline" value="${oneline}">
        <input type="hidden" name="substationId" value="${substationId}">
        <input type="hidden" name="selectedFeeder" value="${oldFeederId}" id="selectedFeeder">
        <form:hidden path="bankId"/>
        <form:hidden path="oldFeederId"/>
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".capbank">
                <span style="font-size: 12px;"><spring:escapeBody htmlEscape="true">${bankName}</spring:escapeBody></span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".currentLocation">
                <span style="font-size: 12px;"><spring:escapeBody htmlEscape="true">${path}</spring:escapeBody></span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    
        <tags:boxContainer2 nameKey="feedersContainer" hideEnabled="false" styleClass="padBottom bankMoveContainer">
            <ul class="bankMoveHierarchy">
                <c:forEach var="area" items="${allAreas}">
                    <li class="toggle plus"><span><spring:escapeBody htmlEscape="true">${area.name}</spring:escapeBody></span>
                        <ul style="display:none">
                            <c:forEach var="station" items="${area.substations}">
                                <li class="toggle plus"><span><spring:escapeBody htmlEscape="true">${station.name}</spring:escapeBody></span>
                                    <ul style="display:none">
                                        <c:forEach var="bus" items="${station.substationBuses}">
                                            <li class="toggle plus"><span><spring:escapeBody htmlEscape="true">${bus.name}</spring:escapeBody></span>
                                                <ul style="display:none">
                                                    <c:forEach var="feeder" items="${bus.feeders}">
                                                        <c:if test="${!(feeder.id == oldFeederId)}">
                                                            <li class="radio"><form:radiobutton path="newFeederId" onclick="selectFeeder(${feeder.id});" label="${feeder.name}" value="${feeder.id}"/></li>
                                                        </c:if>
                                                    </c:forEach>
                                                </ul>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </li>
                            </c:forEach>
                        </ul>
                    </li>
                </c:forEach>
            </ul>
        </tags:boxContainer2>

        <div id="controlOrders" class="padBottom clear"></div>
        <div style="float:left;">
            <span><i:inline key=".displayOrder"/><form:input path="displayOrder" size="1" maxlength="3" cssStyle="margin-left:3px;"/></span>
            <span><i:inline key=".closeOrder"/><form:input path="closeOrder" size="1" maxlength="3"  cssStyle="margin-left:3px;"/></span>
            <span><i:inline key=".tripOrder"/><form:input path="tripOrder" size="1" maxlength="3"  cssStyle="margin-left:3px;"/></span>
        </div>
        <div style="float:right;">
            <cti:button nameKey="tempMove" type="submit" name="tempMove"/>
            <cti:button nameKey="move" type="submit" name="move"/>
        </div>
    </form:form>
</div>
<script type="text/javascript">
updateFeederBankInfo();
</script>
</cti:msgScope>
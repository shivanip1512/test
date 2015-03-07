<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.config.new">
<cti:includeScript link="/JavaScript/yukon.assets.config.new.js"/>

<cti:msgScope paths=",yukon.common">

<div class="stacked-md">
    <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
</div>

<c:if test="${fn:length(configs) > 1}">
<table class="stacked-md">
    <tr>
        <td><span class="label label-warning"><i:inline key="yukon.common.warning"/></span></td>
        <td><i:inline key=".multiConfigWarning"/></td>
    </tr>
</table>
</c:if>

<form:form commandName="settings" id="config-form" action="send">
    <cti:csrfToken/>
    
    <div class="dn js-inventory-params">
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
    </div>
    
    <tags:nameValueContainer2 tableClass="with-form-controls stacked-md">
    
        <tags:nameValue2 nameKey=".configType">
            <c:choose>
                <c:when test="${fn:length(configs) == 1}">
                    <i:inline key="${settings.config.type}"/>
                    <form:hidden cssClass="js-config-type" path="config.type"/>
                </c:when>
                <c:otherwise>
                    <%-- Multiple configs, user needs to pick one. --%>
                    <form:select path="config.type" cssClass="js-config-type">
                        <c:forEach var="type" items="${configs}">
                            <form:option value="${type}"><i:inline key="${type}"/></form:option>
                        </c:forEach>
                    </form:select>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
        
        <c:if test="${showRoute}">
            <tags:nameValue2 nameKey=".route">
                <tags:switchButton path="specificRoute" onNameKey=".route.specific" offNameKey=".route.device" 
                    color="false" toggleGroup="specific-route" offClasses="M0"/>
                <select data-toggle-group="specific-route" disabled>
                    <c:choose>
                        <c:when test="${not empty defaultRoute}">
                            <option value="0">
                                <cti:msg2 key="yukon.common.route.default" argument="${defaultRoute.paoName}"/>
                            </option>
                        </c:when>
                        <c:otherwise>
                            <option value="0"><cti:msg2 key="yukon.common.route.default.none"/></option>
                        </c:otherwise>
                    </c:choose>
                    <c:forEach var="route" items="${routes}">
                        <option value="${route.liteID}">${fn:escapeXml(route.paoName)}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
        </c:if>
        
        <tags:nameValue2 excludeColon="true">
            <label><form:checkbox path="inService"/><i:inline key=".inService"/></label>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <dr:hardwareAddressingInfo type="${settings.config.type}" path="config."/>
    
    <div class="page-action-area">
        <cti:button nameKey="start" classes="action primary" type="submit"/>
        <cti:button nameKey="cancel"/>
    </div>
</form:form>

</cti:msgScope>
</cti:standardPage>
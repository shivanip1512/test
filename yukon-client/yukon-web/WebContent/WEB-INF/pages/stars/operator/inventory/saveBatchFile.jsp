<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="saveToFile">
<cti:includeScript link="/resources/js/pages/yukon.assets.config.batch.js"/>

<div class="stacked-md" data-single-config="${singleConfigType}">
    <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
</div>
<table class="stacked-md">
    <tr>
        <td><span class="label label-warning"><i:inline key="yukon.common.warning"/></span></td>
        <td><i:inline key=".instructions"/></td>
    </tr>
    <c:if test="${empty task && !uniformHardwareConfigType}">
        <tr>
            <td><span class="label label-warning"><i:inline key="yukon.common.warning"/></span></td>
            <td>
                <i:inline key=".multipleHardwareConfigTypes"/>
                <i:inline key=".enforceSingleHardwareConfigType"/>
            </td>
        </tr>
    </c:if>
</table>

<c:if test="${empty task}">
    <form:form action="do" modelAttribute="saveToBatchInfo">
        <cti:csrfToken/>
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        <form:hidden path="ecDefaultRoute"/>
        <div class="clearfix">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".route">
                    <div class="js-routes">
                        <div class="stacked" id="currentRoute">
                            <form:radiobutton id="useCurrentRoutes" path="useRoutes" value="current"/>
                            <label for="useCurrentRoutes">
                                <i:inline key=".useCurrentRoutes"/>
                            </label>
                        </div>
                        <div class="stacked" id="defaultRoute">
                            <form:radiobutton id="useDefaultRoute" path="useRoutes" value="default"/>
                            <label for="useDefaultRoute">
                                <i:inline key=".useDefaultRoute"/>${fn:escapeXml(ecDefaultRoute)} 
                            </label>
                        </div>
                        <div class="stacked" id="newRoute">
                            <form:radiobutton id="useNewRoute" path="useRoutes" value="new"/>
                            <label for="useNewRoute">
                                <i:inline key=".selectRoute"/>
                            </label>
                            <form:select path="routeId" id="routeId">
                                <c:forEach var="route" items="${routes}">
                                    <form:option value="${route.paoIdentifier.paoId}">${fn:escapeXml(route.paoName)}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".group">
                    <div class="js-groups"> 
                        <div class="stacked">
                            <form:radiobutton id="useCurrentGroup" path="useGroups" value="current"/>
                            <label for="useCurrentGroup">
                                <i:inline key=".useCurrentConfiguration"/>
                            </label>
                        </div>
                        <div class="stacked">
                            <form:radiobutton id="useNewGroup" path="useGroups" value="new"/>
                            <label for="useNewGroup">
                                <i:inline key=".selectGroup"/>
                            </label>
                            <form:hidden path="groupId" id="groupId"/>
                            <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker"
                                               id="groupPicker"
                                               buttonStyleClass="M0"
                                               linkType="selection"
                                               multiSelectMode="false"
                                               immediateSelectMode="true"
                                               selectionProperty="paoName"
                                               destinationFieldId="groupId"/>
                        </div>
                    </div>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="page-action-area">
            <cti:button nameKey="save" type="submit" name="save" classes="action primary"/>
            <cti:button nameKey="cancel" type="submit" name="cancel"/>
        </div>
    </form:form>
</c:if>
<c:if test="${not empty task}">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".progress">
            <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="page-action-area">
        <cti:url var="url" value="/stars/operator/inventory/home"/>
        <a href="${url}"><i:inline key=".inventoryHome"/></a>
    </div>
</c:if>

</cti:standardPage>
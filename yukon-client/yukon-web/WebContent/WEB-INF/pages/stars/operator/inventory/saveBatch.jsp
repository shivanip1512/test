<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="operator" page="saveToBatch">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <script type="text/javascript">
        jQuery(document).ready(function() {
        	var singleHwConfigType = ${uniformHardwareConfigType};
        	if(${!uniformHardwareConfigType}) {
        		jQuery('#groupId').attr("disabled","disabled");
        	}
        });
    </script>
    
    <tags:boxContainer2 nameKey="something">
        <div class="containerHeader">
            <table>
                <tr>
                    <td valign="top" colspan="2">
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </td>
                </tr>
                <tr>
                    <td class="smallBoldLabel errorMessage"><i:inline key=".instructionsLabel"/></td>
                    <td><i:inline key=".instructions"/></td>
                </tr>
                <c:if test="${empty task}">
                    <c:if test="${!uniformHardwareConfigType}">
                        <tr>
                            <td class="smallBoldLabel errorMessage"><i:inline key=".note"/></td>
                            <td><i:inline key=".multipleHardwareConfigTypes"/></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td><i:inline key=".enforceSingleHardwareConfigType"/></td>
                        </tr>
                    </c:if>
                </c:if>
            </table>
        </div>
        
        <c:if test="${empty task}">
            <form:form action="do" commandName="saveToBatchInfo">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <div class="formElementContainer">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".route">
                            <form:select path="routeId" id="routeId">
                                <form:option value="${useCurrentRoutes}"></form:option>
                                <c:choose>
                                    <c:when test="${ecDefaultRoute == 'none'}">
                                        <form:option value="${yukonDefaultRoute}"><i:inline key=".noDefaultRoute"/></form:option>
                                    </c:when>
                                    <c:otherwise>
                                        <form:option value="${ecDefaultRoute.liteID}"><i:inline key=".ecDefaultRoute"/>${fn:escapeXml(ecDefaultRoute.paoName)}</form:option>
                                    </c:otherwise>
                                </c:choose>
                                <c:forEach var="route" items="${routes}">
                                    <form:option value="${route.paoIdentifier.paoId}">${fn:escapeXml(route.paoName)}</form:option>
                                </c:forEach>
                            </form:select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".group">
                            <form:select path="groupId" id="groupId" class="saveToBatchDropdown">
                                <form:option value="${useCurrentGroups}"><i:inline key=".useCurrentConfiguration"/></form:option>
                                <c:forEach var="group" items="${groups}">
                                    <form:option value="${group.paoIdentifier.paoId}">${fn:escapeXml(group.name)}</form:option>
                                </c:forEach>
                            </form:select>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <cti:button nameKey="cancel" type="submit" name="cancel"/>
                <cti:button nameKey="start" type="submit" name="start"/>
            </form:form>
        </c:if>
        <c:if test="${not empty task}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".progress">
                    <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <br>
            <a href="/spring/stars/operator/inventory/home"><i:inline key=".inventoryHome"/></a>
        </c:if>
    </tags:boxContainer2>
</cti:standardPage>
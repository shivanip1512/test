<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="commander.command">
<style type="text/css">
    .secondary-menu {
        margin: -15px auto 15px auto;
        width: 98%;
        padding: 2px 5px;
}
</style>

<c:set var="isDevicesPage" value='${category == "MCT" || category == "IED" || category == "RTU" || category == "TRANSMITTER"}'/>
<c:set var="isCapCtrlPage" value='${category == "CAP"}'/>
<c:set var="isLoadMngtPage" value='${not (isDevicesPage or isCapCtrlPage)}'/>
<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.menu.config.commander.select.devices" 
                                   initiallySelected="${isDevicesPage}">
        <c:url value="/commander/select?category=MCT" />
    </cti:linkTab>
    <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.menu.config.commander.select.lm" 
                                    initiallySelected="${isLoadMngtPage}">
        <c:url value="/commander/select?category=LMGROUP" />
    </cti:linkTab>
    <cti:linkTab tabId="capControlTab" selectorKey="yukon.web.menu.config.commander.select.capcontrol"
                                    initiallySelected="${isCapCtrlPage}">
        <c:url value="/commander/select?category=CAP" />
    </cti:linkTab>
</cti:linkTabbedContainer>

<%-- START Secondary Menu --%>
<c:if test="${isDevicesPage}">
    <div id="menuL2Devices" class="secondary-menu">
        <c:url var="tab_url" value="/commander/select" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.mct" showPlainText='${category == "MCT"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=IED" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.ied" showPlainText='${category == "IED"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=RTU" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.rtu" showPlainText='${category == "RTU"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=TRANSMITTER" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.transmitter" showPlainText='${category == "TRANSMITTER"}' href="${tab_url}" />
    </div>
</c:if>
<c:if test="${isLoadMngtPage}">
    <div id="menuL2LoadMgt" class="secondary-menu">
        <c:url var="tab_url" value="/commander/select?category=LMGROUP" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.group" showPlainText='${category == "LMGROUP"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/command/xcom" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.xcom.tab.title" showPlainText='${serialType == "xcom"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/command/vcom" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.vcom.tab.title" showPlainText='${serialType == "vcom"}' href="${tab_url}" />
    </div>
</c:if>
<%-- END Secondary Menu --%>

    <div class="column-16-8">
        <div class="column one">
            <cti:url var="commandFormUrl" value="/servlet/CommanderServlet"/>
            <form name="commandForm" method="POST" action="${commandFormUrl}">
                <cti:csrfToken/>
                <input type="hidden" name="deviceID" value="${deviceId}">
                <input type="hidden" name="timeOut" value="8000">
                <input id="redirect" type="hidden" name="REDIRECT" value="${redirectURI}">
                <input id="referrer" type="hidden" name="REFERRER" value="${redirectURI}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceName">
                        <c:choose>
                            <c:when test="${meterDetailDisplayable}"><cti:paoDetailUrl yukonPao="${lPao}">${fn:escapeXml(deviceName)}</cti:paoDetailUrl></c:when>
                            <c:otherwise>${fn:escapeXml(deviceName)}</c:otherwise>
                        </c:choose>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".deviceType">
                       <c:if test="${not empty lPao}"><cti:paoTypeIcon yukonPao="${lPao}"/>&nbsp;</c:if>${deviceType}
                    </tags:nameValue2>
                    <c:if test="${not empty serialType}">
                        <tags:nameValue2 nameKey=".serialNumber">
                            <input type="text" name="serialNumber" size="20" value="${fn:escapeXml(serialNumber)}">
                        </tags:nameValue2>
                    </c:if>
                    <c:if test="${not empty availableRoutes}">
                        <tags:nameValue2 nameKey=".route">
                            <select id="routeID" name="routeID">
                                <option VALUE="-1"><cti:msg2 key=".route.select"/>
                                <c:forEach var="route" items="${availableRoutes}">
                                    <option value="${route.yukonID}">${fn:escapeXml(route.paoName)}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                    </c:if>
                    <c:if test="${not empty availableCommands}">
                        <tags:nameValue2 nameKey=".availableCommands">
                            <tags:commanderPrompter/>
                            <select name="commonCommand" class="f-loadCommanderCommand" data-cmdfield="command" style="max-width: 300px;">
                                <option value=""><cti:msg2 key=".availableCommands.select"/></option>
                                <c:forEach var="command" items="${availableCommands}">
                                    <option value="${command.key}">${command.value}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                    </c:if>
                    <tags:nameValue2 nameKey=".executeCommand">
                        <input type="text" id="command" name="command" size="40" <cti:isPropertyFalse property="EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> value="${fn:escapeXml(currentCommand)}">
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="page-action-area stacked">
                    <cti:button nameKey="execute" type="submit" name="execute" classes="primary action"/>
                    <cti:button nameKey="clear" type="submit" name="clearText"/>
                    <cti:button nameKey="refresh" name="refresh" onclick="window.location.reload()"/>
                </div>
                <div id="command_results" class="lite-container stacked code scroll-large" style="min-height: 200px;">
                    <div class="console"><h4><i:inline key="yukon.common.console"/></h4></div>
                    <c:out value="${resultText}" escapeXml="false"/>
                </div>
            </form>
        </div>
        <div class="column two nogutter">
            <tags:boxContainer2 nameKey="sideMenu">
                <!-- START > Devices section -->
                <h3><i:inline key="yukon.web.modules.tools.commander.sideMenu.devices"/></h3>
                <ul>
                    <c:forEach var="device" items="${deviceHistory}">
                        <c:if test="${cti:isDeviceSortByGroup(device)}">
                            <li>
                                <c:choose>
                                    <c:when test="${device.yukonID == deviceId}">${fn:escapeXml(device.paoName)}</c:when>
                                    <c:otherwise>
                                        <cti:url var="commandUrl" value="/commander/command">
                                            <cti:param name="deviceId" value="${device.yukonID}"/>
                                        </cti:url>
                                        <a title="${device.paoIdentifier}" href="${commandUrl}">${fn:escapeXml(device.paoName)}</a>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
                <hr class="divider" />
                
                <!-- START > Load Management section -->
                <h3><i:inline key="yukon.web.modules.tools.commander.sideMenu.loadManagement"/></h3>
                <c:forEach var="device" items="${deviceHistory}">
                    <c:if test="${cti:isLoadManagementSortByGroup(device)}">
                        <ul>
                            <c:choose>
                                <c:when test="${device.yukonID == deviceId}"><li>${fn:escapeXml(device.paoName)}</li></c:when>
                                <c:otherwise>
                                    <cti:url var="commandUrl" value="/commander/command">
                                        <cti:param name="deviceId" value="${device.yukonID}"/>
                                    </cti:url>
                                    <li><a href="${commandUrl}" class="Link1">${fn:escapeXml(device.paoName)}</a></li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </c:if>
                </c:forEach>
                        
    <!-- START > DCU-205 Serial section -->
            <cti:checkRolesAndProperties value="DCU_SA205_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'sa205' && empty serialNumber}">
                        <div class="sideMenuLink selected"><i:inline key="yukon.web.modules.tools.commander.sideMenu.sa205"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="<cti:url value="/commander/command/sa205"/>" class="Link1"><i:inline key="yukon.web.modules.tools.commander.sideMenu.sa205"/></a>
                        </div>
                    </c:otherwise>
                </c:choose>
                        <ul>
                <c:forEach var="serial" items="${sa205History}">
                    <c:choose>
                        <c:when test="${serialType == 'sa205' && serial == serialNumber}">
                            <li>${serial}</li>
                        </c:when>
                        <c:otherwise>
                            <cti:url var="commandUrl" value="/commander/command/sa205">
                                <cti:param name="serialNumber" value="${serial}"/>
                            </cti:url>
                            <li><a href="${commandUrl}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkRolesAndProperties>
    <!-- END > DCU-205 Serial section -->
    
    <!-- START > DCU-305 Serial section -->
            <cti:checkRolesAndProperties value="DCU_SA305_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'sa305' && empty serialNumber}">
                        <div class="sideMenuLink selected"><i:inline key="yukon.web.modules.tools.commander.sideMenu.sa305"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="<cti:url value="/commander/command/sa305"/>" class="Link1"><i:inline key="yukon.web.modules.tools.commander.sideMenu.sa305"/></a>
                        </div>
                    </c:otherwise>
                </c:choose>
                        <ul>
                <c:forEach var="serial" items="${sa305History}">
                    <c:choose>
                        <c:when test="${serialType == 'sa305' && serial == serialNumber}">
                            <li>${serial}</li>
                        </c:when>
                        <c:otherwise>
                            <cti:url var="commandUrl" value="/commander/command/sa305">
                                <cti:param name="serialNumber" value="${serial}"/>
                            </cti:url>
                            <li><a href="${commandUrl}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkRolesAndProperties>
    <!-- END >DCU-305 Serial section -->
    
    <!-- START > Expresscom Serial section -->
            <cti:checkRolesAndProperties value="EXPRESSCOM_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'xcom' && empty serialNumber}">
                        <div class="sideMenuLink selected"><i:inline key="yukon.web.modules.tools.commander.sideMenu.xcom"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="<cti:url value="/commander/command/xcom"/>" class="Link1"><i:inline key="yukon.web.modules.tools.commander.sideMenu.xcom"/></a>
                        </div>
                    </c:otherwise>
                </c:choose>
                        <ul>
                <c:forEach var="serial" items="${xcomHistory}">
                    <c:choose>
                        <c:when test="${serialType == 'xcom' && serial == serialNumber}">
                            <li>${serial}</li>
                        </c:when>
                        <c:otherwise>
                            <cti:url var="commandUrl" value="/commander/command/xcom">
                                <cti:param name="serialNumber" value="${serial}"/>
                            </cti:url>
                            <li><a href="${commandUrl}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkRolesAndProperties>
    <!-- END > Expresscom Serial section -->
    
                <!-- START > Versacom Serial section -->
                <cti:checkRolesAndProperties value="VERSACOM_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'vcom' && empty serialNumber}">
                        <div class="sideMenuLink selected"><i:inline key="yukon.web.modules.tools.commander.sideMenu.vcom"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="<cti:url value="/commander/command/vcom"/>" class="Link1"><i:inline key="yukon.web.modules.tools.commander.sideMenu.vcom"/></a>
                        </div>
                    </c:otherwise>
                </c:choose>
                        <ul>
                <c:forEach var="serial" items="${vcomHistory}">
                    <c:choose>
                        <c:when test="${serialType == 'vcom' && serial == serialNumber}">
                            <li>${serial}</li>
                        </c:when>
                        <c:otherwise>
                            <cti:url var="commandUrl" value="/commander/command/vcom">
                                <cti:param name="serialNumber" value="${serial}"/>
                            </cti:url>
                            <li><a href="${commandUrl}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkRolesAndProperties>
            <!-- END > Load Management section -->
    
                <hr class="divider">
    
                <!-- START > CAP Control section -->
                <h3><i:inline key="yukon.web.modules.tools.commander.sideMenu.capControl"/></h3>
                <ul>
                    <c:forEach var="device" items="${deviceHistory}">
                <c:if test="${cti:isCapControlSortByGroup(device)}">
                        <li>
                    <c:choose>
                        <c:when test="${device.yukonID == deviceId}">
                            ${fn:escapeXml(device.paoName)}
                        </c:when>
                        <c:otherwise>
                            <cti:url var="commandUrl" value="/commander/command">
                                <cti:param name="deviceId" value="${device.yukonID}"/>
                            </cti:url>
                                    <a title="${device.paoIdentifier}" href="${commandUrl}">${fn:escapeXml(device.paoName)}</a>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
            </tags:boxContainer2>
        </div>
    </div>
</cti:standardPage>
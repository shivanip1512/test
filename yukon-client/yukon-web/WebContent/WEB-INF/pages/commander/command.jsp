<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="commanderSelect" page="command">
    <cti:standardMenu menuSelection="${menuSelection}" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
        
        <%-- bulk home --%>
        <cti:crumbLink><i:inline key=".pageTitle"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><i:inline key=".pageTitle"/></h2>
    <br>
    
    <script language="JavaScript">
    function disableButton(x) {
        x.disabled = true;
        document.commandForm.submit();
    }
    </script>
    
    <table class="compactResultsTable">
        <tr>
            <td>
                <tags:boxContainer2 nameKey="commander">
                    <form name="commandForm" method="POST" action="/servlet/CommanderServlet">
                            <input type="hidden" name="deviceID" value="${deviceId}">
                            <input type="hidden" name="timeOut" value="8000">
                            <input id="redirect" type="hidden" name="REDIRECT" value="${redirectURI}">
                            <input id="referrer" type="hidden" name="REFERRER" value="${redirectURI}">
                            
                            <table class="nameValueTable">
                                <tr> 
                                    <td class="name"><i:inline key=".deviceName"/></td>
                                    <td class="value">
                                        <c:choose>
                                            <c:when test="${meterDetailDisplayable}"><cti:paoDetailUrl yukonPao="${lPao}">${deviceName}</cti:paoDetailUrl></c:when>
                                            <c:otherwise>${deviceName}</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr> 
                                    <td class="name"><i:inline key=".deviceType"/></td>
                                    <td class="value"><c:if test="${not empty lPao}"><cti:paoTypeIcon yukonPao="${lPao}"/>&nbsp;</c:if>${deviceType}</td>
                                </tr>
                        <c:if test="${not empty serialType}">
                                <tr> 
                                    <td class="name"><i:inline key=".serialNumber"/></td>
                                    <td class="value">
                                        <input type="text" name="serialNumber" size="20" value="${serialNumber}">
                                    </td>
                                </tr>
                            <c:if test="${not empty availableRoutes}">
                                <tr> 
                                    <td class="name"><i:inline key=".route"/></td>
                                    <td class="value"> 
                                        <select id="routeID" name="routeID">
                                            <option VALUE="-1"><cti:msg2 key=".route.select"/>
                                        <c:forEach var="route" items="${availableRoutes}">
                                            <option value="${route.yukonID}">${route.paoName}</option>
                                        </c:forEach>
                                        </select>
                                    </td>
                                </tr>
                            </c:if>
                        </c:if>
                            <c:if test="${not empty availableCommands}">
                                <tr> 
                                    <td class="name"><i:inline key=".availableCommands"/></td>
                                    <td class="value">
                                        <tags:commanderPrompter/>
                                        <select name="commonCommand" class="f_loadCommanderCommand" data-cmdfield="command">
                                            <option value=""><cti:msg2 key=".availableCommands.select"/></option>
                                            
                                        <c:forEach var="command" items="${availableCommands}">
                                            <option value="${command.key}">${command.value}</option>
                                        </c:forEach>
                                            
                                        </select>
                                    </td>
                                </tr>
                            </c:if>
                                <tr> 
                                    <td class="name"><i:inline key=".executeCommand"/></td>
                                    <td class="value">
                                        <input type="text" id="command" name="command" size="40" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> value="${currentCommand}">
                                        <cti:msg2 var="executeButtonLabel" key=".executeButton"/>
                                        <input type="submit" name="execute" value="${executeButtonLabel}" onClick="disableButton(this)">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="3" style="padding-top:20px;">
                                    <div class="scroll">
                                        <c:out value="${resultText}" escapeXml="false"/>
                                    </div>
                                    <br>
                                    <cti:msg2 var="clearResultsButtonLabel" key=".clearResultsButton"/>
                                    <cti:msg2 var="refreshButtonLabel" key=".refreshButton"/>
                                    <input type="submit" name="clearText" value="${clearResultsButtonLabel}">
                                    <input type="reset" name="refresh" value="${refreshButtonLabel}" onClick="window.location.reload()">
                                    </td>
                                </tr>
                            </table>
                    </form>
                </tags:boxContainer2>
            </td>
            
            <td>
                <tags:boxContainer2 nameKey="sideMenu">
<!-- START > Devices section -->
                    <div class="header"><i:inline key="yukon.web.modules.commanderSelect.sideMenu.devices"/></div>
                    <ul>
            <c:forEach var="device" items="${deviceHistory}">
                <c:if test="${cti:isDeviceSortByGroup(device)}">
                        <li>
                    <c:choose>
                        <c:when test="${device.yukonID == deviceId}">
                            <cti:paoName paoId="${device.yukonID}"/>
                        </c:when>
                        <c:otherwise>
                            <a title="${device.paoIdentifier}" href="/commander/command?deviceId=${device.yukonID}"><cti:paoName paoId="${device.yukonID}"/></a>
                        </c:otherwise>
                    </c:choose>
                        </li>
                </c:if>
            </c:forEach>
                    </ul>
<!-- END > Devices section -->

                    <div class="horizontalRule"/>

<!-- START > Load Management section -->
                    <div class="header"><i:inline key="yukon.web.modules.commanderSelect.sideMenu.loadManagement"/></div>
                    
            <c:forEach var="device" items="${deviceHistory}">
                <c:if test="${cti:isLoadManagementSortByGroup(device)}">
                        <ul>
                    <c:choose>
                        <c:when test="${device.yukonID == deviceId}">
                            <li><cti:paoName paoId="${device.yukonID}"/></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="/commander/command?deviceId=${device.yukonID}" class="Link1"><cti:paoName paoId="${device.yukonID}"/></a></li>
                        </c:otherwise>
                    </c:choose>
                        </ul>
                </c:if>
            </c:forEach>
                    
    <!-- START > DCU-205 Serial section -->
            <cti:checkProperty property="CommanderRole.DCU_SA205_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'sa205' && empty serialNumber}">
                        <div class="sideMenuLink selected">DCU-205 Serial</div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="/commander/command/sa205" class="Link1">DCU-205 Serial</a>
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
                            <li><a href="/commander/command/sa205?serialNumber=${serial}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkProperty>
    <!-- END > DCU-205 Serial section -->

    <!-- START > DCU-305 Serial section -->
            <cti:checkProperty property="CommanderRole.DCU_SA305_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'sa305' && empty serialNumber}">
                        <div class="sideMenuLink selected">DCU-305 Serial</div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="/commander/command/sa305" class="Link1">DCU-305 Serial</a>
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
                            <li><a href="/commander/command/sa305?serialNumber=${serial}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkProperty>
    <!-- END >DCU-305 Serial section -->

    <!-- START > Expresscom Serial section -->
            <cti:checkProperty property="CommanderRole.EXPRESSCOM_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'xcom' && empty serialNumber}">
                        <div class="sideMenuLink selected">Expresscom Serial</div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="/commander/command/xcom" class="Link1">Expresscom Serial</a>
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
                            <li><a href="/commander/command/xcom?serialNumber=${serial}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkProperty>
    <!-- END > Expresscom Serial section -->

    <!-- START > Versacom Serial section -->
            <cti:checkProperty property="CommanderRole.VERSACOM_SERIAL_MODEL">
                <c:choose>
                    <c:when test="${serialType == 'vcom' && empty serialNumber}">
                        <div class="sideMenuLink selected">Versacom Serial</div>
                    </c:when>
                    <c:otherwise>
                        <div class="sideMenuLink">
                            <a href="/commander/command/vcom" class="Link1">Versacom Serial</a>
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
                            <li><a href="/commander/command/vcom?serialNumber=${serial}">${serial}</a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                        </ul>
            </cti:checkProperty>
    <!-- END > Versacom Serial section -->
<!-- END > Load Management section -->

                    <div class="horizontalRule"/>

<!-- START > CAP Control section -->
                    <div class="header"><i:inline key="yukon.web.modules.commanderSelect.sideMenu.capControl"/></div>
                    <ul>
            <c:forEach var="device" items="${deviceHistory}">
                <c:if test="${cti:isCapControlSortByGroup(device)}">
                        <li>
                    <c:choose>
                        <c:when test="${device.yukonID == deviceId}">
                            <cti:paoName paoId="${device.yukonID}"/>
                        </c:when>
                        <c:otherwise>
                            <a title="${device.paoIdentifier}" href="/commander/command?deviceId=${device.yukonID}"><cti:paoName paoId="${device.yukonID}"/></a>
                        </c:otherwise>
                    </c:choose>
                        </li>
                </c:if>
            </c:forEach>
                    </ul>
<!-- END > CAP Control section -->
                </tags:boxContainer2>
            </td>
        </tr>
    </table>
</cti:standardPage>
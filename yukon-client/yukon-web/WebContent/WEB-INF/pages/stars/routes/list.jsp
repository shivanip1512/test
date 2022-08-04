<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="routes.list">

    <!-- Actions dropdown -->
     <div id="page-actions" class="dn">
     <!-- //ADD premissions after discussion -->
         <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" data-popup="#js-create-route-popup"/>
     </div>
     
     <div id="js-create-route-popup" class="dn" data-title="<i:inline key=".createObject.title"/>" data-width="400">
            <table style="width:100%">
                <tr>
                    <td>
                        <cti:url var="createCommRoute" value="create"/>
                        <a href="${createCommRoute}"><i:inline key=".routes.createCommRoute.create"/></a>
                    </td>
                    <td>
                        <cti:url var="createMacroRoute" value="macroRoutes/create"/>
                        <a href="${createMacroRoute}"><i:inline key=".macroRoutes.createMacroRoute.create"/></a>
                    </td>

                </tr>
            </table>
        </div>

    <hr/>
    
    <div class="js-filtered-results-container">
        <table class="compact-results-table">
            <thead>
                <th><i:inline key="yukon.common.name"/></th>
                <th><i:inline key="yukon.common.type"/></th>
            </thead>
            <tbody>
                <c:forEach var="nonCCUAndMacroRoutes" items="${nonCCUAndMacroRoutes}">
                    <c:set var="cssClass" value="error" />
                    <cti:msg2 var="status" key="yukon.common.disabled"/>
                    <tr>
                        <td>
                           <c:choose>
                                <c:when test="${nonCCUAndMacroRoutes.deviceType == 'ROUTE_MACRO'}">
                                    <cti:url value="/stars/device/routes/macroRoutes/${nonCCUAndMacroRoutes.deviceId}" var="viewMacroRouteUrl"/>
                                    <a href="${viewMacroRouteUrl}">${fn:escapeXml(nonCCUAndMacroRoutes.deviceName)}</a>
                                </c:when>
                                <c:otherwise>
                                    <cti:url value="/stars/device/routes/${nonCCUAndMacroRoutes.deviceId}" var="viewUrl"/>
                                    <a href="${viewUrl}">${fn:escapeXml(nonCCUAndMacroRoutes.deviceName)}</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><i:inline key="${nonCCUAndMacroRoutes.deviceType}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</cti:standardPage>
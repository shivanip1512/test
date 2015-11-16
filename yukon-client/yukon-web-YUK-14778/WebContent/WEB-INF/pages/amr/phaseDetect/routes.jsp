<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:standardPageFragment module="amr" pageName="phaseDetect.home" fragmentName="routes">
    <c:if test="${show}">
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:nameValue2 nameKey=".devicesSelected">${currentSubstation}</tags:nameValue2>
            <tags:nameValueGap2 gapHeight="3px"/>
            <tags:nameValue2 nameKey=".deviceCount">${deviceCount}</tags:nameValue2>
        </tags:nameValueContainer2>
        <table class="compact-results-table stacked">
            <thead>
                <tr>
                    <th class="wsnw"><i:inline key=".routeName"/></th>
                    <th><i:inline key=".pageDescription"/></th>
                </tr>
            </thead>
            <tbody> 
                <c:forEach var="route" items="${routes}">
                    <tr>
                        <td>
                            <label><input id="read_route_${route.id}" name="read_route_${route.id}" checked="checked" type="checkbox" onclick="checkRoutes()">${route.name}</label>
                        </td>
                        <td>${routeSizeMap[route.id]}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</tags:standardPageFragment>
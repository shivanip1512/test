<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="dashboard" page="infrastructureWarnings.detail">

<cti:msgScope paths="widgets.infrastructureWarnings">

    <table class="compact-results-table">
        <th><cti:msg2 key=".name"/></th>
        <th><cti:msg2 key=".type"/></th>
        <th><cti:msg2 key=".status"/></th>
    
        <c:forEach var="warning" items="${warnings}">
            <tr>
                <td class="wsnw">
                    <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                        <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                    </cti:paoDetailUrl>
                </td>
                <td class="wsnw">${warning.paoIdentifier.paoType.paoTypeName}</td>
                <td>
                    <c:set var="warningColor" value="warning"/>
                    <c:if test="${warning.severity == 'HIGH'}">
                        <c:set var="warningColor" value="error"/>
                    </c:if>
                    <span class="${warningColor}"><cti:msg2 key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/></td>
                </td>
            </tr>
        </c:forEach>
    </table>
    
    </cti:msgScope>
    
</cti:standardPage>
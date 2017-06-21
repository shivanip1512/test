<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <table class="compact-results-table">
        <th class="fwn" style="padding-bottom:0"><a href="${allWarningsUrl}"><cti:msg2 key=".gateways"/></a></th>
        <th class="fwn" style="padding-bottom:0"><a href="${allWarningsUrl}"><cti:msg2 key=".relays"/></a></th>
        <th class="fwn" style="padding-bottom:0"><a href="${allWarningsUrl}"><cti:msg2 key=".CCUs"/></a></th>
        <th class="fwn" style="padding-bottom:0"><a href="${allWarningsUrl}"><cti:msg2 key=".repeaters"/></a></th>
        <tr>
            <td style="padding-top:0"><span class="label label-success" style="margin-right:5px;">8</span><span class="label label-warning">1</span></td>
            <td style="padding-top:0"><span class="label label-success" style="margin-right:5px;">45</span><span class="label label-warning">1</span></td>
            <td style="padding-top:0"><span class="label label-success" style="margin-right:5px;">8</span><span class="label label-warning">1</span></td>
            <td style="padding-top:0"><span class="label label-success" style="margin-right:5px;">8</span><span class="label label-warning">1</span></td>
        </tr>
    </table>  
    <span class="fr"><a href="${allWarningsUrl}" target="_blank"><cti:msg2 key=".seeAll"/></a></span>
    <br/>  
    
    <tags:sectionContainer2 nameKey="recentIssues">
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
    </tags:sectionContainer2>
    
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="js-last-updated fl" style="font-size:11px" title="${lastUpdatedMsg}">06/15/2017 09:01:20 AM</span>
        <cti:msg2 key="yukon.web.widgets.forceUpdate" var="forceUpdateMsg"/>
        <cti:icon icon="icon-arrow-refresh" title="${forceUpdateMsg}" classes="js-force-update cp"/>
    </span>

</cti:msgScope>
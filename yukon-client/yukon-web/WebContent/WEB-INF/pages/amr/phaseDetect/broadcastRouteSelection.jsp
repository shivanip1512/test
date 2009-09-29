<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step2.sectionTitle" var="sectionTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step2.noteLabel" var="noteLabel"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step2.noteText" var="noteText"/>

<cti:standardPage title="Phase Detection" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:standardMenu menuSelection="meters" />

    <cti:url var="routesUrl" value="/spring/amr/phaseDetect/routes"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink title="${pageTitle}" />
    </cti:breadCrumbs>
    
    <%-- Phase Detect Title --%>
    <h2 style="display: inline;">
        ${pageTitle}
    </h2>
    <br>
    <br>
    <form action="/spring/amr/phaseDetect/saveBroadcastRoutes" method=post>
        <tags:sectionContainer title="${sectionTitle}">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="smallBoldLabel">${noteLabel}</td>
                    <td style="font-size:11px;">${noteText}</td>
                </tr>
            </table>
                <table class="resultsTable" style="padding-top: 5px;width: 10%;">
                    <thead>
                        <tr>
                            <th nowrap="nowrap">
                                Route Name
                            </th>
                        </tr>
                    </thead>
                    <tbody> 
                        <c:forEach var="route" items="${routes}">
                            <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                                <td nowrap="nowrap" style="padding-right: 10px;">
                                    <input id="read_route_${route.id}" name="read_route_${route.id}" checked="checked" type="checkbox">
                                    ${route.name}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
        </tags:sectionContainer>
        <input id="cancelButton" name="cancel" type="submit" value="Cancel Test">
        <input id="nextButton" type="submit" value="Next">
    </form>
</cti:standardPage>
    
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.sectionTitle" var="sectionTitle"/>

<cti:standardPage title="Phase Detection" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:standardMenu menuSelection="meters" />
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
    <form action="/spring/amr/phaseDetect/saveTestSettings" method="post">
        <tags:sectionContainer title="${sectionTitle}">
            <table style="padding-right: 20px;padding-bottom: 10px;">
                <tr valign="top">
                    <td>
                        <tags:nameValueContainer>
                            <tags:nameValue name="Substation">
                                ${substationName}
                            </tags:nameValue>
                            <tags:nameValue name="Interval Length">
                               <select id="intervalLength" name="intervalLength">
                                   <option value="60">60</option>
                                   <option value="45">45</option>
                                   <option value="30" selected="selected">30</option>
                                   <option value="15">15</option>
                               </select>
                           </tags:nameValue>
                           <tags:nameValue name="Delta Voltage">
                               <select id="deltaVoltage" name="deltaVoltage">
                                   <option value="4">+4</option>
                                   <option value="3">+3</option>
                                   <option value="2" selected="selected">+2</option>
                                   <option value="1">+1</option>
                                   <option value="-1">-1</option>
                                   <option value="-2">-2</option>
                                   <option value="-3">-3</option>
                                   <option value="-4">-4</option>
                               </select>
                           </tags:nameValue>
                           <tags:nameValue name="Number of Intervals">
                               <select id="numIntervals" name="numIntervals">
                                   <option value="6" selected="selected">6</option>
                                   <option value="5">5</option>
                                   <option value="4">4</option>
                               </select>
                           </tags:nameValue>
                       </tags:nameValueContainer>
                    </td>
                </tr>
            </table>
        </tags:sectionContainer>
        <input id="cancelButton" name="cancel" type="submit" value="Cancel Test">
        <input id="nextButton" type="submit" value="Next">
    </form>
</cti:standardPage>
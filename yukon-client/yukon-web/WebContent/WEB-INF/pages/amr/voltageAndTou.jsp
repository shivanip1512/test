
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<cti:standardPage title="Voltage & TOU" module="amr">
    <cti:standardMenu menuSelection="deviceselection" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
        <cti:crumbLink url="/spring/csr/search" title="Device Selection"  />
        <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail"  />
        &gt; Voltage &amp; TOU
    </cti:breadCrumbs>
    
    <h2>Voltage &amp; TOU</h2>
    <br>
    
    
    <c:if test="${not empty errorMsg}">
        <span class="ErrorMsg">* ${errorMsg}</span><br/><br/>
    </c:if>
    
    <div style="width: 50%;">
    
      <%-- METER INFORMATION WIDGET --%>
      <tags:widgetContainer deviceId="${deviceId}">
        <tags:widget bean="meterInformationWidget" />
      </tags:widgetContainer>
        
        <%-- VOLTAGE --%>
        <form id="voltageForm" method="POST" action="/servlet/CommanderServlet">

            <input type="hidden" name="deviceID" value="${deviceId}">
            <input type="hidden" name="command" value="getvalue voltage & getvalue demand">
            <input type="hidden" name="timeOut" value="8000">
            <input id="redirect" type="hidden" name="REDIRECT" value="/spring/amr/voltageAndTou/home?deviceId=${deviceId}">
            <input id="referrer" type="hidden" name="REFERRER" value="/spring/amr/voltageAndTou/home?deviceId=${deviceId}">
    
            <br>
            <tags:boxContainer title="Voltage" hideEnabled="false">
                
                <tags:nameValueContainer>
                
                    <tags:nameValue name="Last Interval">
                        <tags:attributeValue device="${device}" attribute="${voltageAttribute}" />
                    </tags:nameValue>
                    
                    <tags:nameValue name="Minimum">
                        <tags:attributeValue device="${device}" attribute="${minimumVoltageAttribute}" />
                    </tags:nameValue>
                    
                    <tags:nameValue name="Maximum">
                        <tags:attributeValue device="${device}" attribute="${maximumVoltageAttribute}" />
                    </tags:nameValue>
                
                </tags:nameValueContainer>
                
                <br>
                <c:if test="${isReadable}">
                    <tags:slowInput myFormId="voltageForm" label="Read" labelBusy="Reading" />
                </c:if>
            </tags:boxContainer>
        </form>
        
        <%-- TOU SCHEDULE --%>
        <form id="touForm" method="POST" action="/servlet/CommanderServlet">

            <input type="hidden" name="deviceID" value="${deviceId}">
            <input type="hidden" name="command" value="putconfig tou ">
            <input type="hidden" name="timeOut" value="8000">
            <input id="redirect" type="hidden" name="REDIRECT" value="/spring/amr/voltageAndTou/home?deviceId=${deviceId}">
            <input id="referrer" type="hidden" name="REFERRER" value="/spring/amr/voltageAndTou/home?deviceId=${deviceId}">
    
    
            <c:if test="${not empty schedules && fn:length(schedules) > 0}">
        
                <br>
                <tags:boxContainer title="TOU Schedule" hideEnabled="false">
                    <table width="100%">
                        <tr> 
                            <td>
                                <select name="scheduleID" id="scheduleID">
                                    <c:forEach var="schedule" items="${schedules}">
                                        <option value="${schedule.scheduleID}">${schedule.scheduleName}</option>
                                    </c:forEach>
                                </select>
                                <tags:slowInput myFormId="touForm" label="Download TOU Schedule" labelBusy="Download TOU Schedule" />
                            </td>
                        </tr>
                    </table>
                </tags:boxContainer>
                  
            </c:if>

        </form>
    </div>

</cti:standardPage>

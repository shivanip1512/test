<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Economic Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2>
  <t:outputText value="#{sEconomicDetail.event.program.programType.name} #{sEconomicDetail.event.program.name}"/> 
  Event for 
  <t:outputText value="#{sNotifEconomicDetail.participant.customer.companyName}"/>
</h2>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:commandLink action="#{sNotifEconomicDetail.mainEvent}" value="Return"/>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>


<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Start Date</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.notificationTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.dateFormat}"
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
      (all times <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Notification Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.notificationTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.timeFormat}" 
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.startTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.timeFormat}" 
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Stop Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.stopTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.timeFormat}" 
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Operations</td>
    <td>
    <div class="eventControlButtons">
    <t:commandButton action="#{sNotifEconomicDetail.refresh}" value="Refresh"/>
    </div>
    </td>
  </tr>
</table>

<h3>Customer Notification Status</h3>
<div id="detailInner"> 
  <t:aliasBean alias="#{notifTableData}" value="#{sNotifEconomicDetail.notifList}">
    <jsp:include page="../include/notifTable.jsp"/>
  </t:aliasBean>
</div>



</h:form>
 
</cti:standardPage>
</f:view>


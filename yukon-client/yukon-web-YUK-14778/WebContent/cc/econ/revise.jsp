<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Economic Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sEconomicDetail.event.program.name}"/> Event</h2>
<h3>Enter New Energy Prices</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<div>
<t:dataTable value="#{sEconomicDetail.nextRevisionPrices}" 
             var="thisPrice" 
             forceIdIndexFormula="#{thisPrice.offset}"
             styleClass="horizBorders">
  <t:column>
    <t:outputText value="#{thisPrice.startTime}">
     <f:convertDateTime 
      pattern="#{sCommercialCurtailment.timeFormat}" 
      timeZone="#{sCommercialCurtailment.timeZone}" />
    </t:outputText>
  </t:column>
  <t:column>
    <t:inputText value="#{thisPrice.energyPrice}">
      <f:validateLongRange minimum="0" maximum="10000"/>
    </t:inputText>
    <t:outputText value="($/MWh)"/>
    
  </t:column>
</t:dataTable>
</div>

<div class="actionButtons">
<h:commandButton action="#{sEconomicDetail.doCreateRevision}" value="Next"/>
<h:commandButton action="#{sEconomicDetail.cancelRevision}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>


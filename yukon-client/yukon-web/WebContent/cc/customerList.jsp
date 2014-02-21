<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Customer List" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup|ccurt_customers"/>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:dataList id="customerList" value="#{rCustomerList.customerList}" var="thisCustomer" layout="unorderedList">
    <h:commandLink action="#{sCustomerDetail.showCustomer}">
      <f:param name="customerId" value="#{thisCustomer.id}"/>
      <h:outputText value="#{thisCustomer.companyName}" />
    </h:commandLink>
</t:dataList>

</h:form>

</cti:standardPage>
</f:view>


<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<cti:standardMenu />


<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<h3>Current Events</h3>
<t:aliasBean alias="#{eventList}" value="#{sCustomerEventBean.currentEventList}">
  <jsp:include page="include/userEventList.jsp"/>
</t:aliasBean>

<h3>Pending Events</h3>
<t:aliasBean alias="#{eventList}" value="#{sCustomerEventBean.pendingEventList}">
  <jsp:include page="include/userEventList.jsp"/>
</t:aliasBean>

<h3>Recent Events</h3>
<t:aliasBean alias="#{eventList}" value="#{sCustomerEventBean.recentEventList}">
  <jsp:include page="include/userEventList.jsp"/>
</t:aliasBean>

</h:form>

</cti:standardPage>
</f:view>


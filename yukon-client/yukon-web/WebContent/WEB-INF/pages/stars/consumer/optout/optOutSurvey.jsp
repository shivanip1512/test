<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="optOutUrl" value="/stars/consumer/optout"/>
<cti:url var="actionUrl" value="${optOutUrl}/optOutQuestions"/>

<cti:standardPage module="consumer" page="optoutconfirm">
<cti:standardMenu/>
<cti:flashScopeMessages/>
<form:form id="form" commandName="optOutBackingBean" method="POST"
    action="${actionUrl}">

    <h3><cti:msg key="yukon.dr.consumer.optoutconfirm.header"/></h3>

    <div align="center">
        <cti:msg key="yukon.dr.consumer.optoutconfirm.description"/>

        <br>
        <br>

        <div class="survey">
            <%@ include file="survey.jspf" %>
        </div>

	    <div>
	        <br>
	        <span style="padding-right: 0.5em;">
	            <input type="submit" value="<cti:msg key='yukon.dr.consumer.optoutconfirm.save'/>"></input>
	        </span>
	        <input type="button" value="<cti:msg key='yukon.dr.consumer.optoutconfirm.cancel'/>"
	               onclick="javascript:location.href='${optOutUrl}';"></input>
	    </div>
    </div>
</form:form>
</cti:standardPage>

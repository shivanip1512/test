<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="generalUrl" value="/stars/consumer/general"/>

<cti:standardPage module="consumer" page="optoutresult">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optoutresult.header"/></h3>
    
    <div align="center">
    
        <cti:msg key="${result}"/>
    
        <br>
        <br>
        <cti:url value="/stars/consumer/optout" var="url"/>
        <cti:msg key="yukon.dr.consumer.optoutresult.ok" var="ok"/>
        <cti:button label="${ok}" href="${url}"/>
    </div>
    
    
</cti:standardPage>
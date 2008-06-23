<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="generalUrl" value="/spring/stars/consumer/general"/>

<cti:standardPage module="consumer" page="optout">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optoutresult.header"/></h3>
    
    <div align="center">
    
        <cti:msg key="${result}"/>
    
        <br>
        <br>
    
        <input type="button" value='<cti:msg key="yukon.dr.consumer.optoutresult.ok"/>'
               onclick="location.href='${generalUrl}';"></input>
    </div>
    
    
</cti:standardPage>    
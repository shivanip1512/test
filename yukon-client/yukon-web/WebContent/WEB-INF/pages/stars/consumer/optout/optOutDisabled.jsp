<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="generalUrl" value="/spring/stars/consumer/general"/>

<cti:standardPage module="consumer" page="optoutdisabled">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optoutdisabled.header"/></h3>
    
    <div align="center">
    
        <cti:msg key="yukon.dr.consumer.optoutdisabled.msg"/>
    
        <br>
        <br>
    
        <input type="button" value='<cti:msg key="yukon.dr.consumer.optoutdisabled.home"/>'
               onclick="location.href='/spring/stars/consumer/general';"></input>
    </div>
    
    
</cti:standardPage>    
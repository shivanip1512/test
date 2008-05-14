<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="changeLoginUrl" value="/spring/login/changelogin"/>
<c:url var="consumerChangeLoginUrl" value="/spring/stars/consumer/changelogin"/>

<cti:standardPage module="consumer" page="changelogin">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.changelogin.header" /></h3>
    <div align="center" style="border-top: 1px solid #ccc;">
        <br>
        <jsp:include page="${changeLoginUrl}?redirectUrl=${consumerChangeLoginUrl}"/>
    </div>
</cti:standardPage>    

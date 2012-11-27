<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="changeLoginUrl" value="/login/changelogin"/>
<cti:url var="consumerChangeLoginUrl" value="/stars/consumer/changelogin"/>

<cti:standardPage module="consumer" page="changelogin">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.changelogin.header" /></h3>
    <div align="center">
        <br>
        <jsp:include page="${changeLoginUrl}?redirectUrl=${consumerChangeLoginUrl}"/>
    </div>
</cti:standardPage>    

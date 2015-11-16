<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="consumer" page="changelogin">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.changelogin.header" /></h3>
    <div align="center">
        <br>
        <jsp:include page="/login/changelogin?redirectUrl=/stars/consumer/changelogin"/>
    </div>
</cti:standardPage>    

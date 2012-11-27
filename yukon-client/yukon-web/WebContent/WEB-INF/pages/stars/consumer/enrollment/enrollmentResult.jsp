<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="enrollmentResult">
    <cti:standardMenu/>

    <h3>
        <cti:msg key="yukon.dr.consumer.enrollment.header" /><br>
    </h3>
    <br>
    <br>
    <div align="center">
        <div><cti:msg key="${enrollmentResult}"/></div>
        <br>
        <cti:url var="okUrl" value="/stars/consumer/enrollment"/>
        <input type="button"
            value='<cti:msg key="yukon.dr.consumer.enrollmentResult.ok"/>'
            onclick="location.href='${okUrl}';"></input>
    </div>    

</cti:standardPage>
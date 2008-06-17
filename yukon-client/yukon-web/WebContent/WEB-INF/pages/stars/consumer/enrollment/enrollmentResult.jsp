<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="enrollmentComplete">
    <cti:standardMenu/>

    <h3>
        <cti:msg key="yukon.dr.consumer.enrollment.header" /><br>
    </h3>
    <br>
    <br>
    <div align="center">
        <div><cti:msg key="${enrollmentResult}"/></div>
        <br>
        <input type="button" value='<cti:msg key="yukon.dr.consumer.enrollment.back"/>' onclick="location.href='${backUrl}';"></input>
    </div>    

</cti:standardPage>
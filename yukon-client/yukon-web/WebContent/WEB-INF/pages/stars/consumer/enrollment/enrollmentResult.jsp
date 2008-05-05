<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:url var="backUrl" value="/spring/stars/consumer/enrollment"/>

<cti:standardPage module="consumer" title="Consumer Energy Services">
    <cti:standardMenu/>

<table class="contentTable">
    <tr>
        <td class="leftColumn">
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
        </td>
        <td class="rightColumn">
            <div id="rightDiv">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}"/>
                <br>
                <img src="<cti:theme key="yukon.dr.consumer.enrollment.rightColumnLogo" default="/WebConfig/yukon/Family.jpg" url="true"/>"></img>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>
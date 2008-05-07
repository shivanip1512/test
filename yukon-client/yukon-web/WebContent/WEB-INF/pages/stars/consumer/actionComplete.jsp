<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
<cti:standardMenu/>

<table class="contentTable">
    <tr>
        <td class="leftColumn">
            <h3>
                <cti:msg key="yukon.dr.consumer.actionComplete.header" /><br>
            </h3>
            
            <div class="message">
                <div style="text-align: left;">
                    <cti:msg key="${message}" />
                </div>
                
                <c:url var="viewUrl" value="${viewUrl}">
                    <c:param name="thermostatIds" value="${thermostatIds}" />
                </c:url>
                <br><br>
                <a href="${viewUrl}"><cti:msg key="yukon.dr.consumer.actionComplete.ok" /></a>
            </div>

        </td>
        <td class="rightColumn">
            <div id="rightDiv">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}"/>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>
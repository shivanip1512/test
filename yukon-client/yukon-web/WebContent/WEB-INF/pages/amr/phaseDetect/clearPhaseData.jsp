<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step3.sectionTitle" var="sectionTitle"/>

<cti:standardPage title="Phase Detection" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:standardMenu menuSelection="meters" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink title="${pageTitle}" />
    </cti:breadCrumbs>
    
    <script type="text/javascript">

    function startSpinner() {
        $('spinner').show();
    }

    </script>
    
    <cti:url var="clearURL" value="/spring/amr/phaseDetect/clear"/>
    
    <%-- Phase Detect Title --%>
    <h2 style="display: inline;">
        ${pageTitle}
    </h2>
    <br>
    <br>
    <form action="/spring/amr/phaseDetect/clear" method="post">
        <tags:sectionContainer title="${sectionTitle}">
	        <table>
	            <tr>
	                <td style="vertical-align: top;">
				        <table style="padding-right: 20px;">
				            <tr>
				                <td>
				                   <tags:nameValueContainer>
				                       <tags:nameValue name="Substation">
				                           ${substationName}
				                       </tags:nameValue>
				                   </tags:nameValueContainer>
				                </td>
				                <td style="padding-left: 10px;">
				                    <input id="clear" type="submit" value="Clear Phase Data" onclick="startSpinner();">
				                    <img style="display: none;" id="spinner" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
				                </td>
				            </tr>
			            </table>
	                </td>
	            </tr>
	            <tr>    
	                <td style="vertical-align: top;">
	                    <div id="clearDiv">
						    <c:if test="${not empty param.errorReason}">
						        <span class="errorRed"><b>Error Sending Clear Command: ${param.errorReason}</b></span> 
						    </c:if>
	                    </div>
	                </td>
	            </tr>
	        </table>
        </tags:sectionContainer>
        <input id="cancelButton" name="cancel" type="submit" value="Cancel Test">
    </form>
</cti:standardPage>
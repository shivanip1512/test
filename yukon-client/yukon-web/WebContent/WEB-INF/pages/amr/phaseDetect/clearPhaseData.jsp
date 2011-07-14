<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.clearPhaseData">
    
    <script type="text/javascript">

    function startSpinner() {
        $('spinner').show();
    }

    </script>
    
    <cti:url var="clearURL" value="/spring/amr/phaseDetect/clear"/>
    <form action="/spring/amr/phaseDetect/clear" method="post">
        <tags:sectionContainer2 nameKey="clearData">
	        <table>
	            <tr>
	                <td style="vertical-align: top;">
				        <table style="padding-right: 20px;">
				            <tr>
				                <td>
				                   <tags:nameValueContainer2>
				                       <tags:nameValue2 nameKey=".substation">
				                           ${substationName}
				                       </tags:nameValue2>
				                   </tags:nameValueContainer2>
				                </td>
				                <td style="padding-left: 10px;">
                                    <cti:msg2 var="clearPhaseData" key=".clearPhaseData"/>
				                    <input id="clear" type="submit" value="${clearPhaseData}" onclick="startSpinner();" class="formSubmit">
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
						        <span class="errorMessage"><b><i:inline key=".errorSending" arguments="${param.errorReason}"/></b></span>
						    </c:if>
	                    </div>
	                </td>
	            </tr>
	        </table>
        </tags:sectionContainer2>
        <cti:button key="cancelTest" name="cancel" type="submit"/>
    </form>
</cti:standardPage>
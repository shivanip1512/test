<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step1.sectionTitle" var="sectionTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step1.noteLabel" var="noteLabel"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step1.noteText" var="noteText"/>

<cti:standardPage title="Phase Detection" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:standardMenu menuSelection="meters" />

    <cti:url var="routesUrl" value="/spring/amr/phaseDetect/routes"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink title="${pageTitle}" />
    </cti:breadCrumbs>
    
	
	<script type="text/javascript">
        function selectThisSub() {
		    var selection = $F('substations');
		    $("selectedSub").value = selection;
		    var button = $('nextButton');
		    if(selection == '-1'){
		    	button.disable();
		    }else{
		    	button.enable();
		    }
		    var params = {'substationId': selection};
            new Ajax.Updater('routesDiv', '${routesUrl}', {method: 'get', evalScripts: true, parameters: params});
        }
	</script>
	
	<%-- Phase Detect Title --%>
    <h2 style="display: inline;">
        ${pageTitle}
    </h2>
    <br>
	<br>
    <form action="/spring/amr/phaseDetect/saveSubstationAndReadMethod" method=post>
        <tags:sectionContainer title="${sectionTitle}">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="smallBoldLabel" style="padding-right: 5px;"><span class="errorRed">${noteLabel}</span></td>
                    <td style="font-size:11px;"><span>${noteText}</span></td>
                </tr>
            </table>
            <input type="hidden" name="selectedSub" id="selectedSub" value="-1">
		    <table style="padding-right: 20px;padding-bottom: 10px;">
		        <tr valign="top">
		            <td style="padding-top: 3px;">
		               <tags:nameValueContainer>
		                   <tags:nameValue name="Substation" nameColumnWidth="150px">
		                       <select id="substations" onchange="selectThisSub();">
		                           <option value="-1">(none)</option>
			                       <c:forEach var="substation" items="${substations}">
			                           <option value="${substation.id}">${substation.name}</option>
			                       </c:forEach>
			                   </select>
		                   </tags:nameValue>
		               </tags:nameValueContainer>
		            </td>
		        </tr>
		        <tr>
                    <td style="padding-top: 3px;">
                        <div id="routesDiv"></div>
                    </td>
		        </tr>
		        <tr>
                    <td style="padding-top: 3px;">
                        <input type="radio" name="readPhasesWhen" value="after" 
                            title="Do phase detection tests for all three phases before reading phase data from meters." 
                            checked="checked"> 
                            Read Meters After All Phase Detection Tests
                    </td>
                </tr>
		        <tr>
	                <td style="padding-top: 3px;">
	                    <input type="radio" name="readPhasesWhen" value="between" 
                            title="Read phase data from meters in between phase detection tests for each phase."> 
                            Read Meters Between Phase Detection Tests
	                </td>
		        </tr>
		    </table>
        </tags:sectionContainer>
        <input id="nextButton" name="next" type="submit" value="Next" disabled="disabled">
	</form>
</cti:standardPage>
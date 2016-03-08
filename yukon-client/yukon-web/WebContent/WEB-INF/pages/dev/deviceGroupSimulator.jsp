<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cti:standardPage module="dev" page="devcieGroupSimulator">
<script type="text/javascript">
function generateDeviceGroup(){
	 $('#generate-deviceGroups').attr("disabled","disabled");
	 $("#deviceGroupGeneratorForm").submit();
}
</script>

    <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
    <cti:dataGridCell> 
    <form id='deviceGroupGeneratorForm' action="generateDeviceGroups" method="post">
      <cti:csrfToken />
        <tags:sectionContainer title="Generate Device Groups">
        <c:if test="${not empty errorMsg}">
            <div class="error stacked">${errorMsg}</div>
        </c:if>
        <div class="success">${successMsg}</div>
            <tags:nameValueContainer2>
                <tags:nameValue2 label="modules.dev.generateDeviceGroups.noOfGroups">
                   <input id="noOfGroups" name="noOfGroups" type="text" value="1"/>
                </tags:nameValue2>
                
                <tags:nameValue2 label="modules.dev.generateDeviceGroups.noOfDevices">
                   <input id="noOfDevices" name="noOfDevices" type="text" value="1"/>
                </tags:nameValue2>
                
                <tags:nameValue2 label="modules.dev.generateDeviceGroups.useNestedGroups">
                    <cti:msg2 var="no" key="yukon.common.no" />
                    <cti:msg2 var="yes" key="yukon.common.yes" />
                     <input type="radio" name="useNestedGroups" value="Yes">${yes}</input>
                     <input type="radio" name="useNestedGroups" value="No" checked="checked">${no}</input>
                </tags:nameValue2>
                
                <tags:nameValue2 label="modules.dev.generateDeviceGroups.deviceType">
                    <select id="deviceType" name="deviceType">
                        <option value="LCR">LCR</option>
                        <option value="MCT">MCT</option>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer>
        
        <div class="page-action-area">
            <cti:button id="generate-deviceGroups" label="Generate Device Groups" type="submit" onclick="generateDeviceGroup();"/>
         </div>
    </form>
    </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>
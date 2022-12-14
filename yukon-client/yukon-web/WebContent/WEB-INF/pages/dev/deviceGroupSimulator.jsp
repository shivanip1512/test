<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cti:standardPage module="dev" page="deviceGroupSimulator">
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
        <cti:msg2 key="modules.dev.deviceGroupGenerator.helpText" var="helpText"/>
        <tags:sectionContainer title="Generate Device Groups" helpText="${helpText}" >
            <tags:nameValueContainer>
                <tags:nameValue name="No Of Groups" >
                   <input id="noOfGroups" name="noOfGroups" type="text" value="1"/>
                </tags:nameValue>
                
                <tags:nameValue name="No Of Devices in each group">
                   <input id="noOfDevicesPerGroup" name="noOfDevicesPerGroup" type="text" value="1"/>
                </tags:nameValue>
                
                <tags:nameValue name="Use Nested Groups">
                    <cti:msg2 var="no" key="yukon.common.no" />
                    <cti:msg2 var="yes" key="yukon.common.yes" />
                     <input type="radio" name="useNestedGroups" value="true">${yes}</input>
                     <input type="radio" name="useNestedGroups" value="false" checked="checked">${no}</input>
                </tags:nameValue>
                
                <tags:nameValue name="Device Type">
                    <select id="deviceType" name="deviceType">
                        <option value="LCR">LCR</option>
                        <option value="MCT">MCT</option>
                    </select>
                </tags:nameValue>
            </tags:nameValueContainer>
        </tags:sectionContainer>
        
        <div class="page-action-area">
            <cti:button id="generate-deviceGroups" label="Generate Device Groups" type="submit" onclick="generateDeviceGroup();"/>
         </div>
    </form>
    </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>
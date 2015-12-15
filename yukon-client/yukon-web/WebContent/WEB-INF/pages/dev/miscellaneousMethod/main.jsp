<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="miscellaneousMethod">
<cti:checkEnergyCompanyOperator showError="true" >
<cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
    <cti:dataGridCell>
        <tags:sectionContainer title="ISOC">
        <form action="insertProgramTypes" method="post">
            <cti:csrfToken/>
            <cti:button nameKey="insertProgramTypes" type="submit"/>
        </form>
        </tags:sectionContainer>
        
        <tags:sectionContainer title="SEP Text Message Tester"> 
            <form action="sendTestTextMessage" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="loadProgramId" id="loadProgramId">
                    <tags:pickerDialog  type="lmProgramPicker"
                                                        extraArgs="${energyCompanyId}"
                                                        id="programPicker" 
                                                        immediateSelectMode="true"
                                                        destinationFieldId="loadProgramId"
                                                        selectionProperty="paoName"
                                                        linkType="selection">
                        <cti:icon icon="icon-add"/>
                        Choose Program
                   </tags:pickerDialog>
                <cti:button nameKey="sendTestTextMessage" type="submit"/>
            </form>
            <form action="sendTestCancelMessage" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="loadProgramId" id="loadProgramId2">
                    <tags:pickerDialog  type="lmProgramPicker"
                                                        extraArgs="${energyCompanyId}"
                                                        id="programPicker2" 
                                                        immediateSelectMode="true"
                                                        destinationFieldId="loadProgramId2"
                                                        selectionProperty="paoName"
                                                        linkType="selection">
                        <cti:icon icon="icon-add"/>
                        Choose Program
                    </tags:pickerDialog>
                <cti:button nameKey="sendTestCancelMessage" type="submit"/>
            </form>
            </tags:sectionContainer>
            
    </cti:dataGridCell>
</cti:dataGrid>
 </cti:checkEnergyCompanyOperator>
</cti:standardPage>

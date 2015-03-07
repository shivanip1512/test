<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.home">

<cti:checkEnergyCompanyOperator showError="true" >

<cti:url var="action" value="/stars/operator/inventory/setupFilterRules"/>

<script type="text/javascript">
function submitSelectionForm(items) {
    $('#selectByInventoryPickerForm').submit();
    return true;
}

function addMeter() {
    $('#addMeterForm').submit();
    return true;
}

</script>

<!-- Popup for menus -->
<div id="menuPopup" class="dn menuPopup"></div>

<!-- Page Dropdown Actions -->
<c:if test="${showActions}">
    <div id="page-actions" class="dn">
        <c:if test="${showAccountCreate}">
            <cti:url var="createAccountUrl" value="/stars/operator/account/accountCreate"/>
            <cm:dropdownOption href="${createAccountUrl}" icon="icon-plus-green" key=".create.account"/>
        </c:if>
        <c:if test="${showHardwareCreate}">
            <cm:dropdownOption data-popup="#add-hardware-dialog" icon="icon-plus-green" key=".create.device"/>
        </c:if>
    </div>
</c:if>

<div id="add-hardware-dialog" class="dn" data-title="<cti:msg2 key=".create.device"/>">
    <div class="stacked clearfix">
        <form action="creationPage" method="post">
            <cti:csrfToken/>
            <cti:button nameKey="addHardware" id="addHardwareBtn" type="submit"/>
            <select name="hardwareTypeId">
                <c:forEach items="${addHardwareTypes}" var="deviceType">
                    <option value="${deviceType.entryID}">${fn:escapeXml(deviceType.entryText)}</option>
                </c:forEach>
            </select>
        </form>
    </div>
    <div class="stacked clearfix">
        <form action="abr/view" method="post">
            <cti:csrfToken/>
            <cti:button nameKey="addHardwareByRange" id="addHardwareByRangeBtn" type="submit"/>
            <select name="hardwareTypeId">
                <c:forEach items="${addHardwareByRangeTypes}" var="deviceType">
                    <option value="${deviceType.entryID}">${fn:escapeXml(deviceType.entryText)}</option>
                </c:forEach>
            </select>
        </form>
    </div>

    <c:if test="${showAddMeter}">
        <div class="stacked clearfix">
            <form action="addMeter/view" id="addMeterForm">
                <tags:pickerDialog id="addMeterPicker"
                                   type="drUntrackedMctPicker"
                                   nameKey="addMeter"
                                   linkType="button"
                                   destinationFieldName="mctId"
                                   allowEmptySelection="false"
                                   multiSelectMode="false"
                                   immediateSelectMode="true"
                                   endAction="addMeter"/>
            </form>
        </div>
    </c:if>
</div>

<tags:widgetContainer identify="false">
    
    <div class="column-14-10">
        <div class="one column">
            <cti:checkRolesAndProperties value="INVENTORY">
                <tags:sectionContainer2 nameKey="deviceSelection" hideEnabled="false" styleClass="stacked">
                    <table class="link-table">
                        <%-- INVENTORY PICKER--%>
                        <tr>
                            <td>
                                <cti:url value="/stars/operator/inventory/inventoryActions" var="inventoryPickerUrl"/>
                                <form id="selectByInventoryPickerForm" action="${inventoryPickerUrl}" method="post">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="collectionType" value="idList"/>
                                    <input type="hidden" name="idList.ids" id="inventoryIds"/>
                                    <tags:pickerDialog type="lmHardwareBasePicker" 
                                                       extraArgs="${energyCompanyId}" 
                                                       id="inventoryPicker" 
                                                       multiSelectMode="true" 
                                                       destinationFieldId="inventoryIds" 
                                                       endAction="submitSelectionForm" 
                                                       nameKey="selectInventoryButton"
                                                       ><i:inline key=".selectInventoryButton.label"/></tags:pickerDialog>
                                </form>
                            </td>
                            <td><i:inline key=".selectInventoryDescription"/></td>
                        </tr>
                        <%-- INVENTORY FILTER --%>
                        <tr>
                            <td><a href="setupFilterRules"><i:inline key=".selectFilterButton.label"/></a></td>
                            <td><i:inline key=".selectFilterDescription"/></td>
                        </tr>
                        <%-- FILE UPLOAD --%>
                        <tr>
                            <td>
                                <a href="javascript:void(0);" data-popup="#file-upload-popup">
                                    <i:inline key=".selectFileButton.label"/>
                                </a>
                                <cti:msg2 var="uploadText" key="yukon.web.defaults.upload"/>
                                <div id="file-upload-popup" class="dn" data-dialog data-form 
                                    data-ok-text="${uploadText}" 
                                    data-title="${fileUploadTitle}" 
                                    data-width="500">
                                    <cti:url var="url" value="/stars/operator/inventory/upload-file"/>
                                    <form method="post" action="${url}" enctype="multipart/form-data">
                                        <cti:csrfToken/>
                                        <tags:nameValueContainer2>
                                            <tags:nameValue2 nameKey=".fileLabel">
                                                <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile">
                                                <input type="hidden" name="collectionType" value="fileUpload">
                                                <input type="hidden" name="fileUpload.energyCompanyId" 
                                                    value="${energyCompanyId}">
                                            </tags:nameValue2>
                                        </tags:nameValueContainer2>
                                    </form>
                                </div>
                            </td>
                            <td><i:inline key=".selectFileDescription"/></td>
                        </tr>
                    </table>
                </tags:sectionContainer2> 
            </cti:checkRolesAndProperties>
            
            <c:if test="${showLinks}">
                <tags:sectionContainer2 nameKey="links" styleClass="stacked">
                    <a href="/stars/operator/inventory/zbProblemDevices/view"><i:inline key=".zbProblemDevices" /></a>
                </tags:sectionContainer2>
            </c:if>
            
            <%-- DEVICE RECONFIG MONITORS WIDGET --%>
            <tags:widget bean="deviceReconfigMonitorsWidget" container="section" classes="stacked"/>
            
            <%-- COMMAND SCHEDULE WIDGET --%>
            <tags:widget bean="commandScheduleWidget" container="section" classes="stacked"/>
            
        </div>
        
        <div class="two column nogutter">
                
            <%--ACCOUNT SEARCH --%>
            <c:if test="${showAccountSearch}">
                <tags:widget bean="operatorAccountSearchWidget" container="section" classes="stacked"/>
            </c:if>
            
            <%--INVENTORY SEARCH --%>
            <c:if test="${showSearch}">
                <%@ include file="search.jsp" %>
            </c:if>
            
        </div>
    </div>
    
</tags:widgetContainer>

</cti:checkEnergyCompanyOperator>
</cti:standardPage>
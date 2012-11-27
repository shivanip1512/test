<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="action" value="/stars/operator/inventory/setupFilterRules"/>

<cti:standardPage module="operator" page="inventory.home">

    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <script type="text/javascript">
    function submitSelectionForm(items) {
        $('selectByInventoryPickerForm').submit();
        return true;
    }
    function addMeter() {
        $('addMeterForm').submit();
        return true;
    }

    function showFileUpload() {
        $('fileUploadPopup').show();
    }

    function closeFileUpload() {
        $('fileUpload.dataFile').value = '';
        $('fileUploadPopup').hide();
    }
    </script>
    
    <tags:widgetContainer identify="false">
    
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-bottom:10px;" tableClasses="inventoryOperationsLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
            
                <tags:boxContainer2 nameKey="deviceSelection" hideEnabled="false">
                    <table class="inventoryActionsTable">
                    
                        <%-- INVENTORY PICKER--%>
                        <tr>
                            <td class="button top">
                                <form id="selectByInventoryPickerForm" action="/stars/operator/inventory/inventoryActions" method="post">
                                    <input type="hidden" name="collectionType" value="idList"/>
                                    <input type="hidden" name="idList.ids" id="inventoryIds"/>
                                    <tags:pickerDialog type="lmHardwareBasePicker" extraArgs="${energyCompanyId}" id="inventoryPicker" multiSelectMode="true" 
                                                            destinationFieldId="inventoryIds" endAction="submitSelectionForm" linkType="button" buttonStyleClass="buttonGroup" nameKey="selectInventoryButton"/>
                                </form>
                            </td>
                
                            <td class="description top"><i:inline key=".selectInventoryDescription"/></td>
                        </tr>
                        
                        <%-- INVENTORY FILTER --%>
                        <tr>
                            <td class="button middle">
                                <form id="selectByFilterForm" action="/stars/operator/inventory/setupFilterRules" method="get">
                                    <cti:button nameKey="selectFilterButton" type="submit" styleClass="buttonGroup" name="filterButton"/>
                                </form>
                            </td>
                
                            <td class="description middle"><i:inline key=".selectFilterDescription"/></td>
                        </tr>
                
                        <%-- FILE UPLOAD --%>
                        <tr>
                            <td class="button">
                                <cti:button nameKey="selectFileButton" type="button" styleClass="buttonGroup" name="fileButton" onclick="showFileUpload()"/>
                                <tags:simplePopup id="fileUploadPopup" title="${fileUploadTitle}" styleClass="mediumSimplePopup">
                                    <cti:url var="submitUrl" value="/stars/operator/inventory/uploadFile"/>
                                    <form method="post" action="${submitUrl}" enctype="multipart/form-data">
                                        <tags:nameValueContainer2>
                                            <tags:nameValue2 nameKey=".fileLabel">
                                                <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile" size="40">
                                                <input type="hidden" name="collectionType" value="fileUpload">
                                                <input type="hidden" name="fileUpload.energyCompanyId" value="${energyCompanyId}">
                                            </tags:nameValue2>
                                        </tags:nameValueContainer2>
                                        <div class="actionArea">
                                            <cti:button nameKey="ok" type="submit" styleClass="f_blocker"/>
                                            <cti:button nameKey="cancel" onclick="closeFileUpload();"/>
                                        </div>
                                    </form>
                                </tags:simplePopup>
                            </td>
                
                            <td class="description"><i:inline key=".selectFileDescription"/></td>
                        </tr>
                    </table>
                             
                </tags:boxContainer2>
                
                <br>
                
                <%-- DEVICE RECONFIG MONITORS WIDGET --%>
                <tags:widget bean="deviceReconfigMonitorsWidget"/>
                
                <br>
                
                <%-- COMMAND SCHEDULE WIDGET --%>
                <tags:widget bean="commandScheduleWidget" />
            
            </cti:dataGridCell>
            
            <%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
            
                <%--SEARCH --%>
                <c:if test="${showSearch}">
                    <%@ include file="search.jsp" %>
                </c:if>
                
                <c:if test="${showActions}">
                
                    <tags:boxContainer2 nameKey="actions">
                        <ul class="buttonStack">
                            <li>
                                <form action="creationPage" method="post">
                                    <cti:button nameKey="addHardware" id="addHardwareBtn" type="submit"/>
                                    <select name="hardwareTypeId">
                                        <c:forEach items="${addHardwareTypes}" var="deviceType">
                                            <option value="${deviceType.entryID}"><spring:escapeBody htmlEscape="true">${deviceType.entryText}</spring:escapeBody></option>
                                        </c:forEach>
                                    </select>
                                </form>
                            </li>
                            <c:if test="${showAddByRange}">
                                <li>
                                    <form action="abr/view" method="post">
                                        <cti:button nameKey="addHardwareByRange" id="addHardwareByRangeBtn" type="submit"/>
                                        <select name="hardwareTypeId">
                                            <c:forEach items="${addHardwareByRangeTypes}" var="deviceType">
                                                <option value="${deviceType.entryID}"><spring:escapeBody htmlEscape="true">${deviceType.entryText}</spring:escapeBody></option>
                                            </c:forEach>
                                        </select>
                                    </form>
                                </li>
                            </c:if>
                            <c:if test="${showAddMeter}">
                                <li>
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
                                </li>
                            </c:if>
                        </ul>
                    </tags:boxContainer2>
                
                </c:if>
                    
                <c:if test="${showLinks}">
                    <br>
                    
                    <tags:boxContainer2 nameKey="links">
                        <a href="/stars/operator/inventory/zbProblemDevices/view"><i:inline key=".zbProblemDevices" /></a>
                    </tags:boxContainer2>
                </c:if>
                
            </cti:dataGridCell>
            
        </cti:dataGrid>

    </tags:widgetContainer>
    
</cti:standardPage>
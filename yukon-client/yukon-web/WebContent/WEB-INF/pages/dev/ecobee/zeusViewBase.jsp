<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="ecobee.mockTest">
    <script type="text/javascript">
    function enabledSelectedOption(checkBoxId,selectId) {
        var checked = $(checkBoxId).prop('checked');
        if (checked) {
            $(selectId).prop('disabled', false);
        } else {
            $(selectId).prop('disabled', true);
        }
    }

     $(function() {
         $('#authenticate, #createDevice, #deleteDevice, #enrollment, #showUser, #issueDemandResponse, #createPushConfiguration, #showPushConfiguration, #runtimeData, #cancelDemandResponse, #getGroup, #generateDiscrepency, #paginatedResponse, #deviceStatusResponse').click(function () {
         switch(this.name){
             case 'authenticate' : {
                 enabledSelectedOption(authenticate, authenticateOp);
             } break;
             case 'createDevice' : {
                 enabledSelectedOption(createDevice, createDeviceOp);
             } break;
             case 'deleteDevice' : {
                 enabledSelectedOption(deleteDevice, deleteDeviceOp);
             } break;
             case 'enrollment' : {
                 enabledSelectedOption(enrollment, enrollmentOp);
             } break;
             case 'issueDemandResponse' : {
                 enabledSelectedOption(issueDemandResponse, issueDemandResponseOp);
             } break;
             case 'createPushConfiguration' : {
                 enabledSelectedOption(createPushConfiguration, createPushConfigurationOp);
             } break;
             case 'showPushConfiguration' : {
                 enabledSelectedOption(showPushConfiguration, showPushConfigurationOp);
             } break;
             case 'showUser' : {
                 enabledSelectedOption(showUser, showUserOp);
             } break;
             case 'runtimeData' : {
                 enabledSelectedOption(runtimeData, enableRuntime);
             } break;
             case 'cancelDemandResponse' : {
                 enabledSelectedOption(cancelDemandResponse, cancelDemandResponseOp);
             } break;
             case 'getGroup' : {
                 enabledSelectedOption(getGroup, getAllGroupOp);
             } break;
             case 'generateDiscrepency' : {
                 enabledSelectedOption(generateDiscrepency, generateAllDiscrepencyOp);
             } break;
             case 'paginatedResponse' : {
                 enabledSelectedOption(paginatedResponse, paginatedResponseOp);
             } break;
             case 'deviceStatusResponse' : {
                 enabledSelectedOption(deviceStatusResponse, deviceStatusResponseOp);
             } break;
         }
         });
     });
    </script>

    <div class="user-message error">Warning: The ecobee simulator will only work if you remove your proxy settings!</div>

    <cti:url var="updateRequestUrl" value="update" />
    <form id="ecobeeForm" action="${updateRequestUrl}" method="post">
        <div class="column-8-8-8">
            <div class="column one">
                <tags:sectionContainer title="Endpoint">
                    <ul class="simple-list">
                        <li>
                            <label> 
                                <input id="authenticate" type="checkbox" name="authenticate"> 
                                <i:inline key=".authenticate" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="createDevice" type="checkbox" name="createDevice"> 
                                <i:inline key=".createDevice" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="deleteDevice" type="checkbox" name="deleteDevice"> 
                                <i:inline key=".deleteDevice" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="enrollment" type="checkbox" name="enrollment"> 
                                <i:inline key=".enrollment" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="issueDemandResponse" type="checkbox" name="issueDemandResponse"> 
                                <i:inline key=".issueDemandResponse" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="createPushConfiguration" type="checkbox" name="createPushConfiguration"> 
                                <i:inline key=".createPushConfiguration" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="showPushConfiguration" type="checkbox" name="showPushConfiguration"> 
                                <i:inline key=".showPushConfiguration" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="showUser" type="checkbox" name="showUser"> 
                                <i:inline key=".showUser" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="runtimeData" type="checkbox" name="enableRuntime"> 
                                <i:inline key=".runtimeData" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="cancelDemandResponse" type="checkbox" name="cancelDemandResponse"> 
                                <i:inline key=".cancelDemandResponse" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="getGroup" type="checkbox" name="getGroup"> 
                                <i:inline key=".getGroup" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="generateDiscrepency" type="checkbox" name="generateDiscrepency"> 
                                <i:inline key=".generateDiscrepency" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="paginatedResponse" type="checkbox" name="paginatedResponse"> 
                                <i:inline key=".paginatedResponse" />
                            </label>
                        </li>
                        <li>
                            <label> 
                                <input id="deviceStatusResponse" type="checkbox" name="deviceStatusResponse"> 
                                <i:inline key=".deviceStatusResponse" />
                            </label>
                        </li>
                    </ul>
                </tags:sectionContainer>
            </div>
            <div class="column two">
                <tags:sectionContainer title="Status Code">
                    <tags:nameValueContainer>
                        <ul class="simple-list">
                            <li>
                                <select id="authenticateOp" name="authenticateOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="authenticateOp" value="0" />
                            </li>
                            <li>
                                <select id="createDeviceOp" name="createDeviceOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="createDeviceOp" value="0" />
                            </li>
                            <li>
                                <select id="deleteDeviceOp" name="deleteDeviceOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="deleteDeviceOp" value="0" />
                            </li>
                            <li>
                                <select id="enrollmentOp" name="enrollmentOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="enrollmentOp" value="0" />
                            </li>
                            <li>
                                <select id="issueDemandResponseOp" name="issueDemandResponseOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="issueDemandResponseOp" value="0" />
                            </li>
                            <li>
                                <select id="createPushConfigurationOp" name="createPushConfigurationOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="createPushConfigurationOp" value="0" />
                            </li>
                            <li>
                                <select id="showPushConfigurationOp" name="showPushConfigurationOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="showPushConfigurationOp" value="0" />
                            </li>
                            <li>
                                <select id="showUserOp" name="showUserOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="showUserOp" value="0" />
                            </li>
                            <li>
                                <select id="cancelDemandResponseOp" name="cancelDemandResponseOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="cancelDemandResponseOp" value="0" />
                            </li>
                            <li>
                                <select id="getAllGroupOp" name="getAllGroupOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="getAllGroupOp" value="0" />
                            </li>
                            <li>
                                <select id="generateAllDiscrepencyOp" name="generateAllDiscrepencyOp" disabled="disabled">
                                    <c:forEach var="decrepencyStatus" items="${decrepencyStatus}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${decrepencyStatus}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="generateAllDiscrepencyOp" value="0" />
                            </li>
                            <li>
                                <select id="paginatedResponseOp" name="paginatedResponseOp" disabled="disabled">
                                    <c:forEach var="paginatedStatus" items="${paginatedStatus}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${paginatedStatus}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="paginatedResponseOp" value="0" />
                            </li>
                            <li>
                                <select id="deviceStatusResponseOp" name="deviceStatusResponseOp" disabled="disabled">
                                    <c:forEach var="status" items="${deviceStatus}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="deviceStatusResponseOp" value="0" />
                            </li>
                        </ul>
                    </tags:nameValueContainer>
                </tags:sectionContainer>
            </div>
            <div class="column three nogutter">
                <div class="action-area">
                    <button id="update">Update</button>
                </div>
            </div>
        </div>
    </form>
</cti:standardPage>
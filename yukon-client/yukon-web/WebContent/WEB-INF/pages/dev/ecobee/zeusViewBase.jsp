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
         $('#authenticate, #createDevice, #deleteDevice, #groupManagement').click(function () {
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
             case 'groupManagement' : {
                 enabledSelectedOption(groupManagement, groupManagementOp);
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
                                <input id="groupManagement" type="checkbox" name="groupManagement"> 
                                <i:inline key=".groupManagement" />
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
                                <select id="groupManagementOp" name="groupManagementOp" disabled="disabled">
                                    <c:forEach var="status" items="${status}" varStatus="loopCounter">
                                        <option value="${loopCounter.count-1}">${status}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" name="groupManagementOp" value="0" />
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
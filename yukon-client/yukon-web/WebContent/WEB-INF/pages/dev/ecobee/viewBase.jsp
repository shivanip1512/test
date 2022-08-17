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
         $('#registerDevice,#moveDevice,#createSet,#moveSet,#removeSet,#sendDR,#sendRestore,#getHierarchy,#authenticate,#runtimeReport,#assignThermostat').click(function () {
        	 switch(this.name){
             case 'registerDevice' : {
            	 enabledSelectedOption(registerDevice,regDevice);
             }break;
             case 'moveDevice' : {
            	 enabledSelectedOption(moveDevice,movDevice);
             } break;
             case 'createSet' : {
            	 enabledSelectedOption(createSet,creatSet);
             } break;
             case 'moveSet' : {
            	 enabledSelectedOption(moveSet,movSet);
             } break;
             case 'removeSet' : {
            	 enabledSelectedOption(removeSet,remSet);
             } break;
             case 'sendDR' : {
            	 enabledSelectedOption(sendDR,drSend);
             } break;
             case 'sendRestore' : {
            	 enabledSelectedOption(sendRestore,restoreSend);
             } break;
             case 'getHierarchy' : {
            	 enabledSelectedOption(getHierarchy,listHierarchy);
             } break;
             case 'authenticate' : {
            	 enabledSelectedOption(authenticate,authenticateOp);
             } break;
             case 'runtimeReport' : {
            	 enabledSelectedOption(runtimeReport,runtimeReportOp);
             } break;
             case 'assignThermostat' : {
            	 enabledSelectedOption(assignThermostat,assignThermostatOp);
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
						<label> <input type="checkbox" id="registerDevice" name="registerDevice"> <i:inline
									key=".registerDevice" /></label>
						</li>
						<li>
							<label> <input id="moveDevice" type="checkbox" name="moveDevice"> <i:inline
									key=".moveDevice" />
							</label>
						</li>
						<li>
							<label> <input id="createSet" type="checkbox" name="createSet"> <i:inline
									key=".createSet" />
							</label>
						</li>
						<li>
							<label> <input id="moveSet"	type="checkbox" name="moveSet"> <i:inline
									key=".moveSet" />
							</label>
						</li>
						<li>
							<label> <input id="removeSet" type="checkbox" name="removeSet"> <i:inline
									key=".removeSet" />
							</label>
						</li>
						<li>
							<label> <input id="sendDR" type="checkbox" name="sendDR"> <i:inline
									key=".sendDR" />
							</label>
						</li>
						<li>
							<label> <input id="sendRestore"	type="checkbox" name="sendRestore"> <i:inline
									key=".sendRestore" />
							</label>
						</li>
						<li>
							<label> <input id="getHierarchy" type="checkbox" name="getHierarchy"> <i:inline
									key=".getHierarchy" />
							</label>
						</li>
						<li>
							<label> <input id="authenticate" type="checkbox" name="authenticate"> <i:inline
									key=".authenticate" />
							</label>
						</li>
						<li>
							<label> <input id="runtimeReport" type="checkbox" name="runtimeReport"> <i:inline
									key=".runtimeReport" />
							</label>
						</li>
						<li>
							<label> <input id="assignThermostat" type="checkbox" name="assignThermostat"> <i:inline
									key=".assignThermostat" />
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
                    			<select id="regDevice" name="regDevice" disabled="disabled" default=0>
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
                                <input type="hidden" name="regDevice" value="0" />
								</li>
								<li>
                    			<select id="movDevice" name="movDevice" disabled="disabled" value="${register}">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
                                <input type="hidden" name="movDevice" value="0" />
								</li>
								<li>
                    			<select id="creatSet" name="creatSet" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				                <input type="hidden" name="creatSet" value="0" />
								</li>
								<li>
                    			<select id="movSet" name="movSet" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				              <input type="hidden" name="movSet" value="0" />
								</li>
								<li>
                    			<select id="remSet" name="remSet" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				                <input type="hidden" name="remSet" value="0" />
								</li>
								<li>
                    			<select id="drSend" name="drSend" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				                <input type="hidden" name="drSend" value="0" />
								</li>
								<li>
                    			<select id="restoreSend" name="restoreSend" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				                <input type="hidden" name="restoreSend" value="0" />
								</li>
								<li>
                    			<select id="listHierarchy" name="listHierarchy" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
				                <input type="hidden" name="listHierarchy" value="0" />
								</li>
								<li>
                    			<select id="authenticateOp" name="authenticateOp" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
                                <input type="hidden" name="authenticateOp" value="0" />
								</li>
								<li>
                    			<select id="runtimeReportOp" name="runtimeReportOp" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
                                <input type="hidden" name="runtimeReportOp" value="0" />
								</li>
								<li>
                    			<select id="assignThermostatOp" name="assignThermostatOp" disabled="disabled">
                    				<c:forEach var="status" items="${status}" varStatus="loopCounter">
                        				<option value="${loopCounter.count-1}">${status}</option>
                    				</c:forEach>
                                </select>
                                <input type="hidden" name="assignThermostatOp" value="0" />
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
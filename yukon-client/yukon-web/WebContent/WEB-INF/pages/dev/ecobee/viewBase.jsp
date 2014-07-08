<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="ecobee.mockTest">
	<script type="text/javascript">
	function registerDeviceChecked() {
	    var checked = $('#registerDevice').prop('checked');
	    if (checked) {
	        $('#regDevice').prop('disabled', false);
	    } else {
	        $('#regDevice').prop('disabled', true);
	    }
	}	
	
	function moveDeviceChecked() {
	    var checked = $('#moveDevice').prop('checked');
	    if (checked) {
	        $('#movDevice').prop('disabled', false);
	    } else {
	        $('#movDevice').prop('disabled', true);
	    }
	}	
	
	function createSetChecked() {
	    var checked = $('#createSet').prop('checked');
	    if (checked) {
	        $('#creatSet').prop('disabled', false);
	    } else {
	        $('#creatSet').prop('disabled', true);
	    }
	}	
	
	function moveSetChecked() {
	    var checked = $('#moveSet').prop('checked');
	    if (checked) {
	        $('#movSet').prop('disabled', false);
	    } else {
	        $('#movSet').prop('disabled', true);
	    }
	}	
	
	function removeSetChecked() {
	    var checked = $('#removeSet').prop('checked');
	    if (checked) {
	        $('#remSet').prop('disabled', false);
	    } else {
	        $('#remSet').prop('disabled', true);
	    }
	}	
	
	function sendDRChecked() {
	    var checked = $('#sendDR').prop('checked');
	    if (checked) {
	        $('#drSend').prop('disabled', false);
	    } else {
	        $('#drSend').prop('disabled', true);
	    }
	}	
	
	function sendRestoreChecked() {
	    var checked = $('#sendRestore').prop('checked');
	    if (checked) {
	        $('#restoreSend').prop('disabled', false);
	    } else {
	        $('#restoreSend').prop('disabled', true);
	    }
	}	
	
	function getHierarchyChecked() {
	    var checked = $('#getHierarchy').prop('checked');
	    if (checked) {
	        $('#listHierarchy').prop('disabled', false);
	    } else {
	        $('#listHierarchy').prop('disabled', true);
	    }
	}	
	
	function authenticateChecked() {
	    var checked = $('#authenticate').prop('checked');
	    if (checked) {
	        $('#authenticateOp').prop('disabled', false);
	    } else {
	        $('#authenticateOp').prop('disabled', true);
	    }
	}	
	
	function runtimeReportChecked() {
	    var checked = $('#runtimeReport').prop('checked');
	    if (checked) {
	        $('#runtimeReportOp').prop('disabled', false);
	    } else {
	        $('#runtimeReportOp').prop('disabled', true);
	    }
	}	
	
	function assignThermostatChecked() {
	    var checked = $('#assignThermostat').prop('checked');
	    if (checked) {
	        $('#assignThermostatOp').prop('disabled', false);
	    } else {
	        $('#assignThermostatOp').prop('disabled', true);
	    }
	}	
	
	</script>
	<cti:url var="updateRequestUrl" value="update" />
	<form id="ecobeeForm" action="${updateRequestUrl}" method="post">
		<div class="column-8-8-8">
			<div class="column one">
				<tags:sectionContainer title="Endpoint">
					<ul class="simple-list">
						<li>
						<label><input type="checkbox" id="registerDevice" name="registerDevice" onclick="registerDeviceChecked()"><i:inline
									key=".registerDevice" /></label>
						</li>
						<li>
							<label> <input id="moveDevice" type="checkbox" name="moveDevice" onclick="moveDeviceChecked()"> <i:inline
									key=".moveDevice" />
							</label>
						</li>
						<li>
							<label> <input id="createSet" type="checkbox" name="createSet" onclick="createSetChecked()"> <i:inline
									key=".createSet" />
							</label>
						</li>
						<li>
							<label> <input id="moveSet"	type="checkbox" name="moveSet" onclick="moveSetChecked()"> <i:inline
									key=".moveSet" />
							</label>
						</li>
						<li>
							<label> <input id="removeSet" type="checkbox" name="removeSet" onclick="removeSetChecked()"> <i:inline
									key=".removeSet" />
							</label>
						</li>
						<li>
							<label> <input id="sendDR" type="checkbox" name="sendDR" onclick="sendDRChecked()"> <i:inline
									key=".sendDR" />
							</label>
						</li>
						<li>
							<label> <input id="sendRestore"	type="checkbox" name="sendRestore" onclick="sendRestoreChecked()"> <i:inline
									key=".sendRestore" />
							</label>
						</li>
						<li>
							<label> <input id="getHierarchy" type="checkbox" name="getHierarchy" onclick="getHierarchyChecked()"> <i:inline
									key=".getHierarchy" />
							</label>
						</li>
						<li>
							<label> <input id="authenticate" type="checkbox" name="authenticate" onclick="authenticateChecked()"> <i:inline
									key=".authenticate" />
							</label>
						</li>
						<li>
							<label> <input id="runtimeReport" type="checkbox" name="runtimeReport" onclick="runtimeReportChecked()"> <i:inline
									key=".runtimeReport" />
							</label>
						</li>
						<li>
							<label> <input id="assignThermostat" type="checkbox" name="assignThermostat" onclick="assignThermostatChecked()"> <i:inline
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
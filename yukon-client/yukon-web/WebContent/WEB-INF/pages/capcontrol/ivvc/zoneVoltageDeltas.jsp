<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneVoltageDeltas">
	<cti:includeCss link="/WebConfig/yukon/styles/da/ivvc.css" />

	<script>
		$(document).ready(function() {
		    $(document).on('click', '.viewDelta', function(e) {
				var currentTarget = $(e.currentTarget); 
				currentTarget.hide();
				currentTarget.next().show();
				currentTarget.next().find("input").focus();
				enableButtons();
			});

		    $(document).on('keydown', '.editDelta input', function(e) {
				if (e.which == 27) {
					/* Escape Key */
					cancelEdit(e);
				}
			});
		    
		    $(document).on('disable-buttons', function() {
		        disableButtons();
		    });
			
			$(document).on('click', 'input[type="checkbox"].editableStaticDelta', function(e) {
				$(e.currentTarget).toggleClass("staticChanged");
				$(e.currentTarget).parent("label").toggleClass("staticChangedLabel");

				if ($(".editDelta:visible").length == 0 &&
					$("input.staticChanged").length == 0) {
				    disableButtons();
				} else {
				    enableButtons();
				}
			});

			$(document).on('click', '.cancelEdit', cancelEdit);

			$(document).on('click','#deltaReset', function(e) {
				$(".editDelta").hide();
				$(".viewDelta").show();
				
				$("input[type='checkbox']").removeClass("staticChanged");
				$("label").removeClass("staticChangedLabel");
			});

			$(document).on('click','#deltaSubmitBtn', function() {
				if ($(".editDelta:visible").length == 0 &&
						$("input.staticChanged").length == 0) {
				    disableButtons();
					return;
				}
	
				var inputs = [];
				var index = 0;
				$("form#deltaForm tr").each(function() {
					var tr = $(this);
					var staticChanged = tr.find("input.staticChanged");
					var editDeltaInput = tr.find(".editDelta:visible input");
	
					if (staticChanged.length > 0 || editDeltaInput.length > 0) {
	
	            		params = {};
						params.newDelta = editDeltaInput.length > 0 ? editDeltaInput.val() : tr.find(".viewDelta input[type='hidden']").val();
	            		params.newStaticValue = staticChanged.is(":checked");
	        			params.bankId = tr.find("td.bankAndPointIds").find(".pointDeltaBankId").val();
	        			params.pointId = tr.find("td.bankAndPointIds").find(".pointDeltaPointId").val();
	
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].delta" value="'+ params.newDelta +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].staticDelta" value="'+ params.newStaticValue +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].pointId" value="'+ params.pointId +'"/>');
						inputs.push('<input type="hidden" name="pointDeltas['+index+'].bankId" value="'+ params.bankId +'"/>');
						index++;
					}
				});
				
				if (inputs.length > 0) {
					$('form#deltaForm').append(inputs.join(''));
					yukon.ui.blockPage();
					$('form#deltaForm').submit();
				}
			});
		});
		
		function cancelEdit(e) {
			$(e.currentTarget).closest("td").find(".editDelta").hide();
			$(e.currentTarget).closest("td").find(".viewDelta").show();
			if ($(".editDelta:visible").length == 0 &&
				$("input.staticChanged").length == 0) {
			    disableButtons();
			}
		}
		
		function disableButtons(){
		    $("#deltaFormButtons").find('button').prop('disabled', true);
		}
		function enableButtons(){
		    $("#deltaFormButtons").find('button').prop('disabled', false);
		}
	</script>

	<form:form id="deltaForm" action="/capcontrol/ivvc/zone/deltaUpdate" method="POST" commandName="zoneVoltageDeltas">
		<input type="hidden" name="zoneId" id="zoneId" value="${zoneId}">

        <div class="f-ajax-page stacked" data-reloadable="disable-buttons">
            <%@ include file="zoneVoltageDeltasTable.jsp" %>
        </div>
			<div id="deltaFormButtons" class="action-area clear">
				<cti:button id="deltaSubmitBtn" nameKey="update" type="submit" classes="primary action" disabled="true"/>
			    <cti:button nameKey="cancel" type="reset" id="deltaReset" disabled="true"/>
			</div>
    </form:form>
</cti:standardPage>

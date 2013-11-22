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
		jQuery(document).ready(function() {
		    jQuery(document).on('click', '.viewDelta', function(e) {
				var currentTarget = jQuery(e.currentTarget); 
				currentTarget.hide();
				currentTarget.next().show();
				currentTarget.next().find("input").focus();
				enableButtons();
			});

		    jQuery(document).on('keydown', '.editDelta input', function(e) {
				if (e.which == 27) {
					/* Escape Key */
					cancelEdit(e);
				}
			});
		    
		    jQuery(document).on('disable-buttons', function() {
		        disableButtons();
		    });
			
			jQuery(document).on('click', 'input[type="checkbox"].editableStaticDelta', function(e) {
				jQuery(e.currentTarget).toggleClass("staticChanged");
				jQuery(e.currentTarget).parent("label").toggleClass("staticChangedLabel");

				if (jQuery(".editDelta:visible").length == 0 &&
					jQuery("input.staticChanged").length == 0) {
				    disableButtons();
				} else {
				    enableButtons();
				}
			});

			jQuery(document).on('click', '.cancelEdit', cancelEdit);

			jQuery(document).on('click','#deltaReset', function(e) {
				jQuery(".editDelta").hide();
				jQuery(".viewDelta").show();
				
				jQuery("input[type='checkbox']").removeClass("staticChanged");
				jQuery("label").removeClass("staticChangedLabel");
			});

			jQuery(document).on('click','#deltaSubmitBtn', function() {
				if (jQuery(".editDelta:visible").length == 0 &&
						jQuery("input.staticChanged").length == 0) {
				    disableButtons();
					return;
				}
	
				var inputs = [];
				var index = 0;
				jQuery("form#deltaForm tr").each(function() {
					var tr = jQuery(this);
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
					jQuery('form#deltaForm').append(inputs.join(''));
					Yukon.ui.blockPage();
					jQuery('form#deltaForm').submit();
				}
			});
		});
		
		function cancelEdit(e) {
			jQuery(e.currentTarget).closest("td").find(".editDelta").hide();
			jQuery(e.currentTarget).closest("td").find(".viewDelta").show();
			if (jQuery(".editDelta:visible").length == 0 &&
				jQuery("input.staticChanged").length == 0) {
			    disableButtons();
			}
		}
		
		function disableButtons(){
		    jQuery("#deltaFormButtons").find('button').prop('disabled', true);
		}
		function enableButtons(){
		    jQuery("#deltaFormButtons").find('button').prop('disabled', false);
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

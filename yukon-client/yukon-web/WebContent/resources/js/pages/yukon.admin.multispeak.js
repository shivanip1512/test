yukon.namespace('yukon.admin.multispeak');

/**
 * Module that manages the Yukon Setup and Vendor Setup tabs for multispeak under Admin menu.
 * @module yukon.admin.multispeak
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
yukon.admin.multispeak = (function () {
	'use strict';
	 var
	    _initialized = false;
	    mod = {
	    		enableEndpointValue : function (selected, mspInterface) {
	    			
	    					if(document.getElementById("interfaceURL"+mspInterface+"V3") != null) {
	    						document.getElementById("interfaceURL"+mspInterface+"V3").disabled = !selected;
	    					} else if(document.getElementById("interfaceURL"+mspInterface+"V5") != null) {
	    						document.getElementById("interfaceURL"+mspInterface+"V5").disabled = !selected;
	    					}
	    				if (document.getElementById("select"+mspInterface) != null) {
	    				   document.getElementById("select"+mspInterface).disabled = !selected;
	    				}
	    				if(document.getElementById("ping"+mspInterface) != null){
	    					document.getElementById("ping"+mspInterface).disabled = !selected;
	    				}
	    				if(document.getElementById("getMethods"+mspInterface) != null){
	    					document.getElementById("getMethods"+mspInterface).disabled = !selected;
	    				}
	    				
	            },
	            
	            enableExtension: function (selected) {
	                
	                document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
	            },
	            
	            executeRequest : function (service, call, version) {
	            	$('#actionService').val(service);
	            	var formData = $('#mspForm').serialize();
	                $.ajax({
	                    url: yukon.url('/multispeak/setup/' + call + '/' + version),
	                    type: 'post',
	                    data: formData 
	                }).done(function(data) {
	                	$('#results').css('color', data.resultColor);
	                	$('#results').val(data.MSP_RESULT_MSG);
	                }).fail(function(data) {
	                    $('#start-simulator').removeAttr("disabled");
	                    if (data.hasError) {
	                        $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
	                    } else {
	                        $('#taskStatusMessage').hide();
	                    }
	                });
	            },
	            
	            vendorChanged : function () {
	            	document.mspForm.action = yukon.url("/multispeak/setup/vendorHome/"+ $('#mspVendorId').val());
	                document.mspForm.submit();
	            },
	            
	            showHideData : function (id, showData) {
	               $('#'+id).attr('type', showData ? 'text' : 'password');   
	            },
	            
	            /**
	             * Initializes the module, hooking up event handlers to components.
	             * Depends on localized text in the jsp, so only run after DOM is ready.
	             */
	            init: function () {
	            	 $(document).on('click', '.js-eye-icon', function() {
	 	                var targetRow = $(this).closest('.switch-btn');
	 	                var id = targetRow.find('.switch-btn-checkbox').attr('id');
	 	                var isSelected = targetRow.find('.switch-btn-checkbox').prop('checked');
	 	               yukon.admin.multispeak.showHideData(id, !isSelected);
	 	            });
	            	 $('#vendor-edit').click(function(ev) {
	 	                	document.mspForm.action = yukon.url("/multispeak/setup/editVendorSetup/"+ $('#mspVendorId').val());
	 	                    document.mspForm.submit();
	                 });
	            	 
	            	 $(document).on('yukon:multispeak:vendor:delete', function () {
	            		 $('#delete-vendor').submit();
	                 });
	                if (_initialized) return;
	                _initialized = true;
	            }
	        };
return mod;
}());

$(function () { yukon.admin.multispeak.init(); });
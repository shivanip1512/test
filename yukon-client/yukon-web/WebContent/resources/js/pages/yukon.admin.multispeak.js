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
	    		enableEndpointValue : function (isNew, selected, mspInterface) {
	    			
	    				if(mspInterface !== 'NOT_Server'){
	    	                document.getElementById("mspEndpoint"+mspInterface).disabled = !selected;
	    	                //document.getElementById(mspInterface).disabled = !selected;
	    	                $('.'+mspInterface).prop('disabled', !selected);
	    				}
	    	            document.getElementById("mspEndpoint"+mspInterface+"v5").disabled = !selected;
	            },
	            
	            enableExtension: function (selected) {
	                
	                document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
	            },
	            
	            executeRequest : function (service, call) {
	            	debugger;
	            	document.mspForm.actionService.value = service;
  	                document.mspForm.action = yukon.url("/multispeak/setup/" + call);
	                document.mspForm.submit();
	            },
	            
	            vendorChanged : function () {
	            	
	            	document.mspForm.action = yukon.url("/multispeak/setup/vendorHome");
	                document.mspForm.submit();
	            },
	            
	            confirmDelete : function () {
	                if (confirm("Are you sure you want to delete this interface?")) {
	                    $('#delete-form').submit();
	                }
	            },
	            
	           

	            showHideData : function (id, showData) {
	            	debugger;
	               $('#'+id).attr('type', showData ? 'text' : 'password');   
	            },

	            
	            /**
	             * Initializes the module, hooking up event handlers to components.
	             * Depends on localized text in the jsp, so only run after DOM is ready.
	             */
	            init: function () {
	            	 $(document).on('click', '.js-eye-icon', function() {
	            		 debugger;
	 	                var targetRow = $(this).closest('.switch-btn');
	 	                var id = targetRow.find('.switch-btn-checkbox').attr('id');
	 	                var isSelected = targetRow.find('.switch-btn-checkbox').prop('checked');
	 	               yukon.admin.multispeak.showHideData(id, !isSelected);
	 	            });
	            	 
	                if (_initialized) return;
	                _initialized = true;
	            }
	        };
return mod;
}());

$(function () { yukon.admin.multispeak.init(); });
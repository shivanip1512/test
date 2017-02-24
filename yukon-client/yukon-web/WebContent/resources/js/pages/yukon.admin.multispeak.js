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
	    				if (mspInterface !== 'NOT_Server') {
	    					if(document.getElementById(mspInterface+"3.0") != null) {
	    						document.getElementById(mspInterface+"3.0").disabled = !selected;
	    					}
	    	                $('.'+mspInterface).prop('disabled', !selected);
	    				}
	    				if (document.getElementById("select"+mspInterface) != null) {
	    				   document.getElementById("select"+mspInterface).disabled = !selected;
	    				}
	    				if(mspInterface == 'LM_Server') {
	    					mspInterface = "DR_Server";
	    				}
	    	            document.getElementById(mspInterface+"5.0").disabled = !selected;
	            },
	            
	            enableExtension: function (selected) {
	                
	                document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
	            },
	            
	            executeRequest : function (service, call, version) {
	            	document.mspForm.version.value = version;
	            	document.mspForm.actionService.value = service;
  	                document.mspForm.action = yukon.url("/multispeak/setup/" + call);
	                document.mspForm.submit();
	            },
	            
	            vendorChanged : function () {
	            	
	            	document.mspForm.action = yukon.url("/multispeak/setup/vendorHome");
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
	            	 
	            	 $(document).on('yukon:multispeak:vendor:delete', function () {
	            		 $('#delete-form').submit();
	                 });
	                if (_initialized) return;
	                _initialized = true;
	            }
	        };
return mod;
}());

$(function () { yukon.admin.multispeak.init(); });
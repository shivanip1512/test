yukon.namespace('yukon.admin.multispeak');

/**
 * Module that manages the substations and route mapping on the substations page under Admin menu.
 * @module yukon.admin.substations
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
	    			debugger;
	    			if (!isNew) {
	    	             if( selected ) {
	    	                document.getElementById("mspPing"+mspInterface).style.cursor = 'pointer';
	    	                document.getElementById("mspMethods"+mspInterface).style.cursor = 'pointer';
	    	                document.getElementById("mspPing"+mspInterface).href = 'JavaScript:serviceSubmit(\"'+mspInterface+'\", \"pingURL\");'
	    	                document.getElementById("mspMethods"+mspInterface).href = 'JavaScript:document.mspForm.submit()'
	    	            } else {
	    	                document.getElementById("mspPing"+mspInterface).style.cursor = 'default';
	    	                document.getElementById("mspMethods"+mspInterface).style.cursor = 'default';
	    	                document.getElementById("mspPing"+mspInterface).href = 'javascript:;'
	    	                document.getElementById("mspMethods"+mspInterface).href = 'javascript:;'
	    	            }
	    	         }
	    	            document.getElementById("mspEndpoint"+mspInterface).disabled = !selected;
	    	            document.getElementById("mspEndpoint"+mspInterface+"v5").disabled = !selected;
	            },
	            
	            enableExtension: function (selected) {
	                
	                document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
	            },
	            
	            executeRequest : function (service, call) {
	            	
	            	document.mspForm.actionService.value = service;
  	                document.mspForm.action = yukon.url("/multispeak/setup/" + call);
	                document.mspForm.submit();
	            },
	            
	            vendorChanged : function () {
	            	
	            	document.mspForm.action = yukon.url("/multispeak/setup/home");
	                document.mspForm.submit();
	            },
	            
	            /**
	             * Initializes the module, hooking up event handlers to components.
	             * Depends on localized text in the jsp, so only run after DOM is ready.
	             */
	            init: function () {
	            	
	                if (_initialized) return;
	                _initialized = true;
	            }
	        };
return mod;
}());

$(function () { yukon.admin.multispeak.init(); });
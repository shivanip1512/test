yukon.namespace('yukon.admin.substations');

/**
 * Module that manages the substations and route mapping on the substations page under Admin menu.
 * @module yukon.admin.substations
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
yukon.admin.substations = (function () {
	 var
	    _initialized = false,
	    removedRouteIds=[];
	    mod = {
	            
	            /**
	             * Initializes the module, hooking up event handlers to components.
	             * Depends on localized text in the jsp, so only run after DOM is ready.
	             */
	            init: function () {
	            	/** Move row up. */
	                $(document).on('click','.js-up', function (ev) {
	                	debugger;
	                    var row = $(this).closest('tr'),
	                        prevRow = row.prev();
	                    
	                    row.insertBefore(prevRow);
	                    yukon.ui.reindexInputs(row.closest('table'));
	                    
	                });
	                
	                /** Move row down. */
	                $(document).on('click','.js-down', function (ev) {
	                    var row = $(this).closest('tr'),
	                        nextRow = row.next();
	                    
	                    row.insertAfter(nextRow);
	                    yukon.ui.reindexInputs(row.closest('table'));
	                    
	                });
	                
	                /** Remove attribute button clicked, remove row and re-index the rest. Update any fields necessary */
	                $(document).on('click', '#routes-table .js-remove', function (ev) {
	                	var row = $(this).closest('tr');
	                	//
	                	var routeId = $(this).find("input").val();
	                	removedRouteIds.push(routeId);
	                	alert(removedRouteIds);
	                	//
	                    var table = row.closest('table');
	                    row.remove();
	                    yukon.ui.reindexInputs(table);
	                });
	                
	                $('#b-add-route').click(function (ev) {
	                    $('#selectedRoutes :selected').each(function(i, selected){ 
	                    	
	                    	var id = $(selected).val();
	                    	
	                    	var name = $(selected).text();
	                    	var row = $('#route-template tr').clone(),
	                        field = row.find('td:first-child');
	                    	field.find('input').val(id);
	                    	field.find('input').addClass('routeId');
	                        field.append('<span>' + name + '</span>');
	                        $('#routes-table tbody').append(row);
	                        yukon.ui.reindexInputs(row.closest('table'));
	                        selected.remove();
	                    });
	                    
	                    
	                });
	                $('#saveAllRoutes').click(function (ev) {
	                	var routeIds=[];
	                	
	                	$('#routes-table > tbody  > tr').each(function() {
	                		
	                		var routeId = $(this).find("input").val();
	                		routeIds.push(routeId);
	                		});
	                    
	                	var substationId=$("#substation").val();
	                    $.ajax({
	                        url: yukon.url('/admin/substations/routeMapping/save'),
	                        method: 'post',
	                        data: { routeIds: routeIds, substationId: substationId}
	                    }).done(function () {
	                    	$("div.user-message").remove();
	                    	$('#saveStatusMessage').addMessage({message:'Changes Saved Successfully', messageClass:'success'}).show();
	                    	yukon.ui.unbusy('#saveAllRoutes');
	                    });

	                });
	                submitTypeSelect = function() {
	                	document.substationForm.action = yukon.url("/admin/substations/routeMapping/view");
	                    document.substationForm.submit();
	                };
	               
	                if (_initialized) return;
	                _initialized = true;
	            }
	        };
return mod;
}());

$(function () { yukon.admin.substations.init(); });
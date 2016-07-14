yukon.namespace('yukon.substation.mappings');

/**
 * This module handles behavior on the device configuration pages.
 * @module yukon.substation.mappings
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.dialog.confirm
 * 
 */
yukon.substation.mappings = (function () {

    /**
     * Reload Mapping Table
     */
    var _reloadAllMappingsTable = function () {
            var sortColumnLink = $('[data-mappings-table]').find('.sortable.desc, .sortable.asc'),
                dir = sortColumnLink.is('asc') ? 'asc' : 'desc',
                sort = sortColumnLink.data('sort'),
                container = sortColumnLink.closest('[data-url]'),
                url = container.data('url'),
                params = {'sort': sort, 'dir': dir};

            container.load(url, params);
        },

        /**
         * Get names of strategyName and substation and fire a callback.
         * @param {function} [callback] - A callback function to fire after the Json data is fetched.
         */
        _getMappedName = function (callback) {
            $.getJSON(yukon.url('/multispeak/setup/lmMappings/find-mapping'),
                {
                    'strategyName': $('.js-strategy.js-mapping-input').val(),
                    'substationName': $('.js-substation.js-mapping-input').val()
                })
                .done(function (data, textStatus, jqXHR) {
                    callback(data);
                });
        },
        
        _getMappedId = function (callback) {
        	$.getJSON(yukon.url('/multispeak/setup/lmMappings/find-mappingId'),
                    {
                        'strategyName': $('.js-strategy-popup.js-edit-mapping-popup').val(),
                        'substationName': $('.js-substation-popup.js-edit-mapping-popup').val()
                    })
                    .done(function (data, textStatus, jqXHR) {
                        callback(data);
                    });
        },

        mod = {
            init : function () {
                var addBtn = $('.js-add-btn');

                yukon.dialogConfirm.add({
                    'on': '.js-add-btn',
                    'eventType': 'yukon_substation_mappings_confirm_add',
                    'strings' : {
                        'message': addBtn.data('message'),
                        'title': addBtn.data('title'),
                        'ok': yg.text.ok,
                        'cancel': yg.text.cancel
                    }
                });

                $(document).on('click', '.js-add-btn', function () {
                    var btn = $(this),
                    	errors = false,
                        strategyName = $('.js-strategy.js-mapping-input').val(),
                        substationName = $('.js-substation.js-mapping-input').val();

                    $('.js-mapping-errors').hide();
                    $('.js-mapping-input').removeClass('error');

                    if (strategyName === '') {
                    	errors = true;
                        $('.js-strategy').show().addClass('error');
                    }
                    if (substationName === '') {
                    	errors = true;
                        $('.js-substation').show().addClass('error');
                    }

                    if (errors) {
                        yukon.ui.unbusy(btn);
                    } else {
                        _getMappedName(function (data) {
                            yukon.ui.unbusy(btn);
                            if (data.found) {
                                btn.trigger('yukon_substation_mappings_confirm_add');
                            } else {
                                btn.trigger('yukon_substation_mappings_add');
                            }
                        });
                    }
                });
                
                $(document).on('click','.js-edit-mapping', function() {
                	var rowId = $(this).attr('data-mapping-id');
                		$('.js-strategy-popup.js-edit-mapping-popup').val($('#strategy'+rowId).html());
                		$('.js-substation-popup.js-edit-mapping-popup').val($('#substation'+rowId).html());
                		$('span.mapped-pao-name-popup').text($('#tpao-name'+rowId).html());
                		_getMappedId(function (data) {
                			if(data.found){
                				$('#mappedNameId-popup').val(data.mappedNameId);
                			}
                		});
                		$('.js-edit-mapping-popup').removeClass('error');
                		$('#edit-mapping').attr('data-mapping-id', rowId);
                });
                
                $(document).on('click', '.js-edit-btn', function () {
                    var btn = $(this),
                    	errors = false,
                        strategyName = $('.js-edit-mapping-popup.js-strategy-popup').val(),
                        substationName = $('.js-edit-mapping-popup.js-substation-popup').val();

                    $('.js-edit-mapping-popup-errors').hide();
                    $('.js-edit-mapping-popup').removeClass('error');

                    if (strategyName === '') {
                    	errors = true;
                        $('.js-edit-mapping-popup-errors.js-strategy-popup').show().addClass('error');
                    }
                    if (substationName === '') {
                    	errors = true;
                        $('.js-edit-mapping-popup-errors.js-substation-popup').show().addClass('error');
                    }
                    
                    if(!errors){
                        paoPickerEditPopup.show();                    	
                    }
                    yukon.ui.unbusy(btn);
                });
                
                $(document).on('input', '.js-mapping-input', function () {
                    var paoName = $('.mapped-pao-name');
                    paoName.addClass('disabled');
                    _getMappedName(function (data) {
                        var addBtnText = addBtn.find('.b-label');
                        if (data.found) {
                            paoName.text(data.mappedName);
                            addBtnText.text(addBtn.data('editText'));
                        } else {
                            paoName.text(paoName.data('emptyText'));
                            addBtnText.text(addBtn.data('addText'));
                        }
                        paoName.removeClass('disabled');
                    });
                });

                $(document).on('yukon_substation_mappings_add', function () {
                    paoPicker.show();
                });
                
                $(document).on('yukon.substation.mappings.delete', function (ev) {
                    var mappingId = $(ev.target).data('mappingId');

                    $.getJSON(yukon.url('/multispeak/setup/lmMappings/removeMapping'),
                        {'mspLMInterfaceMappingId': mappingId}
                    ).done(function (data, textStatus, jqXHR) {
                        _reloadAllMappingsTable();
                    });
                });
                
                $(document).on('yukon.substation.mappings.updateMap', function(ev) {
                	 var mappingId = $(ev.target).attr('data-mapping-id'),
                	 	strategyName = $('.js-edit-mapping-popup.js-strategy-popup').val().trim(),
                	 	substationName = $('.js-edit-mapping-popup.js-substation-popup').val().trim(),
                	 	mappedNameId = $('#mappedNameId-popup').val(),
                	 	errors = false;

                 $('.js-mapping-errors-popup').hide();
                 $('.js-edit-mapping-popup').removeClass('error');

                 if (strategyName === '') {
                 	errors = true;
                     $('.js-strategy-popup').show().addClass('error');
                 }
                 if (substationName === '') {
                 	errors = true;
                     $('.js-substation-popup').show().addClass('error');
                 }
                 
                 if(!errors){
	                	 if(mappedNameId === ''){
                			 return;
                		 }
	
		                 $.getJSON(yukon.url('/multispeak/setup/lmMappings/updateMappingById'),
		                     {
		                	     'mappingId': mappingId,
		                         'strategyName': strategyName,
		                         'substationName': substationName,
		                         'mappedNameId': mappedNameId
		                     }
		                 ).done(function (data, textStatus, jqXHR) {
		                	 if("error" in data){
		                		 $('div.js-map-edit-warning').removeClass('dn');
		                	 }else{
		                		 $('div.js-map-edit-warning').addClass('dn');
		                		 _reloadAllMappingsTable();
		                	 }
		                 });
                	
		                 $('div#edit-mapping.dn.ui-dialog-content.ui-widget-content').parent().remove();
                 	}
                });
                
            },

            /**
             * Sets Mapped Name Id and reloads the mapping table.
             */
            setMappedNameId : function () {
                var strategyName = $('.js-strategy.js-mapping-input').val().trim(),
                    substationName = $('.js-substation.js-mapping-input').val().trim(),
                    mappedNameId = $('#mappedNameId').val().trim();

                if ($('#mappedNameId').val() === '') {
                    return;
                }

                $.getJSON(yukon.url('/multispeak/setup/lmMappings/addOrUpdateMapping'),
                    {
                        'strategyName': strategyName,
                        'substationName': substationName,
                        'mappedNameId': mappedNameId
                    }
                ).done(function (data, textStatus, jqXHR) {
                    $('.mapped-pao-name').text($('#mappedName').val());
                    _reloadAllMappingsTable();
                });
            },
            /**
             * Sets Mapped Name Id  in pop-up 
             */
            setMappedNameIdOnEdit : function () {            	
                	$('span.mapped-pao-name-popup').text($('#mappedName-popup').val().trim());
            }
        };

    return mod;
}());

$(function () { yukon.substation.mappings.init(); });
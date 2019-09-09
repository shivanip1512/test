yukon.namespace('yukon.ami.meterProgramming.summary');
/**
 * Module for the Meter Programming Summary pages
 * @module yukon.ami.meterProgramming.summary
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.meterProgramming.summary = (function () {
    

    'use strict';
    var _initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {
                                
                if (_initialized) return;
                
                $('.js-selected-programs').chosen({width: "450px"});
                
                $(document).on('click', '.js-delete-program', function() {
                    var guid = $(this).data('programGuid');
                    $.ajax({
                        url: yukon.url('/amr/meterProgramming/' + guid + '/delete'),
                        type: 'delete'
                    }).done(function () {
                        window.location.reload();
                    });    
                });
                
                $(document).on('click', '.js-filter-programs', function() {
                    var selectedOptions = $('.js-selected-programs option:selected'),
                        length = selectedOptions.length,
                        tableContainer = $('#results-container'),
                        form = $('#filter-form');
                    //remove all hidden fields first
                    form.find('input:hidden').remove();
                    for (var i = 0; i < length; i++) {
                        var program =  selectedOptions[i],
                            guid = program.dataset.guid,
                            source = program.dataset.source,
                            name = program.text;
                        form.append('<input type="hidden" name="programs[' + i + '].guid" value="' + guid + '" />');
                        form.append('<input type="hidden" name="programs[' + i + '].name" value="' + name + '" />');
                        form.append('<input type="hidden" name="programs[' + i + '].source" value="' + source + '" />');
                    }
                    form.ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            tableContainer.html(data);
                            tableContainer.data('url', yukon.url('/amr/meterProgramming/summaryFilter?' + form.serialize()));
                        }
                    });   
                });

                _initialized = true;

            }

    };

    return mod;

})();

$(function () { yukon.ami.meterProgramming.summary.init(); });
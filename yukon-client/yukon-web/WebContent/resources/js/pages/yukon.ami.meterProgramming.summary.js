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
                
                $(document).on('click', '.js-delete-program', function() {
                    var guid = $(this).data('programGuid');
                    $.ajax({
                        url: yukon.url('/amr/meterProgramming/' + guid + '/delete'),
                        type: 'delete'
                    }).done(function () {
                        window.location.reload();
                    });    
                });

                _initialized = true;

            }

    };

    return mod;

})();

$(function () { yukon.ami.meterProgramming.summary.init(); });
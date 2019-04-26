yukon.namespace('yukon.da.strategy');

/**
 * Module that manages the strategy page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.strategy = (function () {

    'use strict';

    var _algorithmToSettings = {};
    var _methodToAlgorithms = {};


    var _algorithmSetup = function () {

        var algorithm = $('#control-algorithm').val();
        var method = $('#control-method').val();
        var settings = _algorithmToSettings[algorithm];

        $('#kvarMessage').hide();
        $('[data-mapping]').hide().find('input').prop('disabled', true);

        settings.forEach(function (setting) {
            $('[data-mapping="' + setting + '"]').show().find('input').prop('disabled', false);
        });
        
        $('.js-time-of-day-only').toggle(algorithm === 'TIME_OF_DAY');
        $('.js-not-time-of-day').toggle(algorithm !== 'TIME_OF_DAY');
        $('.js-ivvc-only').toggle(algorithm === 'INTEGRATED_VOLT_VAR');
        $('.js-bus-ivvc-only').toggle(algorithm === 'INTEGRATED_VOLT_VAR' &&
                                      method === 'BUSOPTIMIZED_FEEDER');
        $('.js-max-delta').toggle(algorithm === 'INTEGRATED_VOLT_VAR' || algorithm === 'MULTI_VOLT_VAR' || algorithm === 'MULTI_VOLT');
        
        if (algorithm == 'KVAR' || algorithm == 'MULTI_VOLT_VAR') {
           $('#kvarMessage').show();
        }
    };
    
    var _methodSetup = function () {

        var method = $('#control-method');
        var algorithm = $('#control-algorithm');
        var algorithms = _methodToAlgorithms[method.val()];

        algorithm.find('option').prop('disabled', true);
        
        algorithms.forEach(function (item) {
            algorithm.find('option[value="' + item + '"]').prop('disabled', false);
        });
        
        if (algorithms.indexOf(algorithm.val()) === -1) {
            algorithm.val(algorithms[0]).trigger('change');
        }
        
        $('.js-bus-ivvc-only').toggle(algorithm.val() === 'INTEGRATED_VOLT_VAR' &&
                                      method.val() === 'BUSOPTIMIZED_FEEDER');
    };
    
    var mod = {

        init : function () {
            
            _methodToAlgorithms = yukon.fromJson('#method-to-algorithms');
            _algorithmToSettings = yukon.fromJson('#algorithm-to-settings');
            
            _algorithmSetup();
            _methodSetup();
            
            $('#control-algorithm').on('change', function () {
                _algorithmSetup();
            });
            
            $('#control-method').on('change', function () {
                _methodSetup();
            });
            
            $(document).on('yukon:da:strategy:delete', function () {
                $('#delete-strategy').submit();
            });
        }
    };

    return mod;
}());

$(function () { yukon.da.strategy.init(); });

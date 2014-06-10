/**
 * Handles display of gear names in control history detail popups
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.assets.controlHistory.detail');

yukon.assets.controlHistory.detail = (function () {
    
    var _initialized = false, 
        mod;
    
    mod = {
        
        init: function () {
            if (_initialized) {
                return;
            }
            
            $(function () {
                mod.updateControlEvents('PAST_DAY');
            });
            
            $(document).on('click', '.js-show-details', function (ev) {
                var link = $(this),
                    popup = link.data('popup'),
                    programId, 
                    startDates = [],
                    endDates = [];
                
                if (typeof popup === 'undefined') {
                    popup = link.prev();
                    link.data('popup', popup);
                }
                
                programId = popup.find('input[name=programId]').val(),
                popup.find('input[name=startDate]').each(function (index, item) {
                    startDates.push($(item).val());
                });
                popup.find('input[name=endDate]').each(function (index,item) {
                    endDates.push($(item).val());
                });
                
                $.get(yukon.url('stars/operator/program/controlHistory/controlHistoryEventGearName'), 
                        {programId: programId, startDates: startDates.toString(), endDates: endDates.toString()},
                        function (data) {
                            var gearNames = $.parseJSON(data);
                            popup.find('.js-gear-names').each(function (index, item) {
                                $(item).html(gearNames[index]);
                            });
                        },
                        'text');
                popup.dialog({width:650});
            });
            _initialized = true;
        },
        
        updateControlEvents: function (controlPeriod) {
            var accountId,
                programId,
                past;
            yukon.ui.elementGlass.show($('.js-control-events'));
            accountId = $('input[name=accountId]').val();
            programId = $('input[name=programId]').val();
            past = $('input[name=past]').val();
            
            $.ajax({
                url: yukon.url('stars/operator/program/controlHistory/innerCompleteHistoryView'),
                method: 'POST',
                data: { 
                    'programId': programId,
                    'past': past,
                    'accountId': accountId,
                    'controlPeriod': controlPeriod
                }
            }).done(function(data){
                $('.js-control-events').html(data);
            }).always(function() {
                yukon.ui.elementGlass.hide($('.js-control-events'));
            });
        }
    };
    
    return mod;
}());

$(function () { yukon.assets.controlHistory.detail.init(); });
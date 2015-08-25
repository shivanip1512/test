/**
 * Handles display of gear names in control history detail popups
 * 
 * @requires JQUERY
 * @requires JQUERYUI
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
            
            mod.updateControlEvents('PAST_DAY');

            /** This function gathers control history start and end dates from all of the detail control events 
             * contained in a single grouped control history event.  It passes the start/end dates to the server
             * where the historical gear names are retrieved up and returned via AJAX to be displayed in a dialog.*/
            $(document).on('click', '.js-show-details', function (ev) {
                var link = $(this),
                    popup = link.data('detailsPopup'),
                    programId, 
                    startDates = [],
                    endDates = [];
                
                if (typeof popup === 'undefined') {
                    popup = link.prev();
                    link.data('detailsPopup', popup);
                }
                
                programId = popup.find('[data-program-id]').data('programId');
                
                popup.find('[data-start-date]').each(function (index, item) {
                    startDates.push($(item).data('startDate'));
                });
                popup.find('[data-end-date]').each(function (index,item) {
                    endDates.push($(item).data('endDate'));
                });
                
                var params = {programId: programId, startDates: startDates.toString(), endDates: endDates.toString()}; 
                $.getJSON(yukon.url('/stars/operator/program/controlHistory/controlHistoryEventGearName'), params)
                    .done(function (gearNames) {
                        popup.find('.js-gear-names').each(function (index, item) {
                            $(item).html(gearNames[index]);
                        });
                    });
                popup.dialog({ width: 650 });
            });
            _initialized = true;
        },
        
        /** Retrieves a collection of control history event data for the control period selected by the user
         * and replaces the contents of the program control history detail view with the new data set  */
        updateControlEvents: function (controlPeriod) {
            var accountId,
                programId,
                past, 
                data;
            yukon.ui.block($('.js-control-events'));
            accountId = $('[data-account-id]').data('accountId');
            programId = $('[data-program-id]').data('programId');
            past = $('[data-past]').data('past');
            
            data = {'accountId': accountId, 'programId': programId, 'past': past, 'controlPeriod': controlPeriod};
            
            $('.js-control-events').load(yukon.url('/stars/operator/program/controlHistory/innerCompleteHistoryView'),
                data,
                function() {
                    yukon.ui.unblock($('.js-control-events'));
                });
        }
    };
    
    return mod;
}());

$(function () { yukon.assets.controlHistory.detail.init(); });
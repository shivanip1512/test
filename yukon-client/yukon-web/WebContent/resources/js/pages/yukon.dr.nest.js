yukon.namespace('yukon.dr.nest');

/**
 * Module that serves the nest details page
 * @module   yukon.dr.nest
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 */
yukon.dr.nest = (function () {

    mod = null;
    _updateInterval = 6000,
    _updateTimeout = null;
    
    _update = function () {

            $.ajax({
                url: yukon.url('/dr/nest/updateButton'),
                async: false
            }).done(function (data) {
                var syncButton = $('#syncButton');
                syncButton.attr('disabled', !data.syncButtonEnabled);
            });
        

    
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, _updateInterval);
        
    };
    
    mod = {

        init: function () {
            
            _update();
            
            $(document).on('click', '.nest-sync-button-group-toggle .button', function () {
                var scheduledSyncOn = $('.nest-sync-button-group-toggle .button.yes').hasClass('on');
                if (scheduledSyncOn) {
                    $('#nest-sync').val('true');
                    $('.nest-sync-time-slider').show('fade');
                } else {
                    $('#nest-sync').val('false');
                    $('.nest-sync-time-slider').hide('fade');
                }
            });
            
            $(document).on('change', 'input[name=types], select[name=syncId]', function() {
                var formData = $('#discrepancyForm').serialize(),
                    table = $('#discrepancyContainer'),
                    params = {};
                yukon.ui.getSortingPagingParameters(params);
                $.ajax({
                    url: yukon.url('/dr/nest/discrepancies?' + $.param(params)),
                    data: formData
                }).done(function (data) {
                    table.html(data);
                    table.data('url', yukon.url('/dr/nest/discrepancies?' + formData));
                    //update the badge with the correct number of results
                    var count = $('#badgeCount').val();
                    $('.js-count').html(count);
                    //hide the cog if no discrepancies
                    $('.js-cog-menu').toggleClass('dn', count == 0);
                });  
            });
            
            $(document).on('click', '.js-download', function () {
                var formData = $('#discrepancyForm').serialize();
                window.location = yukon.url('/dr/nest/download?' + formData);
            });
            
            if ('true' === $('#nest-sync').val()) {
                $('.nest-sync-button-group-toggle .yes').addClass('on');
                $('.nest-sync-button-group-toggle .no').removeClass('on');
                $('.nest-sync-time-slider').show();
            } else {
                $('.nest-sync-button-group-toggle .no').addClass('on');
                $('.nest-sync-button-group-toggle .yes').removeClass('on');
                $('.nest-sync-time-slider').hide();
            }
            $('#nest-sync-settings-row').removeClass('dn');
        }
    };
    return mod;
})();

$(function () { yukon.dr.nest.init(); });

yukon.namespace('yukon.assets.rtu');

/**
 * Module that handles the behavior on the RTU Details page
 * @module yukon.assets.rtu
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.rtu = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    /**
     * @type Array.number
     * Array of ids of comm ports that use tcp fields
     */
    tcpCommPorts = [];
    var updateCommPortFields = function () {
        var commPort = +$('#comm-port').val();
        var isTcp = tcpCommPorts.indexOf(commPort) !== -1;
        $('[data-tcp-port]').toggleClass('dn', !isTcp);
    };

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** User clicked on one of the show hide buttons on the Child Hierarchy tab */
            $(document).on('click', '.js-show-hide', function () {
                var paoId = $(this).attr('data-paoId'),
                    pointsDiv = $('.js-points-' + paoId),
                    childPointsDiv = pointsDiv.find('.js-child-points'),
                    borderedDiv = pointsDiv.find('.bordered-div'),
                    icon = $(this).find('i');
                //check if points are already visible
                if (borderedDiv.is(":visible")) {
                    childPointsDiv.addClass('dn');
                    icon.removeClass('icon-collapse');
                    icon.addClass('icon-expand');
                } else {
                    $.ajax({ url: yukon.url('/stars/rtu/child/' + paoId + '/points') })
                    .done(function (details) {
                        pointsDiv.html(details);
                        icon.removeClass('icon-expand');
                        icon.addClass('icon-collapse');
                    });
                }
            });
            
            tcpCommPorts = yukon.fromJson('#tcp-comm-ports');
            
            /** User change comm channel dropdown value */
            $('#comm-port').on('change', updateCommPortFields);
            updateCommPortFields();
            
            /** User clicked on the All Points tab */
            $(document).on('click', '.js-all-points-tab', function () {
                var rtuId = $('#rtuId').val();
                $.ajax({ url: yukon.url('/stars/rtu/' + rtuId + '/allPoints') })
                .done(function (details) {
                    $('.js-all-points').html(details);
                });
            });
            
            /** User filtered the All Points tab */
            $(document).on('click', '.js-filter', function (ev) {
                var tableContainer = $('.js-all-points'),
                    rtuId = $('#rtuId').val(),
                    form = $('#rtuAllPoints');
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/stars/rtu/' + rtuId + '/allPoints?' + form.serialize()));
                    }
                });
            });
            
            $(document).on('click', '#cancel-btn', function (event) {
                window.history.back();
            });
            
            $(document).on('click', '#commander-menu-option', function (ev) {
                var params = {
                    target: 'DEVICE',
                    paoId: $('#rtuId').val()
                };
                $.ajax({
                    type: 'POST',
                    url: yukon.url('/tools/commander/updateCommanderPreferences'),
                    data: params 
                }).done(function() {
                    window.location.href = yukon.url('/tools/redirectToCommander');
                });
            });
            $(document).on("yukon:rtu:delete", function () {
                yukon.ui.blockPage();
                $('#delete-rtu-form').submit();
            });
            $(document).on("yukon:rtu:copy", function () {
                yukon.ui.blockPage();
                $('#rtu-copy-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href=yukon.url('/stars/rtu/' + data.paoId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#copy-rtu-popup').html(xhr.responseText);
                        yukon.ui.initContent('#copy-rtu-popup');
                        yukon.ui.unblockPage();
                    }
                });
            });
            // Initiablize the data-url for that table that displays all the RTUs.
            $('#rtu-list-container').data('url', yukon.url('/stars/rtu-list?' + $('#filter-rtu-form').serialize()));
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.rtu.init(); });
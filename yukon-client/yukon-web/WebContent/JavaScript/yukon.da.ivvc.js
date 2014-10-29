yukon.namespace('yukon.da.ivvc');

/**
 * Singleton that manages the ivvc pages in capcontrol
 * 
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.da.command.js
 */

yukon.da.ivvc = (function () {

    var
    /** Updates the recent events.*/
    _updateRecentEvents = function (zoneId, busId) {
            var params = {
                'zoneId': zoneId,
                'subBusId': busId,
                'mostRecent': $('#most-recent-update').val()
            };
            $.ajax({
                url: 'recentEvents',
                method: 'GET',
                data: params
            }).done(function (data, textStatus, jqXHR) {
                var ii,
                    table = $('#recent-events'),
                    body = table.find('tbody'),
                    events = data.events,
                    newRow,
                    event,
                    templateRow = $('#template-row');

                if (events.length) {
                    table.siblings('.empty-list').hide();
                    table.show();
                }

                for (ii = events.length - 1; ii >= 0; ii -= 1) {
                    event = events[ii];
                    newRow = $(templateRow.clone());
                    newRow.find('.js-device-name').text(event.deviceName);
                    newRow.find('.js-description').text(event.description);
                    newRow.find('.js-formatted-time').text(event.formattedTime);
                    body.prepend(newRow);
                    newRow.flash();
                }

                $('#most-recent-update').val(data.timestamp);

                // Keep table size <= 20 rows
                $('#recent-events tbody tr:gt(20)').each(function (index, tr) {
                    tr.remove();
                });
            });
        },

        mod = {

            /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
            init : function (params) {
                var recentEvents = $('#recent-events'),
                    timeout = recentEvents.data('timeout') || 4000,
                    zoneId = recentEvents.data('zoneId'),
                    busId = recentEvents.data('busId');

                $('.js-zone-editor').click(function () {
                    var link = $(this),
                        url = link.closest('[data-editor-url]').data('editorUrl'),
                        title = link.closest('[data-editor-title]').data('editorTitle');
                    openSimpleDialog('zoneWizardPopup', url, title, null, 'get');
                });

                $(document).on('click', '.js-command-button', function (event) {
                    var button = $(this),
                        paoId = button.closest('[data-pao-id]').data('paoId'),
                        cmdId = button.data('commandId');
                    doItemCommand(paoId, cmdId, event);
                });

                setInterval(function () { _updateRecentEvents(zoneId, busId); }, timeout);
            },

            /** 
             * Returns the point data.
             * @param {number} point - Point Id to find the specific point details.
             */
            setRedBulletForPoint : function (pointId) {
                return function (data) {

                    var redBulletSpans = $('.redBullet_' + pointId),
                        quality = data.quality;

                    redBulletSpans.each(function (index, redBulletSpan) {
                        if (quality !== 'Normal') {
                            $(redBulletSpan).show();
                        } else {
                            $(redBulletSpan).hide();
                        }
                    });
                };
            }
        };
    return mod;
}());

$( function () { yukon.da.ivvc.init(); });
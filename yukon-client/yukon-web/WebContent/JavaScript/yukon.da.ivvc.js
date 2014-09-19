/**
 * Singleton that manages the ivvc pages in capcontrol
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.da');
yukon.namespace('yukon.da.ivvc');

yukon.da.ivvc = (function () {

    var _zoneId,
        _subBusId,
        _timeOut,
        _updateRecentEvents = function () {
            var params = {
                'zoneId': _zoneId,
                'subBusId': _subBusId,
                'mostRecent': $('#mostRecentDateTime').val()
            };
            $.ajax({
                url: 'recentEvents',
                method: 'GET',
                data: params
            }).done(function (data, textStatus, jqXHR) {
                var ii,
                    table = $('#recentEventsTable'),
                    body = table.find('tbody'),
                    timeStamp = Object.keys(data)[0],
                    events = data[timeStamp],
                    newRow,
                    event,
                    templateRow = $($('#templateRowContainer').find('tr')[0]);

                for (ii = events.length - 1; ii >= 0; ii -= 1) {
                    table.siblings('.empty-list').hide();
                    table.show();
                    newRow = templateRow.clone();
                    newRow = $(newRow);
                    event = events[ii];
                    newRow.find('.deviceName').text(event.deviceName);
                    newRow.find('.description').text(event.description);
                    newRow.find('.formattedTime').text(event.formattedTime);
                    body.prepend(newRow);
                    newRow.flash();
                }
                
                $('#mostRecentDateTime').val(timeStamp);
                
                // Keep table size <= 20 rows
                $('#recentEventsTable tbody tr:gt(20)').each(function (index, tr) {
                    tr.remove();
                });
                setTimeout(_updateRecentEvents, _timeOut);
            });
        },

        mod = {

            init : function (params) {
                var controlRole = params.controlRole === 'true' ? true : false,
                    localCapControl = yukon.da;
                _timeOut = +params.timeOut || 4000;
                _zoneId = +params.zoneId;
                _subBusId = +params.subBusId;

                $('.js-zone-editor').click(function () {
                    var link = $(this),
                        url = link.closest('[data-editor-url]').attr('data-editor-url'),
                        title = link.closest('[data-editor-title]').attr('data-editor-title');
                    openSimpleDialog('zoneWizardPopup', url, title, null, 'get');
                });

                $(document).on('click', 'button.commandButton', function (event) {
                    var button = $(event.target).closest('button'),
                        cmdId = button.nextAll('input.cmdId')[0].value,
                        paoId = button.nextAll('input.paoId')[0].value;
                    doItemCommand(paoId, cmdId, event);
                });

                setTimeout(_updateRecentEvents, _timeOut);

                if (controlRole) {
                    $('tr[id^="tr_cap"]').each(function (index, row) {
                        var bankId = $(row)[0].id.split('_')[2],
                            bankName,
                            bankState;
                        // Add menus
                        bankName = $(row).find('button[id^="bankName"]')[0];
                        $(bankName).click(function (event) {
                            localCapControl.getCommandMenu(bankId, event);
                            return false;
                        });

                        bankState = $(row).find('a[id^="capbankState"]');
                        $(bankState).click(function (event) {
                            localCapControl.getMenuFromURL('/capcontrol/menu/capBankState?id=' + encodeURIComponent(bankId), event);
                            return false;
                        });
                    });
                }
            },

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
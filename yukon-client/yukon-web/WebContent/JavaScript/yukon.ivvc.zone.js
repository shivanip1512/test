var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});
yukon.namespace('yukon.Ivvc.Zone');
yukon.Ivvc.Zone = (function () {

    var _zoneId,
        _subBusId,
        _timeOut,
        _updateRecentEvents = function () {
            var params = {
                'zoneId': _zoneId,
                'subBusId': _subBusId,
                'mostRecent': jQuery('#mostRecentDateTime').val()
            };
            jQuery.ajax({
                url: 'recentEvents',
                method: 'GET',
                data: params
            }).done(function (data, textStatus, jqXHR) {
                var ii,
                    table = jQuery('#recentEventsTable'),
                    body = table.find('tbody'),
                    timeStamp = Object.keys(data)[0],
                    events = data[timeStamp],
                    newRow,
                    event,
                    templateRow = jQuery(jQuery('#templateRowContainer').find('tr')[0]);

                for (ii = events.length - 1; ii >= 0; ii -= 1) {
                    table.siblings('.empty-list').hide();
                    table.show();
                    newRow = templateRow.clone();
                    newRow = jQuery(newRow);
                    event = events[ii];
                    newRow.find('.deviceName').text(event.deviceName);
                    newRow.find('.description').text(event.description);
                    newRow.find('.formattedTime').text(event.formattedTime);
                    body.prepend(newRow);
                    flashYellow(newRow);
                }

                jQuery('#mostRecentDateTime').val(timeStamp);

                // Keep table size <= 20 rows
                jQuery('#recentEventsTable tbody tr:gt(20)').each(function (index, tr) {
                    tr.remove();
                });
                setTimeout(_updateRecentEvents, _timeOut);
            });
        },

        mod = {

            init : function (params) {
                var controlRole = params.controlRole === 'true' ? true : false,
                    localCapControl = yukon.CapControl;
                _timeOut = +params.timeOut || 4000;
                _zoneId = +params.zoneId;
                _subBusId = +params.subBusId;

                jQuery('.f-zone-editor').click(function () {
                    var link = jQuery(this),
                        url = link.closest('[data-editor-url]').attr('data-editor-url'),
                        title = link.closest('[data-editor-title]').attr('data-editor-title');
                    openSimpleDialog('zoneWizardPopup', url, title, null, 'get');
                });

                jQuery(document).on('click', 'button.commandButton', function (event) {
                    var button = jQuery(event.target).closest('button'),
                        cmdId = button.nextAll('input.cmdId')[0].value,
                        paoId = button.nextAll('input.paoId')[0].value;
                    doItemCommand(paoId, cmdId, event);
                });

                setTimeout(_updateRecentEvents, _timeOut);

                if (controlRole) {
                    jQuery('tr[id^="tr_cap"]').each(function (index, row) {
                        var bankId = jQuery(row)[0].id.split('_')[2],
                            bankName,
                            bankState;
                        // Add menus
                        bankName = jQuery(row).find('button[id^="bankName"]')[0];
                        jQuery(bankName).click(function (event) {
                            localCapControl.getCommandMenu(bankId, event);
                            return false;
                        });

                        bankState = jQuery(row).find('a[id^="capbankState"]');
                        jQuery(bankState).click(function (event) {
                            localCapControl.getMenuFromURL('/capcontrol/menu/capBankState?id=' + encodeURIComponent(bankId), event);
                            return false;
                        });
                    });
                }
            },

            setRedBulletForPoint : function (pointId) {
                return function (data) {

                    var redBulletSpans = jQuery('.redBullet_' + pointId),
                        quality = data.quality;

                    redBulletSpans.each(function (index, redBulletSpan) {
                        if (quality !== 'Normal') {
                            jQuery(redBulletSpan).show();
                        } else {
                            jQuery(redBulletSpan).hide();
                        }
                    });
                };
            }
        };
    return mod;
}());
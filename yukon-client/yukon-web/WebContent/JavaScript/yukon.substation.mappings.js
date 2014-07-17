yukon.namespace('yukon.substation.mappings');

/**
 * This module handles behavior on the device configuration pages.
 * @module yukon.deviceConfig
 */
yukon.substation.mappings = (function () {

    var _reloadAllMappingsTable = function () {
            var sortColumnLink = $('[data-mappings-table]').find('.sortable.desc, .sortable.asc'),
                dir = sortColumnLink.is('asc') ? 'asc' : 'desc',
                sort = sortColumnLink.data('sort'),
                container = sortColumnLink.closest('[data-url]'),
                url = container.data('url'),
                params = {'sort': sort, 'dir': dir};

            container.load(url, params, function (response, status, jqXhr) {
                if (status === 'error') {
                    $('.js-reload-error').show();
                } else {
                    $('.js-reload-error').hide();
                }
            });
        },

        _getMappedName = function (callback) {
            $.getJSON(yukon.url('/multispeak/setup/lmMappings/findMapping'),
                {
                    'strategyName': $('.js-strategy.js-mapping-input').val(),
                    'substationName': $('.js-substation.js-mapping-input').val()
                })
                .done(function (data, textStatus, jqXHR) {
                    callback(data);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    callback({'error': errorThrown});
                });
        },

        mod = {
            init : function () {
                var addBtn = $('.js-add-btn');

                yukon.dialogConfirm.add(
                        {
                            'on': '.js-add-btn',
                            'eventType': 'yukon_substation_mappings_confirm_add',
                            'strings' : {
                                'message': addBtn.data('message'),
                                'title': addBtn.data('title'),
                                'ok': yg.text.ok,
                                'cancel': yg.text.cancel,
                            }
                        }
                    );
                $(document).on('click', '.js-add-btn', function () {
                    var btn = $(this),
                        errors = false,
                        strategyName = $('.js-strategy.js-mapping-input').val(),
                        substationName = $('.js-substation.js-mapping-input').val();

                    $('.js-mapping-errors').hide();
                    $('.js-mapping-input').removeClass('error');

                    if (strategyName === '') {
                        errors = true;
                        $('.js-strategy').show().addClass('error');
                    }
                    if (substationName === '') {
                        errors = true;
                        $('.js-substation').show().addClass('error');
                    }

                    if (errors) {
                        yukon.ui.unbusy(btn);
                    } else {
                        _getMappedName(function (data) {
                            yukon.ui.unbusy(btn);
                            if (data.mappedName) {
                                btn.trigger('yukon_substation_mappings_confirm_add');
                            } else {
                                btn.trigger('yukon_substation_mappings_add');
                            }
                        });
                    }
                });

                $(document).on('input', '.js-mapping-input', function () {
                    var paoValue = $('.pao-values').find('*');
                    paoValue.addClass('disabled');
                    _getMappedName(function (data) {
                        var container = $('.mapped-pao-name'),
                            addBtnText = $('.js-add-btn').find('.b-label');
                        if (data.mappedName) {
                            container.text(data.mappedName);
                            addBtnText.text('Change');
                        } else {
                            container.text(container.data('emptyText'));
                            addBtnText.text('Add');
                        }
                        paoValue.removeClass('disabled');
                    });
                });

                $(document).on('yukon_substation_mappings_add', function () {
                    paoPicker.show();
                });

                $(document).on('yukon.substation.mappings.delete', function (ev) {
                    var mappingId = $(ev.target).data('mappingId');

                    $.getJSON(yukon.url('/multispeak/setup/lmMappings/removeMapping'),
                        {'mspLMInterfaceMappingId': mappingId}
                    ).done(function (data, textStatus, jqXHR) {
                        $('.js-delete-error').hide();
                        _reloadAllMappingsTable();
                    }).fail(function (jqXHR, textStatus, errorThrown) {
                        $('.js-delete-error').show();
                    });
                });
            },

            setMappedNameId : function () {
                var strategyName = $('.js-strategy.js-mapping-input').val(),
                    substationName = $('.js-substation.js-mapping-input').val(),
                    mappedNameId = $('#mappedNameId').val();

                if ($.trim($('#mappedNameId').val()) === '') {
                    return;
                }

                $.getJSON(yukon.url('/multispeak/setup/lmMappings/addOrUpdateMapping'),
                    {
                        'strategyName': strategyName,
                        'substationName': substationName,
                        'mappedNameId': mappedNameId
                    }
                ).done(function (data, textStatus, jqXHR) {
                    $('.js-add-error').hide();
                    $('.mapped-pao-name').html($('#mappedName').val());
                    _reloadAllMappingsTable();
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    $('.js-add-error').show();
                });
            }
        };

    return mod;
}());

$(function () { yukon.substation.mappings.init(); });
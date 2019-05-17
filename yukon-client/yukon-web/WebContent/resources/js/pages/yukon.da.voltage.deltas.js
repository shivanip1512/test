yukon.namespace('yukon.da.voltageDeltas');

/**
 *
 * @module yukon.da.voltageDeltas
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.voltageDeltas = (function () {

    var _isOriginal = function () {
            return $('.js-edit-delta:visible').length == 0 && $('.js-static-delta.changed').length == 0;
        },

        _cancelEdit = function (e) {
            $(e.currentTarget).closest('td').find('.js-edit-delta').hide();
            $(e.currentTarget).closest('td').find('.js-view-delta').show();
            $('#delta-form-buttons').find('button').prop('disabled', _isOriginal());
        },

        mod = {

            init : function () {
                $(document).on('click', '.js-view-delta', function(e) {
                    var currentValue = $(this).text().trim();
                    $(this).siblings('.js-edit-delta').find('.js-edit-delta-value').val(currentValue);
                    $(this).hide()
                        .siblings('.js-edit-delta').show().find('input').focus();
                    $('#delta-form-buttons').find('button').prop('disabled', false);
                });

                $(document).on('keydown', '.js-edit-delta input', function(e) {
                    if (e.which === yg.keys.escape) {
                        _cancelEdit(e);
                    }
                });

                $(document).on('click', '.js-static-delta', function(e) {
                    $(e.currentTarget).toggleClass('changed');

                    $('#delta-form-buttons').find('button').prop('disabled', _isOriginal());
                });

                $(document).on('click', '.js-cancel-edit', _cancelEdit);

                $(document).on('click','#delta-reset', function(e) {
                    $('.js-edit-delta').hide();
                    $('.js-view-delta').show();

                    $('.js-static-delta').removeClass('changed');
                    $('#delta-form-buttons').find('button').prop('disabled', true);
                    $("#delta-form")[0].reset();

                });

                $(document).on('click','#delta-submit', function() {
                    if (_isOriginal()) {
                        $('#delta-form-buttons').find('button').prop('disabled', true);
                        return;
                    }

                    var inputs = [],
                        index = 0;
                    $('#delta-form tr').each(function(a,b,c,d,e) {
                        var tr = $(this);
                            staticChanged = tr.find('.js-static-delta');
                            deltaInput = tr.find('.js-edit-delta input'),
                            params = {};

                        if (staticChanged.is('.changed') || deltaInput.is(':visible')) {

                            params.newDelta = deltaInput.is(':visible') ? deltaInput.val() : deltaInput.attr('value');
                            params.newStaticValue = staticChanged.is(':checked');
                            params.bankId = tr.data('bankId');
                            params.pointId = tr.data('pointId');

                            inputs.push('<input type="hidden" name="pointDeltas[' + index + '].delta" value="'+ params.newDelta +'"/>');
                            inputs.push('<input type="hidden" name="pointDeltas[' + index + '].staticDelta" value="'+ params.newStaticValue +'"/>');
                            inputs.push('<input type="hidden" name="pointDeltas[' + index + '].pointId" value="'+ params.pointId +'"/>');
                            inputs.push('<input type="hidden" name="pointDeltas[' + index + '].bankId" value="'+ params.bankId +'"/>');
                            index++;
                        }
                    });

                    if (inputs.length > 0) {
                        yukon.ui.blockPage();
                        $('#delta-form').append(inputs.join(''));
                        $('#delta-form').submit();
                    }
                });
            }
        };

    return mod;
}());

$(function () { yukon.da.voltageDeltas.init(); });
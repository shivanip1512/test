yukon.namespace('yukon.assets.enroll');
 
/**
 * Module to handle behavior on the operator enrollment page.
 * @module yukon.assets.enroll
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.enroll = (function () {
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            /** Open unenroll popup. */
            $('.js-unenroll').click(function (ev) {
                var icon = $(this),
                    popup = $('#enroll-popup'),
                    programId = icon.closest('tr').data('programId');
                
                popup.load('confirmUnenroll', {
                    assignedProgramId: programId,
                    accountId: popup.data('accountId')
                }, function () {
                    popup.dialog({
                        width: 500,
                        title: popup.data('removeTitle'),
                        buttons: yukon.ui.buttons({
                            event: 'yukon_assets_unenroll_confirm'
                        })
                    });
                });
            });
            
            /** Ok button clicked on the unenroll confirmation popup. */
            $(document).on('yukon_assets_unenroll_confirm', function (ev) {
                $('#enroll-popup').find('form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        window.location.href = window.location.href;
                    }
                });
                
                return false;
            });
            
            /** Open enrollment edit popup. */
            $('.js-edit-enrollment').click(function (ev) {
                var icon = $(this),
                    popup = $('#enroll-popup'),
                    programId = icon.closest('tr').data('programId'),
                    accountId = popup.data('accountId');
                
                popup.load('edit', {
                    assignedProgramId: programId,
                    accountId: accountId
                }, function () {
                    popup.dialog({
                        width: 500,
                        title: popup.data('editTitle'),
                        buttons: yukon.ui.buttons({
                            okText: yg.text.save,
                            event: 'yukon_assets_enroll_save'
                        })
                    });
                });
            });
            
            /** Save button clicked on the enrollment edit popup. */
            $(document).on('yukon_assets_enroll_save', function (ev) {
                $('#edit-enroll-form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $('#enroll-popup')
                        .html(xhr.responseText)
                        .dialog({
                            width: 500,
                            buttons: yukon.ui.buttons({
                                okText: yg.text.save,
                                event: 'yukon_assets_enroll_save_confirm'
                            })
                        });
                    }
                });
                
                return false;
            });
            
            /** Save button clicked on the enrollment edit confirmation popup. */
            $(document).on('yukon_assets_enroll_save_confirm', function (ev) {
                $('#enroll-popup').find('form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        window.location.href = window.location.href;
                    }
                });
                
                return false;
            });
            
            /** Enrollment checkbox changed, update popup. */
            $(document).on('change', '.js-enroll-cb', function (ev) {
                var cb = $(this),
                    checked = cb.is(':checked'),
                    checkedCount = $('#enroll-popup .js-enroll-cb:checked').length,
                    row = cb.closest('tr'),
                    relay = row.find('.js-enroll-relay');
                
                relay.prop('disabled', !checked);
                $('.ui-dialog-buttonset .primary').prop('disabled', checkedCount == 0);
            });

            _initialized = true;
        },
        
        /** A program was selected to enroll in. */
        add: function (programs) {
            var popup = $('#enroll-popup'),
                program = programs[0];
            
            popup.load('add', {
                assignedProgramId: program.assignedProgramId,
                accountId: popup.data('accountId')
            }, function () {
                var disableString = $('#isDisable').val();
                var isSaveDisabled = (disableString === 'true');
                popup.dialog({
                    width: 500,
                    title: popup.data('addTitle'),
                    buttons: yukon.ui.buttons({
                        okText: yg.text.save,
                        event: 'yukon_assets_enroll_save',
                        okDisabled : isSaveDisabled
                    })
                });
            });
        }
    };
 
    return mod;
})();
 
$(function () { yukon.assets.enroll.init(); });
yukon.namespace('yukon.assets.commChannel.js');
 
/** 
 * Module that handles the behavior on the comm channel
 * @module yukon.assets.commChannel.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.commChannel = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            $(document).on("yukon:commChannel:delete", function () {
                yukon.ui.blockPage();
                $('#delete-commChannel-form').submit();
            });

            $(document).on('change', '#js-comm-channel-type', function (event) {
                var type = $(this).val(); 
                yukon.ui.block($('js-commChannel-container'));
                var name = $('#js-comm-channel-name').val();
                $.ajax({
                    url: yukon.url('/stars/device/commChannel/create/' + type),
                    type: 'get',
                    data: {name: name}
                }).done(function(data) {
                     $('.commChannel-create-form').html(data);
                     yukon.ui.unblock($('js-commChannel-container'));
               });
            });

            $(document).on("yukon:assets:commChannel:create", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('.commChannel-create-form'),
                    popup = $('#js-create-comm-channel-popup');

                $.ajax({
                    type: "POST",
                    url: yukon.url("/stars/device/commChannel/save"),
                    data: form.serialize()
                }).done(function (data) {
                    window.location.href = yukon.url('/stars/device/commChannel/' + data.id);
                    dialog.dialog('close');
                    dialog.empty();
                }).fail(function (xhr, status, error){
                    popup.html(xhr.responseText);
                    yukon.ui.initContent(popup);
                    yukon.ui.unblockPage();
                });
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.commChannel.init(); });
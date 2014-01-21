Yukon.namespace('Yukon.veeReview');

Yukon.veeReview = (function () {
    var _resetDeleteAccept = function (action, deleteImgEl, acceptImgEl) {
            var elemToDisable = action === 'DELETE' ? deleteImgEl : acceptImgEl;
            jQuery(elemToDisable).addClass('disabled');
        },
        _toggleDeleteAccept = function (action, deleteImgEl, acceptImgEl) {
            if (action === 'DELETE') {
                jQuery(deleteImgEl).removeClass('disabled');
                jQuery(acceptImgEl).addClass('disabled');
            }
            if (action === 'ACCEPT') {
                jQuery(acceptImgEl).removeClass('disabled');
                jQuery(deleteImgEl).addClass('disabled');
            }
        },
        _getActionTdHash = function (el) {
            var descendants = jQuery(el).children(),
                idParts = descendants[0].id.split('_'),
                action = idParts[1],
                changeId = idParts[3];

            return {
                action : action,
                deleteImgEl : jQuery('#ACTION_DELETE_IMG_' + changeId).find('.icon')[0],
                acceptImgEl : jQuery('#ACTION_ACCEPT_IMG_' + changeId).find('.icon')[0],
                valueEl : jQuery('#ACTION_' + changeId)[0]
            };
        },
        mod;
    mod = {
        checkUncheckAll : function (action) {
            var checkAllState = jQuery('#checkAllState');

            jQuery('td.ACTION_TD').each(function (index, el) {
                var h = _getActionTdHash(el),
                    tdAction = h.action,
                    valueEl = h.valueEl,
                    delImgEl = h.deleteImgEl,
                    accImgEl = h.acceptImgEl;

                if (checkAllState.val() === action) {
                    _resetDeleteAccept(tdAction, delImgEl, accImgEl);
                    valueEl.value = '';
                } else {
                    _toggleDeleteAccept(action, delImgEl, accImgEl);
                    valueEl.value = action;
                }
            });
            if (checkAllState.val() === action) {
                checkAllState.val('');
            } else {
                checkAllState.val(action);
            }
        },
        reloadForm : function () {
            var reloadMsg = jQuery('#reloadForm').data('reloadmsg');
            jQuery('#saveButton').prop('disabled', true);
            jQuery('#reloadButton').val(reloadMsg);
            jQuery('#reloadButton').prop('disabled', true);
            jQuery('#reloadSpinner').show();
            jQuery("#saveForm input[type='checkbox']").each(function (index, el) {
                var h = document.createElement('input');
                h.setAttribute('type', 'hidden');
                h.setAttribute('name', el.getAttribute('name'));
                h.setAttribute('value', el.checked);
                jQuery('#reloadForm')[0].appendChild(h);
                jQuery(el).prop('disabled', true);
            });
            jQuery('#reloadForm').submit();
        }
    };
    jQuery(function () {
        jQuery('#saveForm').on('click', 'td.ACTION_TD', function (ev) {
            var elTarg = ev.currentTarget,
                h = _getActionTdHash(elTarg),
                action = h.action,
                valueEl = h.valueEl,
                delImgEl = h.deleteImgEl,
                accImgEl = h.acceptImgEl;
            if (valueEl.value === action) {
                _resetDeleteAccept(action, delImgEl, accImgEl);
                valueEl.value = '';
            } else {
                _toggleDeleteAccept(action, delImgEl, accImgEl);
                valueEl.value = action;
            }
        });
    });
    return mod;
})();
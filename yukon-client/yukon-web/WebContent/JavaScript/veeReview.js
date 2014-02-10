yukon.namespace('yukon.VeeReview');

yukon.VeeReview = (function () {
        var _resetElementSelected = function (action, deleteEl, acceptEl, ignoreEl) {
            if(action === 'DELETE'){
                jQuery(deleteEl).removeClass('on');
            } else if(action === 'ACCEPT'){
                jQuery(acceptEl).removeClass('on');
            } else {
                jQuery(ignoreEl).removeClass('on');
            }
            
        },
        _toggleElementSelected = function (action, deleteEl, acceptEl, ignoreEl) {
            if (action === 'DELETE') {
                jQuery(deleteEl).addClass('on');
                jQuery(acceptEl).removeClass('on');
                jQuery(ignoreEl).removeClass('on');
            }
            if (action === 'ACCEPT') {
                jQuery(deleteEl).removeClass('on');
                jQuery(ignoreEl).removeClass('on');
                jQuery(acceptEl).addClass('on');
            }
            if (action === 'IGNORE') {
                jQuery(deleteEl).removeClass('on');
                jQuery(acceptEl).removeClass('on');
                jQuery(ignoreEl).addClass('on');
            }
        },
        _getActionValues = function (el) {
                idParts = el.id.split('_'),
                action = idParts[1],
                changeId = idParts[2];
            return {
                action : action,
                deleteEl : jQuery('#ACTION_DELETE_' + changeId),
                acceptEl : jQuery('#ACTION_ACCEPT_' + changeId),
                ignoreEl : jQuery('#ACTION_IGNORE_' + changeId),
                valueEl : jQuery('#ACTION_' + changeId)[0]
            };
        },
        mod;
    mod = {
        checkUncheckAll : function (actionChecked) {
            var checkAll = false;
            var attributeSelector = '[id*="DELETE"]';
            if (actionChecked === 'ACCEPT') {
                attributeSelector = '[id*="ACCEPT"]';
                if (jQuery('#accept-all').hasClass('on')) {
                    jQuery('#accept-all').removeClass('on');
                } else {
                    checkAll = true;
                    jQuery('#accept-all').addClass('on');
                    jQuery('#ignore-all').removeClass('on');
                    jQuery('#delete-all').removeClass('on');
                }
                
            } else if (actionChecked === 'IGNORE') {
                attributeSelector = '[id*="IGNORE"]';
                if (jQuery('#ignore-all').hasClass('on')) {
                    jQuery('#ignore-all').removeClass('on');
                } else {
                    checkAll = true;
                    jQuery('#ignore-all').addClass('on');
                    jQuery('#accept-all').removeClass('on');
                    jQuery('#delete-all').removeClass('on');
                }
            } else if (actionChecked === 'DELETE') {
                if (jQuery('#delete-all').hasClass('on')) {
                    jQuery('#delete-all').removeClass('on');
                } else {
                    checkAll = true;
                    jQuery('#delete-all').addClass('on');
                    jQuery('#ignore-all').removeClass('on');
                    jQuery('#accept-all').removeClass('on');
                }
            }

            jQuery(attributeSelector).each(function (index, buttonElement) {
                if(checkAll === true) {
                    if(jQuery(buttonElement).hasClass('on') === false) {
                        buttonElement.click();
                    }
                } else {
                    if(jQuery(buttonElement).hasClass('on') === true) {
                        buttonElement.click();
                    }
                }
            });
                
        },
        reloadForm : function () {
            var reloadMsg = jQuery('#reloadForm').data('reloadmsg');
            jQuery('#saveButton').prop('disabled', true);
            jQuery('#reloadButton').val(reloadMsg);
            jQuery('#reloadButton').prop('disabled', true);
            jQuery('#reloadSpinner').show();
            jQuery("#review-form input[type='checkbox']").each(function (index, el) {
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
        jQuery('#review-form [id*="ACTION"]').click(function(e) {
            var elementClicked = e.currentTarget;
            h = _getActionValues(elementClicked);
            action = h.action,
            valueElement = h.valueEl,
            deleteButtonElement = h.deleteEl,
            acceptButtonElement = h.acceptEl;
            ignoreButtonElement = h.ignoreEl;
            //making sure that the element clicked isn't already clicked
            if (valueElement.value !== action) {
                _toggleElementSelected(action, deleteButtonElement, acceptButtonElement, ignoreButtonElement);
                valueElement.value = action;
            }
        });
    });
    return mod;
})();
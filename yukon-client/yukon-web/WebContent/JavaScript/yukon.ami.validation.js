
yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.validation');

yukon.ami.validation = (function () {
        var _resetElementSelected = function (action, deleteEl, acceptEl) {
            if (action === 'DELETE') {
                jQuery(deleteEl).removeClass('on');
            } else if (action === 'ACCEPT') {
                jQuery(acceptEl).removeClass('on');
            } else {
                jQuery(ignoreEl).removeClass('on');
            }
            
        },
        _toggleElementSelected = function (action, deleteEl, acceptEl, ignoreEl) {
            if (action === 'DELETE') {
                jQuery(deleteEl).addClass('on');
                jQuery(acceptEl).removeClass('on');
            }
            if (action === 'ACCEPT') {
                jQuery(deleteEl).removeClass('on');
                jQuery(acceptEl).addClass('on');
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
                valueEl : jQuery('#ACTION_' + changeId)[0]
            };
        };
    
    jQuery(function () {
        jQuery('#review-form [id="display-type-checkbox"]').click(function(e) {
            var reviewTableUrl = yukon.url('/amr/veeReview/reviewTable');
            jQuery.post(reviewTableUrl, jQuery('#review-form').serialize()).done(function(result) {
                jQuery('#reviewTable').html(result);
                yukon.ui.unbusy(jQuery('#saveButton'));
            });
        });
        //save/remove actioned items
        jQuery('#review-form').on('click','[id="saveButton"]', function(e) {
            var urlParams ='?itemsPerPage=';
            if (jQuery('.paging-area .selectedItem').text().length > 0) {
                urlParams += jQuery('.paging-area .selectedItem').text();
            } else {
                urlParams = '';
            }
            var saveUrl = yukon.url('/amr/veeReview/save');
            jQuery.post(saveUrl + urlParams, jQuery('#review-form').serialize()).done(function(result) {
                jQuery('#reviewTable').html(result);
                yukon.ui.unbusy(jQuery('#saveButton'));
                jQuery('#accept-all').removeClass('on');
                jQuery('#delete-all').removeClass('on');
            });
            return false;
        });
        //check/uncheck all
        jQuery('#review-form').on('click', '#accept-all, #delete-all', function(e) { 
            var checkAll = false;
            var buttonClicked = e.currentTarget;
            var attributeSelector = '[id*="DELETE"]';
            if (buttonClicked.id === 'accept-all') {
                attributeSelector = '[id*="ACCEPT"]';
                if(jQuery('#accept-all').hasClass('on')) {
                    jQuery('#accept-all').removeClass('on');
                } else {
                    checkAll = true;
                    jQuery('#accept-all').addClass('on');
                    jQuery('#delete-all').removeClass('on');
                }
            } else {
                if (jQuery('#delete-all').hasClass('on')) {
                    jQuery('#delete-all').removeClass('on');
                } else {
                    checkAll = true;
                    jQuery('#delete-all').addClass('on');
                    jQuery('#accept-all').removeClass('on');
                }
            }

            jQuery(attributeSelector).each(function (index, buttonElement) {
                if (checkAll === true) {
                    if (jQuery(buttonElement).hasClass('on') === false) {
                        buttonElement.click();
                    }
                } else {
                    if(jQuery(buttonElement).hasClass('on') === true) {
                        buttonElement.click();
                    }
                }
                
            });
        });
        
        jQuery('#review-form').on('click','[id*="ACTION"]', function(e) {
            var elementClicked = e.currentTarget,
                h = _getActionValues(elementClicked),
                action = h.action,
                valueElement = h.valueEl,
                deleteButtonElement = h.deleteEl,
                acceptButtonElement = h.acceptEl;
            
            if (valueElement.value === action) {
                _resetElementSelected(action, deleteButtonElement, acceptButtonElement);
                valueElement.value = '';
            } else {
                _toggleElementSelected(action, deleteButtonElement, acceptButtonElement);
                valueElement.value = action;
            }
        });
    });
})();
/**
 * Handles validation engines reviews page. 
 * 
 * @module yukon.ami.validation
 * @requires JQUERY
 * @requires yukon
 */

yukon.namespace('yukon.ami.validation');

yukon.ami.validation = (function () {
	
	    /** Toggles between the action selected Accepted/Deleted.  
	     * @param {string} action - Action selected accept/delete.
	     * @param {Object} deleteEl - delete button instance.
	     * @param {Object} acceptEl - accept button instance. 
	     */ 
        var _resetElementSelected = function (action, deleteEl, acceptEl) {
            if (action === 'DELETE') {
                $(deleteEl).removeClass('on');
            } else if (action === 'ACCEPT') {
                $(acceptEl).removeClass('on');
            } else {
                $(ignoreEl).removeClass('on');
            }
            
        },
        
        /** Toggles between the action selected Accepted/Deleted.  
	     * @param {string} action - Action selected accept/delete.
	     * @param {Object} deleteEl - delete button instance.
	     * @param {Object} ignoreEl - ignore Expression. 
	     */
        _toggleElementSelected = function (action, deleteEl, acceptEl, ignoreEl) {
            if (action === 'DELETE') {
                $(deleteEl).addClass('on');
                $(acceptEl).removeClass('on');
            }
            if (action === 'ACCEPT') {
                $(deleteEl).removeClass('on');
                $(acceptEl).addClass('on');
            }
        },
        
        /** Gets the delete or accept buttons instance.  
	     *  @param {Object} el - Id of the button clicked.
	     */
        _getActionValues = function (el) {
                idParts = el.id.split('_'),
                action = idParts[1],
                changeId = idParts[2];
            return {
                action : action,
                deleteEl : $('#ACTION_DELETE_' + changeId),
                acceptEl : $('#ACTION_ACCEPT_' + changeId),
                valueEl : $('#ACTION_' + changeId)[0]
            };
        };
    
    $(function () {
        $('#review-form [id="display-type-checkbox"]').click(function(e) {
            var reviewTableUrl = yukon.url('/amr/veeReview/reviewTable');
            $.post(reviewTableUrl, $('#review-form').serialize()).done(function(result) {
                $('#reviewTable').html(result);
                yukon.ui.unbusy($('#saveButton'));
            });
        });
        //save/remove actioned items
        $('#review-form').on('click','[id="saveButton"]', function(e) {
            var urlParams ='?itemsPerPage=';
            if ($('.paging-area .selectedItem').text().length > 0) {
                urlParams += $('.paging-area .selectedItem').text();
            } else {
                urlParams = '';
            }
            var saveUrl = yukon.url('/amr/veeReview/save');
            $.post(saveUrl + urlParams, $('#review-form').serialize()).done(function(result) {
                $('#reviewTable').html(result);
                yukon.ui.unbusy($('#saveButton'));
                $('#accept-all').removeClass('on');
                $('#delete-all').removeClass('on');
            });
            return false;
        });
        //check/uncheck all
        $('#review-form').on('click', '#accept-all, #delete-all', function(e) { 
            var checkAll = false;
            var buttonClicked = e.currentTarget;
            var attributeSelector = '[id*="DELETE"]';
            if (buttonClicked.id === 'accept-all') {
                attributeSelector = '[id*="ACCEPT"]';
                if($('#accept-all').hasClass('on')) {
                    $('#accept-all').removeClass('on');
                } else {
                    checkAll = true;
                    $('#accept-all').addClass('on');
                    $('#delete-all').removeClass('on');
                }
            } else {
                if ($('#delete-all').hasClass('on')) {
                    $('#delete-all').removeClass('on');
                } else {
                    checkAll = true;
                    $('#delete-all').addClass('on');
                    $('#accept-all').removeClass('on');
                }
            }

            $(attributeSelector).each(function (index, buttonElement) {
                if (checkAll === true) {
                    if ($(buttonElement).hasClass('on') === false) {
                        buttonElement.click();
                    }
                } else {
                    if($(buttonElement).hasClass('on') === true) {
                        buttonElement.click();
                    }
                }
                
            });
        });
        
        $('#review-form').on('click','[id*="ACTION"]', function(e) {
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
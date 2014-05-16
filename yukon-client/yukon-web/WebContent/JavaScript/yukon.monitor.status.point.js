/**
 * Singleton that manages the status point monitor feature
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.StatusPointMonitor');

yukon.StatusPointMonitor = (function () {
    
    var mod = {
        
        /** 
         * Re-index a row so that the spring form binding works 
         */
        reindex: function(row, index) {
            row.data('row', index);
            row.find('.f-row-prev-state').attr('name', 'processors[' + index + '].prevState');
            row.find('.f-row-next-state').attr('name', 'processors[' + index + '].nextState');
            row.find('.f-row-action-type').attr('name', 'processors[' + index + '].actionType');
            row.find('.f-remove').removeAttr('id');
        },
        
        /** 
         * Re-index all rows for spring form binding and
         * adjusts input states for each row.
         */
        reindexAll: function() {
            var rows = $('#processors-table tbody tr');
            rows.each(function(index, elem) {
                mod.reindex($(elem), index);
            });
        }
    };
    
    return mod;
}());

$(function() {
    
    $(document).on('yukon.dialog.confirm.cancel', function(ev) {
        yukon.ui.unbusy('#deleteButton');
        $('.page-action-area .button').enable();
    });
    
    
    /** ADD RULE */
    $('.f-add').click(function(event) {
        
        var row = $('.f-template-row').clone();
        
        row.removeClass('f-template-row');
        row.appendTo('#processors-table tbody');
        
        yukon.StatusPointMonitor.reindexAll();
    });
    
    /** REMOVE RULE */
    $(document).on('click','.f-remove', function(event) {
        $(this).closest('tr').remove();
        yukon.StatusPointMonitor.reindexAll();
    });
    
});
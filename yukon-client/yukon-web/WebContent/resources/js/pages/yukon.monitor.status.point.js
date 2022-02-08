yukon.namespace('yukon.StatusPointMonitor');

/**
 * Singleton that manages the status point monitor feature
 * 
 * @module yukon.StatusPointMonitor
 * @requires JQUERY
 * @requires yukon 
 * @requires yukon.dialog.confirm.
 */
yukon.StatusPointMonitor = (function () {
    
    var mod = {
        
        /** 
         * Re-index a row so that the spring form binding works 
         */
        reindex: function(row, index) {
            row.data('row', index);
            row.find('.js-row-prev-state').attr('name', 'processors[' + index + '].prevState');
            row.find('.js-row-next-state').attr('name', 'processors[' + index + '].nextState');
            row.find('.js-row-action-type').attr('name', 'processors[' + index + '].actionType');
            row.find('.js-row-notify-on-alarm-only').attr('name', 'processors[' + index + '].notifyOnAlarmOnly');
            row.find('.js-remove').removeAttr('id');
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
    
    /** ADD RULE */
    $('.js-add').click(function(event) {
        
        var row = $('.js-template-row').clone();
        
        row.removeClass('js-template-row');
        row.appendTo('#processors-table tbody');
        
        yukon.StatusPointMonitor.reindexAll();
    });
    
    /** REMOVE RULE */
    $(document).on('click','.js-remove', function(event) {
        $(this).closest('tr').remove();
        yukon.StatusPointMonitor.reindexAll();
    });
    
    
});
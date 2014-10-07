yukon.namespace('yukon.PorterResponseMonitor');

/**
 * Singleton that manages the porter response monitor feature.
 * 
 * @module yukon.PorterResponseMonitor
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.PorterResponseMonitor = (function () {
    
    var mod = {
        
        /** Re-index a row so that the spring form binding works. 
         *  @param {Object} row - row to index.
         *  @param {Object} index - index id.
         */
        reindex: function(row, index) {
            row.data('row', index);
            row.find('.js-row-id').attr('name', 'rules[' + index + '].ruleId');
            row.find('.js-row-order').attr('name', 'rules[' + index + '].ruleOrder');
            row.find('.js-row-order').val(index + 1);
            row.find('.js-row-order-text').html(index + 1);
            row.find('.js-row-success').attr('name', 'rules[' + index + '].success');
            row.find('.js-row-error-codes').attr('name', 'rules[' + index + '].errorCodes');
            row.find('.js-row-match-style').attr('name', 'rules[' + index + '].matchStyle');
            row.find('.js-row-state').attr('name', 'rules[' + index + '].state');
            row.find('.js-remove').removeAttr('id');
        },
        
        /** Re-index all rows for spring form binding and adjusts button states for each row. */ 
        reindexAll: function() {
            var rows = $('#rules-table tbody tr');
            rows.each(function(index, elem) {
                
                var row = $(elem);
                mod.reindex(row, index);
                
                if (rows.length === 1) { // only one row
                    row.find('.js-up, .js-down').prop('disabled', true); 
                } else if (index === 0) { // first row
                    row.find('.js-up').prop('disabled', true);
                    row.find('.js-down').prop('disabled', false);
                } else if (index === rows.length -1) { // last row
                    row.find('.js-up').prop('disabled', false);
                    row.find('.js-down').prop('disabled', true);
                } else { // middle row
                    row.find('.js-up').prop('disabled', false);
                    row.find('.js-down').prop('disabled', false);
                }
            });
        }
    };
    
    return mod;
}());

$(function() {
    
    /** SETUP ROWS AFTER PAGE LOAD*/
    // just get rid of the extra hidden input that spring uses for the checkbox state
    $('#rules-table tbody tr').each(function(idx, elem) {
        $(elem).find('[name^="_rule"]').remove();
    });
    $('#rules-table tbody tr:first-child .js-up').prop('disabled', true);
    $('#rules-table tbody tr:last-child .js-down').prop('disabled', true);
    
    /** ADD RULE */
    $('.js-add-rule').click(function(event) {
        
        var row = $('.js-template-row').clone();
        
        row.removeClass('js-template-row');
        row.appendTo('#rules-table tbody');
        
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** REMOVE RULE */
    $(document).on('click','.js-remove', function(event) {
        $(this).closest('tr').remove();
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** MOVE RULE UP */
    $(document).on('click','.js-up', function(event) {
        var row = $(this).closest('tr'),
            prevRow = row.prev();
        
        row.insertBefore(prevRow);
        
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** MOVE RULE DOWN */
    $(document).on('click','.js-down', function(event) {
        var row = $(this).closest('tr'),
            nextRow = row.next();
        
        row.insertAfter(nextRow);
        
        yukon.PorterResponseMonitor.reindexAll();
    });
});
/**
 * Singleton that manages the porter response monitor feature
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.PorterResponseMonitor');

yukon.PorterResponseMonitor = (function () {
    
    var mod = {
        
        /** 
         * Re-index a row so that the spring form binding works 
         */
        reindex: function(row, index) {
            row.data('row', index);
            row.find('.f-row-id').attr('name', 'rules[' + index + '].ruleId');
            row.find('.f-row-order').attr('name', 'rules[' + index + '].ruleOrder');
            row.find('.f-row-order').val(index + 1);
            row.find('.f-row-order-text').html(index + 1);
            row.find('.f-row-success').attr('name', 'rules[' + index + '].success');
            row.find('.f-row-error-codes').attr('name', 'rules[' + index + '].errorCodes');
            row.find('.f-row-match-style').attr('name', 'rules[' + index + '].matchStyle');
            row.find('.f-row-state').attr('name', 'rules[' + index + '].state');
            row.find('.f-remove').removeAttr('id');
        },
        
        /** 
         * Re-index all rows for spring form binding and
         * adjusts button states for each row.
         */
        reindexAll: function() {
            var rows = jQuery('#rules-table tbody tr');
            rows.each(function(index, elem) {
                
                var row = jQuery(elem);
                mod.reindex(row, index);
                
                if (rows.length === 1) { // only one row
                    row.find('.f-up, .f-down').prop('disabled', true); 
                } else if (index === 0) { // first row
                    row.find('.f-up').prop('disabled', true);
                    row.find('.f-down').prop('disabled', false);
                } else if (index === rows.length -1) { // last row
                    row.find('.f-up').prop('disabled', false);
                    row.find('.f-down').prop('disabled', true);
                } else { // middle row
                    row.find('.f-up').prop('disabled', false);
                    row.find('.f-down').prop('disabled', false);
                }
            });
        }
    };
    
    return mod;
}());

jQuery(function() {
    
    /** SETUP ROWS AFTER PAGE LOAD*/
    // just get rid of the extra hidden input that spring uses for the checkbox state
    jQuery('#rules-table tbody tr').each(function(idx, elem) {
        jQuery(elem).find('[name^="_rule"]').remove();
    });
    jQuery('#rules-table tbody tr:first-child .f-up').prop('disabled', true);
    jQuery('#rules-table tbody tr:last-child .f-down').prop('disabled', true);
    
    /** ADD RULE */
    jQuery('.f-add-rule').click(function(event) {
        
        var row = jQuery('.f-template-row').clone();
        
        row.removeClass('f-template-row');
        row.appendTo('#rules-table tbody');
        
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** REMOVE RULE */
    jQuery(document).on('click','.f-remove', function(event) {
        jQuery(this).closest('tr').remove();
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** MOVE RULE UP */
    jQuery(document).on('click','.f-up', function(event) {
        var row = jQuery(this).closest('tr'),
            prevRow = row.prev();
        
        row.insertBefore(prevRow);
        
        yukon.PorterResponseMonitor.reindexAll();
    });
    
    /** MOVE RULE DOWN */
    jQuery(document).on('click','.f-down', function(event) {
        var row = jQuery(this).closest('tr'),
            nextRow = row.next();
        
        row.insertAfter(nextRow);
        
        yukon.PorterResponseMonitor.reindexAll();
    });
});
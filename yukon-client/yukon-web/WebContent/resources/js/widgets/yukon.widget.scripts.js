/**
 * Update the state of toggle for the scripts page.
 */
$(function() {
    $(document).on('change', '.js-scripts-toggle .checkbox-input', function() {
        var checkbox = $(this);
        var form = checkbox.closest('form');
        
        //add any sorting/paging parameters
        var 
        sortColumn = $('.sortable.desc, .sortable.asc'),
        pageSize = $('span[data-page-size]'),
        extras = {};
        
        if(sortColumn.length > 0 || pageSize.length > 0){
            if (sortColumn.length > 0) {
                extras.sort = sortColumn.data('sort');
                extras.dir = sortColumn.is('.desc') ? 'desc' : 'asc';
            }
            if (pageSize.length > 0) {
                extras.itemsPerPage = pageSize.data('pageSize');
            }

            var url = form[0].attributes["action"].value;
            form[0].attributes["action"].value = url + '&' + $.param(extras);
        }

        form.submit();
    });
});

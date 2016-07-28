yukon.namespace('yukon.ami.search');
 
/**
 * Handles the ami search page
 * @module yukon.ami.search
 * @requires yukon
 * @requires JQUERY
 */
yukon.ami.search = (function () {
 
    var
    _initialized = false,
    
    /** Do a filter request with filtering, sorting, and paging settings. */
    _filter = function () {
        var 
        sortColumn = $('.sortable.desc, .sortable.asc'),
        pageSize = $('span[data-page-size]'),
        url = '/meter/search?',
        criteria = $('#filter-form').serialize(),
        extras = {};
        
        if (sortColumn.length > 0) {
            extras.sort = sortColumn.data('sort');
            extras.dir = sortColumn.is('.desc') ? 'desc' : 'asc';
        }
        if (pageSize.length > 0) {
            extras.itemsPerPage = pageSize.data('pageSize');
        }
        
        window.location.href = yukon.url(url + criteria + '&' + $.param(extras));
    },
 
    _mod = {
 
        init: function () {
 
            if (_initialized) return;
            
            /** Clear filter but remember current sorting and paging settings. */
            $('.js-ami-search-clear').click(function (ev) {
                
                var sortColumn = $('.sortable.desc, .sortable.asc');
                var url, sortVal = sortColumn.data('sort');
                if (sortVal && sortVal !== '') {
                    url = '/meter/search?' + $.param({
                        itemsPerPage: $('span[data-page-size]').data('pageSize'),
                        sort: sortVal,
                        dir: sortColumn.is('.desc') ? 'desc' : 'asc'
                    });
                } else {
                    url = '/meter/search';
                }
                window.location.href = yukon.url(url);
            });
            
            /** Do a filter request when enter hit in textfield or search button clicked. */
            $('.js-ami-search').click(function (ev) { _filter(); });
            $('#filter-form :text').keyup(function (e) { if (e.keyCode == 13) _filter(); });
            
            _initialized = true;
        }
 
    };
 
    return _mod;
})();
 
$(function () { yukon.ami.search.init(); });
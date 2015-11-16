yukon.namespace('yukon.bulk.routeLocate.results');

/**
 * Singleton that handles some events of locate routes results page.
 * 
 * @module yukon.bulk.routeLocate.results
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.routeLocate.results = (function () {
    var mod = {
        complete: function (data) {
            var isComplete = data.isComplete,
                isCanceled = data.isCanceled,
                routesButton = $('.js-routes-button'),
                cancelDiv = $('#cancelLocateDiv');

            if (isComplete === 'true') {
                routesButton.prop('disabled', false);
                cancelDiv.hide();
            }

            if (isCanceled === 'true') {
                routesButton.prop('disabled', false);
                cancelDiv.hide();
                $('#commandsCanceledDiv').show();
            }
        }
    };
    return mod;
})();
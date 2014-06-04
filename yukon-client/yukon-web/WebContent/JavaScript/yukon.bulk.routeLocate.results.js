yukon.namespace('yukon.bulk.routeLocate.results');

yukon.bulk.routeLocate.results = (function () {
    var mod = {
        complete: function (data) {
            var isComplete = data.isComplete,
                isCanceled = data.isCanceled,
                routesButton = $('.f-routes-button'),
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
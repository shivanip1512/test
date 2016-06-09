yukon.namespace('yukon.support.systemHealth');

/**
 * Handles behavior for the system health metrics.
 * @module yukon.support.systemHealth
 * @requires JQUERY
 */
yukon.support.systemHealth = (function() {
    
    'use strict';
    
    var 
    _initialized = false,
    
    /** @type {number} - The setTimeout reference for periodic updating of metrics. */
    _updateInterval = 4000,
    
    /** Method to update metrics with recursive setTimeout. */
    _update = function() {
        var updateUrl = yukon.url('/support/systemHealth/dataUpdate');
        
        $.getJSON(updateUrl).done(function(metrics) {
            
            //TODO: this should be more extensible (e.g. infer data columns from metric type)
            var
            queueMetrics = metrics['JMS_QUEUE'],
            extendedQueueMetrics = metrics['JMS_QUEUE_EXTENDED'];
            
            for (var i in queueMetrics) {
                var queue = queueMetrics[i],
                    id = queue.metricIdentifier;
                $('#' + id + '-enq').text(queue.enqueuedCount);
                $('#' + id + '-deq').text(queue.dequeuedCount);
                $('#' + id + '-size').text(queue.queueSize);
                $('#' + id + '-avg').text(queue.averageEnqueueTime);
                $('#' + id + '-status i').removeClass()
                                         .addClass('icon')
                                         .addClass(queue.status.iconName)
                                         .prop('title', queue.status.allMessages);
            }
            
            for (var i in extendedQueueMetrics) {
                var queue = extendedQueueMetrics[i],
                    id = queue.metricIdentifier;
                $('#' + id + '-enq').text(queue.enqueuedCount);
                $('#' + id + '-deq').text(queue.dequeuedCount);
                $('#' + id + '-size').text(queue.queueSize);
                $('#' + id + '-avg').text(queue.averageEnqueueTime);
                $('#' + id + '-status i').removeClass()
                                         .addClass('icon')
                                         .addClass(queue.status.iconName)
                                         .prop('title', queue.status.allMessages);
                $('#' + id + '-arc').text(queue.archivedReadingsCount !== null ? queue.archivedReadingsCount : '');
                $('#' + id + '-arp').text(queue.archivedRequestsProcessed);
            }
        }).fail(function(xhr, status, error) {
            debug.log('update failed:' + status + ': ' + error);
        }).always(function() {
            setTimeout(_update, _updateInterval);
        });
        
    },
    
    _mod = {
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '#favMetricButton', function (event) {
                var button = $(event.currentTarget),
                    metric = button.data('metric'),
                    action = button.data('action'),
                    favUrl = yukon.url('/support/systemHealth/' + metric + '/' + action),
                    csrfToken = $('#ajax-csrf-token').val();
                
                $.ajax({
                   url: favUrl,
                   type: 'POST',
                   data: {"com.cannontech.yukon.request.csrf.token": csrfToken},
                   success: function(data) {
                       if (action == 'favorite') {
                           button.data('action', 'unfavorite');
                           button.children('.icon-favorite-not')
                                 .removeClass('icon-favorite-not')
                                 .addClass('icon-star');
                       } else {
                           button.data('action', 'favorite');
                           button.children('.icon-star')
                                 .removeClass('icon-star')
                                 .addClass('icon-favorite-not');
                       }
                   },
                   error: function(xhr, status, error) {
                       var errorMsg = xhr.responseJSON.message;
                       yukon.ui.alertError(errorMsg);
                   }
                });
            });
            
            _update();
            
            _initialized = true;
        }
    };
    
    return _mod;
})();

$(function () { yukon.support.systemHealth.init(); });
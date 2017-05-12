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
                    id = queue.metricIdentifier,
                    statusText;
                $('#' + id + '-enq').text(queue.enqueuedCount);
                $('#' + id + '-deq').text(queue.dequeuedCount);
                $('#' + id + '-size').text(queue.queueSize);
                $('#' + id + '-avg').text(queue.averageEnqueueTime);
                $('#' + id + '-status i').removeClass()
                                         .addClass('icon')
                                         .addClass(queue.status.iconName)
                                         .prop('title', queue.status.allMessages);
                //These only apply to the detail page
                statusText = $('#metric-status-' + queue.status.metricStatus).text();
                $('#' + id + '-status-name').text(statusText);
                $('#' + id + '-status-messages').text(queue.status.allMessages);
            }
            
            for (var i in extendedQueueMetrics) {
                var queue = extendedQueueMetrics[i],
                    id = queue.metricIdentifier;
                $('#' + id + '-arc').text(queue.archivedReadingsCount !== null ? queue.archivedReadingsCount : '');
                $('#' + id + '-arp').text(queue.archiveRequestsProcessed);
                $('#' + id + '-enq').text(queue.enqueuedCount);
                $('#' + id + '-deq').text(queue.dequeuedCount);
                $('#' + id + '-size').text(queue.queueSize);
                $('#' + id + '-avg').text(queue.averageEnqueueTime);
                $('#' + id + '-status i').removeClass()
                                         .addClass('icon')
                                         .addClass(queue.status.iconName)
                                         .prop('title', queue.status.allMessages);
                //These only apply to the detail page
                statusText = $('#metric-status-' + queue.status.metricStatus).text();
                $('#' + id + '-status-name').text(statusText);
                $('#' + id + '-status-messages').text(queue.status.allMessages);
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
            
            $(document).on('yukon:support:systemhealth:sync', function(e) {
                $.ajax({
                    url: yukon.url('/support/systemHealth/sync'),
                    type: 'GET',
                    success: function(data) {
                        var successMsg = data.message;
                        yukon.ui.alertSuccess(successMsg);
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
var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

yukon.namespace('yukon.DataAnalysis');

yukon.DataAnalysis = (function() {
    var dataAnalysisModule = {
        changeStatus : function (msg) {
            var data = jQuery.parseJSON(msg.value),
                status = data.status.trim(),
                row = jQuery('[data-analysis=' + data.analysisId + ']'),
                viewButton = row.find('.f-results-button'),
                statusDiv = row.find('.f-analysis-status');
            
            if (row.data('status') === status) {
                //do nothing, status is unchanged
                return;
            }
            
            if (status === 'COMPLETE' || status === 'INTERRUPTED') {
                //set status string
                statusDiv.html(data.formattedStatus);
                //only enable results button if devices were analyzed
                if (data.hasDevices) {
                    viewButton.prop('disabled', false);
                } else {
                    viewButton.prop('disabled', true);
                }
            } else if (status === 'READING') {
                //set status string, with link
                statusDiv.html('<a href="/bulk/archiveDataAnalysis/read/readResults?resultsId=' + data.statusId
                        + '&analysisId=' + data.analysisId + '">' + data.formattedStatus + '</a>');
                //enable results button
                viewButton.prop('disabled', false);
            } else if (status === 'RUNNING') {
                //set status string, with link
                statusDiv.html('<a href="/bulk/archiveDataAnalysis/home/processing?resultsId=' + data.statusId
                        + '&analysisId=' + data.analysisId + '">' + data.formattedStatus + '</a>');
                //disable results button
                viewButton.prop('disabled', true);
            }
            //set view button tooltip
            viewButton.prop('title', data.buttonTooltip);
            //set status data in row
            row.data('status', status);
        }
    };
    
    return dataAnalysisModule;
})();

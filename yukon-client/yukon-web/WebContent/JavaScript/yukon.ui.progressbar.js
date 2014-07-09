
//'trys' used to hide initial errors when updater run before page is fully loaded
// because updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\

yukon.namespace('yukon.ui.progressbar');

yukon.ui.progressbar = (function () {
    
    var _setupProgressBar = function (pbarId, completedCount, totalCount, completionCallback) {
        var percentDone = 100,
            width,
            progressContainer;
        if (totalCount > 0) {
            percentDone = yukon.percent(completedCount, totalCount, 2);
        }

        try {
            width = yukon.percent(completedCount, totalCount, 2);
            progressContainer = _getProgressBarContainer(pbarId);
            progressContainer.find('.progress-bar').css('width', width);
            progressContainer.find('.progressbar-percent-complete').html(percentDone);
            progressContainer.find('.progressbar-completed-count').html(completedCount);
        } catch (e) {};

        if (completionCallback != null && percentDone == 100) {
            completionCallback();
        }
    },
    _setupSuccessFailureProgressBar = function (pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback) {
        var progressContainer = _getProgressBarContainer(pbarId),
            totalCompletedCount,
            percentDone,
            successWidth,
            failureWidth;
        
        if (progressContainer.length === 0) return;
        
        totalCompletedCount = parseInt(successCompletedCount, 10) + parseInt(failureCompletedCount, 10);
        percentDone = totalCount > 0 ? percentDone = yukon.percent(totalCompletedCount, totalCount, 2) : '100%';
        
        try {
            successWidth = yukon.percent(successCompletedCount, totalCount, 2);
            failureWidth = yukon.percent(failureCompletedCount, totalCount, 2);

            progressContainer.find('.progress-bar-success').css('width', successWidth);
            progressContainer.find('.progress-bar-danger').css('width', failureWidth);
            progressContainer.find('.progressbar-completed-count').html(totalCompletedCount);
            progressContainer.find('.progressbar-percent-complete').html(percentDone);
        } catch (e) {};

        // completionCallback
        if (typeof completionCallback === 'function' && totalCompletedCount === totalCount) {
            completionCallback();
        }
    },
    
    _updateTotalCount = function (pbarId, totalCount) {
        var progressContainer = _getProgressBarContainer(pbarId);
        if (progressContainer.length === 0) {
            return;
        }
        progressContainer.find('.progressbar-total').html(totalCount);
    },
    
    _getProgressBarContainer = function (pbarId) {
        return $('#' + pbarId);
    },
    mod;

    mod = {
        updateProgressBar : function (pbarId, totalCount, completionCallback) {
            return function (data) {
                var progressContainer = _getProgressBarContainer(pbarId),
                    completedCount;
                if (progressContainer.length === 0) {
                    return;
                }
                completedCount = data.completedCount;
                _setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
            };
        },
        updateProgressBarWithDynamicTotal : function (pbarId, completionCallback) {
            return function (data) {
                var progressContainer = _getProgressBarContainer(pbarId),
                    completedCount,
                    totalCount;
                if (progressContainer.length === 0) {
                    return;
                }

                completedCount = data.completedCount;
                totalCount = data.totalCount;
                _updateTotalCount(pbarId, totalCount);
                _setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
            };
        },
        updateSuccessFailureProgressBar : function (pbarId, totalCount, completionCallback) {
            return function (data) {
                var successCompletedCount = data.successCompletedCount,
                    failureCompletedCount = data.failureCompletedCount;
                _setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
            };
        },
        updateProgressStatus : function (pDescId) {
            return function (data) {
                var statusText = data.statusText;
                $('#progressStatus_' + pDescId).html(statusText);
            };
        },
        toggleElementsWhenTrue : function (elementsToToggle, show) {
            return function (data) {
                var value = data.value;
                if (value === 'true') {
                    elementsToToggle.forEach( function (el, index, arr) {
                        if (show) {
                            $('#' + el).show();
                        } else {
                            $('#' + el).hide();
                        }
                    });
                }
            };
        },
        updateSuccessFailureProgressBarWithDynamicTotal : function (pbarId, completionCallback) {
            return function (data) {
                var successCompletedCount = data.successCompletedCount,
                    failureCompletedCount = data.failureCompletedCount,
                    totalCount = data.totalCount;
                _updateTotalCount(pbarId, totalCount);
                _setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
            };
        },
        abortProgressBar : function (pbarId) {
            return function (data) {
                var progressContainer;
                if (data.isAborted === 'true') {
                    progressContainer = _getProgressBarContainer(pbarId);

                    // Check if we are a normal progress bar or success / fail progress bar
                    if (progressContainer.find('.progress-bar-danger').length <= 0) {
                        progressContainer.find('.progress-bar').addClass('progress-bar-danger');
                        progressContainer.find('.progress-bar').css('width', '100%');
                    } else {
                        progressContainer.find('.progress-bar').css('width', '0%');
                        progressContainer.find('.progress-bar-danger').css('width', '100%');
                    }
                }
            };
        }
    };
    
    return mod;
})();
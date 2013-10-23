Yukon.namespace('Yukon.ui.progressBar');

Yukon.ui.progressBar = (function () {
    var  setupProgressBar = function (pbarId, completedCount, totalCount, completionCallback) {
        var percentDone = 100,
            innerWidth,
            progressContainer;
        if (totalCount > 0) {
            percentDone = Math.floor((completedCount / totalCount) * 100);
        }

        try {
            innerWidth = getBarWidth(pbarId, completedCount, totalCount);
            progressContainer = getProgressBarContainer(pbarId);
            progressContainer.find('.progressBarInner').css('width', innerWidth + 'px');
            progressContainer.find('.progressBarPercentComplete').html(percentDone + '%');
            progressContainer.find('.progressBarCompletedCount').html(completedCount);
        } catch (e) {};

        if (completionCallback != null && percentDone == 100) {
            completionCallback();
        }
    },
    setupSuccessFailureProgressBar = function (pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback) {
        var progressContainer = getProgressBarContainer(pbarId),
            totalCompletedCount,
            percentDone,
            successWidth,
            failureWidth;
        if (progressContainer.length === 0) {
            return;
        }
        totalCompletedCount = parseInt(successCompletedCount, 10) + parseInt(failureCompletedCount, 10);

        percentDone = 100;
        if (totalCount > 0) {
            percentDone = Math.floor((totalCompletedCount / totalCount) * 100);
        }

        try {
            successWidth = getBarWidth(pbarId, successCompletedCount, totalCount);
            failureWidth = getBarWidth(pbarId, failureCompletedCount, totalCount);

            progressContainer.find('.progressBarInnerSuccess').css('width', successWidth + 'px');
            progressContainer.find('.progressBarInnerFailure').css('width', failureWidth + 'px');
            progressContainer.find('.progressBarCompletedCount').html(totalCompletedCount);
            progressContainer.find('.progressBarPercentComplete').html(percentDone + '%');
        } catch (e) {};

        // completionCallback
        if (completionCallback != null && percentDone == 100) {
            completionCallback();
        }
    },
    updateTotalCount = function (pbarId, totalCount) {
        var progressContainer = getProgressBarContainer(pbarId);
        if (progressContainer.length === 0) {
            return;
        }
        progressContainer.find('.progressBarTotal').html(totalCount);
    },
    getBarWidth = function (pbarId, completed, total) {
        var progressContainer,
            progressBorder,
            width,
            percentDecimal,
            length;
        if (completed == 0 || total == 0) {
            return 0;
        }
        progressContainer = getProgressBarContainer(pbarId);
        progressBorder = progressContainer.find('.progressBarBorder');
        width = progressBorder.width(); // subtract 1 px for each border
        percentDecimal = parseFloat(completed / total);
        length = Math.floor(percentDecimal * width);
        return length;
    },
    getProgressBarContainer = function (pbarId) {
        return jQuery('#progressContainer_' + pbarId);
    },
    barMod;
    // 'trys' used to hide initial errors when updater run before page is fully loaded
    // because updater are contained in tags used throughout page instead of usual place at
    // bottom of page, this occurs sometimes =\

    barMod = {
        updateProgressBar : function (pbarId, totalCount, completionCallback) {
            return function (data) {
                var progressContainer = getProgressBarContainer(pbarId),
                    completedCount;
                if (progressContainer.length === 0) {
                    return;
                }
                completedCount = data.completedCount;
                setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
            };
        },
        updateProgressBarWithDynamicTotal : function (pbarId, completionCallback) {
            return function (data) {
                var progressContainer = getProgressBarContainer(pbarId),
                    completedCount,
                    totalCount;
                if (progressContainer.length === 0) {
                    return;
                }

                completedCount = data.completedCount;
                totalCount = data.totalCount;
                updateTotalCount(pbarId, totalCount);
                setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
            };
        },
        updateSuccessFailureProgressBar : function (pbarId, totalCount, completionCallback) {
            return function (data) {
                var successCompletedCount = data.successCompletedCount,
                    failureCompletedCount = data.failureCompletedCount;
                setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
            };
        },
        updateProgressStatus : function (pDescId) {
            return function (data) {
                var statusText = data.statusText;
                jQuery('#progressStatus_' + pDescId).html(statusText);
            };
        },
        toggleElementsWhenTrue : function (elementsToToggle, show) {
            return function (data) {
                var value = data.value;
                 if (value === 'true') {
                    elementsToToggle.forEach( function (el, index, arr) {
                        if (show) {
                            jQuery(el).show();
                        } else {
                            jQuery(el).hide();
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
                updateTotalCount(pbarId, totalCount);
                setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
            };
        },
        abortProgressBar : function (pbarId) {
            return function (data) {
            var progressContainer;
                if (data.isAborted === 'true') {
                    progressContainer = getProgressBarContainer(pbarId);

                    // Check if we are a normal progress bar or success / fail progress bar
                    if (progressContainer.find('.progressBarInner').length < 0) {
                        progressContainer.find('.progressBarInner').addClass('progressBarInnerFailure');
                        progressContainer.find('.progressBarInner').css('width', '100%');
                    } else {
                        progressContainer.find('.progressBarInnerFailure').css('width', '100%');
                    }
                }
            };
        }
    };
    return barMod;
})();
// 'trys' used to hide initial errors when updater run before page is fully loaded
// becuase updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\


function updateProgressBar(pbarId, totalCount, completionCallback) {
    return function(data) {
        var progressContainer = getProgressBarContainer(pbarId),
            completedCount;
        if (progressContainer.length === 0) {
            return;
        }

        completedCount = data.completedCount;
        setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
    };
}

function updateProgressBarWithDynamicTotal(pbarId, completionCallback) {
    return function(data) {
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
}
    
function setupProgressBar(pbarId, completedCount, totalCount, completionCallback) {
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
        progressContainer.find('.progressBarCompletedCount span').html(completedCount);
    } catch(e) {}

    if (completionCallback != null && percentDone == 100) {
        completionCallback();
    }
}

function updateSuccessFailureProgressBar(pbarId, totalCount, completionCallback) {
    return function(data) {
        var successCompletedCount = data.successCompletedCount,
            failureCompletedCount = data.failureCompletedCount;
        setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
    };
}

function updateSuccessFailureProgressBarWithDynamicTotal(pbarId, completionCallback) {
    return function(data) {
        var successCompletedCount = data.successCompletedCount,
            failureCompletedCount = data.failureCompletedCount,
            totalCount = data.totalCount;
        updateTotalCount(pbarId, totalCount);
        setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
    };
}

function setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback) {
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
        progressContainer.find('.progressBarCompletedCount span').html(totalCompletedCount);
        progressContainer.find('.progressBarPercentComplete').html(percentDone + '%');
    } catch (e) {}

    // completionCallback
    if (completionCallback != null && percentDone == 100) {
        completionCallback();
    }
}

function updateTotalCount(pbarId, totalCount) {
    var progressContainer = getProgressBarContainer(pbarId);
    if (progressContainer.length === 0) {
        return;
    }
    progressContainer.find('.progressBarTotal').html(totalCount);
}

function getBarWidth(pbarId, completed, total) {
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
}

function getProgressBarContainer(pbarId) {
    return jQuery('#progressContainer_' + pbarId);
}

function abortProgressBar(pbarId) {
    return function(data) {
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

function updateProgressStatus(pDescId) {
    return function(data) {
        var statusText = data.statusText;
        jQuery('#progressStatus_' + pDescId).html(statusText);
    };
}

function toggleElementsWhenTrue(elementsToToggle, show) {
    return function(data) {
        var value = data.value;
        
        if (value == 'true') {
            
            $A(elementsToToggle).each(function(el) {
                if (show) {
                    $(el).show();
                } else {
                    $(el).hide();
                }
            });
        }
    };
}
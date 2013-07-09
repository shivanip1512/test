// 'trys' used to hide initial errors when updater run before page is fully loaded
// becuase updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\


function updateProgressBar(pbarId, totalCount, completionCallback) {
    return function(data) {
        var progressContainer = getProgressBarContainer(pbarId),
        completedCount;
        if (progressContainer == null) {
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
        if (progressContainer == null) {
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
        progressContainer.down('.progressBarInner').style.width = innerWidth + 'px';
        progressContainer.down('.progressBarPercentComplete').innerHTML = percentDone + '%';
        progressContainer.down('.progressBarCompletedCount span').innerHTML = completedCount;
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
    if (progressContainer == null) {
        return;
    }
    totalCompletedCount = parseInt(successCompletedCount) + parseInt(failureCompletedCount);

    percentDone = 100;
    if (totalCount > 0) {
        percentDone = Math.floor((totalCompletedCount / totalCount) * 100);
    }

    try {
        successWidth = getBarWidth(pbarId, successCompletedCount, totalCount);
        failureWidth = getBarWidth(pbarId, totalCompletedCount, totalCount);
        
        progressContainer.down('.progressBarInnerSuccess').style.width = successWidth + 'px';
        progressContainer.down('.progressBarInnerFailure').style.width = failureWidth + 'px';
        progressContainer.down('.progressBarCompletedCount span').innerHTML = totalCompletedCount;
        progressContainer.down('.progressBarPercentComplete').innerHTML = percentDone + '%';
    } catch (e) {}

    // completionCallback
    if (completionCallback != null && percentDone == 100) {
        completionCallback();
    }
}

function updateTotalCount(pbarId, totalCount) {
    var progressContainer = getProgressBarContainer(pbarId);
    if (progressContainer == null) {
        return;
    }
    progressContainer.down('.progressBarTotal').innerHTML = totalCount;
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
    progressBorder = progressContainer.down('.progressBarBorder');
    width = Math.ceil(progressBorder.measure('width'));
    percentDecimal = parseFloat(completed / total);
    length = Math.ceil(percentDecimal * width);
    return length;
}

function getProgressBarContainer(pbarId) {
    return $('progressContainer_' + pbarId);
}

function abortProgressBar(pbarId) {
    return function(data) {
	var progressContainer;
        if (data.isAborted === 'true') {
            progressContainer = getProgressBarContainer(pbarId);

            // Check if we are a normal progress bar or success / fail progress bar
            if (progressContainer.down('.progressBarInner') != null) {
                progressContainer.down('.progressBarInner').addClassName('progressBarInnerFailure');
                progressContainer.down('.progressBarInner').setStyle({width: '100%'});
            } else {
                progressContainer.down('.progressBarInnerFailure').setStyle({width: '100%'});
            }
        }
    };
}

function updateProgressStatus(pDescId) {
    return function(data) {
        var statusText = data.statusText;
        $('progressStatus_' + pDescId).innerHTML = statusText;
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
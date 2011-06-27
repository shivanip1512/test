// 'trys' used to hide initial errors when updater run before page is fully loaded
// becuase updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\

function updateProgressBar(pbarId, totalCount, completionCallback) {
  //assumes data is of type Hash
    return function(data) {
        var progressContainer = $('progressContainer_' + pbarId);
        if (progressContainer == null) {
            return;
        }

        var completedCount = data.get('completedCount');
        setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
    };
}

function updateProgressBarWithDynamicTotal(pbarId, completionCallback) {
  //assumes data is of type Hash
    return function(data) {
        var progressContainer = $('progressContainer_' + pbarId);
        if (progressContainer == null) {
            return;
        }

        var completedCount = data.get('completedCount');
        var totalCount = data.get('totalCount');
        setupProgressBar(pbarId, completedCount, totalCount, completionCallback);
    };
}
    
function setupProgressBar(pbarId, completedCount, totalCount, completionCallback) {
    var percentDone = 100;
    if (totalCount > 0) {
        percentDone = Math.floor((completedCount / totalCount) * 100);
    }
    
    try {
        var innerWidth = getBarWidth(pbarId, completedCount, totalCount);
        var progressContainer = $('progressContainer_' + pbarId);
        progressContainer.down('.progressBarInner').style.width = innerWidth + 'px';
        progressContainer.down('.progressBarPercentComplete').innerHTML = percentDone + '%';
        progressContainer.down('.progressBarCompletedCount span').innerHTML = completedCount;
    } catch(e) {}

    if (completionCallback != null && percentDone == 100) {
        completionCallback();
    }
}

function updateSuccessFailureProgressBar(pbarId, totalCount, completionCallback) {
    // assumes data is of type Hash
    return function(data) {
        var successCompletedCount = data.get('successCompletedCount');
        var failureCompletedCount = data.get('failureCompletedCount');
        setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
    };
}

function updateSuccessFailureProgressBarWithDynamicTotal(pbarId, completionCallback) {
    // assumes data is of type Hash
    return function(data) {
        var successCompletedCount = data.get('successCompletedCount');
        var failureCompletedCount = data.get('failureCompletedCount');
        var totalCount = data.get('totalCount');
        setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback);
    };
}

function setupSuccessFailureProgressBar(pbarId, totalCount, successCompletedCount, failureCompletedCount, completionCallback) {
    var progressContainer = $('progressContainer_' + pbarId);
    if (progressContainer == null) {
        return;
    }
    var totalCompletedCount = parseInt(successCompletedCount) + parseInt(failureCompletedCount);

    var percentDone = 100;
    if (totalCount > 0) {
        percentDone = Math.floor((totalCompletedCount / totalCount) * 100);
    }

    try {
        var successWidth = getBarWidth(pbarId, successCompletedCount, totalCount);
        var failureWidth = getBarWidth(pbarId, totalCompletedCount, totalCount);
        
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

function updateTotalCount(pbarId) {
    return function(data) {
        var progressContainer = $('progressContainer_' + pbarId);
        if (progressContainer == null) {
            return;
        }

        var totalCount = data.get('total');
        progressContainer.down('.progressBarTotal').innerHTML = totalCount;
    };
}

function getBarWidth(pbarId, completed, total) {
    if (completed == 0 || total == 0) {
        return 0;
    }
    var progressBorder = $('progressContainer_' + pbarId).down('.progressBarBorder');
    var width = Math.ceil(progressBorder.measure('width'));
    var percentDecimal = parseFloat(completed / total);
    var length = Math.ceil(percentDecimal * width)
    return length;
}

function abortProgressBar(pbarId) {
  //assumes data is of type Hash
    return function(data) {
        if (data.get('isAborted') == 'true') {
            var progressContainer = $('progressContainer_' + pbarId);

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
  //assumes data is of type Hash
    return function(data) {
    	var statusText = data.get('statusText');
    	$('progressStatus_' + pDescId).innerHTML = statusText;
    };
}

function toggleElementsWhenTrue(elementsToToggle, show) {
  //assumes data is of type Hash
    return function(data) {
        var value = data.get('value');
        
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
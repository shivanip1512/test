// 'trys' used to hide initial errors when updater run before page is fully loaded
// becuase updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\

function updateProgressBar(pbarId, totalCount) {

    return function(data) {
    
        var completedCount = data['completedCount'];
        
        var percentDone = Math.floor((completedCount / totalCount) * 100);
        if (totalCount == 0) {
            percentDone = 100;
        }
        
        try {
            $('progressInner_' + pbarId).style.width = percentDone + 'px';
            $('completedCount_' + pbarId).innerHTML = completedCount; 
            $('percentComplete_' + pbarId).innerHTML = percentDone + '%'; 
        } catch(e) {}
    };
}

function cancelProgressBar(pbarId) {

    return function(data) {

        if (data['isCanceled'] == 'true') {
            $('progressBorder_' + pbarId).style.backgroundColor = '#CC0000';
        }
    };
}

function updateProgressDescription(pDescId, text) {

    return function(data) {
    
        var isCompleteCondition = data['isCompleteCondition'];
        
        if (isCompleteCondition == 'true') {
            $('progressDescription_' + pDescId).innerHTML = text;
        }
    };
}

function toggleElementsOnComplete(elementsToToggle, show) {
    
    return function(data) {
        
        var isComplete = data['isComplete'];
        
        if (isComplete == 'true') {
            
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
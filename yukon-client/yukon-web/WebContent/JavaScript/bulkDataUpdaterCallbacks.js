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

function updateProgressDescription(pDescId, totalCount, completeText) {

    return function(data) {
    
        var completedCount = data['completedCount'];
        
        if (completedCount == totalCount) {
            try {
                $('progressDescription_' + pDescId).innerHTML = completeText;
            } catch(e) {}
        }
    };
}

function showElementsOnComplete(totalCount, elementsToShow) {
    
    return function(data) {

        var completedCount = data['completedCount'];
        
        if (completedCount == totalCount) {
        
            $A(elementsToShow).each(function(el) {
                $(el).show();
            });
        }
    };
}
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

function abortProgressBar(pbarId) {

    return function(data) {

        if (data['isAborted'] == 'true') {
            $('progressBorder_' + pbarId).style.backgroundImage = "url('/WebConfig/yukon/Icons/progressbar_red.gif')";
        }
    };
}

function updateProgressStatus(pDescId) {

    return function(data) {
    
    	var statusText = data['statusText'];
    	$('progressStatus_' + pDescId).innerHTML = statusText;
    };
}

function toggleElementsWhenTrue(elementsToToggle, show) {
    
    return function(data) {
        
        var value = data['value'];
        
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
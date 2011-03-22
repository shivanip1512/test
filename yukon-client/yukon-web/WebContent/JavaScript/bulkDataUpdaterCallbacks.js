// 'trys' used to hide initial errors when updater run before page is fully loaded
// becuase updater are contained in tags used throughout page instead of usual place at
// bottom of page, this occurs sometimes =\

function updateProgressBar(pbarId, totalCount, completionCallback) {
  //assumes data is of type Hash
    return function(data) {
        var completedCount = data.get('completedCount');
        
        var percentDone = Math.floor((completedCount / totalCount) * 100);
        if (totalCount == 0) {
            percentDone = 100;
        }
        
        try {
            $('progressInner_' + pbarId).style.width = percentDone + 'px';
            $('completedCount_' + pbarId).innerHTML = completedCount; 
            $('percentComplete_' + pbarId).innerHTML = percentDone + '%'; 
        } catch(e) {}
        
        // completionCallback
        if (completionCallback != null && percentDone == 100) {
        	completionCallback();
        }
    };
}

function abortProgressBar(pbarId) {
  //assumes data is of type Hash
    return function(data) {
        if (data.get('isAborted') == 'true') {
            $('progressBorder_' + pbarId).style.backgroundImage = "url('/WebConfig/yukon/Icons/progressbar_red.gif')";
        }
    };
}

function updateProgressStatus(pDescId) {
  //assumes data is of type Hash
    return function(data) {
    	var statusText = data.get('statusText');
    	$('progressStatus_' + pDescId).innerHTML = statusText;
    	if (statusText = "error") {
    		$('progressStatus_' + pDescId).addClassName("ErrorMsg");
    	}
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
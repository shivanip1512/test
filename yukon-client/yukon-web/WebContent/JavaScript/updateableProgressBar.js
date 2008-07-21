function updateProgressBar(pbarId, totalCount, updateCallback) {

    return function(data) {
    
        var completedCount = data['completedCount'];
        
        var percentDone = Math.floor((completedCount / totalCount) * 100);
        if (totalCount == 0) {
            percentDone = 100;
        }
        
        $('progressInner_' + pbarId).style.width = percentDone + 'px';
        $('completedCount_' + pbarId).innerHTML = completedCount; 
        $('percentComplete_' + pbarId).innerHTML = percentDone + '%'; 
        
        if (updateCallback != '') {
            eval(updateCallback + '(' + completedCount + ', ' + totalCount + ');');
        }
         
    };
}
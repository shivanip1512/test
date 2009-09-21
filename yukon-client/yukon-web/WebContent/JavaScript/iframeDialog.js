function openIFrameDialog(dialogId, url, title, width, height) {
    var dialogDiv = $(dialogId);

    var windowWidth = 790, windowHeight = 580;
    if (navigator.appName.indexOf("Microsoft")!=-1) {
        windowWidth = document.body.offsetWidth;
        windowHeight = document.body.offsetHeight;
    } else {
        windowWidth = window.innerWidth;
        windowHeight = window.innerHeight;
    }

    var dialogWidth = dialogDiv.getWidth();
    var dialogHeight = dialogDiv.getHeight();
    var x = 0, y = 0;
    if (windowWidth > dialogWidth) {
        x = (windowWidth - dialogWidth) / 2; 
    }
    if (windowHeight > dialogHeight) {
        y = (windowHeight - dialogHeight) / 2; 
    }

    dialogDiv.setStyle({
        'top': y + "px",
        'left': x + "px"
    });
    var iframe = $(dialogId + '_iframe');
    iframe.src = url;

    if (arguments.length > 2) {
        $(dialogId + '_title').innerHTML = title;
    }
    if (arguments.length > 3) {
        dialogDiv.setStyle({
            'width': width + "px"
        });
    }
    if (arguments.length > 4) {
        dialogDiv.setStyle({
            'height': height + "px"
        });
    }
    dialogDiv.show();
}

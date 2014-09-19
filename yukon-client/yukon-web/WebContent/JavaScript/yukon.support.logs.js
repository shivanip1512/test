$(function() {
    var fileLength = 0;

    var update = function() {
        var requestData = {
            fileLength : fileLength,
            numLines : $('#numLines').val(),
            file : $('#file').val()
        };

        var request = $.ajax({
            url : $('#updateUrl').val(),
            type : "POST",
            data : JSON.stringify(requestData),
            contentType : 'application/json',
            dataType : 'json'
        });

        request.done(function(response) {
            var logLines = response.logLines;
            var lastModified = response.lastModified;
            var fileSize = response.fileSize;

            if (logLines != null && logLines != "") {
                // This part of the function updates the log last modified field shown on the screen
                if (fileLength !== fileSize) {
                    fileLength = fileSize;
                    $('#lastMod').html(lastModified);
                    $('#fileLength').html(response.readableFileSize);
                    $("#lastMod, #fileLength").flash();
                }

                // This part of the function updates the log contents shown on the screen
                $(logLines).each(function(index, newLogLine) {
                    var newDiv = $("<div class='logLine'>" + newLogLine +"\n</div>");
                    $("#logOutput").append(newDiv);
                    //This animation sets overflow:hidden at the end. This prevents us from scrolling left-right.
                    //newDiv.slideDown(200);
                });
                // remove extra lines but do not reload page if numLines is more than we currently have
                removeExtraLines(false);
            }
        });

        request.fail(function(jqXHR, textStatus) {
            clearInterval(id);
            id = setInterval(update, 1000 * 10);
            var newDiv = $("<div class='logLine'>Error: " + textStatus + "...Trying again in 10 seconds.</div>").hide();
            $("#logOutput").append(newDiv);
            newDiv.slideDown();
        });
    };

    setNumberOfLines = function(num) {
        if (isNaN(num) || num <= 10) {
            $("#numLines").val(10);
            $("#decrementLinesBtn").removeClass("prev").addClass("prev_disabled");
        } else {
            $("#numLines").val(num);
            $("#decrementLinesBtn").removeClass("prev_disabled").addClass("prev");
        }
        removeExtraLines(true);
    };

    removeExtraLines = function(reloadIfNeeded) {
        var numLogsCurrent = $('.logLine').size();
        var numLinesNew = $("#numLines").val();
        var linesToSlice = numLogsCurrent - numLinesNew;
        if (linesToSlice > 0) {
            $('.logLine').slice(0, linesToSlice).slideUp(200, function() {
                this.remove();
            });
        } else if (linesToSlice < 0 && reloadIfNeeded) {
            // we requested more lines than we currently have, reload page to obtain
            window.location.href = "?file=" + $('#file').val() + "&numLines=" + numLinesNew;
        }
    };

    update();
    var repeatingTaskId = setInterval(update, 1500);
    $("#startBtn").hide();

    $("#numLines").change(function() {
        setNumberOfLines($("#numLines").val());
    });

    $("#decrementLinesBtn").click(function() {
        var num = parseInt($("#numLines").val());
        setNumberOfLines(num-10);
    });

    $("#incrementLinesBtn").click(function() {
        var num = parseInt($("#numLines").val());
        setNumberOfLines(num+10);
    });

    $("#pauseBtn").click(function() {
        clearInterval(repeatingTaskId);
        $("#pauseBtn").fadeOut(100, function() {
            $("#startBtn").fadeIn(25);
        });
    });

    $("#startBtn").click(function() {
        repeatingTaskId = setInterval(update, 1500);
        $("#startBtn").fadeOut(100, function() {
            $("#pauseBtn").fadeIn(25);
        });
    });
});
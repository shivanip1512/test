jQuery(function() {
	fileLength = 0;

	var update = function() {
		// if none exist on this page, get out build up JS object to be used for request
		var requestData = {
			fileLength : fileLength,
			numLines : jQuery('#numLines').val(),
			file : jQuery('#file').val()
		};

		var request = jQuery.ajax({
			url : jQuery('#updateUrl').val(),
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
					jQuery('#lastMod').html(lastModified);
					jQuery('#fileLength').html(response.readableFileSize);
					jQuery("#lastMod, #fileLength").flashColor({
						color : "#FF8",
						duration : 1000
					});
				}

				// This part of the function updates the log contents shown on the screen
				logLines.each(function(newLogLine) {
					var newDiv = jQuery("<div class='logLine'>" + newLogLine +"\n</div>").hide();
					jQuery("#logOutput").append(newDiv);
					newDiv.slideDown(200);
				});
				// remove extra lines but do not reload page if numLines is more than we currently have
				removeExtraLines(false);
			}
		});

		request.fail(function(jqXHR, textStatus) {
			clearInterval(id);
			id = setInterval(update, 1000 * 10);
			var newDiv = jQuery("<div class='logLine'>Error: " + textStatus + "...Trying again in 10 seconds.</div>").hide();
			jQuery("#logOutput").append(newDiv);
			newDiv.slideDown();
		});
	};

	setNumberOfLines = function(num) {
		if (isNaN(num) || num <= 5) {
			jQuery("#numLines").val(5);
			jQuery("#decrementLinesBtn").removeClass("prev").addClass("prev_disabled");
		} else {
			jQuery("#numLines").val(num);
			jQuery("#decrementLinesBtn").removeClass("prev_disabled").addClass("prev");
		}
		removeExtraLines(true);
	};

	removeExtraLines = function(reloadIfNeeded) {
		var numLogsCurrent = jQuery('.logLine').size();
		var numLinesNew = jQuery("#numLines").val();
		var linesToSlice = numLogsCurrent - numLinesNew;

		if (linesToSlice > 0) {
			jQuery('.logLine').slice(0, linesToSlice).slideUp(300, function() {
				this.remove();
			});
		} else if (linesToSlice < 0 && reloadIfNeeded) {
			// we requested more lines than we currently have, reload page to obtain
			window.location.href = "?file=" + jQuery('#file').val() + "&numLines=" + numLinesNew;
		}
	};


	update();
	var repeatingTaskId = setInterval(update, 1000 * 3);
	jQuery("#startBtn").hide();
	
	jQuery("#numLines").change(function() {
		setNumberOfLines(jQuery("#numLines").val());
	});

	jQuery("#decrementLinesBtn").click(function() {
		var num = parseInt(jQuery("#numLines").val());
		setNumberOfLines(num-5);
	});

	jQuery("#incrementLinesBtn").click(function() {
		var num = parseInt(jQuery("#numLines").val());
		setNumberOfLines(num+5);
	});

	jQuery("#pauseBtn").click(function() {
		clearInterval(repeatingTaskId);
		jQuery("#pauseBtn").hide();
		jQuery("#startBtn").show();
	});

	jQuery("#startBtn").click(function() {
		repeatingTaskId = setInterval(update, 1000 * 3);
		jQuery("#startBtn").hide();
		jQuery("#pauseBtn").show();
	});
});
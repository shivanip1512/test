IconChooser = Class.create();

IconChooser.prototype = {
	initialize: function(iconId, baseDir) {
		this.iconId = iconId;
		this.baseDir = baseDir;
		this.timeout = false;

		this.iconInput = $(this.iconId + 'IconInput');
		this.hiddenIconInput = $(this.iconId + 'HiddenIconInput');
		this.iconPreviewImg = $(this.iconId + 'IconPreviewImg');
	},

	iconSelected: function() {
		var selected = $(this.iconId + 'IconSelect').value;
		if (selected == 'OTHER') {
			this.iconInput.enable();
			this.iconPreviewImg.src = this.baseDir + this.iconInput.value;
			this.hiddenIconInput.value = this.iconInput.value;
			this.iconPreviewImg.show();
	        this.iconInput.focus();
		} else {
			this.iconInput.disable();
			if (selected == 'NONE') {
				this.iconPreviewImg.hide();
				this.hiddenIconInput.value = '';
			} else {
				this.iconPreviewImg.src =
					this.baseDir + this.iconFilenames[selected];
				this.iconPreviewImg.show();
				this.hiddenIconInput.value = this.iconFilenames[selected];
			}
		}
	},

	iconInputChanged: function() {
		var that = this;
		updatePreview = function() {
			that.timeout[that.iconId] = false;
			that.hiddenIconInput.value = that.iconInput.value;
			that.iconPreviewImg.src = that.baseDir + that.iconInput.value;
		}

		if (this.iconPreviewImg.src.endsWith(this.baseDir + this.iconInput.value)) {
			return;
		}
		if (this.timeout) {
			clearTimeout(this.timeout);
		}
		var quietDelay = 300;
		this.timeout = setTimeout(updatePreview, quietDelay);
	}
}

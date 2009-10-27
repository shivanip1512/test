JsWidgetObject = Class.create();

JsWidgetObject.prototype = { 
  initialize: function (shortName, parameters) {
    // Don't use $() up here because this is called before page is fully loaded
    this.shortName = shortName;
    this.parameters = parameters;
    this.container = "widgetContainer_" + this.parameters.widgetId;
    this.linkInfo = $H();
  },

  render: function() {
    var url = "/spring/widget/" + this.shortName + "/render";
    new Ajax.Updater(this.container, url, {'parameters': this.getWidgetParameters(), 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  onSuccess: function(transport, json) {
    this.parameters = json;
    // this helps prevent this from growing like crazy each time the widget is refreshed
    this.linkInfo = $H();
  },
  
  doDirectActionRefresh: function(cmd) {
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    params = this.getWidgetParameters();
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(this.container, url, {'parameters': params, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  doDirectActionContainerRefresh: function(cmd, container) {
    $(container).getElementsBySelector('input').invoke('disable');
    
    params = this.getWidgetParameters();
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(container, url, {'parameters': params, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  doActionRefresh: function(cmd, actionButton, waitingLabel, key) {
    $(actionButton).getElementsBySelector('.widgetAction_waiting').invoke('show');
    $(actionButton).getElementsBySelector('input').each(function(it) {it.value = waitingLabel});
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    newParams = $H(this.linkInfo[key]);
    oldParams = this.getWidgetParameters();
    oldParams.merge(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(this.container, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  doActionUpdate: function(cmd, theContainer, actionButton, waitingLabel, key) {
    $(actionButton).getElementsBySelector('.widgetAction_waiting').invoke('show');
    var input = $(actionButton).getElementsBySelector('input').first();
    var initialLabel = input.value;
    input.value = waitingLabel;
    input.disable();
    
    var localSuccess = function() {
      // the following is only useful for the actionUpdate case
      $(actionButton).getElementsBySelector('.widgetAction_waiting').invoke('hide'); 
      input.value = initialLabel;
      input.enable();
    
      this.onSuccess();
    }
    
    newParams = $H(this.linkInfo[key]);
    oldParams = this.getWidgetParameters();
    oldParams.merge(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(theContainer, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': localSuccess});
  },
  
  setParameter: function(name, value){
  	this.parameters[name] = value;
  },

  setupLink: function(key, jsonData){
  	this.linkInfo[key] = jsonData;
  },

  doActionLinkRefresh: function(cmd, actionSpan, waitingLabel, key, container) {
    $(actionSpan).getElementsBySelector('.widgetAction_waiting').invoke('show');
    $(actionSpan).getElementsBySelector('span').innerHTML = waitingLabel;
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    newParams = $H(this.linkInfo[key]);
    oldParams = this.getWidgetParameters();
    oldParams.merge(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    
    var useContainer = container;
    if (container == undefined || container == '') {
        useContainer = this.container;
    }
    new Ajax.Updater(useContainer, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
    $(this.container).getElementsBySelector('input').invoke('enable');
  },
  
  getWidgetParameters: function() {
    var container = $(this.container);
    var theseParameters = {};
    
    // now find all inputs under the container
    var widgetInputs = container.getElementsByTagName('input');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        if (el.type != 'radio') {
            theseParameters[el.name] = $F(el);
        }
        else {
            if (el.checked) {
                theseParameters[el.name] = $F(el);
            }
        }
      }
    }
    var widgetInputs = container.getElementsByTagName('select');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
      }
    }
    var widgetInputs = container.getElementsByTagName('textarea');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
      }
    }
    
    var mergedParameters = $H(this.parameters);
    mergedParameters.merge(theseParameters);
    return mergedParameters;
  },
  
  
  doActionPopup: function(cmd, actionSpan, key, dialogId, width, height) {
	    newParams = $H(this.linkInfo[key]);
	    oldParams = this.getWidgetParameters();
	    oldParams.merge(newParams);
	    
	    var url = "/spring/widget/" + this.shortName + "/" + cmd;

	    
	    var dialogDiv = $(dialogId);

	    if (arguments.length > 2) {
	        $(dialogId + '_title').innerHTML = key;
	    }

	    var dlgWidth = 0;
	    var dlgHeight = 0;
	    if (arguments.length > 4) {
	        dlgWidth = width;
	    }
	    if (arguments.length > 5) {
	        dlgHeight = height;
	    }

	    var successCallback = function(transport) {
	        if (dlgWidth) {
	            dialogDiv.setStyle({
	                'width': dlgWidth + "px"
	            });
	        }
	        if (dlgHeight) {
	            dialogDiv.setStyle({
	                'height': dlgHeight + "px"
	            });
	        }

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

	        // annoyingly, IE uses the document size and not actually the window
	        // height, so the calculated y won't work...
	        y = 150;

	        dialogDiv.setStyle({
	            'top': y + "px",
	            'left': x + "px"
	        });

	        dialogDiv.show();
	    }

	    new Ajax.Updater(dialogId + '_body',
	    		         url, 
	    		         {'parameters': oldParams,
	    	              'onSuccess': successCallback
	                     });
	    
  }
};

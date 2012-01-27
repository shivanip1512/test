
// TODO: Update the rest of this file to use jQuery
// Currently the only jQuery in here are the .ajax calls (except for doPeriodicRefresh)

function JsWidgetObject(shortName, parameters) { 
  // Don't use $() up here because this is called before page is fully loaded
  this.shortName = shortName;
  this.parameters = parameters;
  this.container = "widgetContainer_" + this.parameters.widgetId;
  this.linkInfo = {};

  this.render = function() {
    var url = "/spring/widget/" + this.shortName + "/render";
    var params = this.getWidgetParameters();
    var _self = this;

    jQuery.ajax({
    	url: url,
    	data: params,
    	success: function(data, status, xhr) {
    		_self.onSuccess(xhr);
    		jQuery("#" + _self.container).html(data);
    	}
    });
  };
  
  this.onSuccess = function(xhr) {
	  this.parameters = getHeaderJSON(xhr);
	  // this helps prevent this from growing like crazy each time the widget is refreshed
	  this.linkInfo = {};
  };
  
  this.onSuccessPeriodic = function(transport, json) {
    this.parameters = json;
    // this helps prevent this from growing like crazy each time the widget is refreshed
    this.linkInfo = {};
  };
  
  this.doDirectActionRefresh = function(cmd) {
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    var params = this.getWidgetParameters();
    var _self = this;
    
    jQuery.ajax({
    	url: url,
    	data: params,
    	success: function(data, status, xhr) {
    		_self.onSuccess(xhr);
    		jQuery("#" + _self.container).html(data);
    	}
    });
  };
  
  this.doDirectActionContainerRefresh = function(cmd, container) {
    $(container).getElementsBySelector('input, button').invoke('disable');
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    var params = this.getWidgetParameters();
    var _self = this;
    
    jQuery.ajax({
    	url: url,
    	data: params,
    	success: function(data, status, xhr) {
    		_self.onSuccess(xhr);
    		jQuery("#" + container).html(data);
    	}
    });
  };
  
  /**
   * args = {
   *    command = string final path for the url
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
  this.doActionRefresh = function(args) {
      var defaultButtonText = $(args.buttonID).down('span').innerHTML;
      $(args.buttonID).down('span').innerHTML = args.waitingText;
    $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('show');
    var container = this.container;
    $(container).getElementsBySelector('input').invoke('disable');
    $(container).getElementsBySelector('button').invoke('disable');
    
    var _self = this;
    var localSuccess = function(xhr) {
        $(args.buttonID).down('span').innerHTML = defaultButtonText;
        $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('hide');
        $(container).getElementsBySelector('input').invoke('enable');
        $(container).getElementsBySelector('button').invoke('enable');
        _self.onSuccess(xhr);
    }
    
    oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[args.key]);
    
    var url = "/spring/widget/" + this.shortName + "/" + args.command;

    jQuery.ajax({
    	url: url,
    	data: oldParams,
    	success: function(data, status, xhr) {
    		localSuccess(xhr);
    		jQuery("#" + _self.container).html(data);
    	}
    });
  };
  
  /**
   * args = {
   *    command = string final path for the url
   *    containerID = string containing the ID of the container
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
  this.doActionUpdate = function(args) {
    var defaultButtonText = $(args.buttonID).down('span').innerHTML;
    $(args.buttonID).down('span').innerHTML = args.waitingText;
    $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('show');
    var container = this.container;
    $(container).getElementsBySelector('button').invoke('disable');
    
    var localSuccess = function() {
        $(args.buttonID).down('span').innerHTML = defaultButtonText;
        $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('hide');
        $(container).getElementsBySelector('button').invoke('enable');
    }
    
    oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[args.key]);
    
    var url = "/spring/widget/" + this.shortName + "/" + args.command;

    jQuery.ajax({
    	url: url,
    	data: oldParams,
    	success: function(data) {
    		localSuccess();
    		jQuery("#" + args.containerID).html(data);
    	}
    });
  };
  
  this.setParameter = function(name, value){
  	this.parameters[name] = value;
  };

  this.setupLink = function(key, jsonData){
  	this.linkInfo[key] = jsonData;
  };

  this.doActionLinkRefresh = function(cmd, actionSpan, waitingLabel, key, container) {
	$(actionSpan).getElementsBySelector('.actionLinkAnchor').invoke('hide');
    $(actionSpan).getElementsBySelector('.widgetAction_waiting').invoke('show');
    $(actionSpan).getElementsBySelector('span').innerHTML = waitingLabel;
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[key]);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    
    var useContainer = container;
    if (container == undefined || container == '') {
        useContainer = this.container;
    }

    var _self = this;
    jQuery.ajax({
    	url: url,
    	data: oldParams,
    	success: function(data, status, xhr) {
    		_self.onSuccess(xhr);
    		jQuery("#" + useContainer).html(data);
    	},
    	complete: function() {
    		jQuery("#" + actionSpan + " .actionLinkAnchor").show();
    	}
    });

    $(this.container).getElementsBySelector('input').invoke('enable');
  };
  
  this.doPeriodicRefresh = function(cmd, newParams, period, container) {
      var containerToUse = container;
      if (container == undefined || container == '') {
          containerToUse = this.container;
      }
      oldParams = jQuery.extend(true, this.getWidgetParameters(), newParams);
      
      var url = "/spring/widget/" + this.shortName + "/" + cmd;
      return new Ajax.PeriodicalUpdater(containerToUse, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccessPeriodic.bind(this), 'frequency': period});
  };
  
  /**
   * @returns   {Hash}
   */
  this.getWidgetParameters = function() {
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
    
    var mergedParameters = jQuery.extend(true, theseParameters, this.parameters);
    return mergedParameters;
  };
  
  
  this.doActionPopup = function(cmd, actionSpan, key, dialogId, width, height) {
	    oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[key]);

	    var url = "/spring/widget/" + this.shortName + "/" + cmd;

	    openSimpleDialog(dialogId, url, key, oldParams, width, height);
  };

}

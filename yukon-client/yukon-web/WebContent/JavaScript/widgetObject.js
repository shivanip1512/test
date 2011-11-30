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
  
  /**
   * args = {
   *    command = string final path for the url
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
  doActionRefresh: function(args) {
      var defaultButtonText = $(args.buttonID).down('span').innerHTML;
      $(args.buttonID).down('span').innerHTML = args.waitingText;
    $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('show');
    var container = this.container;
    $(container).getElementsBySelector('input').invoke('disable');
    $(container).getElementsBySelector('button').invoke('disable');
    
    var that = this;
    var localSuccess = function(transport, json) {
        $(args.buttonID).down('span').innerHTML = defaultButtonText;
        $(args.buttonID).getElementsBySelector('.widgetAction_waiting').invoke('hide');
        $(container).getElementsBySelector('input').invoke('enable');
        $(container).getElementsBySelector('button').invoke('enable');
        that.onSuccess(transport, json);
    }
    
    newParams = $H(this.linkInfo.get(args.key));
    oldParams = this.getWidgetParameters();
    oldParams.update(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + args.command;
    new Ajax.Updater(this.container, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': localSuccess});
  },
  
  /**
   * args = {
   *    command = string final path for the url
   *    containerID = string containing the ID of the container
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
  doActionUpdate: function(args) {
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
    
    newParams = $H(this.linkInfo.get(args.key));
    oldParams = this.getWidgetParameters();
    oldParams.update(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + args.command;
    new Ajax.Updater(args.containerID, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': localSuccess});
  },
  
  setParameter: function(name, value){
  	this.parameters[name] = value;
  },

  setupLink: function(key, jsonData){
  	this.linkInfo.set(key, jsonData);
  },

  doActionLinkRefresh: function(cmd, actionSpan, waitingLabel, key, container) {
    $(actionSpan).getElementsBySelector('.widgetAction_waiting').invoke('show');
    $(actionSpan).getElementsBySelector('span').innerHTML = waitingLabel;
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    newParams = $H(this.linkInfo.get(key));
    oldParams = this.getWidgetParameters();
    oldParams.update(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    
    var useContainer = container;
    if (container == undefined || container == '') {
        useContainer = this.container;
    }
    new Ajax.Updater(useContainer, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
    $(this.container).getElementsBySelector('input').invoke('enable');
  },
  
  doPeriodicRefresh: function(cmd, newParams, period, container) {
      var containerToUse = container;
      if (container == undefined || container == '') {
          containerToUse = this.container;
      }
      oldParams = this.getWidgetParameters();
      oldParams.update(newParams);
      
      var url = "/spring/widget/" + this.shortName + "/" + cmd;
      return new Ajax.PeriodicalUpdater(containerToUse, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this), 'frequency': period});
  },
  
  /**
   * @returns   {Hash}
   */
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
    mergedParameters.update(theseParameters);
    return mergedParameters;
  },
  
  
  doActionPopup: function(cmd, actionSpan, key, dialogId, width, height) {
	    newParams = $H(this.linkInfo.get(key));
	    oldParams = this.getWidgetParameters();
	    oldParams.update(newParams);
	    
	    var url = "/spring/widget/" + this.shortName + "/" + cmd;

	    openSimpleDialog(dialogId, url, key, oldParams, width, height);
  }

};

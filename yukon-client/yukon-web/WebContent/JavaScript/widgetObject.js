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
    
    newParams = $H(this.linkInfo.get(key));
    oldParams = this.getWidgetParameters();
    oldParams.update(newParams);
    
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
    
    newParams = $H(this.linkInfo.get(key));
    oldParams = this.getWidgetParameters();
    oldParams.update(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(theContainer, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': localSuccess});
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

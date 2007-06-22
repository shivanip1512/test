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
  
  doActionRefresh: function(cmd, actionButton, waitingLabel) {
    $(actionButton).getElementsByClassName('widgetAction_waiting').invoke('show');
    $(actionButton).getElementsBySelector('input').each(function(it) {it.value = waitingLabel});
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(this.container, url, {'parameters': this.getWidgetParameters(), 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  doActionUpdate: function(cmd, theContainer, actionButton, waitingLabel) {
    $(actionButton).getElementsByClassName('widgetAction_waiting').invoke('show');
    var input = $(actionButton).getElementsBySelector('input').first();
    var initialLabel = input.value;
    input.value = waitingLabel;
    input.disable();
    
    var localSuccess = function() {
      // the following is only useful for the actionUpdate case
      $(actionButton).getElementsByClassName('widgetAction_waiting').invoke('hide'); 
      input.value = initialLabel;
      input.enable();
    
      this.onSuccess();
    }
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(theContainer, url, {'parameters': this.getWidgetParameters(), 'evalScripts': true, 'onSuccess': localSuccess});
  },
  
  setupLink: function(key, jsonData){
  	this.linkInfo[key] = jsonData;
  },

  doActionLinkRefresh: function(cmd, actionSpan, waitingLabel, key) {
    $(actionSpan).getElementsByClassName('widgetAction_waiting').invoke('show');
    $(actionSpan).getElementsBySelector('span').innerHTML = waitingLabel;
    $(this.container).getElementsBySelector('input').invoke('disable');
    
    newParams = $H(this.linkInfo[key]);
    oldParams = this.getWidgetParameters();
    oldParams.merge(newParams);
    
    var url = "/spring/widget/" + this.shortName + "/" + cmd;
    new Ajax.Updater(this.container, url, {'parameters': oldParams, 'evalScripts': true, 'onSuccess': this.onSuccess.bind(this)});
  },
  
  getWidgetParameters: function() {
    var container = $(this.container);
    var theseParameters = {};
    
    // now find all inputs under the container
    var widgetInputs = container.getElementsByTagName('input');
    for (var i = 0; i < widgetInputs.length; i += 1) {
      var el = $(widgetInputs[i]);
      if (el.name) {
        theseParameters[el.name] = $F(el);
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
  }

};

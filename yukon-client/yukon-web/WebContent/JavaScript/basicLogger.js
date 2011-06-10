/**
 *  A cross-browser interface for viewing simple JS logging information in Firefox, Webkit based
 *  browsers as well as a basic JS console in IE.
 *  
 *  Requirements:
 *  Include this file in your document, no external libs necessary.
 *  
 *  Usage:
 *  ...
 *  var foo = 'bar';
 *  ...
 *  debug(foo);
 *  ...
 *  
 *  If your browser is Firefox, Safari or Opera 'foo' will be evaled and outputted directly to the 
 *  built-in JS consoles (firebug included).
 *  
 *  If your browser is IE (currently 9.x and below) the first call to debug() will overlay a div in 
 *  the upper left corner of the screen with the evaluation of foo.
 *  
 *  On-screen Console Usage (IE <= 9.x):
 *  * UP/DOWN arrows: cycle through your previous commands
 *  * '!close': close the on-screen console
 *  * You may enter arbitrary javascript commands into the input field provided.  Variables are
 *    pretty-printed to the screen and functions are evaled
 *  * The only way to re-show a closed on-screen console is through a programmatic call to debug()
 *    or other DOM manipulation of the console div.
 *  
 */

if (typeof(_logger) == 'undefined') {
    var _logger = {
        _debugRowCount : 0,
        _debugStack : [],
        _debugStackIter : 0,

        _displayDebugObject : function(obj, options) {
            this._setupDebugConsole();
            var stack = document.getElementById('debug_stack');
            var li = document.createElement('li');
            li.style.padding = '2px 5px';
                li.title = "["+typeof(obj)+"]";
            if (options) {
                if (options.error) {
                    li.style.backgroundColor = "#FFBBBB";
                    li.style.color = "#FF0000";
                }
            }
            this._debugRowCount++ % 2 ? li.style.background = '#DFDFDF' : true;
            switch (typeof(obj)) {
            case 'object':
                if (typeof(obj.length) == 'number') {
                    li.innerHTML += this._arrayToString(obj);
                } else {
                    li.innerHTML += this._objectToString(obj);
                }
                break;
            case 'function':
                li.innerHTML += "function()";
                break;
            case 'string':
                if (options) {
                    if (options.hideQuotes) {
                        li.innerHTML += this._escapeHTMLEntities(obj);
                    } else {
                        li.innerHTML += "&#34;" + this._escapeHTMLEntities(obj) +"&#34;";
                    }
                    break;
                }
                li.innerHTML += "&#34;" + this._escapeHTMLEntities(obj) +"&#34;";
                break;
            case 'number':
            default:
                li.innerHTML += this._escapeHTMLEntities(obj);
                break;
            }
            stack.appendChild(li);
            stack.scrollTop = li.offsetTop;
        },
        
        _arrayToString : function(obj) {
            var retString = [];
            for (var i = 0; i < obj.length; i++) {
                switch (typeof(obj[i])) {
                case 'object':
                    if (typeof(obj[i].length) == 'number') {
                        retString.push(this._arrayToString(obj[i]));
                    } else {
                        retString.push(this._objectToString(obj[i]));
                    }
                    break;
                case 'string':
                    retString.push("&#34;" + obj[i] + "&#34;");
                    break;
                case 'function':
                    retString.push("function()");
                    break;
                default:
                    retString.push(obj[i]);
                    break;
                }
            }
            return "[" + retString.join(", ") + "]";
        },
        
        _objectToString : function(obj) {
            var keys = Object.keys(obj);
            var retString = [];
            for (var i = 0; i < keys.length; i++) {
                switch (typeof(obj[keys[i]])) {
                case 'object':
                    if (typeof(obj[keys[i]].length) == 'number') {
                        retString.push(keys[i] + ":" + this._arrayToString(obj[keys[i]]));
                    } else {
                        retString.push(keys[i] + ":" + this._objectToString(obj[keys[i]]));
                    }
                    break;
                case 'string':
                    retString.push(keys[i] + ":" + "&#34;" + this._escapeHTMLEntities(obj[keys[i]]) + "&#34;");
                    break;
                case 'function':
                    retString.push(keys[i] + ":" + "function()");
                    break;
                default:
                    retString.push(keys[i] + ": " + obj[keys[i]]);
                    break;
                }
            }
            return "{" + retString.join(", ") + "}";
        },
        
        _escapeHTMLEntities : function(str) {
            //escape the HTML entities with # codes
            return String(str).replace("<", "&lt;");
        },
        
        
        _setupDebugConsole : function() {
            var debug_console = document.getElementById('debug_console');
            if (debug_console) {
                debug_console.style.display = '';
                return true;
            } else {
                var container = document.createElement('div');
                container.id = 'debug_console';
                container.style.position = 'absolute';
                container.style.right = 0;
                container.style.top = 0;
                container.style.width = '250px';
                container.style.height = '150px';
                container.style.border = 'solid 1px #CFCFCF';
                container.style.background = '#FFFFFF';
                container.style.fontSize = '10px';
                
                var stack = document.createElement('div');
                stack.style.overflow = 'auto';
                stack.style.height = '130px';
                stack.style.width = '100%';
                stack.id = 'debug_stack';
                container.appendChild(stack);
                
                var input = document.createElement('input');
                input.type = 'text';
                input.id = 'debug_input';
                input.style.width = '100%';
                input.style.height = '20px';
                input.onkeyup = _logger._evaluateDebugInput;
                container.appendChild(input);
                
                document.body.appendChild(container);
            }
        },
        
        _evaluateDebugInput : function(e) {
            var input = document.getElementById('debug_input');
            if (!e) {
                if (window.event) {
                    //IEism
                    e = window.event;
                } else {
                    //FAIL.
                    return;
                }
            }
            switch (e.keyCode) {
            case 13:
                if (input.value == "!close") {
                    _logger._hideDebugWindow();
                    input.value = "";
                    return false;
                }
                try {
                    var result = eval(input.value.replace(/^var /, ""));
                    _logger._displayDebugObject(">>> " + input.value, {hideQuotes: true});
                    _logger._displayDebugObject(result);
                } catch (err) {
                    _logger._displayDebugObject(">>> " + input.value, {hideQuotes: true});
                    var errorMessage = [];
                    errorMessage.push(err.name + ": '" + err.message + "'");
                    if (err.fileName) {
                        errorMessage.push(" (" + err.fileName);
                        if (err.lineNumber) {
                            errorMessage.push(" line:" + err.lineNumber);
                        }
                        errorMessage.push(")");
                    }
                    _logger._displayDebugObject(errorMessage.join(""), {hideQuotes: true, error: true});
                }
                _logger._debugStack.push(input.value);
                _logger._debugStackIter = _logger._debugStack.length - 1;
                input.value = "";
                break;
                return false;
            case 38:
                if (_logger._debugStackIter >= 0) {
                    input.value = _logger._debugStack[_logger._debugStackIter--];
                }
                return false;
            case 40:
                if (_logger._debugStackIter < _logger._debugStack.length - 1) {
                    input.value = _logger._debugStack[_logger._debugStackIter++];
                }
                return false;
            default:
                return true;
            }
        },
        
        _hideDebugWindow : function() {
            document.getElementById('debug_console').style.display = 'none';
        }
    };
}

var debug = function(obj) {
    if (typeof(console) == 'object') {
        if (typeof(console.log) == 'function') {
            console.log(obj);
            return;
        }
    }
    // no support for console.log, so we will create an overlay and output debug information there
    _logger._displayDebugObject(obj);
};
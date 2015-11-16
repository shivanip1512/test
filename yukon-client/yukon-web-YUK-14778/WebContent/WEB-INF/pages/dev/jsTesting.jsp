<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="jsTesting">

<h1>iframe clickjacking test</h1>
    <p>
        May or may not display Yukon website, depending on whether or not domain is "localhost" or not. If the domain is "localhost", the
        website should appear. If not, a blank iframe should appear in Firefox or Chrome, and a warning should appear in IE. Testing this
        assumes you can run your server on your local machine.
    </p>
    <script>
        $(function () {
            // YUK-12513 Click Jacking Vulnerability - Frame Breaking
            // if logged in to http://pspl-sw-night.eatoneaseng.net:8080/, you should see the
            // home page in the iframe.
            // if logged in to another Yukon host, the ifram should be blank, and console messages
            // such as the following should appear in the debugger console:
            // Refused to display 'http://10.106.38.173:8080/login.jsp?REDIRECTED_FROM=%2F' in a frame because it set 'X-Frame-Options' to 'SAMEORIGIN'.
            // 14:56:44.474] Load denied by X-Frame-Options: http://10.106.38.173:8080/login.jsp?REDIRECTED_FROM=%2F does not permit cross-origin framing.
            var iframeUrl = 'http://localhost:8080/yukon/';
            $('#clickjack').attr('src', iframeUrl);
        });
    </script> 
    <div>
        <iframe id="clickjack" width="800" height="400"></iframe>
    </div>
<br>

<div style='clear left; clear right;'>
    <div style='height: 5px; width: 10px; border: 5px dashed #000; border-bottom-color: #000; border-bottom-style: solid; border-top: none; border-left-color: transparent; border-right-color: transparent;'>
    </div>
</div>

<script type="text/javascript">
// this week's hotness: the "Sandbox" JavaScript module pattern
//
/*
var fakeYukon = fakeYukon || {};
$( function () {
        yukon.modules = {};
        yukon.modules.dom = function (box) {
            box.getElement = function () { console.log('getElement called'); };
            box.getStyle = function () { console.log('getStyle called'); };
            box.foo = "bar";
        };
        yukon.modules.event = function (box) {
        // access to the Sandbox prototype if needed:
        // box.constructor.prototype.m = "mmm";
            box.attachEvent = function () { console.log('attachEvent called'); };
            box.detachEvent = function () { console.log('detachEvent called'); };
        };
        yukon.modules.ajax = function (box) {
            box.makeRequest = function () { console.log('makeRequest called'); };
            box.getResponse = function () { console.log('getResponse called'); };
        };
        function Sandbox() {
            // turning arguments into an array
            var args = Array.prototype.slice.call(arguments),
                // the last argument is the callback
                callback = args.pop(),
                // modules can be passed as an array or as individual parameters
                modules = (args[0] && typeof args[0] === "string") ? args : args[0],
                i;
            // make sure the function is called
            // as a constructor
            if (!(this instanceof Sandbox)) {
                console.log('this is not an instance of Sandbox. What\'s going on? Really?');
                return new Sandbox(modules, callback);
            }
            // add properties to 'this' as needed:
            this.a = 1;
            this.b = 2;
            // now add modules to the core 'this' object
            // no modules or "*" both mean "use all modules"
            if (!modules || modules === '*') {
                modules = [];
                for (i in yukon.modules) {
                        if (yukon.modules.hasOwnProperty(i)) {
                            modules.push(i);
                        }
                }
            }
            // initialize the required modules
            for (i = 0; i < modules.length; i += 1) {
                yukon.modules[modules[i]](this);
            }
            // call the callback
            callback(this);
        }
        // any prototype properties as needed
        Sandbox.prototype = {
            name: "My Application",
            version: "1.0",
            getName: function () {
                return this.name;
            }
        };
        var mybox = Sandbox('ajax','event','dom', function(myappbox) {
            console.log('i should have some box:');
            console.dir(myappbox);
            myappbox.getElement();
            myappbox.getResponse();
            myappbox.detachEvent();
            myappbox.attachEvent();
            myappbox.makeRequest();
            myappbox.getResponse();
        });
        console.log("mybox dump:");
        console.dir(mybox);
        // add some new modules
        yukon.modules.ui = function (box) {
            var uiState = false;
            box.getUiState = function () {
                return uiState;
            };
            box.setUiState = function (newUiState) {
                uiState = newUiState;
                return uiState;
            };
        };
        var anotherSandbox = Sandbox('ui', function (appbox) {
            var uiState;
            console.log('added to sandbox');
            console.dir(appbox);
            uiState = appbox.getUiState();
            console.log('got uiState=' + uiState);
            uiState = appbox.setUiState(true);
            console.log('set uiState=' + uiState);
        });
        console.log('anotherSandbox dump:');
        console.dir(anotherSandbox);
        console.log('merging mybox and anotherSandbox into a separate object...');
        var superObject = $.extend({}, mybox, anotherSandbox);
        console.log('done! dumping');
        console.dir(superObject);
        superObject.attachEvent();
        console.log('superObject.getUiState=' + superObject.getUiState());
        fakeYukon = superObject;
});
*/
</script>
<script type="text/javascript">
/* serious, and unsuccessful, hacking
$(document).tooltip({
  items: '*',
  content: function() {
    var element = $(this), tip, toolTipped;
    toolTipped = element.closest('.js-has-tooltip');
    if ( toolTipped.length ) {
      tip = toolTipped.nextAll('.js-tooltip').first();
      console.log("returning tip.html()");
      return tip.html();
    } else {
      var title = element.attr('title') || '',
          closest = $(element.closest('[title]')[0]);
      if (title === '' && closest.length > 0) {
        console.log('doing closest: length=' + closest.length + ' title="' + closest.attr('title') + '"');
        console.dir(closest);
        title = $(element.closest('[title]')[0]).attr('title');
      } else {
        console.log('title="' + title + '" did not do closest');
        console.dir(element.closest('[title]'));
      }
      console.log("returning something else, title: " + title);
      console.log('"' + $('<a>').text(title).html() + '"');
        return $('<a>').text(title).html();
    }
  }
});
*/
</script>

<script type="text/javascript">
/* the following can't work, since there is no server-side picker code set up for it
$(function() {
    try {
        console.log("javascript object creation test begin");
        // globally-accessible functions
        yukon.protoPicker.prototype.dumpVars = function () {
            console.log("pickerType=" + this.pickerType + " pickerId=" + this.pickerId);
        };

        // some uses of the above mechanisms
        var stock = new Picker('Okey-Doke', 'Forget it', '(none selected)', 'userStockPicker', 'yomama', 'stock', '', null);
        var horse = new Picker('Alright', 'No way', '(none selected)', 'userHorsePicker', 'bite me', 'horse', '', null);
        console.log("stock picker: stock instanceof Picker: " + (stock instanceof Picker));
        console.dir(stock);
        console.log("horse picker: horse instanceof Picker: " + (horse instanceof Picker));
        console.dir(horse);
        stock.show(true);
        stock.dumpVars();
        horse.dumpVars();
        stock.multiSelectMode = false;
        console.log("stock.multiSelectMode=" + stock.multiSelectMode
            + " horse.multiSelectMode=" + horse.multiSelectMode);
        // This method, init, does secondary initialization which needs to happen after
        // the HTML elements in the tag file have been fully created.
        $(function () {
            stock.init.bind(stock, true)();
            horse.init.bind(horse, true)();
        });
        //console.log("calling stock.resetSearchFields...");
        // should, and does, cause an exception, because the method
        // is not available to Picker, but private to protoPicker
        // stock.resetSearchFields();
        console.log("stock.getInSearch()=" + stock.getInSearch());
        console.log("horse.getInSearch()=" + horse.getInSearch());
        stock.setInSearch(true);
        console.log("stock.getInSearch()=" + stock.getInSearch());
        console.log("horse.getInSearch()=" + horse.getInSearch());
        horse.setInSearch(true);
        stock.setInSearch(false);
        console.log("stock.getInSearch()=" + stock.getInSearch());
        console.log("horse.getInSearch()=" + horse.getInSearch());
    } catch(testingex) {
        console.log("testing exception: '" + testingex + "'");
    }
});
*/
</script>
</cti:standardPage>
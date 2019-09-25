<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="yukon-js-lib">
<cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
<cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>
<cti:includeCss link="/resources/css/lib/keys.css"/>

<style>
.description { line-height: 22px; font-size: 16px; }
h2 { margin-top: 40px; }
</style>

<p class="description">
    This section describes Yukon's JavaScript library; it's available api and it's built-in event handling and 
    behavior throught the application.
</p>

<h2>EcmaScript</h2>
<p class="description">
    Yukon uses <strong>ES5</strong> support of IE10+, Chrome and Firefox. The following are functions are polyfilled if not supported
    natively:
    <ul>
        <li>Array.prototype.forEach</li>
        <li>Object.create</li>
        <li>Function.prototype.bind</li>
        <li>String.prototype.startsWith</li>
        <li>String.prototype.endsWith</li>
        <li>String.prototype.contains</li>
        <li>String.prototype.trim</li>
    </ul>
</p>

<h2>Targeting Elements from JavaScript</h2>
<p class="description">
    When targetting an html element from JavaScript, use a class name with a <span class="label label-attr">js-</span> 
    prefix.<br>
    <span class="label label-info">Note:</span> these targetting class names should <strong>NEVER</strong> be 
    referenced in a CSS file.
</p>
<pre class="code prettyprint">
&lt;span class=&quot;js-targetting-example&quot;&gt;Find me from JavaScript.&lt;/span&gt;
&lt;script&gt;
$(&#39;.js-targetting-example&#39;).data(&#39;found&#39;, true);
&lt;/script&gt;
</pre>

<h2>Storing and Retrieving Data</h2>
<p class="description">
    Data needed in JavaScript should be stored in the DOM with html5 data attributes or using 
    <span class="label label-attr">&lt;cti:toJson/&gt;</span> (see <a href="#json-example">example</a> below).<br>
    Jquery also has a handy <a href="http://api.jquery.com/data/">api</a> that extends data attributes, allowing you 
    to store complex JavaScript objects as data.<br>
    <span class="label label-info">Note:</span> Most of our tags support dynamic attributes.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="js-fav-num-box" data-my-favorite-number="${favoriteNumber}">
        My favorite number is <span class="js-fav-num"></span>.
    </div>
    <script>
    $('.js-fav-num').text($('.js-fav-num-box').data('myFavoriteNumber'));
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-fav-num-box&quot; data-my-favorite-number=&quot;&#36;{favoriteNumber}&quot;&gt;
    My favorite number is &lt;span class=&quot;js-fav-num&quot;&gt;&lt;/span&gt;.
&lt;/div&gt;
&lt;script&gt;
$(&#39;.js-fav-num&#39;).text($(&#39;.js-fav-num-box&#39;).data(&#39;myFavoriteNumber&#39;));
&lt;/script&gt;
</pre>

<h2>Custom Events</h2>
<p class="description">
    Custom Yukon events should use colons and be of the pattern: 
    <span class="label label-attr">yukon:module/section:page:what-happened</span>.<br>
    <span class="label label-info">Note:</span> A <span class="label label-attr">.</span> in JavaScript 
    event names has meaning, which is why we use colons.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <cti:button classes="js-event-btn" label="Do A Thing"/>
    <script>
    (function () {
        $('.js-event-btn').click(function () {
            $(this).trigger('yukon:dev:js-api:they-did-a-thing');
        });
        
        $(document).on('yukon:dev:js-api:they-did-a-thing', function () {
            alert('Congradulations! You did a thing!');
        });
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button classes=&quot;js-event-btn&quot; label=&quot;Do A Thing&quot;/&gt;
&lt;script&gt;
(function () {
    $(&#39;.js-event-btn&#39;).click(function () {
        $(this).trigger(&#39;yukon:dev:js-api:they-did-a-thing&#39;);
    });
    
    $(document).on(&#39;yukon:dev:js-api:they-did-a-thing&#39;, function () {
        alert(&#39;Congradulations! You did a thing!&#39;);
    });
})();
&lt;/script&gt;
</pre>

<h2>The 'yukon' Namespace</h2>
<p class="description">
    The Yukon js library is all housed under a single global variable <span class="label label-attr">yukon</span>.
    Yukon has a <span class="label label-attr">namespace</span> function that will instantiate the object chains.
</p>
<pre class="code prettyprint">
yukon.namespace('yukon.my.nested.object');

yukon.my.nested.object = { foo: 'bar' };
</pre>

<h2>Modules</h2>
<p class="description">
    Yukon uses the simplest module pattern described 
    <a target="_blank" href="http://www.adequatelygood.com/JavaScript-Module-Pattern-In-Depth.html">here</a> and does 
    not handle dependency injection.  Modules that depend on other modules must be executed in the right order.
</p>
<p class="description">
    Yukon uses the <span class="label label-attr">namespace</span> to instantiate the object chain needed for a 
    module so that all modules are nested under the <span class="label label-attr">yukon</span> module.
</p>
<p class="description">
    Modules are always created with an 
    <a target="_blank" href="https://en.wikipedia.org/wiki/Immediately-invoked_function_expression">IIFE</a> which provides
     a closure to house module data.
    Modules return an object usually named <span class="label label-attr">mod</span> which is the public api of that module. 
    Yukon modules usually have a public <span class="label label-attr">init</span> function to do any intitializing work 
    which is called after the function and wrapped in a jquery 'document ready' event handler.<br>
    <span class="label label-info">Note:</span>  All properties of the <span class="label label-attr">mod</span> object 
    are public.  All variables and functions declared outside of <span class="label label-attr">mod</span> are private 
    and are prefixed with an '_' underscore. 
</p>
<%-- BLANK MODULE EXAMPLE --%>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <p>
            A blank module named <span class="label label-attr">yukon.thing</span>, you can copy this if you need to start a 
            new module.
        </p>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
yukon.namespace('yukon.thing');
 
/**
 * TODO
 * @module yukon.thing
 * @requires JQUERY
 * @requires yukon
 */
yukon.thing = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
 
            // TODO
 
            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.thing.init(); });
</pre>
<%-- CIRCLE MODULE EXAMPLE --%>
<div class="column-4-20 clearfix">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <p>
            A module for cirles named <span class="label label-attr">yukon.circle</span> with 
            <a target="_blank" href="http://usejsdoc.org/">JSDoc</a> documentation and
            comments explaining what's going on.
        </p>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
yukon.namespace('yukon.circle');
 
/**
 * Module handling circle functionality in Yukon
 * @module yukon.circle
 * @requires JQUERY
 * @requires yukon
 */
yukon.circle = (function () {
 
    'use strict';
 
    // PRIVATE
 
    var
    _initialized = false,
    _pi = Math.PI, /** @constant {number} The value of pi. */
 
    /** 
     * Calculates the circumference of a circle for a given radius.
     * @private
     * @param {number} radius - The the radius of the circle
     */
    _circumference = function (radius) {
        return 2 * _pi * radius;
    },
 
    /** 
     * Calculates the radius of a circle for a given circumference.
     * @private
     * @param {number} circumference - The the circumference of the circle
     */
    _radius = function (circumference) {
        return circumference / (2 * _pi);
    },
 
    /** 
     * Calculates the area of a circle for a given radius.
     * @private
     * @param {number} radius - The the radius of the circle
     */
    _area = function (radius) {
        return Math.pow(radius, 2) * _pi;
    },
 
    mod = { // this is the module object that will be exported
 
        // PUBLIC 
        
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            // nothing to initialize for this module
            _initialized = true;
        },
 
        /** Returns the value of pi */
        getPi: function () {
            return _pi;
        },
 
        /** 
        * Calculates the circumference of a circle for a given radius.
        * @private
        * @param {number} radius - The the radius of the circle
        */
        getCircumference: function (radius) {
            return _circumference(radius); 
        },
 
        /** 
         * Calculates the area of a circle for a given radius.
         * @private
         * @param {number} radius - The the radius of the circle
         */
        getArea: function (radius) {
            return _area(radius); 
        },
 
       /** 
        * Calculates the radius of a circle for a given circumference.
        * @private
        * @param {number} circumference - The the circumference of the circle
        */
        getRadius: function (circumference) {
            return _radius(circumference); 
        }
    };
 
    // export the module
    return mod;
})();
 
// initialize the module after page loaded
$(function () { yukon.circle.init(); });
</pre>

<h2>Module Usage</h2>
<p class="description">
    These modules should be used to house <strong>ALL</strong> JavaScript in Yukon.  They ensure that the global namespace
    is not polluted and that the code itself is modular and portable.  They should usually be in thier own js file.  Never
    write JavaScript directly in a jsp or tag file. If you need data from the jsp or tag layer, use 
    <a href="#json-example">Yukon's JSON</a> utils to
    store it and load it from module's init function.
</p>
<p class="description">
    Module js files should live in the appropriate sub folder under <span class="label label-attr">/resources/js/</span>.
</p>
<dl>
    <dt><span class="label label-attr">/resources/js/common</span></dt>
    <dd class="buffered">Modules for common code.</dd>
    <dt><span class="label label-attr">/resources/js/pages</span></dt>
    <dd class="buffered">Modules for specific pages.</dd>
    <dt><span class="label label-attr">/resources/js/tags</span></dt>
    <dd class="buffered">Modules for tag files.</dd>
</dl>

<h2>Yukon Global Data</h2>
<p class="description">
    Yukon has one other global object <span class="label label-attr">yg</span> which stores global data for the application.
    It is built by the <span class="label label-attr">&lt;tags:jsGlobals/&gt;</span> tag.
    Below is the <span class="label label-attr">yg</span> object printed out in tree form.
</p>
<div class="yukon-props scroll-lg code js-yg-data ">
</div>
<style>
.yukon-props dt {
    display: inline-block;
    margin-bottom: 5px;
}
</style>
<script>
(function () {
    
    var ygData = $('.js-yg-data');
    
    var addProps = function (elem, t, d) {
        
        var propList = $('<dl>');
        var propTerm = $('<dt class="label label-attr">' + t + '</dt>');
        var propDef = $('<dd>');
        
        propList.append(propTerm).append('&nbsp;{').append(propDef);
        
        if (d instanceof RegExp) {
            propDef.append('<span style="color:#06C;">' + d + '</span>');
        } else if (typeof(d) === 'object') {
            Object.keys(d).forEach(function (key) {
                var label = Array.isArray(d) ? '[' + key + ']' : key;
                addProps(propDef, label, d[key]);
            });
        } else if (typeof(d) === 'boolean') {
            propDef.append('<span style="color:#7F007F;">' + d + '</span>');
        } else {
            propDef.append('<span style="color:#06C;">\'' + d + '\'</span>');
        }
        
        propList.append('}');
        $(elem).append(propList);
    };
    
    addProps(ygData, 'yg', yg);
})();
</script>

<h2>Module: Yukon</h2>
<p class="description">
    The <span class="label label-attr">yukon</span> module contains some basic functionality as well as houses all other 
    modules in Yukon.
</p>

<h3>Function: yukon.url</h3>
<p class="description">
    Similar to the <span class="label label-attr">&lt;cti:url/&gt;</span> tag, it will convert any add the 
    application context path if necessary.<br>
    <span class="label label-info">Note:</span> It is safe to use on relative paths, which don't need the app context.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well js-url"></div>
    <script>
    $('.js-url').text(yukon.url('/this-is/my/cool/url'));
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well js-url&quot;&gt;&lt;/div&gt;
&lt;script&gt;
$(&#39;.js-url&#39;).text(yukon.url(&#39;/this-is/my/cool/url&#39;));
&lt;/script&gt;
</pre>

<h3 id="json-example">Function: yukon.fromJson</h3>
<p class="description">
    Usually used with the <span class="label label-attr">&lt;cti:toJson/&gt;</span> tag, it will grab the inner text 
    of the element specified, convert it to JSON and return it.<br>
    <span class="label label-info">Note:</span> This is the the best way to get data into your JavaScript modules.
    Intsead of mixing JavaScript and jsp code in jsp's or tag files, Drop it in the DOM with 
    <span class="label label-attr">&lt;cti:toJson/&gt;</span> and pull it out with 
    <span class="label label-attr">yukon.fromJson</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <cti:toJson object="${company}" id="my-company"/>
    <div class="well js-from-json-ex"></div>
    <script>
    $('.js-from-json-ex').text(JSON.stringify(yukon.fromJson('#my-company')));
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:toJson object=&quot;&#36;{company}&quot; id=&quot;my-company&quot;/&gt;
&lt;div class=&quot;well js-from-json-ex&quot;&gt;&lt;/div&gt;
&lt;script&gt;
&#36;(&#39;.js-from-json-ex&#39;).text(JSON.stringify(yukon.fromJson(&#39;#my-company&#39;)));
&lt;/script&gt;
</pre>

<h3>Function: yukon.random</h3>
<p class="description">
    Returns a random number between min (inclusive) and max (exclusive).
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well js-rand-ex"></div>
    <script>
    (function () {
        var rando = function () {
            var rand = yukon.random(0, 9000);
            $('.js-rand-ex').text(rand);
            setTimeout(rando, 1000);
        };
        rando();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well js-rand-ex&quot;&gt;&lt;/div&gt;
&lt;script&gt;
(function () {
    var rando = function () {
        var rand = yukon.random(0, 9000);
        $(&#39;.js-rand-ex&#39;).text(rand);
        setTimeout(rando, 1000);
    };
    rando();
})();
&lt;/script&gt;
</pre>

<h3>Function: yukon.randomInt</h3>
<p class="description">
    Returns a random integer between min (inclusive) and max (inclusive).
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well js-rand-int-ex"></div>
    <script>
    (function () {
        var rando = function () {
            var rand = yukon.randomInt(0, 9000);
            $('.js-rand-int-ex').text(rand);
            setTimeout(rando, 1000);
        };
        rando();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well js-rand-int-ex&quot;&gt;&lt;/div&gt;
&lt;script&gt;
(function () {
    var rando = function () {
        var rand = yukon.randomInt(0, 9000);
        $(&#39;.js-rand-int-ex&#39;).text(rand);
        setTimeout(rando, 1000);
    };
    rando();
})();
&lt;/script&gt;
</pre>

<h3>Function: yukon.percent</h3>
<p class="description">
    Takes a count, a total, and an amount of decimal digits and returns a percent string.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="yukon.percent(13, 205, 3)" valueClass="js-percent-1"></tags:nameValue>
            <tags:nameValue name="yukon.percent(5, 10, 3)" valueClass="js-percent-2"></tags:nameValue>
            <tr>
                <td class="name">Progress Bar: <span class="js-percent-pb-text"></span></td>
                <td class="value js-percent-pb">
                    <div class="progress">
                        <div class="progress-bar progress-bar-info progress-bar-striped active"></div>
                    </div>
                </td>
            </tr>
        </tags:nameValueContainer>
    </div>
    <script>
    (function () {
        $('.js-percent-1').text(yukon.percent(13, 205, 3));
        $('.js-percent-2').text(yukon.percent(5, 10, 3));
        var pb = function () {
            var count = yukon.random(0, 9000);
            var percent = yukon.percent(count, 9000, 2);
            $('.js-percent-pb .progress-bar').css({ width: percent });
            $('.js-percent-pb-text').text(percent);
            setTimeout(pb, 1000);
        };
        pb();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well&quot;&gt;
    &lt;tags:nameValueContainer tableClass=&quot;natural-width&quot;&gt;
        &lt;tags:nameValue name=&quot;yukon.percent(13, 205, 3)&quot; valueClass=&quot;js-percent-1&quot;&gt;&lt;/tags:nameValue&gt;
        &lt;tags:nameValue name=&quot;yukon.percent(5, 10, 3)&quot; valueClass=&quot;js-percent-2&quot;&gt;&lt;/tags:nameValue&gt;
        &lt;tr&gt;
            &lt;td class=&quot;name&quot;&gt;Progress Bar: &lt;span class=&quot;js-percent-pb-text&quot;&gt;&lt;/span&gt;&lt;/td&gt;
            &lt;td class=&quot;value js-percent-pb&quot;&gt;
                &lt;div class=&quot;progress&quot;&gt;
                    &lt;div class=&quot;progress-bar progress-bar-info progress-bar-striped active&quot;&gt;&lt;/div&gt;
                &lt;/div&gt;
            &lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tags:nameValueContainer&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    $(&#39;.js-percent-1&#39;).text(yukon.percent(13, 205, 3));
    $(&#39;.js-percent-2&#39;).text(yukon.percent(5, 10, 3));
    var pb = function () {
        var count = yukon.random(0, 9000);
        var percent = yukon.percent(count, 9000, 2);
        $(&#39;.js-percent-pb .progress-bar&#39;).css({ width: percent });
        $(&#39;.js-percent-pb-text&#39;).text(percent);
        setTimeout(pb, 1000);
    };
    pb();
})();
&lt;/script&gt;
</pre>

<h3>Function: yukon.values</h3>
<p class="description">
    Returns the values of an 
    <a target="_blank" 
        href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys">Object.keys</a> 
    call.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well">
        <div class="stacked js-values-literal">
            <span>The values of <span class="label label-attr">{ foo: 'bar', fee: 'baz' }</span> are: </span>
            <span class="js-result"></span>
        </div>
        <div class="stacked js-values-class">
            <span>The values of a <span class="label label-attr">new Circle(42, 43, 44)</span> instance are: </span>
            <span class="js-result"></span>
        </div>
    </div>
    <script>
    (function () {
        // Object Literal
        $('.js-values-literal .js-result').text(yukon.values({ foo: 'bar', fee: 'baz' }));
        
        // Shape - superclass
        function Shape(x, y) {
            this.x = x;
            this.y = y;
        }
        // Circle - subclass
        function Circle(x, y, radius) {
            this.radius = radius;
            Shape.call(this, x, y); // Call super constructor.
        }
        // Subclass extends superclass.
        Circle.prototype = Object.create(Shape.prototype);
        Circle.prototype.constructor = Circle;
        
        var c = new Circle(42, 43, 44);
        $('.js-values-class .js-result').text(yukon.values(c));
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well&quot;&gt;
    &lt;div class=&quot;stacked js-values-literal&quot;&gt;
        &lt;span&gt;The values of &lt;span class=&quot;label label-attr&quot;&gt;{ foo: &#39;bar&#39;, fee: &#39;baz&#39; }&lt;/span&gt; are: &lt;/span&gt;
        &lt;span class=&quot;js-result&quot;&gt;&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class=&quot;stacked js-values-class&quot;&gt;
        &lt;span&gt;The values of a &lt;span class=&quot;label label-attr&quot;&gt;new Circle(42, 43, 44)&lt;/span&gt; instance are: &lt;/span&gt;
        &lt;span class=&quot;js-result&quot;&gt;&lt;/span&gt;
    &lt;/div&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    // Object Literal
    $(&#39;.js-values-literal .js-result&#39;).text(yukon.values({ foo: &#39;bar&#39;, fee: &#39;baz&#39; }));
    
    // Shape - superclass
    function Shape(x, y) {
        this.x = x;
        this.y = y;
    }
    // Circle - subclass
    function Circle(x, y, radius) {
        this.radius = radius;
        Shape.call(this, x, y); // Call super constructor.
    }
    // Subclass extends superclass.
    Circle.prototype = Object.create(Shape.prototype);
    Circle.prototype.constructor = Circle;
    
    var c = new Circle(42, 43, 44);
    $(&#39;.js-values-class .js-result&#39;).text(yukon.values(c));
})();
&lt;/script&gt;
</pre>

<h3>Function: yukon.validate</h3>
<p class="description">
    A set of general purpose validators: <span class="label label-attr">yukon.validate.latitude</span>,
    <span class="label label-attr">yukon.validate.longitude</span>, and
    <span class="label label-attr">yukon.validate.email</span>. 
</p>
<h4 class="subtle">Example:</h4>
<div class="well clearfix">
    <tags:nameValueContainer tableClass="natural-width">
        <tags:nameValue name="yukon.validate.latitude(100.1234)">
            <span>Is Valid? </span><span class="js-lat-1"></span>
        </tags:nameValue>
        <tags:nameValue name="yukon.validate.latitude(-100.1234)">
            <span>Is Valid? </span><span class="js-lat-2"></span>
        </tags:nameValue>
        <tags:nameValue name="yukon.validate.latitude(45.1234)">
            <span>Is Valid? </span><span class="js-lat-3"></span>
        </tags:nameValue>
        <tags:nameValueGap/>
        <tags:nameValue name="yukon.validate.longitude(190.1234)">
            <span>Is Valid? </span><span class="js-long-1"></span>
        </tags:nameValue>
        <tags:nameValue name="yukon.validate.longitude(-190.1234)">
            <span>Is Valid? </span><span class="js-long-2"></span>
        </tags:nameValue>
        <tags:nameValue name="yukon.validate.longitude(-120.1234)">
            <span>Is Valid? </span><span class="js-long-3"></span>
        </tags:nameValue>
        <tags:nameValueGap/>
        <tags:nameValue name="yukon.validate.email(bobvila.com)">
            <span>Is Valid? </span><span class="js-email-1"></span>
        </tags:nameValue>
        <tags:nameValue name="yukon.validate.email(bob@vila.com)">
            <span>Is Valid? </span><span class="js-email-2"></span>
        </tags:nameValue>
    </tags:nameValueContainer>
    <script>
    var lat1 = yukon.validate.latitude(100.1234);
    $('.js-lat-1').text(lat1).addClass(lat1 ? 'success' : 'error');
    var lat2 = yukon.validate.latitude(-100.1234);
    $('.js-lat-2').text(lat2).addClass(lat2 ? 'success' : 'error');
    var lat3 = yukon.validate.latitude(45.1234);
    $('.js-lat-3').text(lat3).addClass(lat3 ? 'success' : 'error');
    var long1 = yukon.validate.longitude(190.1234);
    $('.js-long-1').text(long1).addClass(long1 ? 'success' : 'error');
    var long2 = yukon.validate.longitude(-190.1234);
    $('.js-long-2').text(long2).addClass(long2 ? 'success' : 'error');
    var long3 = yukon.validate.longitude(-120.1234);
    $('.js-long-3').text(long3).addClass(long3 ? 'success' : 'error');
    var email1 = yukon.validate.email('bobvila.com');
    $('.js-email-1').text(email1).addClass(email1 ? 'success' : 'error');
    var email2 = yukon.validate.email('bob@vila.com');
    $('.js-email-2').text(email2).addClass(email2 ? 'success' : 'error');
    </script>
</div>


<h3 id="yukon-cookie-ex">Function: yukon.cookie</h3>
<p class="description">
    <span class="label label-attr">yukon.cookie</span> is a small module to get and set
    cookie values.  These values are actually stored in a single cookie named <em>yukon</em>.
    It has simple <em>get</em> and <em>set</em> methods. Values are a combination of a scope and an id.
    In the example below, 'dev', is the scope and 'foo' is the id.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well js-cookie-ex">
        The value of the 'dev.foo' in the yukon cookie is <span class="success js-cookie-ex-value"></span>.
    </div>
    <input type="text" class="js-cookie-ex-set"><cti:button label="Set 'dev.foo'" classes="js-set-cookie-btn"/>
    <script>
    yukon.cookie.set('dev', 'foo', 'bar');
    $('.js-cookie-ex-value').text(yukon.cookie.get('dev', 'foo'));
    $('.js-set-cookie-btn').click(function () {
        yukon.cookie.set('dev', 'foo', $('.js-cookie-ex-set').val());
        $('.js-cookie-ex-value').text(yukon.cookie.get('dev', 'foo'));
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well js-cookie-ex&quot;&gt;
    The value of the &#39;dev.foo&#39; in the yukon cookie is &lt;span class=&quot;success js-cookie-ex-value&quot;&gt;&lt;/span&gt;.
&lt;/div&gt;
&lt;input type=&quot;text&quot; class=&quot;js-cookie-ex-set&quot;&gt;&lt;cti:button label=&quot;Set &#39;dev.foo&#39;&quot; classes=&quot;js-set-cookie-btn&quot;/&gt;
&lt;script&gt;
yukon.cookie.set(&#39;dev&#39;, &#39;foo&#39;, &#39;bar&#39;);
$(&#39;.js-cookie-ex-value&#39;).text(yukon.cookie.get(&#39;dev&#39;, &#39;foo&#39;));
$(&#39;.js-set-cookie-btn&#39;).click(function () {
    yukon.cookie.set(&#39;dev&#39;, &#39;foo&#39;, $(&#39;.js-cookie-ex-set&#39;).val());
    $(&#39;.js-cookie-ex-value&#39;).text(yukon.cookie.get(&#39;dev&#39;, &#39;foo&#39;));
});
&lt;/script&gt;
</pre>

<h3 id="yukon-escape-xml-function">Function: yukon.escapeXml</h3>
<p class="description">
    <span class="label label-attr">yukon.escapeXml</span> returns XSS safe string. We often create UI controls dynamically
    in jQuery and append them to the page. If such controls renders a text which is a user input, it must be escaped before
    assigning to the dynamically created control and appending such control to the page.
</p>
<h4 class="subtle">Example:</h4>

<div class="stacked clearfix">
    <label>Enter some text:</label>
    <input type="text" class="js-user-input">
    <cti:button label="Append XSS safe span" classes="js-get-xss-safe-str-btn fn vat"/>
    <br><br>
    <div class="well js-escapeXml-example">
        XSS safe &lt;span&gt; will be appended here.
    </div>
    <script>
        $('.js-get-xss-safe-str-btn').click(function () {
            var userInput = $(".js-user-input").val(),
                span = $('<span />').addClass('label label-attr js-xss-safe-span').html(yukon.escapeXml(userInput));
            if ($('.js-xss-safe-span').exists()) {
                $('.js-xss-safe-span').remove();
            }
            $('.js-escapeXml-example').append(span);
        });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;stacked clearfix&quot;&gt;
    &lt;label&gt;Enter some text:&lt;/label&gt;
    &lt;input type=&quot;text&quot; class=&quot;js-user-input&quot;&gt;
    &lt;cti:button label=&quot;Get XSS safe string&quot; classes=&quot;js-get-xss-safe-str-btn fn vat&quot;/&gt;
    &lt;br&gt;&lt;br&gt;
    &lt;div class=&quot;well js-escapeXml-example&quot;&gt;
        XSS safe &lt;span&gt; will be appended here.
    &lt;/div&gt;
    &lt;script&gt;
        $(&#39;.js-get-xss-safe-str-btn&#39;).click(function () {
            var userInput = $(&quot;.js-user-input&quot;).val(),
                span = $(&#39;&lt;span /&gt;&#39;).addClass(&#39;label label-attr&#39;).html(yukon.escapeXml(userInput));
            if ($(&#39;.js-xss-safe-span&#39;).exists()) {
                $(&#39;.js-xss-safe-span&#39;).remove();
            }
            $(&#39;.js-escapeXml-example&#39;).append(span);
        });
    &lt;/script&gt;
&lt;/div&gt;
</pre>

<h2>Module: Yukon UI</h2>
<p class="description">
    The <span class="label label-attr">yukon.ui</span> module contains ui functionality for the whole application as well 
    as some event handling that is bound on every page through it's <span class="label label-attr">yukon.ui.init</span>
    function which is run on page ready. The ui functionality can be set up with <span class="label label-attr">yukon.ui.initContent</span>
</p>

<h3>Function: yukon.ui.busy and yukon.ui.unbusy</h3>
<p class="description">
    Make a button indicate a waiting state.  While 'busy' the button is disabled and has a spinner icon.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <cti:button classes="js-busy-me" label="My Button"/>
</div>
<script>
$(function() {
    (function () {
        var btn = $('.js-busy-me');
        setInterval(function () {
            if (btn.is('[disabled]')) {
                yukon.ui.unbusy(btn);
            } else {
                yukon.ui.busy(btn);
            }
        }, 1000);
    })();
});
</script>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button classes=&quot;js-busy-me&quot; label=&quot;My Button&quot;/&gt;
&lt;script&gt;
$(function() {
    (function () {
        var btn = $(&#39;.js-busy-me&#39;);
        setInterval(function () {
            if (btn.is(&#39;[disabled]&#39;)) {
                yukon.ui.unbusy(btn);
            } else {
                yukon.ui.busy(btn);
            }
        }, 1000);
    })();
});
&lt;/script&gt;
</pre>

<h3>Function: yukon.ui.dialog</h3>
<p class="description">
    <cti:url var="url" value="/dev/styleguide/dialogs"/>
    Yukon dialogs, discussed <a href="${url}">here</a> can also be opened programatically via 
    <span class="label label-attr">yukon.ui.dialog</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <cti:button classes="js-dialog-btn" label="Open My Dialog"/>
    <div class="js-dialog-example dn" data-title="My Dialog">
        This dialog was opened via <span class="label label-attr">yukon.ui.dialog</span>!
    </div>
</div>
<script>
$(function() {
    (function () {
        $('.js-dialog-btn').click(function() {
            yukon.ui.dialog('.js-dialog-example');
        });
    })();
});
</script>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button classes=&quot;js-dialog-btn&quot; label=&quot;Open My Dialog&quot;/&gt;
&lt;div class=&quot;js-dialog-example dn&quot; data-title=&quot;My Dialog&quot;&gt;
    This dialog was opened via &lt;span class=&quot;label label-attr&quot;&gt;yukon.ui.dialog&lt;/span&gt;!
&lt;/div&gt;
&lt;script&gt;
$(function() {
    (function () {
        $(&#39;.js-dialog-btn&#39;).click(function() {
            yukon.ui.dialog(&#39;.js-dialog-example&#39;);
        });
    })();
});
&lt;/script&gt;
</pre>

<h3>Attribute: [data-show-hide]</h3>
<p class="description">
    Show or hide something using <span class="label label-attr">[data-show-hide]</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Show/Hide My Stuff" data-show-hide=".js-my-sneaky-stuff"/>
    <div class="js-my-sneaky-stuff dn" class="well">
        My sneaky stuff!
    </div>
</div>
<div class="clearfix">
    <cti:button label="Show/Hide My Ajax'd Stuff" data-show-hide=".js-my-sneaky-ajax-stuff"/>
    <cti:url var="url" value="/dev/js-api/show-hide-ajax"/>
    <div class="js-my-sneaky-ajax-stuff dn" class="well" data-url="${url}"></div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;stacked clearfix&quot;&gt;
    &lt;cti:button label=&quot;Show/Hide My Stuff&quot; data-show-hide=&quot;.js-my-sneaky-stuff&quot;/&gt;
    &lt;div class=&quot;js-my-sneaky-stuff dn&quot; class=&quot;well&quot;&gt;
        My sneaky stuff!
    &lt;/div&gt;
&lt;/div&gt;
&lt;div class=&quot;clearfix&quot;&gt;
    &lt;cti:button label=&quot;Show/Hide My Ajax&#39;d Stuff&quot; data-show-hide=&quot;.js-my-sneaky-ajax-stuff&quot;/&gt;
    &lt;cti:url var=&quot;url&quot; value=&quot;/dev/js-api/show-hide-ajax&quot;/&gt;
    &lt;div class=&quot;js-my-sneaky-ajax-stuff dn&quot; class=&quot;well&quot; data-url=&quot;&#36;{url}&quot;&gt;&lt;/div&gt;
&lt;/div&gt;
</pre>

<h3>Attribute: [data-href]</h3>
<p class="description">
    Follow a link using <span class="label label-attr">[data-href]</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <cti:button label="Let Me Google That For You" data-href="http://www.google.com"/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Let Me Google That For You&quot; data-href=&quot;http://www.google.com&quot;/&gt;
</pre>

<h3>Class: .js-blocker</h3>
<p class="description">
    Block a container if inside a <span class="label label-attr">.js-block-this</span> element or the whole page using 
    <span class="label label-attr">.js-blocker</span>.
    See <a href="<cti:url value="/dev/styleguide/blocking"/>">blocking</a> in the style guide.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="js-block-this clearfix well">
        <cti:button label="Block Me" classes="js-blocker"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-block-this clearfix well&quot;&gt;
    &lt;cti:button label=&quot;Block Me&quot; classes=&quot;js-blocker&quot;/&gt;
&lt;/div&gt;
</pre>

<h3>Function: yukon.ui.block and yukon.ui.unblock</h3>
<p class="description">
    Containers with the <span class="label label-attr">.js-block-this</span> class can be blocked out by calling 
    <span class="label label-attr">yukon.ui.block</span> on an element inside them.
    See <a href="<cti:url value="/dev/styleguide/blocking"/>">blocking</a> in the style guide.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <tags:boxContainer title="Blocked Container" styleClass="js-block-this">
        <cti:button label="Block It Up" classes="js-block-btn"/>
    </tags:boxContainer>
    <script>
    $('.js-block-btn').click(function () {
        var btn = $(this);
        yukon.ui.block(btn);
        setTimeout(function () {
            yukon.ui.unblock(btn);
        }, 2000);
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:boxContainer title=&quot;Blocked Container&quot; styleClass=&quot;js-block-this&quot;&gt;
    &lt;cti:button label=&quot;Block It Up&quot; classes=&quot;js-block-btn&quot;/&gt;
&lt;/tags:boxContainer&gt;
&lt;script&gt;
$(&#39;.js-block-btn&#39;).click(function () {
    var btn = $(this);
    yukon.ui.block(btn);
    setTimeout(function () {
        yukon.ui.unblock(btn);
    }, 2000);
});
&lt;/script&gt;
</pre>

<h3>Function: yukon.ui.blockPage and yukon.ui.unblockPage</h3>
<p class="description">
    The whole page can be blocked/unblocked by using <span class="label label-attr">yukon.ui.blockPage</span>
    and <span class="label label-attr">yukon.ui.unblockPage</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Block It Up" classes="js-block-page-btn"/>
    <script>
    $('.js-block-page-btn').click(function () {
        yukon.ui.blockPage();
        setTimeout(function () {
            yukon.ui.unblockPage();
        }, 2000);
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Block It Up&quot; classes=&quot;js-block-page-btn&quot;/&gt;
&lt;script&gt;
$(&#39;.js-block-page-btn&#39;).click(function () {
    yukon.ui.blockPage();
    setTimeout(function () {
        yukon.ui.unblockPage();
    }, 2000);
});
&lt;/script&gt;
</pre>

<h3>Class: .js-disable-after-click</h3>
<p class="description">
    Disable an element or a set of elements after clicking using the 
    <span class="label label-attr">.js-disable-after-click</span> class and the
    <span class="label label-attr">[data-disable-group]</span> attribute. This class is safe to use on form submit
    buttons.  The form will still be submitted and if the submit button has a name attribute, the button name and value
    will be included in the posted params.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Disable Me" classes="js-disable-after-click"/>
</div>
<div class="clearfix">
    <cti:button label="Disable Me And My Buddies" classes="js-disable-after-click" data-disable-group="my buddies"/>
    <input type="text" value="Buddy #1" data-disable-group="my buddies">
    <input type="text" value="Buddy #2" data-disable-group="my buddies">
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;stacked clearfix&quot;&gt;
    &lt;cti:button label=&quot;Disable Me&quot; classes=&quot;js-disable-after-click&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;clearfix&quot;&gt;
    &lt;cti:button label=&quot;Disable Me And My Buddies&quot; classes=&quot;js-disable-after-click&quot; data-disable-group=&quot;my buddies&quot;/&gt;
    &lt;input type=&quot;text&quot; value=&quot;Buddy #1&quot; data-disable-group=&quot;my buddies&quot;&gt;
    &lt;input type=&quot;text&quot; value=&quot;Buddy #2&quot; data-disable-group=&quot;my buddies&quot;&gt;
&lt;/div&gt;
</pre>

<h3>Class: .js-submit-on-enter and .js-no-submit-on-enter</h3>
<p class="description">
    Disable the default behavior of a form being submitted when hitting <kbd class="light">Enter</kbd> with 
    <span class="label label-attr">.js-no-submit-on-enter</span> and override
    it with <span class="label label-attr">.js-submit-on-enter</span>. 
</p>
<h4 id="no-submit-example" class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:url var="url" value="/dev/js-api#no-submit-example"/>
    <form class="js-no-submit-on-enter" action="${url}">
        <input type="text" name="iWillNeverSubmit" value="Don't Submit on Enter">
        <input type="text" name="iSubmit" value="Submit on Enter" class="js-submit-on-enter">
        <cti:button type="submit" label="Submit"/>
    </form>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:url var=&quot;url&quot; value=&quot;/dev/js-api#no-submit-example&quot;/&gt;
&lt;form class=&quot;js-no-submit-on-enter&quot; action=&quot;&#36;{url}&quot;&gt;
    &lt;input type=&quot;text&quot; name=&quot;iWillNeverSubmit&quot; value=&quot;Don&#39;t Submit on Enter&quot;&gt;
    &lt;input type=&quot;text&quot; name=&quot;iSubmit&quot; value=&quot;Submit on Enter&quot; class=&quot;js-submit-on-enter&quot;&gt;
    &lt;cti:button type=&quot;submit&quot; label=&quot;Submit&quot;/&gt;
&lt;/form&gt;
</pre>

<h3>Class: .js-close, Event: yukon.dialog.ok</h3>
<p class="description">
    Any Yukon dialog will be closed if a element with the class <span class="label label-attr">.js-close</span> inside
    the dialog is clicked.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Open Dialog" data-popup=".js-closing-dialog"/>
    <div data-title="My Dialog" class="dn js-closing-dialog">
        <div>Click the button to close me.</div>
        <cti:button label="Close Dialog" classes="js-close"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Open Dialog&quot; data-popup=&quot;.js-closing-dialog&quot;/&gt;
&lt;div data-title=&quot;My Dialog&quot; class=&quot;dn js-closing-dialog&quot;&gt;
    &lt;div&gt;Click the button to close me.&lt;/div&gt;
    &lt;cti:button label=&quot;Close Dialog&quot; classes=&quot;js-close&quot;/&gt;
&lt;/div&gt;
</pre>
<p class="description">
    The same behavior occurs if any element inside a dialog fires the <span class="label label-attr">yukon.dialog.ok</span>
    event.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Open Dialog" data-popup=".js-closing-dialog-2"/>
    <div data-title="My Dialog" class="dn js-closing-dialog-2">
        <div>Click the button to close me.</div>
        <cti:button label="Close Dialog" classes="js-my-close-btn-2"/>
    </div>
    <script>$('.js-my-close-btn-2').click(function () { $(this).trigger('yukon.dialog.ok'); });</script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Open Dialog&quot; data-popup=&quot;.js-closing-dialog-2&quot;/&gt;
&lt;div data-title=&quot;My Dialog&quot; class=&quot;dn js-closing-dialog-2&quot;&gt;
    &lt;div&gt;Click the button to close me.&lt;/div&gt;
    &lt;cti:button label=&quot;Close Dialog&quot; classes=&quot;js-my-close-btn-2&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;$(&#39;.js-my-close-btn-2&#39;).click(function () { $(this).trigger(&#39;yukon.dialog.ok&#39;); });&lt;/script&gt;
</pre>

<h3>Class: .js-format-phone</h3>
<p class="description">
    Textfields for phone numbers will be formatted on page load and on blur using
    <span class="label label-attr">.js-format-phone</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <input type="text" name="cell" value="11231231234" class="js-format-phone">
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input type=&quot;text&quot; name=&quot;cell&quot; value=&quot;11231231234&quot; class=&quot;js-format-phone&quot;&gt;
</pre>

<h3>Function: yukon.ui.formatPhone</h3>
<p class="description">
    Phone numbers can also be programtically formatted with <span class="label label-attr">yukon.ui.formatPhone</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <input type="text" class="js-my-phone" value="11231231234">
    <cti:button classes="js-format-phone-btn" label="Format Phone #"/>
    <script>
    $('.js-format-phone-btn').click(function () {
        yukon.ui.formatPhone('.js-my-phone');
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input type=&quot;text&quot; class=&quot;js-my-phone&quot; value=&quot;11231231234&quot;&gt;
&lt;cti:button classes=&quot;js-format-phone-btn&quot; label=&quot;Format Phone #&quot;/&gt;
&lt;script&gt;
$(&#39;.js-format-phone-btn&#39;).click(function () {
    yukon.ui.formatPhone(&#39;.js-my-phone&#39;);
});
&lt;/script&gt;
</pre>

<h3>Attribute: [data-toggle]</h3>
<p class="description">
    Form components can be enabled/disabled using the 
    <span class="label label-attr">[data-toggle]</span> and <span class="label label-attr">[data-toggle-group]</span>
    attributes on checkboxes. Components will be enabled/disabled at page load based on checkbox state. The toggled 
    behavior can be inversed by also using <span class="label label-attr">[data-toggle-inverse]</span>. 
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <label class="db stacked"><input type="checkbox" name="disablestuff" data-toggle="my stuff" checked> Click to toggle</label>
    <input type="text" name="firstname" value="Bob" data-toggle-group="my stuff">
    <input type="text" name="lastname" value="Vila" data-toggle-group="my stuff">
    <cti:button label="Save" data-toggle-group="my stuff" classes="fn vat"/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;label class=&quot;db stacked&quot;&gt;&lt;input type=&quot;checkbox&quot; name=&quot;disablestuff&quot; data-toggle=&quot;my stuff&quot; checked&gt; Click to toggle&lt;/label&gt;
&lt;input type=&quot;text&quot; name=&quot;firstname&quot; value=&quot;Bob&quot; data-toggle-group=&quot;my stuff&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;lastname&quot; value=&quot;Vila&quot; data-toggle-group=&quot;my stuff&quot;&gt;
&lt;cti:button label=&quot;Save&quot; data-toggle-group=&quot;my stuff&quot;/&gt;
</pre>
<p class="description">
    Some checkbox-like tags like <span class="label label-attr">tags:switch</span> and 
    <span class="label label-attr">tags:switchButton</span> need named attributes since they cannot support 
    dynamic attributes.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <div class="stacked"><tags:switch name="disablestuff" toggleGroup="my stuff2" checked="true"/> Click to toggle</div>
    <input type="text" name="firstname" value="Bob" data-toggle-group="my stuff2">
    <input type="text" name="lastname" value="Vila" data-toggle-group="my stuff2">
    <cti:button label="Save" data-toggle-group="my stuff2" classes="fn vat"/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;stacked&quot;&gt;&lt;tags:switch name=&quot;disablestuff&quot; toggleGroup=&quot;my stuff2&quot;/&gt; Click to toggle&lt;/div&gt;
&lt;input type=&quot;text&quot; name=&quot;firstname&quot; value=&quot;Bob&quot; data-toggle-group=&quot;my stuff2&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;lastname&quot; value=&quot;Vila&quot; data-toggle-group=&quot;my stuff2&quot;&gt;
&lt;cti:button label=&quot;Save&quot; data-toggle-group=&quot;my stuff2&quot;/&gt;
</pre>
<p class="description">
    The toggle behavior can also be done programatically using <span class="label label-attr">yukon.ui.toggleInputs</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <label class="db stacked">
        <input type="checkbox" name="disablestuff" data-toggle="my stuff3" class="js-toggle-cb-3"> Click to toggle
    </label>
    <input type="text" name="firstname" value="Bob" data-toggle-group="my stuff3">
    <input type="text" name="lastname" value="Vila" data-toggle-group="my stuff3">
    <cti:button label="Save" data-toggle-group="my stuff3" classes="fn vat"/>
    <script>
    (function () {
        var toggle = function () {
            var cb = $('.js-toggle-cb-3'),
                checked = cb.prop('checked');
            cb.prop('checked', !checked);
            yukon.ui.toggleInputs(cb);
            setTimeout(toggle, 1000);
        };
        toggle();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;label class=&quot;db stacked&quot;&gt;
    &lt;input type=&quot;checkbox&quot; name=&quot;disablestuff&quot; data-toggle=&quot;my stuff3&quot; class=&quot;js-toggle-cb-3&quot;&gt; Click to toggle
&lt;/label&gt;
&lt;input type=&quot;text&quot; name=&quot;firstname&quot; value=&quot;Bob&quot; data-toggle-group=&quot;my stuff3&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;lastname&quot; value=&quot;Vila&quot; data-toggle-group=&quot;my stuff3&quot;&gt;
&lt;cti:button label=&quot;Save&quot; data-toggle-group=&quot;my stuff3&quot;/&gt;
&lt;script&gt;
(function () {
    var toggle = function () {
        var cb = $(&#39;.js-toggle-cb-3&#39;),
            checked = cb.prop(&#39;checked&#39;);
        cb.prop(&#39;checked&#39;, !checked);
        yukon.ui.toggleInputs(cb);
        setTimeout(toggle, 1000);
    };
})();
&lt;/script&gt;
</pre>

<h3>Class: .js-select-all</h3>
<p class="description">
    All collection of checkboxes can have 'select all' behavior using the 
    <span class="label label-attr">.js-select-all</span>, <span class="label label-attr">.js-select-all-container</span>,
    and <span class="label label-attr">.js-select-all-item</span> classes. 
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <div class="js-select-all-container">
        <label><input type="checkbox" name="p" class="js-select-all-item"> Pepperoni</label>
        <label><input type="checkbox" name="s" class="js-select-all-item"> Sausage</label>
        <label><input type="checkbox" name="gp" class="js-select-all-item"> Green Peppers</label>
        <label><input type="checkbox" name="pcp" class="js-select-all-item"> Pepperchini Peppers</label>
        <div class="page-action-area">
            <label><input type="checkbox" name="all" class="js-select-all"> Select All</label>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-select-all-container&quot;&gt;
    &lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;p&quot; class=&quot;js-select-all-item&quot;&gt; Pepperoni&lt;/label&gt;
    &lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;s&quot; class=&quot;js-select-all-item&quot;&gt; Sausage&lt;/label&gt;
    &lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;gp&quot; class=&quot;js-select-all-item&quot;&gt; Green Peppers&lt;/label&gt;
    &lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;pcp&quot; class=&quot;js-select-all-item&quot;&gt; Pepperchini Peppers&lt;/label&gt;
    &lt;div class=&quot;page-action-area&quot;&gt;
        &lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;all&quot; class=&quot;js-select-all&quot;&gt; Select All&lt;/label&gt;
    &lt;/div&gt;
&lt;/div&gt;
</pre>

<h3>Class: .js-focus and Attribute: [autofocus]</h3>
<p class="description">
    Any element with the class <span class="label label-attr">.js-focus</span> or the attribute 
    <span class="label label-attr">[autofocus]</span> will be the focused element on page load. These elements can also be 
    focused programatically using <span class="label label-attr">yukon.ui.autofocus</span>.  When an ajax-loaded dialog 
    is opened, it will be check for focus elements as well.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:url var="url" value="/dev/js-api/dialog-focus"/>
    <div class="js-focus-example-dialog" data-url="${url}" data-title="Dialog Focus Example"></div>
    <cti:button label="Open Dialog" data-popup=".js-focus-example-dialog" data-popup-toggle=""/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:url var=&quot;url&quot; value=&quot;/dev/js-api/dialog-focus&quot;/&gt;
&lt;div class=&quot;js-focus-example-dialog&quot; data-url=&quot;${url}&quot; data-title=&quot;Dialog Focus Example&quot;&gt;&lt;/div&gt;
&lt;cti:button label=&quot;Open Dialog&quot; data-popup=&quot;.js-focus-example-dialog&quot;/&gt;

...

From dialog-focus.jsp:
&lt;input type=&quot;text&quot; name=&quot;ignoreme&quot; value=&quot;ignore me&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;focusme&quot; value=&quot;focus on me&quot; class=&quot;js-focus&quot;&gt;
</pre>

<h3>Function: yukon.ui.alertSuccess/Info/Warning/Error/Pending</h3>
<p class="description">
    Alert boxes for the page can be added programatically using the alert functions.  These will always show up
    at the top of the page. Use <span class="label label-attr">yukon.ui.removeAlerts</span> to remove all alerts 
    from the page.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <cti:button label="Alert Success" classes="js-alert-success"/>
    <cti:button label="Alert Info" classes="js-alert-info"/>
    <cti:button label="Alert Warning" classes="js-alert-warning"/>
    <cti:button label="Alert Error" classes="js-alert-error"/>
    <cti:button label="Alert Pending" classes="js-alert-pending"/>
    <cti:button label="Remove Alerts" classes="js-remove-alerts"/>
    <script>
    $('.js-alert-success').click(function () {
        yukon.ui.alertSuccess('Success!');
    });
    $('.js-alert-info').click(function () {
        yukon.ui.alertInfo('Info!');
    });
    $('.js-alert-warning').click(function () {
        yukon.ui.alertWarning('Warning!');
    });
    $('.js-alert-error').click(function () {
        yukon.ui.alertError('Error!');
    });
    $('.js-alert-pending').click(function () {
        yukon.ui.alertPending('Pending!');
    });
    $('.js-remove-alerts').click(function () {
        yukon.ui.removeAlerts();
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Alert Success&quot; classes=&quot;js-alert-success&quot;/&gt;
&lt;cti:button label=&quot;Alert Info&quot; classes=&quot;js-alert-info&quot;/&gt;
&lt;cti:button label=&quot;Alert Warning&quot; classes=&quot;js-alert-warning&quot;/&gt;
&lt;cti:button label=&quot;Alert Error&quot; classes=&quot;js-alert-error&quot;/&gt;
&lt;cti:button label=&quot;Alert Pending&quot; classes=&quot;js-alert-pending&quot;/&gt;
&lt;cti:button label=&quot;Remove Alerts&quot; classes=&quot;js-remove-alerts&quot;/&gt;
&lt;script&gt;
$(&#39;.js-alert-success&#39;).click(function () {
    yukon.ui.alertSuccess(&#39;Success!&#39;);
});
$(&#39;.js-alert-info&#39;).click(function () {
    yukon.ui.alertInfo(&#39;Info!&#39;);
});
$(&#39;.js-alert-warning&#39;).click(function () {
    yukon.ui.alertWarning(&#39;Warning!&#39;);
});
$(&#39;.js-alert-error&#39;).click(function () {
    yukon.ui.alertError(&#39;Error!&#39;);
});
$(&#39;.js-alert-pending&#39;).click(function () {
    yukon.ui.alertPending(&#39;Pending!&#39;);
});
$(&#39;.js-remove-alerts&#39;).click(function () {
    yukon.ui.removeAlerts();
});
&lt;/script&gt;
</pre>

<h3>Function: yukon.ui.reindexInputs and Spring Binding</h3>
<p class="description">
    Use <span class="label label-attr">yukon.ui.reindexInputs</span> when manipulating lists in spring forms.
    Spring forms using lists expect the input names to use square bracket array notation.<br>
    <span class="label label-info">Note:</span> 
    <span class="label label-attr">yukon.ui.reindexInputs</span> will also fix the enabled/disabled state of 
    mover buttons if they have the usual <span class="label label-attr">.js-up</span> and 
    <span class="label label-attr">.js-down</span> classes.<br>
    <span class="label label-info">Note:</span> 
    <span class="label label-attr">yukon.ui.reindexInputs</span> can also fire a callback on each row.<br>
    <span class="label label-warning">Warning:</span> 
    <span class="label label-attr">yukon.ui.reindexInputs</span> currently only works with tables.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <form:form modelAttribute="company" class="js-reindexing-example">
        <table>
            <thead></thead>
            <tfoot>
                <tr class="dn js-reindexing-template">
                    <td><input type="text" name="employees[?].name"></td>
                    <td>
                        <input type="text" name="employees[?].email">
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-down" classes="fn vat left js-down"
                        /><cti:button renderMode="buttonImage" icon="icon-bullet-go-up" classes="fn vat right js-up"/>
                        <cti:button renderMode="buttonImage" icon="icon-cross" classes="fn js-remove vat"/>
                    </td>
                </tr>
            </tfoot>
            <tbody>
                <c:forEach items="${company.employees}" varStatus="status">
                    <tr>
                        <td><tags:input path="employees[${status.index}].name"/></td>
                        <td>
                            <tags:input path="employees[${status.index}].email"/>
                            <c:set var="downdisabled" value="${status.last ? true : false}"/>
                            <c:set var="updisabled" value="${status.first ? true : false}"/>
                            <cti:button renderMode="buttonImage" icon="icon-bullet-go-down" classes="fn vat left js-down" 
                                disabled="${downdisabled}"
                            /><cti:button renderMode="buttonImage" icon="icon-bullet-go-up" classes="fn vat right js-up" 
                                disabled="${updisabled}"/>
                            <cti:button renderMode="buttonImage" icon="icon-cross" classes="fn js-remove vat"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="page-action-area">
            <cti:button label="Add" classes="js-add" icon="icon-plus-green"/>
        </div>
    </form:form>
    <script>
    /** Remove Row */
    $(document).on('click', '.js-reindexing-example .js-remove', function () {
        var table = $(this).closest('table'),
            row = $(this).closest('tr');
        row.remove();
        yukon.ui.reindexInputs(table);
    });
    /** Add Row */
    $(document).on('click', '.js-reindexing-example .js-add', function () {
        
        var table = $('.js-reindexing-example table');
        
        table.find('.js-reindexing-template')
        .clone().removeClass('dn js-reindexing-template')
        .appendTo(table);
        
        yukon.ui.reindexInputs(table, function (row) {
            if (row.is(':last-child')) {
                row.data('added', true);
            }
        });
    });
    /** Move Row */
    $(document).on('click', '.js-reindexing-example .js-up, .js-reindexing-example .js-down', function () {
        
        var btn = $(this),
            row = btn.closest('tr'),
            table = btn.closest('table'),
            up = btn.is('.js-up'),
            neighbor = up ? row.prev() : row.next();
            
        // Move row up or down.
        if (up) {
            row.insertBefore(neighbor);
        } else {
            row.insertAfter(neighbor);
        }
        
        yukon.ui.reindexInputs(table);
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;form:form modelAttribute=&quot;company&quot; class=&quot;js-reindexing-example&quot;&gt;
    &lt;table&gt;
        &lt;thead&gt;&lt;/thead&gt;
        &lt;tfoot&gt;
            &lt;tr class=&quot;dn js-reindexing-template&quot;&gt;
                &lt;td&gt;&lt;input type=&quot;text&quot; name=&quot;employees[?].name&quot;&gt;&lt;/td&gt;
                &lt;td&gt;
                    &lt;input type=&quot;text&quot; name=&quot;employees[?].email&quot;&gt;
                    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-down&quot; classes=&quot;fn vat left js-down&quot;
                    /&gt;&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-up&quot; classes=&quot;fn vat right js-up&quot;/&gt;
                    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-cross&quot; classes=&quot;fn js-remove vat&quot;/&gt;
                &lt;/td&gt;
            &lt;/tr&gt;
        &lt;/tfoot&gt;
        &lt;tbody&gt;
            &lt;c:forEach items=&quot;&#36;{company.employees}&quot; varStatus=&quot;status&quot;&gt;
                &lt;tr&gt;
                    &lt;td&gt;&lt;tags:input path=&quot;employees[&#36;{status.index}].name&quot;/&gt;&lt;/td&gt;
                    &lt;td&gt;
                        &lt;tags:input path=&quot;employees[&#36;{status.index}].email&quot;/&gt;
                        &lt;c:set var=&quot;downdisabled&quot; value=&quot;&#36;{status.last ? true : false}&quot;/&gt;
                        &lt;c:set var=&quot;updisabled&quot; value=&quot;&#36;{status.first ? true : false}&quot;/&gt;
                        &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-down&quot; classes=&quot;fn vat left js-down&quot; 
                            disabled=&quot;&#36;{downdisabled}&quot;
                        /&gt;&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-up&quot; classes=&quot;fn vat right js-up&quot; 
                            disabled=&quot;&#36;{updisabled}&quot;/&gt;
                        &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-cross&quot; classes=&quot;fn js-remove vat&quot;/&gt;
                    &lt;/td&gt;
                &lt;/tr&gt;
            &lt;/c:forEach&gt;
        &lt;/tbody&gt;
    &lt;/table&gt;
    &lt;div class=&quot;page-action-area&quot;&gt;
        &lt;cti:button label=&quot;Add&quot; classes=&quot;js-add&quot; icon=&quot;icon-plus-green&quot;/&gt;
    &lt;/div&gt;
&lt;/form:form&gt;
&lt;script&gt;
/** Remove Row */
$(document).on(&#39;click&#39;, &#39;.js-reindexing-example .js-remove&#39;, function () {
    var table = $(this).closest(&#39;table&#39;),
        row = $(this).closest(&#39;tr&#39;);
    row.remove();
    yukon.ui.reindexInputs(table);
});
/** Add Row */
$(document).on(&#39;click&#39;, &#39;.js-reindexing-example .js-add&#39;, function () {
    
    var table = $(&#39;.js-reindexing-example table&#39;);
    
    table.find(&#39;.js-reindexing-template&#39;)
    .clone().removeClass(&#39;dn js-reindexing-template&#39;)
    .appendTo(table);
    
    yukon.ui.reindexInputs(table, function (row) {
        if (row.is(&#39;:last-child&#39;)) {
            row.data(&#39;added&#39;, true);
        }
    });
});
/** Move Row */
$(document).on('click', '.js-reindexing-example .js-up, .js-reindexing-example .js-down', function () {
    
    var btn = $(this),
        row = btn.closest('tr'),
        table = btn.closest('table'),
        up = btn.is('.js-up'),
        neighbor = up ? row.prev() : row.next();
        
    // Move row up or down.
    if (up) {
        row.insertBefore(neighbor);
    } else {
        row.insertAfter(neighbor);
    }
    
    yukon.ui.reindexInputs(table);
});
&lt;/script&gt;
</pre>

<h3>Function: yukon.ui.adjustRowMovers</h3>
<p class="description">
    <span class="label label-attr">yukon.ui.adjustRowMovers</span> is similar to 
    <span class="label label-attr">yukon.ui.reindexInputs</span> in that it will only fix the enabled/disabled state of 
    mover buttons if they have the usual <span class="label label-attr">.js-up</span> and 
    <span class="label label-attr">.js-down</span> classes. It does NOT adjust spring binding input names.<br>
    <span class="label label-info">Note:</span> 
    <span class="label label-attr">yukon.ui.adjustRowMovers</span> can also fire a callback on each row.<br>
    <span class="label label-warning">Warning:</span> 
    <span class="label label-attr">yukon.ui.adjustRowMovers</span> currently only works with tables.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <table class="js-adjust-movers-example">
        <thead></thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="person" items="${company.employees}" varStatus="status">
                <tr>
                    <td>${fn:escapeXml(person.name)}</td>
                    <td>
                        ${fn:escapeXml(person.email)}
                    </td>
                    <td>
                        <c:set var="downdisabled" value="${status.last ? true : false}"/>
                        <c:set var="updisabled" value="${status.first ? true : false}"/>
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-down" classes="fn vat left js-down" 
                            disabled="${downdisabled}"
                        /><cti:button renderMode="buttonImage" icon="icon-bullet-go-up" classes="fn vat right js-up" 
                            disabled="${updisabled}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <script>
    /** Move Row */
    $(document).on('click', '.js-adjust-movers-example .js-up, .js-adjust-movers-example .js-down', function () {
        
        var btn = $(this),
            row = btn.closest('tr'),
            table = btn.closest('table'),
            up = btn.is('.js-up'),
            neighbor = up ? row.prev() : row.next();
            
        // Move row up or down.
        if (up) {
            row.insertBefore(neighbor);
        } else {
            row.insertAfter(neighbor);
        }
        
        yukon.ui.reindexInputs(table, function (row) {
            if (row.is(':last-child')) {
                row.data('added', true);
            }
        });
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;js-adjust-movers-example&quot;&gt;
    &lt;thead&gt;&lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;c:forEach var=&quot;person&quot; items=&quot;&#36;{company.employees}&quot; varStatus=&quot;status&quot;&gt;
            &lt;tr&gt;
                &lt;td&gt;&#36;{fn:escapeXml(person.name)}&lt;/td&gt;
                &lt;td&gt;
                    &#36;{fn:escapeXml(person.email)}
                &lt;/td&gt;
                &lt;td&gt;
                    &lt;c:set var=&quot;downdisabled&quot; value=&quot;&#36;{status.last ? true : false}&quot;/&gt;
                    &lt;c:set var=&quot;updisabled&quot; value=&quot;&#36;{status.first ? true : false}&quot;/&gt;
                    &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-down&quot; classes=&quot;fn vat left js-down&quot; 
                        disabled=&quot;&#36;{downdisabled}&quot;
                    /&gt;&lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-bullet-go-up&quot; classes=&quot;fn vat right js-up&quot; 
                        disabled=&quot;&#36;{updisabled}&quot;/&gt;
                &lt;/td&gt;
            &lt;/tr&gt;
        &lt;/c:forEach&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
&lt;script&gt;
/** Move Row */
$(document).on(&#39;click&#39;, &#39;.js-adjust-movers-example .js-up, .js-adjust-movers-example .js-down&#39;, function () {
    
    var btn = $(this),
        row = btn.closest(&#39;tr&#39;),
        table = btn.closest(&#39;table&#39;),
        up = btn.is(&#39;.js-up&#39;),
        neighbor = up ? row.prev() : row.next();
        
    // Move row up or down.
    if (up) {
        row.insertBefore(neighbor);
    } else {
        row.insertAfter(neighbor);
    }
    
    yukon.ui.reindexInputs(table, function (row) {
        if (row.is(&#39;:last-child&#39;)) {
            row.data(&#39;added&#39;, true);
        }
    });
});
&lt;/script&gt;
</pre>

<h3>Class: .js-with-movables, .js-move-up, .js-move-down</h3>
<p class="description">
    <span class="label label-attr">yukon.ui.movables</span> is a module that handles moving items in a list of things.
    It works by listening for clicks on <span class="label label-attr">.js-move-up</span> and 
    <span class="label label-attr">.js-move-down</span> elements inside of a container with 
    <span class="label label-attr">.js-with-movables</span>.  It is meant to work with any type of elements, not just
    tables, therefore the <span class="label label-attr">[data-item-selector]</span> attribute is required on the 
    <span class="label label-attr">.js-with-movables</span> container to designate what a 'row' is.<br>
    <span class="label label-info">Note:</span> 
    After adding/removing something to <span class="label label-attr">.js-with-movables</span>, fire the 
    <span class="label label-attr">yukon:ordered-selection:added-removed</span> event on the 
    <span class="label label-attr">.js-with-movables</span> to fix the mover buttons.<br>
    <span class="label label-warning">Warning:</span> 
    <span class="label label-attr">.js-with-movables</span> currently does not fix any spring bindings input names.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <div class="js-with-movables" data-item-selector=".js-row">
        <h3>My Favorite Restaurants</h3>
        <c:forEach var="restaurant" items="${restaurants}" varStatus="status">
            <div class="js-row clearfix" style="width:200px; margin: 10px;">
                <div class="dib fl form-control">${fn:escapeXml(restaurant)}</div>
                <div class="dib fr">
                    <c:set var="disabled" value="${status.first}"/>
                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                            classes="left js-move-up" disabled="${disabled}"/>
                    <c:set var="disabled" value="${status.last}"/>
                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                            classes="right js-move-down" disabled="${disabled}"/>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-with-movables&quot; data-item-selector=&quot;.js-row&quot;&gt;
    &lt;h3&gt;My Favorite Restaurants&lt;/h3&gt;
    &lt;c:forEach var=&quot;restaurant&quot; items=&quot;&#36;{restaurants}&quot; varStatus=&quot;status&quot;&gt;
        &lt;div class=&quot;js-row clearfix&quot; style=&quot;width:200px; margin: 10px;&quot;&gt;
            &lt;div class=&quot;dib fl form-control&quot;&gt;&#36;{fn:escapeXml(restaurant)}&lt;/div&gt;
            &lt;div class=&quot;dib fr&quot;&gt;
                &lt;c:set var=&quot;disabled&quot; value=&quot;&#36;{status.first}&quot;/&gt;
                &lt;cti:button icon=&quot;icon-bullet-go-up&quot; renderMode=&quot;buttonImage&quot;
                        classes=&quot;left js-move-up&quot; disabled=&quot;&#36;{disabled}&quot;/&gt;
                &lt;c:set var=&quot;disabled&quot; value=&quot;&#36;{status.last}&quot;/&gt;
                &lt;cti:button icon=&quot;icon-bullet-go-down&quot; renderMode=&quot;buttonImage&quot;
                        classes=&quot;right js-move-down&quot; disabled=&quot;&#36;{disabled}&quot;/&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/c:forEach&gt;
&lt;/div&gt;
</pre>

<h3>Class: .js-wizard, .js-page, .js-next, .js-prev</h3>
<p class="description">
    <span class="label label-attr">.js-wizard</span> and it's related class names can be used to manage wizard 
    style paging, useful in complex popups.<br>
    The wizard can also be driven using the wizard functions <span class="label label-attr">yukon.ui.wizard.init</span>,
    <span class="label label-attr">yukon.ui.wizard.nextPage</span>, 
    <span class="label label-attr">yukon.ui.wizard.prevPage</span>, and
    <span class="label label-attr">yukon.ui.wizard.reset</span>.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <div class="js-wizard js-my-wizard dn" data-title="My Wizard" data-width="400">
        <div class="js-page">
            <span>I'm page one.<br> I am the coolest of all pages.</span>
            <div class="action-area">
                <cti:button label="Next" classes="action primary js-next"/>
                <cti:button label="Cancel" classes="js-close"/>
            </div>
        </div>
        <div class="js-page dn">
            <span>I'm page two.<br> Page one is thinks he's so cool.</span>
            <div class="action-area">
                <cti:button label="Next" classes="action primary js-next"/>
                <cti:button label="Prev" classes="action primary js-prev"/>
                <cti:button label="Cancel" classes="js-close"/>
            </div>
        </div>
        <div class="js-page dn">
            <span>I'm page three.<br> The last page, which makes me cooler than the other two.</span>
                <div class="action-area">
                <cti:button label="Finish" classes="action primary js-next"/>
                <cti:button label="Prev" classes="action primary js-prev"/>
                <cti:button label="Cancel" classes="js-close"/>
            </div>
        </div>
    </div>
    <cti:button label="Open Wizard" data-popup=".js-my-wizard"/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-wizard js-my-wizard dn&quot; data-title=&quot;My Wizard&quot; data-width=&quot;400&quot;&gt;
    &lt;div class=&quot;js-page&quot;&gt;
        &lt;span&gt;I&#39;m page one.&lt;br&gt; I am the coolest of all pages.&lt;/span&gt;
        &lt;div class=&quot;action-area&quot;&gt;
            &lt;cti:button label=&quot;Next&quot; classes=&quot;action primary js-next&quot;/&gt;
            &lt;cti:button label=&quot;Cancel&quot; classes=&quot;js-close&quot;/&gt;
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class=&quot;js-page dn&quot;&gt;
        &lt;span&gt;I&#39;m page two.&lt;br&gt; Page one is thinks he&#39;s so cool.&lt;/span&gt;
        &lt;div class=&quot;action-area&quot;&gt;
            &lt;cti:button label=&quot;Next&quot; classes=&quot;action primary js-next&quot;/&gt;
            &lt;cti:button label=&quot;Prev&quot; classes=&quot;action primary js-prev&quot;/&gt;
            &lt;cti:button label=&quot;Cancel&quot; classes=&quot;js-close&quot;/&gt;
        &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class=&quot;js-page dn&quot;&gt;
        &lt;span&gt;I&#39;m page three.&lt;br&gt; The last page, which makes me cooler than the other two.&lt;/span&gt;
            &lt;div class=&quot;action-area&quot;&gt;
            &lt;cti:button label=&quot;Finish&quot; classes=&quot;action primary js-next&quot;/&gt;
            &lt;cti:button label=&quot;Prev&quot; classes=&quot;action primary js-prev&quot;/&gt;
            &lt;cti:button label=&quot;Cancel&quot; classes=&quot;js-close&quot;/&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
&lt;cti:button label=&quot;Open Wizard&quot; data-popup=&quot;.js-my-wizard&quot;/&gt;
</pre>

<h3>Function: yukon.ui.removeWithUndo</h3>
<p class="description">
    <span class="label label-attr">yukon.ui.removeWithUndo</span> is a function that attempts to replace a 'row' like 
    element with an undo option.
</p>
<h4 class="subtle">Example:</h4>
<div class="stacked clearfix">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <div class="js-my-todo-list">
                <div class="form-control stacked" data-removed-text="Done!" data-undo-text="Undo">
                    Write a unit test.
                    <cti:button renderMode="buttonImage" icon="icon-tick" classes="js-done fr"/>
                </div>
                <div class="form-control stacked" data-removed-text="Done!" data-undo-text="Undo">
                    Make fun of Joe's clothes.
                    <cti:button renderMode="buttonImage" icon="icon-tick" classes="js-done fr"/>
                </div>
                <div class="form-control stacked" data-removed-text="Done!" data-undo-text="Undo">
                    Tell Jess to hire someone.
                    <cti:button renderMode="buttonImage" icon="icon-tick" classes="js-done fr"/>
                </div>
                <div class="form-control stacked" data-removed-text="Done!" data-undo-text="Undo">
                    Take nap in hammock.
                    <cti:button renderMode="buttonImage" icon="icon-tick" classes="js-done fr"/>
                </div>
            </div>
        </div>
    </div>
    <script>
    (function () {
        var daAThing = function () { console.log('Yay you did a thing!'); };
        var undoAThing = function () { console.log('You still need to do that thing.'); };
        $('.js-my-todo-list .js-done').click(function () {
            yukon.ui.removeWithUndo($(this).closest('div'), daAThing, undoAThing);
        });
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;column-12-12 clearfix&quot;&gt;
    &lt;div class=&quot;column one&quot;&gt;
        &lt;div class=&quot;js-my-todo-list&quot;&gt;
            &lt;div class=&quot;form-control stacked&quot; data-removed-text=&quot;Done!&quot; data-undo-text=&quot;Undo&quot;&gt;
                Write a unit test.
                &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-tick&quot; classes=&quot;js-done fr&quot;/&gt;
            &lt;/div&gt;
            &lt;div class=&quot;form-control stacked&quot; data-removed-text=&quot;Done!&quot; data-undo-text=&quot;Undo&quot;&gt;
                Make fun of Joe&#39;s clothes.
                &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-tick&quot; classes=&quot;js-done fr&quot;/&gt;
            &lt;/div&gt;
            &lt;div class=&quot;form-control stacked&quot; data-removed-text=&quot;Done!&quot; data-undo-text=&quot;Undo&quot;&gt;
                Tell Jess to hire someone.
                &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-tick&quot; classes=&quot;js-done fr&quot;/&gt;
            &lt;/div&gt;
            &lt;div class=&quot;form-control stacked&quot; data-removed-text=&quot;Done!&quot; data-undo-text=&quot;Undo&quot;&gt;
                Take nap in hammock.
                &lt;cti:button renderMode=&quot;buttonImage&quot; icon=&quot;icon-tick&quot; classes=&quot;js-done fr&quot;/&gt;
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    var daAThing = function () { console.log(&#39;Yay you did a thing!&#39;); };
    var undoAThing = function () { console.log(&#39;You still need to do that thing.&#39;); };
    $(&#39;.js-my-todo-list .js-done&#39;).click(function () {
        yukon.ui.removeWithUndo($(this).closest(&#39;div&#39;), daAThing, undoAThing);
    });
})();
&lt;/script&gt;
</pre>


<h2>Yukon Jquery Plugins</h2>
<p class="description">
    A set of jquery plugins we've built for Yukon. Not thirdparty ones, those are discussed elsewhere.
</p>

<h3>$.selectText</h3>
<p class="description">
    Will highlight all the text in an element. Useful for copy actions.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well js-jq-select-ex">
        The <span class="success">coolest</span> thing about<br>
        $.selectText is that it will
        <div class="user-message success">select the text reguardless of nested elements.</div>
    </div>
    <div class="page-action-area">
        <cti:button classes="js-jq-select-btn" label="Highlight All The Things"/>
    </div>
    <script>
    $('.js-jq-select-btn').click(function () {
        $('.js-jq-select-ex').selectText();
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well js-jq-select-ex&quot;&gt;
    The &lt;span class=&quot;success&quot;&gt;coolest&lt;/span&gt; thing about&lt;br&gt;
    $.selectText is that it will
    &lt;div&gt;select the text reguardless of nested elements.&lt;/div&gt;
&lt;/div&gt;
&lt;div class=&quot;page-action-area&quot;&gt;
    &lt;cti:button classes=&quot;js-jq-select-btn&quot; label=&quot;Highlight All The Things&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;
$(&#39;.js-jq-select-btn&#39;).click(function () {
    $(&#39;.js-jq-select-ex&#39;).selectText();
});
&lt;/script&gt;
</pre>

<h3>$.toggleDisabled</h3>
<p class="description">
    Disable everything in the collection that is an html form input type.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="js-jq-disable-ex">
        <input type="text" name="foo" value="bar">
        <input type="checkbox" checked>
        <select name="toppings">
            <option value="p">Pepperoni</option>
            <option value="s">Sausage</option>
        </select>
        <cti:button label="Some Button"/>
    </div>
    <div class="page-action-area">
        <cti:button classes="js-jq-disable-btn-1" label="Enable/Disable Everything"/>
        <cti:button classes="js-jq-disable-btn-2" label="Enable/Disable Text Fields"/>
    </div>
    <script>
    $('.js-jq-disable-btn-1').click(function () {
        $('.js-jq-disable-ex *').toggleDisabled();
    });
    $('.js-jq-disable-btn-2').click(function () {
        $('.js-jq-disable-ex input[type=text]').toggleDisabled();
    });
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-jq-disable-ex&quot;&gt;
    &lt;input type=&quot;text&quot; name=&quot;foo&quot; value=&quot;bar&quot;&gt;
    &lt;input type=&quot;checkbox&quot; checked&gt;
    &lt;select name=&quot;toppings&quot;&gt;
        &lt;option value=&quot;p&quot;&gt;Pepperoni&lt;/option&gt;
        &lt;option value=&quot;s&quot;&gt;Sausage&lt;/option&gt;
    &lt;/select&gt;
    &lt;cti:button label=&quot;Some Button&quot;/&gt;
&lt;/div&gt;
&lt;div class=&quot;page-action-area&quot;&gt;
    &lt;cti:button classes=&quot;js-jq-disable-btn-1&quot; label=&quot;Enable/Disable Everything&quot;/&gt;
    &lt;cti:button classes=&quot;js-jq-disable-btn-2&quot; label=&quot;Enable/Disable Text Fields&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;
$(&#39;.js-jq-disable-btn-1&#39;).click(function () {
    $(&#39;.js-jq-disable-ex *&#39;).toggleDisabled();
});
$(&#39;.js-jq-disable-btn-2&#39;).click(function () {
    $(&#39;.js-jq-disable-ex input[type=text]&#39;).toggleDisabled();
});
&lt;/script&gt;
</pre>

<h3>$.flash</h3>
<p class="description">
    Flash an element's background with a color for a duration. It takes 3 an option 'options' argument with 
    three possible options: color, duration, and a 'complete' callback.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="stacked">
        <span class="js-flash-me">Look Mom, no hands!</span>
        <div class="well buffered js-flash-me">I'm so good at Jquery!</div>
        <div class="js-flash-speed" style="max-width: 400px;"></div>
        Speed: <span class="js-flash-speed-val"></span>
    </div>
    <script>
    (function () {
        
        $('.js-flash-speed').slider({
            value:1800,
            min: 300,
            max: 3000,
            step: 300,
            slide: function (event, ui) {
                var speed = ui.value === 300 ? 'ludicrous' :
                        ui.value === 600 ? 'rave' : ui.value + 'ms';
                $('.js-flash-speed-val').text(speed);
            }
        });
        $('.js-flash-speed-val').text($('.js-flash-speed').slider('value') + 'ms');
        var colors = ['#01B9BB', '#617DC6', '#F7D22B', '#F25E42', '#E26C7A'];
        
        var flasher = function () {
            var c = yukon.randomInt(0, 5);
            var d = $('.js-flash-speed').slider('value');
            $('.js-flash-me').flash({ 
                color: colors[c], 
                duration: d, 
                complete: function () {
                    setTimeout(flasher, 0);
                }
            });
        };
        flasher();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;stacked&quot;&gt;
    &lt;span class=&quot;js-flash-me&quot;&gt;Look Mom, no hands!&lt;/span&gt;
    &lt;div class=&quot;well buffered js-flash-me&quot;&gt;I&#39;m so good at Jquery!&lt;/div&gt;
    &lt;div class=&quot;js-flash-speed&quot; style=&quot;max-width: 400px;&quot;&gt;&lt;/div&gt;
    Speed: &lt;span class=&quot;js-flash-speed-val&quot;&gt;&lt;/span&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    
    $(&#39;.js-flash-speed&#39;).slider({
        value:1800,
        min: 300,
        max: 3000,
        step: 300,
        slide: function (event, ui) {
            var speed = ui.value === 300 ? &#39;ludicrous&#39; :
                    ui.value === 600 ? &#39;rave&#39; : ui.value + &#39;ms&#39;;
            $(&#39;.js-flash-speed-val&#39;).text(speed);
        }
    });
    $(&#39;.js-flash-speed-val&#39;).text($(&#39;.js-flash-speed&#39;).slider(&#39;value&#39;) + &#39;ms&#39;);
    var colors = [&#39;#01B9BB&#39;, &#39;#617DC6&#39;, &#39;#F7D22B&#39;, &#39;#F25E42&#39;, &#39;#E26C7A&#39;];
    
    var flasher = function () {
        var c = yukon.randomInt(0, 5);
        var d = $(&#39;.js-flash-speed&#39;).slider(&#39;value&#39;);
        $(&#39;.js-flash-me&#39;).flash({ 
            color: colors[c], 
            duration: d, 
            complete: function () {
                setTimeout(flasher, 0);
            }
        });
    };
    flasher();
})();
&lt;/script&gt;
</pre>

<h3>$.visible, $.invisible, $.visibilityToggle</h3>
<p class="description">
    Does the same as jquery's built in $.show, $.hide, and $.toggle except they change the CSS <em>visibility</em> property.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well">
        <span>Who ya gonna call? <cti:icon classes="js-ghost fn" icon="icon-status-offline"/> Ghost busters!</span>
    </div>
    <script>
    (function () {
        var runner = function () {
            $('.js-ghost').visibilityToggle();
            setTimeout(runner, 1000);
        };
        runner();
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well&quot;&gt;
    &lt;span&gt;Who ya gonna call? &lt;cti:icon classes=&quot;js-ghost fn&quot; icon=&quot;icon-status-offline&quot;/&gt; Ghost busters!&lt;/span&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    var runner = function () {
        $(&#39;.js-ghost&#39;).visibilityToggle();
        setTimeout(runner, 1000);
    };
    runner();
})();
&lt;/script&gt;
</pre>

<h3 id="add-msg-ex">$.addMessage, $.removeMessages</h3>
<p class="description">
    <span class="label label-attr">$.addMessage</span> adds an alert to a container element and 
    <span class="label label-attr">$.removeMessages</span> removes all alerts from a container element.
</p>
<h4 class="subtle">Example:</h4>
<div class="clearfix">
    <div class="well clearfix js-add-msg-ex">
        <cti:button classes="js-add-msg" label="Add Success" data-label="Success" data-type="success"/>
        <cti:button classes="js-add-msg" label="Add Info" data-label="Info" data-type="info"/>
        <cti:button classes="js-add-msg" label="Add Warning" data-label="Warning" data-type="warning"/>
        <cti:button classes="js-add-msg" label="Add Error" data-label="Error" data-type="error"/>
        <cti:button classes="js-add-msg" label="Add Pending" data-label="Pending" data-type="pending"/>
        <cti:button classes="js-remove-msgs" label="Remove Messages"/>
    </div>
    <script>
    (function () {
        $('.js-add-msg').click(function () { 
            $('.js-add-msg-ex').addMessage({
                message: $(this).data('label'),
                messageClass: $(this).data('type')
            }); 
        });
        $('.js-remove-msgs').click(function () { 
            $('.js-add-msg-ex').removeMessages();
        });
    })();
    </script>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;well clearfix js-add-msg-ex&quot;&gt;
    &lt;cti:button classes=&quot;js-add-msg&quot; label=&quot;Add Success&quot; data-label=&quot;Success&quot; data-type=&quot;success&quot;/&gt;
    &lt;cti:button classes=&quot;js-add-msg&quot; label=&quot;Add Info&quot; data-label=&quot;Info&quot; data-type=&quot;info&quot;/&gt;
    &lt;cti:button classes=&quot;js-add-msg&quot; label=&quot;Add Warning&quot; data-label=&quot;Warning&quot; data-type=&quot;warning&quot;/&gt;
    &lt;cti:button classes=&quot;js-add-msg&quot; label=&quot;Add Error&quot; data-label=&quot;Error&quot; data-type=&quot;error&quot;/&gt;
    &lt;cti:button classes=&quot;js-add-msg&quot; label=&quot;Add Pending&quot; data-label=&quot;Pending&quot; data-type=&quot;pending&quot;/&gt;
    &lt;cti:button classes=&quot;js-remove-msgs&quot; label=&quot;Remove Messages&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;
(function () {
    $(&#39;.js-add-msg&#39;).click(function () { 
        $(&#39;.js-add-msg-ex&#39;).addMessage({
            message: $(this).data(&#39;label&#39;),
            messageClass: $(this).data(&#39;type&#39;)
        }); 
    });
    $(&#39;.js-remove-msgs&#39;).click(function () { 
        $(&#39;.js-add-msg-ex&#39;).removeMessages();
    });
})();
&lt;/script&gt;
</pre>

<script>$(function () { prettyPrint(); });</script>
</cti:standardPage>
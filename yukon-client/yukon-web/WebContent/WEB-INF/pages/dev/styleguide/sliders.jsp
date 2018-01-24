<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="sliders">
<tags:styleguide page="sliders">
<cti:includeScript link="YUKON_TIME_FORMATTER"/>

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Sliders allow users to select a single value or a range within a determined set of possible values.  They are most often used for time selection.
    When used for time selection, you will need to include the script YUKON_TIME_FORMATTER.
    They are built as a jquery widget using jquery's <a href="https://jqueryui.com/widget/">widget factory</a>.
</p>

<h2>Simple Slider allowing selection of a Single Value</h2>

<p class="description">
    Create a slider through the jquery slider function.  You can set options such as the min/max values, default value and step value (how much to increment per step).
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dib column-6-18 clearfix">
            <div class="column one">
                <div class="js-time-label fwb" style="width:70px;"></div>
            </div>
            <div class="column two nogutter">
                <div class="js-time-slider" style="margin-top: 5px;width:250px;"></div>
            </div>
        </div>
        <script>
        (function () {
            var timeLabel = $('.js-time-label'),
                timeSlider = $('.js-time-slider'),
                defaultValue = 360;
            //initialize time slider
            timeSlider.slider({
                max: 24 * 60 - 60,
                min: 0,
                value: defaultValue,
                step: 60,
                slide: function (ev, ui) {
                    timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                },
                change: function (ev, ui) {
                    timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                }
            });
            timeLabel.text(yukon.timeFormatter.formatTime(defaultValue, 0));
        })();
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dib column-6-18 clearfix&quot;&gt;
    &lt;div class=&quot;column one&quot;&gt;
        &lt;div class=&quot;js-time-label fwb&quot; style=&quot;width:70px;&quot;&gt;&lt;/div&gt;
    &lt;/div&gt;
    &lt;div class=&quot;column two nogutter&quot;&gt;
        &lt;div class=&quot;js-time-slider&quot; style=&quot;margin-top: 5px;width:250px;&quot;&gt;&lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
&lt;script&gt;
    (function () {
        var timeLabel = $('.js-time-label'),
            timeSlider = $('.js-time-slider'),
            defaultValue = 360;
        //initialize time slider
        timeSlider.slider({
            max: 24 * 60 - 60,
            min: 0,
            value: defaultValue,
            step: 60,
            slide: function (ev, ui) {
                timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
            },
            change: function (ev, ui) {
                timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
            }
        });
        timeLabel.text(yukon.timeFormatter.formatTime(defaultValue, 0));
    })();
&lt;/script&gt;
</pre>

<h2>Simple Slider allowing selection of a Range</h2>

<p class="description">
    Create a slider through the jquery slider function to allow the user to select a range.  Example: between 8:00AM - 5:00PM.
    You can set options such as the min/max values, default value and step value (how much to increment per step).  Setting range = true allows the user to select a range.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dib column-8-16 clearfix">
            <div class="column one">
                <div class="js-time-range-label fwb" style="width:150px;"></div>
            </div>
            <div class="column two nogutter">
                <div class="js-time-range-slider" style="margin-top: 5px;width:300px;"></div>
            </div>
        </div>
        <script>
        (function () {
            var timeLabel = $('.js-time-range-label'),
                timeSlider = $('.js-time-range-slider'),
                defaultStart = 480,
                defaultEnd = 1020;
            //initialize time slider
            timeSlider.slider({
                max: 24 * 60 - 60,
                min: 0,
                values: [defaultStart, defaultEnd],
                step: 30,
                range: true,
                slide: function (ev, ui) {
                    var start = yukon.timeFormatter.formatTime(ui.values[0], 0),
                        end = yukon.timeFormatter.formatTime(ui.values[1], 0);
                    timeLabel.text(start + ' - ' + end);
                },
                change: function (ev, ui) {
                    var start = yukon.timeFormatter.formatTime(ui.values[0], 0),
                        end = yukon.timeFormatter.formatTime(ui.values[1], 0);
                    timeLabel.text(start + ' - ' + end);
                }
            });
            var start = yukon.timeFormatter.formatTime(defaultStart, 0),
                end = yukon.timeFormatter.formatTime(defaultEnd, 0);
            timeLabel.text(start + ' - ' + end);
            timeSlider.find('.ui-slider-range').css({"background" : "#38c", "height" : "12px", "padding" : "0"});
        })();
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dib column-8-16 clearfix&quot;&gt;
    &lt;div class=&quot;column one&quot;&gt;
        &lt;div class=&quot;js-time-range-label fwb&quot; style=&quot;width:70px;&quot;&gt;&lt;/div&gt;
    &lt;/div&gt;
    &lt;div class=&quot;column two nogutter&quot;&gt;
        &lt;div class=&quot;js-time-range-slider&quot; style=&quot;margin-top: 5px;width:300px;&quot;&gt;&lt;/div&gt;
    &lt;/div&gt;
&lt;/div&gt;
&lt;script&gt;
    (function () {
        var timeLabel = $('.js-time-range-label'),
            timeSlider = $('.js-time-range-slider'),
            defaultStart = 480,
            defaultEnd = 1020;
        //initialize time slider
        timeSlider.slider({
            max: 24 * 60 - 60,
            min: 0,
            values: [defaultStart, defaultEnd],
            step: 30,
            range: true,
            slide: function (ev, ui) {
                var start = yukon.timeFormatter.formatTime(ui.values[0], 0),
                    end = yukon.timeFormatter.formatTime(ui.values[1], 0);
                timeLabel.text(start + ' - ' + end);
            },
            change: function (ev, ui) {
                var start = yukon.timeFormatter.formatTime(ui.values[0], 0),
                    end = yukon.timeFormatter.formatTime(ui.values[1], 0);
                timeLabel.text(start + ' - ' + end);
            }
        });
        var start = yukon.timeFormatter.formatTime(defaultStart, 0),
            end = yukon.timeFormatter.formatTime(defaultEnd, 0);
        timeLabel.text(start + ' - ' + end);
        timeSlider.find('.ui-slider-range').css({"background" : "#38c", "height" : "12px", "padding" : "0"});
    })();
&lt;/script&gt;
</pre>

</tags:styleguide>
</cti:standardPage>
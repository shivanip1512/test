<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="uiToolkitDemo">

<style>
.no_chrome {
    border: none;
    background-color: transparent;
}

.no_chrome:hover {
    background: #ff0000;
}

.icon {
    float: left;
    overflow: hidden;
    position: relative;
    width: 16px;
    height: 1px;
    padding-top: 16px;
}

.icon_remove {
    background: transparent url(WebConfig/yukon/Icons/error.gif) top left no-repeat;
}

table.example {
    border: solid 1px #ccc;
    margin: 20px auto;
    border-collapse:collapse;
    box-shadow: 2px 10px 10px rgba(0,0,0,0.3);
}

table.example td, table.example th {
    border: solid 1px #ccc;
    padding: 5px 10px;
    vertical-align:top;
}

table.example td {
    background: transparent url("data:image/jpeg;base64,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") top left;
}

table.example th { 
color: #FFF; 
text-shadow: 0 1px 3px #000; 
background: transparent url("data:image/jpeg;base64,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"); 
border: none;
}

button {
    cursor: pointer;
}

label {
float:left;
overflow: hidden;
display:block;
width: 6em;
}

.outline_red {
border: solid 1px red;
padding: 4px;
}

.white {
background-color: white;
padding: 5px;
}

.gradient {
background-color: -moz-linear-gradient(#aaa, #fff);
}

.f_tab {
border: solid 1px #ccc;
background-color: white;
}

</style>

<script>
document.observe("dom:loaded", function() {
    Event.observe(window, 'keyup', function(event) {
        if (event.keyCode == 27) {
            Yukon.ui.unblockPage();
        }
    });
    
    
    $$("button.blockElement").each(function(elem){
        elem.observe('click', function(event){
            var elem = event.element().up("div.blockThis");
            if(elem) {
                Yukon.uiUtils.elementGlass.show({element: elem, alpha: 0.5});
            }
        });
    });
});
</script>

    <table class="example">
    <tr>
        <th>Focus An Input on Page Load</th>
        <th>Relevant Markup</th>
    </tr>
    <tr>
        <td>
            <label>Something Important:</label><input type="text" name="important" class="f_focus"/>
        </td>
        <td>
            Add the <b>f_focus</b> class to the input.
        <br/>
        <br/>
            <pre>
&lt;input type="text" name="something" class="f_focus"/&gt;
            </pre>
        </td>
    </tr>
    <tr>
        <th>Blocking</th>
        <th>Relevant Markup</th>
    </tr>
    <tr>
        <td>
        <button name="button1" value="A" class="f_blocker">Block Page</button>
        <br/>
        <br/>
        <small>In this example, hit the 'ESC' key to unblock the page.</small>
        </td>
        <td>
        Add the <b>f_blocker</b> class to the tag/button.
        <br/>
        <br/>
        <pre>
&lt;button type="submit" name="button1" value="who cares?" class="f_blocker"&gt;standard&lt;/button&gt;
        </pre>
        <br/>
        <br/>
        To clear this blocked page you must create your own event handler as such:
        <br/>
        <br/>
        <pre>
$("myCloseButton").observe('click', function(){
    Yukon.ui.unblockPage();
});
        </pre>
        
    </td>
    </tr>
    <tr>
        <td>
        <div class="blockThis box" style="border: solid 1px #ccc; background: white; padding: 10px 20px;"/>
            <span class="info">In this example, clicking the block button will block only this white box.</span>
            Dynamic content is so cool.
            <br/>
            <br/>
            <button name="button2" value="B" class="blockElement">Block Element</button>
        </div>
        </td>
        <td>
        Here we block just a specific element on the page.  This functionality requires a bit of setup.
        <br/>You must explicitly tell the library to block and unblock and element.
        <br/>
        <br/>In this example, we have a click handler on the <em>Block Element</em> button. The
        <br/>function for the handler looks like this:
        <br/>
        <br/>
        <pre>
$("blockTheWhiteContainer").observe('click', function(elem){
    ...
    var elem = $(&lt;THE_ELEMENT_I_WANT_TO_BLOCK&gt;);
    ...        
    Yukon.ui.blockElement({element:elem, opacity:0.5});
    ...
});
        </pre>
        <br/>
        Similarly, we need to tell the library to unblock the element:
        <pre>
...
var elem = $(&lt;THE_SAME_ELEMENT_YOU_CHOOSE_TO_BLOCK&gt;);
...
Yukon.ui.unblockElement({element:elem});
...
        </pre>
        </td>
    </tr>
    <tr>
        <th>Phone Number Formatting</th>
        <th>Relevant Markup</th>
    </tr>
    <tr>
        <td>
            <label>Phone 1:</label><input type="text" name="phone1" class="f_formatPhone"/>
        </td>
        <td>
            Add the <b>f_formatPhone</b> class to the tag/button.
        <br/>
        <br/>
            <pre>
&lt;input type="text" name="phone1" class="f_formatPhone"/&gt;
            </pre>
        </td>
    </tr>
    <tr>
        <th>Enable/Disable Input Groups</th>
        <th>Relevant Markup</th>
    </tr>
    <tr>
        <td>
            <label for="enabled">Enabled?</label><input type="checkbox" name="enabled" class="f_toggle"/>
            <br/>
            <br/>
            <div class="f_toggle box">
                <label for="name">Name:</label><input type="text" name="name"/>
                <br/>
                <br/>
                <label for="group">Group:</label><select name="group">
                    <option>Select One</option>
                    <option>A</option>
                    <option>B</option>
                    <option>C</option>
                </select>
                <br/>
                <br/>
                <label for="cool">Cool?</label><input type="checkbox" name="cool">
                <br/>
                <br/>
                <label for="notes">Notes:</label><textarea name="notes"></textarea>
                <br/> 
                <br/>
                <button name="save">Save</button>
            </div>
            <br/>
            <br/>
            <label for="enabled2">Edit</label><input type="checkbox" name="enabled2" class="f_toggle" checked="checked"/>
            <br/>
            <br/>
            <div class="f_toggle box">
                <label for="address">Address:</label><input type="text" name="address"/>
                <br/>
                <br/>
                <label for="City">City:</label><input type="text" name="City"/>
                <br/>
                <br/>
                <button name="save">Save</button>
                <button name="save">Delete</button>
            </div>
        </td>
        <td>
        Works on page load and clicking a checkbox.
        <br/>
        Add the <b>f_toggle</b> class to the checkbox input.<br/>Then wrap the inputs you want to be toggled
        in a div with a <b>f_toggle</b> class.
        <br/>
                <br/>
        <pre>
&lt;input type="checkbox" name="enabled" class="f_toggle"/&gt;

&lt;div class="f_toggle"&gt;
    ...INPUTS HERE...
&lt;/div&gt;
        </pre>
        <br/>
        <br/>
        Will disable the following: <pre>
input
select
textarea
button
        </pre>
        </td>
    </tr>
    <tr>
        <th>Client Side Tabs</th>
        <th>Relevant Markup</th>
    </tr>
    <tr>
    <td style="width:300px;">
    
    <div class="box white">
        <ul class="f_tabs">
            <li>
                Tab 1
            </li>
            <li>
                Tab 2
            </li>
            <li>
                Tab 3
            </li>
            <li>
                Tab 4
            </li>
        </ul>
        
        <div class="f_tabbed">
            <div class="f_tab">
                The contents of Tab 1
                <br><br>
                Blah, blah, blah
            </div>
            
            <div class="f_tab">
                The contents of Tab 2 is better.
                <br><br>
                Foo, foo, foo
            </div>
            
            <div class="f_tab">
                The contents of Tab 3 is the best!
                
            </div>
        </div>
    </div>
    
    <br>
    <br>
    
    <div class="box gradient">
        <ul class="f_tabs">
            <li>
                Tab A
            </li>
            <li>
                Tab B
            </li>
            <li>
                Tab C
            </li>
        </ul>
        
        <div class="f_tabbed">
            <div class="outline_red" style="margin: 20px 0;">
                See, tabs can actually be somewhat disjointed!
            </div>
            <div class="f_tab">
                The contents of Tab A
                <br>
                <br>
                testing, 1,2...3
            </div>
            
            <div class="f_tab">
                The contents of Tab B is cool.
            </div>
            
            <div class="f_tab">
                The contents of Tab C is radical!
                <br>
                <br>
                C as in Cat, or Cake.
            </div>
        </div>
    </div>
    
    </td>
    <td>
     Works on page load.  By default the first tab will be selected with no effort from you.
        <br>
        <br>
        Your markup must be structured such that the tab controls and tabs container are siblings of each other.  The tab controls must look like such:
        <pre>
&lt;ul class="f_tabs"&gt;
    &lt;li&gt;Tab 1&lt;/li&gt;
    &lt;li&gt;Tab 2&lt;/li&gt;
    ...
    &lt;li&gt;Tab n&lt;/li&gt;
&lt;/ul&gt;

&lt;div class="f_tabbed"&gt;
    any ol' content - it won't be 'tabbed'

    &lt;div class="f_tab"&gt;
        Tab 1 contents
    &lt;/div&gt;
    
    &lt;div class="f_tab"&gt;
        Tab 2 contents
    &lt;/div&gt;
    
    ...
    
    &lt;div class="f_tab"&gt;
        Tab n contents
    &lt;/div&gt;
&lt;/div&gt;        
        </pre>
        
        The tab controls must be an unordered list with the <b>ul</b> taking the class name <b>f_tabs</b>.  The actual tabbed content need not be directly next to the tabs but must be in a container with the class name <b>f_tabbed</b>. The actual contents of each tab must be children of the <b>f_tabbed</b> container and themselves have a class of <b>f_tab</b>.
        <br>
        <br>
        Any out of bounds tab control will simply not function.  Similarly, any out of bounds tab content will never be displayed.
    </tr>
</table>
</cti:standardPage>
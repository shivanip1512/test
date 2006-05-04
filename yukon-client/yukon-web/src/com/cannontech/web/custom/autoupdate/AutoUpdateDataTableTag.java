/**
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cannontech.web.custom.autoupdate;

import javax.faces.component.UIComponent;

import org.apache.myfaces.taglib.html.ext.HtmlDataTableTag;

/**
 * @author J&ouml;rg Artaker
 * @author Thomas Huber
 * @version $Revision$ $Date$
 *          <p/>
 *          $Log$
 *          Revision 1.1  2006/05/04 18:15:08  tmack
 *          Adding a couple customer components copied directly out of the MyFaces Sandbox.
 *
 */
public class AutoUpdateDataTableTag extends HtmlDataTableTag{

    private String _frequency;

    /**
     * @return the _frequency String
     */
    public String getFrequency() {
        return _frequency;
    }

    /**
     * @param _frequency String
     */
    public void setFrequency(String _frequency) {
        this._frequency = _frequency;
    }

    /**
     * @return the ComponentType String
     */
    public String getComponentType() {
        return "org.apache.myfaces.AutoUpdateDataTable";
    }

    /**
     * @return the RendererType String
     */
    public String getRendererType() {
        return "org.apache.myfaces.AutoUpdateDataTable";
    }

    public void release() {
        super.release();
        _frequency = null;
    }

    /**
     * @param component UIComponent
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        setStringProperty(component, "frequency", _frequency);
    }
}

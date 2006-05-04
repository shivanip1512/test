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

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.myfaces.component.html.ext.HtmlDataTable;

import com.cannontech.web.custom.ajax.AjaxComponent;
import com.cannontech.web.custom.ajax.AjaxRenderer;

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
public class AutoUpdateDataTable extends HtmlDataTable implements AjaxComponent {

    private String _frequency;

    /**
     * @param context FacesContext
     * @param state Object
     */
    public void processRestoreState(FacesContext context, Object state) {
        super.processRestoreState(context, state);
    }

    /**
     * @param context FacesContext
     * @return Object
     */
    public Object processSaveState(FacesContext context) {
        return super.processSaveState(context);
    }

    /**
     * @param context FacesContext
     * @return the values Object[]
     */
    public Object saveState(FacesContext context) {
        Object[] values = new Object[2];
        values[0] = super.saveState(context);
        values[1] = _frequency;

        return values;
    }

    /**
     * @param context FacesContext
     * @param state Object
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _frequency = (String) values[1];
    }

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
     * @param context FacesContext
     * @throws java.io.IOException
     */
    public void encodeAjax(FacesContext context) throws IOException {
        if (context == null)
            throw new NullPointerException("context");
        if (!isRendered())
            return;
        Renderer renderer = getRenderer(context);

        if (isValidChildren()) {
            setPreservedDataModel(null);
        }

        if (renderer != null && renderer instanceof AjaxRenderer) {

            ((AjaxRenderer) renderer).encodeAjax(context, this);

        }
    }
}

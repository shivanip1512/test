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
package com.cannontech.web.custom.ajax;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.io.IOException;

/**
 * @author Martin Marinschek
 * @version $Revision$ $Date$
 *          <p/>
 *          $Log$
 *          Revision 1.1  2006/05/04 18:15:08  tmack
 *          Adding a couple customer components copied directly out of the MyFaces Sandbox.
 *
 */
public interface AjaxRenderer
{
    void encodeAjax(FacesContext context, UIComponent component) throws IOException;
}

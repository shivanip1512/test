/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.cannontech.web.layout.impl;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.servlet.ServletRequest;

public class HttpRequestElResolver extends ELResolver {

	public HttpRequestElResolver() {
		super();
	}

	public Object getValue(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			if (property != null) {
				String key = property.toString();
				ServletRequest request = (ServletRequest) context
						.getContext(ServletRequest.class);
				return request.getAttribute(key);
			}
		}

		return null;
	}

	public Class<Object> getType(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			return Object.class;
		}

		return null;
	}

	public void setValue(ELContext context, Object base, Object property,
			Object value) throws NullPointerException,
			PropertyNotFoundException, PropertyNotWritableException,
			ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			if (property != null) {
				String key = property.toString();
                ServletRequest request = (ServletRequest) context.getContext(ServletRequest.class);
                request.setAttribute(key, value);
			}
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
		}

		return false;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {

	    ServletRequest request = (ServletRequest) context.getContext(ServletRequest.class);
		List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
		Enumeration<?> e;
		Object value;
		String name;

		e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			name = (String) e.nextElement();
			value = request.getAttribute(name);
			FeatureDescriptor descriptor = new FeatureDescriptor();
			descriptor.setName(name);
			descriptor.setDisplayName(name);
			descriptor.setExpert(false);
			descriptor.setHidden(false);
			descriptor.setPreferred(true);
			descriptor.setShortDescription("page scoped attribute");
			descriptor.setValue("type", value.getClass());
			descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
			list.add(descriptor);
		}

		return list.iterator();
	}

	public Class<String> getCommonPropertyType(ELContext context, Object base) {
		if (base == null) {
			return String.class;
		}
		return null;
	}
}

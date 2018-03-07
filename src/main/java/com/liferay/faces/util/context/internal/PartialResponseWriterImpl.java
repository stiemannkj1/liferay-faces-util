/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.util.context.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;

import com.liferay.faces.util.client.Script;
import com.liferay.faces.util.client.ScriptsEncoder;
import com.liferay.faces.util.client.ScriptsEncoderFactory;
import com.liferay.faces.util.context.FacesRequestContext;
import com.liferay.faces.util.context.PartialResponseWriterWrapper;


/**
 * This class serves as a wrapper around the {@link PartialResponseWriter} that will encode JavaScript within an
 * <eval>...</eval> section just before the end of the partial-response document.
 *
 * @author  Kyle Stiemann
 */
/* package-private */ class PartialResponseWriterImpl extends PartialResponseWriterWrapper {

	// Private Data Members
	private boolean wroteEval;
	private boolean writingLink;
	private boolean writingScript;
	private Attribute javaScriptType;

	public PartialResponseWriterImpl(PartialResponseWriter partialResponseWriter) {
		super(partialResponseWriter);
	}

	@Override
	public Writer append(char c) throws IOException {

		writeTypeTextJavaScriptIfNecessary();

		return super.append(c);
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {

		writeTypeTextJavaScriptIfNecessary();

		return super.append(csq);
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {

		writeTypeTextJavaScriptIfNecessary();

		return super.append(csq, start, end);
	}

	@Override
	public void endCDATA() throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.endCDATA();
	}

	@Override
	public void endDocument() throws IOException {

		if (!wroteEval) {

			FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
			List<Script> scripts = facesRequestContext.getScripts();

			if (!scripts.isEmpty()) {

				super.startEval();

				FacesContext facesContext = FacesContext.getCurrentInstance();
				encodeScripts(facesContext, scripts);
				super.endEval();
			}
		}

		super.endDocument();
	}

	@Override
	public void endElement(String name) throws IOException {

		if ("script".equalsIgnoreCase(name)) {

			writeTypeTextJavaScriptIfNecessary();
			writingScript = false;
		}
		else if ("link".equalsIgnoreCase(name)) {
			writingLink = false;
		}

		super.endElement(name);
	}

	@Override
	public void endEval() throws IOException {

		FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
		List<Script> scripts = facesRequestContext.getScripts();

		if (!scripts.isEmpty()) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			encodeScripts(facesContext, scripts);
		}

		super.endEval();
		wroteEval = true;
	}

	@Override
	public void startCDATA() throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.startCDATA();
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		if ("script".equalsIgnoreCase(name)) {
			writingScript = true;
		}
		else if ("link".equalsIgnoreCase(name)) {
			writingLink = true;
		}

		super.startElement(name, component);
	}

	@Override
	public void write(String str) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.write(str);
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.write(cbuf);
	}

	@Override
	public void write(int c) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.write(c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.write(cbuf, off, len);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.write(str, off, len);
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		if (writingScript && "type".equalsIgnoreCase(name) && "text/javascript".equalsIgnoreCase((String) value)) {
			javaScriptType = new Attribute(name, value, property);
		}
		else {
			super.writeAttribute(name, value, property);
		}
	}

	@Override
	public void writeComment(Object comment) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.writeComment(comment);
	}

	@Override
	public void writeText(Object text, String property) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.writeText(text, property);
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.writeText(text, off, len);
	}

	@Override
	public void writeText(Object text, UIComponent component, String property) throws IOException {

		writeTypeTextJavaScriptIfNecessary();
		super.writeText(text, component, property);
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {

		// Workaround https://github.com/javaserverfaces/mojarra/issues/4345: JSF script and link resource urls
		// params are HTML escaped twice when added via Ajax. Also see https://issues.liferay.com/browse/FACES-1236.
		if (writingScript || writingLink) {

			Attribute javaScriptType = this.javaScriptType;

			// Other methods in this class such as write() call writeTypeTextJavaScriptIfNecessary(), so ensure that
			// writeTypeTextJavaScriptIfNecessary() is not called recursively when super.write() calls other methods
			// from this class.
			this.javaScriptType = null;

			super.write(" ");
			super.write(name);

			if (value != null) {

				super.write("=\"");
				super.write(value.toString());
				super.write("\"");
			}

			this.javaScriptType = javaScriptType;
		}
	}

	private void encodeScripts(FacesContext facesContext, List<Script> scripts) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ScriptsEncoder ScriptsEncoder = ScriptsEncoderFactory.getScriptsEncoderInstance(externalContext);
		ScriptsEncoder.encodeEvalScripts(facesContext, scripts);
	}

	/**
	 * Workaround https://github.com/javaserverfaces/mojarra/issues/4340: Script resources in
	 * <body>are never run during Ajax requests.
	 *
	 * @throws  IOException
	 */
	private void writeTypeTextJavaScriptIfNecessary() throws IOException {

		if (javaScriptType != null) {

			Attribute javaScriptType = this.javaScriptType;

			// Other methods in this class such as write() call writeTypeTextJavaScriptIfNecessary(), so ensure that
			// writeTypeTextJavaScriptIfNecessary() is not called recursively when super.writeAttribute() calls other
			// methods from this class.
			this.javaScriptType = null;
			super.writeAttribute(javaScriptType.name, javaScriptType.value, javaScriptType.property);
		}
	}

	private static final class Attribute {

		private final String name;
		private final Object value;
		private final String property;

		public Attribute(String name, Object value, String property) {
			this.name = name;
			this.value = value;
			this.property = property;
		}
	}
}

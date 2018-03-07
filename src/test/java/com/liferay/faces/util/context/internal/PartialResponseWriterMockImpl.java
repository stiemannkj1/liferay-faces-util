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
import java.io.StringWriter;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;


/**
 * @author  Kyle Stiemann
 */
public class PartialResponseWriterMockImpl extends PartialResponseWriter {

	// Private Data Members
	private StringWriter stringWriter;

	public PartialResponseWriterMockImpl(StringWriter stringWriter, ResponseWriter responseWriter) {
		super(null);
		this.stringWriter = stringWriter;
	}

	@Override
	public Writer append(char c) throws IOException {
		return this;
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		return this;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		return this;
	}

	@Override
	public ResponseWriter cloneWithWriter(Writer writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void endCDATA() throws IOException {
		// no-op
	}

	@Override
	public void endDocument() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void endElement(String name) throws IOException {
		// no-op
	}

	@Override
	public void flush() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseWriter getWrapped() {
		return this;
	}

	public void resetStringWriter() {
		stringWriter.getBuffer().setLength(0);
	}

	@Override
	public void startCDATA() throws IOException {
		// no-op
	}

	@Override
	public void startDocument() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		stringWriter.append("<");
		stringWriter.append(name);
	}

	@Override
	public void write(String str) throws IOException {
		stringWriter.write(str);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		// no-op
	}

	@Override
	public void write(int c) throws IOException {
		// no-op
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// no-op
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		// no-op
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		stringWriter.append(" ");
		stringWriter.append(name);
		stringWriter.append("=\"");
		stringWriter.append(escapeXML((String) value));
		stringWriter.append("\"");
	}

	@Override
	public void writeComment(Object comment) throws IOException {
		// no-op
	}

	@Override
	public void writeDoctype(String doctype) throws IOException {
		// no-op
	}

	@Override
	public void writePreamble(String preamble) throws IOException {
		// no-op
	}

	@Override
	public void writeText(Object text, String property) throws IOException {
		// no-op
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {
		// no-op
	}

	@Override
	public void writeText(Object text, UIComponent component, String property) throws IOException {
		// no-op
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {
		writeAttribute(name, value, property);
	}

	private String escapeXML(String string) {

		if (string == null) {
			return string;
		}

		//J-
		return string
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&apos;");
		//J+
	}
}

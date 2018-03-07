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

import javax.faces.context.ResponseWriter;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public class PartialResponserWriterTests {

	@Test
	public void testWorkaroundMojarra4340() throws IOException {

		StringWriter stringWriter = new StringWriter();
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.write("");
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.write("", 0, 0);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.write(new char[] {});
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.write(new char[] {}, 0, 0);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.write(Character.getNumericValue(' '));
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.append("");
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.append(' ');
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.append("", 0, 0);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.startCDATA();
					;
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.endCDATA();
					;
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.endElement("script");
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.writeText("", null);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.writeText("", null, null);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.writeText(new char[] {}, 0, 0);
				}
			});
		testWorkaroundMojarra4340(stringWriter, new ResponseWriterMethod() {

				@Override
				public void call(ResponseWriter responseWriter) throws IOException {
					responseWriter.writeComment("");
				}
			});
	}

	@Test
	public void testWorkaroundMojarra4345() throws IOException {

		StringWriter stringWriter = new StringWriter();
		ResponseWriter responseWriter = new PartialResponseWriterImpl(new PartialResponseWriterMockImpl(stringWriter,
					null));
		responseWriter.startElement("script", null);
		responseWriter.writeURIAttribute("src",
			"http://liferay.com/javax.faces.resource/example.js?ln=example&paramName=paramValue", null);
		responseWriter.writeAttribute("type", "text/javascript", null);
		Assert.assertFalse(stringWriter.toString().contains("&amp;"));

		// Reset the response writer's internal string writer.
		stringWriter.getBuffer().setLength(0);

		responseWriter.startElement("link", null);
		responseWriter.writeURIAttribute("href",
			"http://liferay.com/javax.faces.resource/example.css?ln=example&paramName=paramValue", null);
		responseWriter.writeAttribute("type", "text/css", null);
		responseWriter.writeAttribute("rel", "stylesheet", null);
		Assert.assertFalse(stringWriter.toString().contains("&amp;"));
	}

	private void testWorkaroundMojarra4340(StringWriter stringWriter, ResponseWriterMethod responseWriterMethod)
		throws IOException {

		ResponseWriter responseWriter = new PartialResponseWriterImpl(new PartialResponseWriterMockImpl(stringWriter,
					null));
		responseWriter.startElement("script", null);
		responseWriter.writeAttribute("type", "text/javascript", null);
		responseWriter.writeURIAttribute("src",
			"http://liferay.com/javax.faces.resource/example.js?ln=example&paramName=paramValue", null);
		responseWriterMethod.call(responseWriter);
		Assert.assertTrue(stringWriter.toString().endsWith("type=\"text/javascript\""));

		// Reset the response writer's internal string writer.
		stringWriter.getBuffer().setLength(0);
	}

	private static interface ResponseWriterMethod {
		public void call(ResponseWriter responseWriter) throws IOException;
	}
}

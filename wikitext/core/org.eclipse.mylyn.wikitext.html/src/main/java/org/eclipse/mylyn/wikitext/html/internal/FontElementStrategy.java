/*******************************************************************************
 * Copyright (c) 2014, 2021 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.css.CssParser;
import org.eclipse.mylyn.wikitext.parser.css.CssRule;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

public class FontElementStrategy extends SpanHtmlElementStrategy {

	private static final class FontElementMatcher implements ElementMatcher<SpanType> {
		@Override
		public boolean matches(SpanType elementType, Attributes attributes) {
			if (elementType == SpanType.SPAN) {
				String cssStyle = attributes.getCssStyle();
				if (cssStyle != null) {
					Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
					while (rules.hasNext()) {
						CssRule rule = rules.next();
						if (rule.name.equals("color") || rule.name.equals("font-size") //$NON-NLS-1$//$NON-NLS-2$
								|| rule.name.equals("font-family")) { //$NON-NLS-1$
							return true;
						}
					}
				}
			}
			return false;

		}
	}

	private static final class FontSpanStrategy implements SpanStrategy {
		private boolean elementOpened = false;

		@Override
		public void beginSpan(DocumentBuilder builder, SpanType type, Attributes attributes) {
			if (builder instanceof HtmlDocumentBuilder) {
				Map<String, String> fontAttributes = null;
				String cssStyle = attributes.getCssStyle();
				if (cssStyle != null) {
					fontAttributes = new TreeMap<>();

					Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
					while (rules.hasNext()) {
						CssRule rule = rules.next();
						if (rule.name.equals("color")) { //$NON-NLS-1$
							fontAttributes.put("color", rule.value); //$NON-NLS-1$
						} else if (rule.name.equals("font-size")) { //$NON-NLS-1$
							fontAttributes.put("size", rule.value); //$NON-NLS-1$
						} else if (rule.name.equals("font-family")) { //$NON-NLS-1$
							fontAttributes.put("face", rule.value); //$NON-NLS-1$
						}
					}
				}
				if (fontAttributes != null && !fontAttributes.isEmpty()) {
					elementOpened = true;

					HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
					XmlStreamWriter writer = htmlBuilder.getWriter();
					writer.writeStartElement(htmlBuilder.getHtmlNsUri(), "font"); //$NON-NLS-1$
					for (Entry<String, String> attribute : fontAttributes.entrySet()) {
						writer.writeAttribute(attribute.getKey(), attribute.getValue());
					}
				}
			} else {
				builder.beginSpan(type, attributes);
			}
		}

		@Override
		public void endSpan(DocumentBuilder builder) {
			if (builder instanceof HtmlDocumentBuilder) {
				if (elementOpened) {
					HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
					XmlStreamWriter writer = htmlBuilder.getWriter();
					writer.writeEndElement();
				}
			} else {
				builder.endSpan();
			}
		}
	}

	public FontElementStrategy() {
		super(new FontElementMatcher(), new FontSpanStrategy());
	}

	@Override
	public SpanStrategy spanStrategy() {
		return new FontSpanStrategy();
	}
}

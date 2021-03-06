/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A processor that is capable of processing a specific type of markup element
 *
 * @author David Green
 * @since 3.0
 */
public abstract class PatternBasedElementProcessor extends Processor
		implements org.eclipse.mylyn.wikitext.parser.util.Matcher {

	protected int lineStartOffset;

	protected int lineEndOffset;

	protected Map<Integer, Group> groupByIndex;

	private static class Group {
		private final String text;

		private final int start;

		private final int end;

		public Group(String text, int start, int end) {
			this.text = text;
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * Set the captured text for the given group.
	 *
	 * @param group
	 *            the 1-based group
	 * @param capturedText
	 *            the text that was captured
	 * @see #group(int)
	 */
	public void setGroup(int group, String capturedText, int start, int end) {
		if (groupByIndex == null) {
			groupByIndex = new HashMap<>();
		}
		groupByIndex.put(group, new Group(capturedText, start, end));
	}

	/**
	 * Get the offset within the line at which this element was started
	 *
	 * @see Matcher#start()
	 */
	public int getLineStartOffset() {
		return lineStartOffset;
	}

	public void setLineStartOffset(int lineStartOffset) {
		this.lineStartOffset = lineStartOffset;
	}

	/**
	 * Get the offset within the line at which this element ended
	 *
	 * @see Matcher#end()
	 */
	public int getLineEndOffset() {
		return lineEndOffset;
	}

	public void setLineEndOffset(int lineEndOffset) {
		this.lineEndOffset = lineEndOffset;
	}

	/**
	 * Get the capturing group text, or null if the group did not match any text.
	 *
	 * @param groupNumber
	 *            the 1-based group
	 * @return the text, or null if the group did not match any text
	 * @see Matcher#group(int)
	 */
	@Override
	public String group(int groupNumber) {
		if (groupByIndex == null) {
			return null;
		}
		Group group = groupByIndex.get(groupNumber);
		return group == null ? null : group.text;
	}

	/**
	 * Get the start offset of a capturing group, or -1 if the group did not match any text.
	 *
	 * @param groupNumber
	 *            the 1-based group
	 * @return the start offset, or -1 if the group did not match any text
	 * @see Matcher#start(int)
	 */
	@Override
	public int start(int groupNumber) {
		if (groupByIndex == null) {
			return -1;
		}
		Group group = groupByIndex.get(groupNumber);
		return group == null ? -1 : group.start;
	}

	/**
	 * Get the end offset of a capturing group, or -1 if the group did not match any text.
	 *
	 * @param groupNumber
	 *            the 1-based group
	 * @return the end offset, or -1 if the group did not match any text
	 * @see Matcher#start(int)
	 */
	@Override
	public int end(int groupNumber) {
		if (groupByIndex == null) {
			return -1;
		}
		Group group = groupByIndex.get(groupNumber);
		return group == null ? -1 : group.end;
	}

	/**
	 * Emit the content of the element
	 */
	public abstract void emit();
}

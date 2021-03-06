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

package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.Iterator;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

class Util {

	static boolean annotationsIncludeOffset(IAnnotationModel annotationModel, int offset) {
		if (annotationModel == null) {
			return false;
		}
		try {
			// eclipse 3.4
			Iterator<?> annotationIterator = (Iterator<?>) annotationModel.getClass()
					.getMethod("getAnnotationIterator", int.class, int.class, boolean.class, boolean.class) //$NON-NLS-1$
					.invoke(annotationModel, offset, 1, true, true);
			return annotationIterator.hasNext();
		} catch (Exception e) {
			// eclipse 3.3
			Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
			while (annotationIterator.hasNext()) {
				Position position = annotationModel.getPosition(annotationIterator.next());
				if (position != null && (position.offset == offset || position.includes(offset))) {
					return true;
				}
			}
			return false;
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.text;

import org.eclipse.jdt.internal.ui.text.JavaPairMatcher;
import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.sse.ui.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

public class JSPDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	protected final static char[] BRACKETS = {'{', '}', '(', ')', '[', ']'};

	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public JSPDocumentRegionEdgeMatcher() {
		super(new String[]{XMLRegionContext.XML_TAG_NAME, XMLRegionContext.XML_COMMENT_TEXT, DOMJSPRegionContexts.JSP_COMMENT_TEXT, DOMJSPRegionContexts.JSP_DIRECTIVE_NAME, DOMJSPRegionContexts.JSP_ROOT_TAG_NAME, XMLRegionContext.XML_CDATA_TEXT, XMLRegionContext.XML_PI_OPEN, XMLRegionContext.XML_PI_CONTENT}, new JavaPairMatcher(BRACKETS));
	}
}
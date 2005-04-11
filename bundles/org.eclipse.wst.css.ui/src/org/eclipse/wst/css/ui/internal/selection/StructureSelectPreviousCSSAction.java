/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.internal.selection;

import org.eclipse.jface.text.Region;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.ui.internal.CSSUIMessages;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.w3c.dom.Node;

public class StructureSelectPreviousCSSAction extends StructureSelectCSSAction {
	public StructureSelectPreviousCSSAction(StructuredTextEditor editor, SelectionHistory history) {
		super(editor, history);
		setText(CSSUIMessages.StructureSelectPrevious_label);
		setToolTipText(CSSUIMessages.StructureSelectPrevious_tooltip);
		setDescription(CSSUIMessages.StructureSelectPrevious_description);
	}

	protected IndexedRegion getCursorIndexedRegion() {
		return getIndexedRegion(fViewer.getSelectedRange().x);
	}

	protected Region getNewSelectionRegion(Node node, Region region) {
		return null;
	}

	protected Region getNewSelectionRegion(ICSSNode node, Region region) {
		Region newRegion = null;

		ICSSNode newNode = node.getPreviousSibling();
		if (newNode == null) {
			newNode = node.getParentNode();

			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(newIndexedRegion.getStartOffset(), newIndexedRegion.getEndOffset() - newIndexedRegion.getStartOffset());
			}
		} else {
			if (newNode instanceof IndexedRegion) {
				IndexedRegion newIndexedRegion = (IndexedRegion) newNode;
				newRegion = new Region(newIndexedRegion.getStartOffset(), region.getOffset() + region.getLength() - newIndexedRegion.getStartOffset());
			}
		}

		return newRegion;
	}
}
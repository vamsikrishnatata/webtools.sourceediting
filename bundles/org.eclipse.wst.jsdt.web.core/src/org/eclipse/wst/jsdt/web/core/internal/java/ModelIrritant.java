/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;

/**
 * @author childsb
 * 
 */
public class ModelIrritant implements IDocumentPartitioningListener {
	public ModelIrritant(IDocument attachedDoc) {}
	
	public void documentPartitioningChanged(IDocument document) {
		document.removeDocumentPartitioningListener(this);
		if (document instanceof BasicStructuredDocument) {
			try {
				((BasicStructuredDocument) document).replace(0, document.getLength(), document.get());
			} catch (BadLocationException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
// if (document == attachedDoc && document instanceof IDocumentExtension3) {
// IDocumentExtension3 extension3 = (IDocumentExtension3) document;
// String[] partitionings = extension3.getPartitionings();
// Vector partitioners = new Vector();
// for (int i = 0; i < partitionings.length; i++) {
// IDocumentPartitioner partitioner =
// extension3.getDocumentPartitioner(partitionings[i]);
//					
// if (partitioner instanceof StructuredTextPartitioner) {
// IDOMModel xmlModel = null;
// try {
// xmlModel = (IDOMModel)
// StructuredModelManager.getModelManager().getExistingModelForEdit(document);
//						
// xmlModel.getStructuredDocument().
// } catch (Exception e) {
// System.out.println("Exception in adapter for model re-init" + e);
// } finally {
// if (xmlModel != null) {
// document.removeDocumentPartitioningListener(this);
//								
// xmlModel.releaseFromEdit();
//								
//								
// }
// }
//						
// }
// }
// }
	}
}

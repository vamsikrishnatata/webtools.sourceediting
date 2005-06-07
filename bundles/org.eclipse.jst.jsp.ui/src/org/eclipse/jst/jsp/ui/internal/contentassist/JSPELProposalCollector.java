package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.swt.graphics.Image;

public class JSPELProposalCollector extends JSPProposalCollector {

	public JSPELProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		super(cu, translation);
	}

	protected IJavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal) {
		JSPCompletionProposal jspProposal = null;
		
		if(null == proposal || null == proposal.getName())
			return(null);
		
		String rawName = new String(proposal.getName());
		String completion = null;
		
		if(proposal.getKind() == CompletionProposal.METHOD_REF && proposal.findParameterNames(null).length == 0) {
			if(rawName.length() > 3 && rawName.startsWith("get")) { //$NON-NLS-1$
				completion = rawName.substring(3,4).toLowerCase() + rawName.substring(4, rawName.length());
			} else {
				return null;
			}
			
			// java offset
			int offset = proposal.getReplaceStart() + 1;
			
			// replacement length
			int length = proposal.getReplaceEnd() - offset + 1;
			
			// translate offset from Java > JSP
			offset = getTranslation().getJspOffset(offset);
			
			// cursor position after must be calculated
			int positionAfter = offset + completion.length();
				
			// from java proposal
			IJavaCompletionProposal javaProposal = super.createJavaCompletionProposal(proposal);
			Image image = null;
			String longDisplayString = javaProposal.getDisplayString();
			int fistSpaceIndex = longDisplayString.indexOf(' ');
			String shortDisplayString = longDisplayString;
			
			if(fistSpaceIndex != -1) {
				shortDisplayString = longDisplayString.substring(fistSpaceIndex);
			}
				
			String displayString = completion + " " + shortDisplayString; //$NON-NLS-1$
			IContextInformation contextInformation = javaProposal.getContextInformation();
			String additionalInfo = javaProposal.getAdditionalProposalInfo();
			int relevance = javaProposal.getRelevance();
			
			boolean updateLengthOnValidate = true;
			
			jspProposal = new JSPCompletionProposal(completion, offset, length, positionAfter, image, displayString, contextInformation, additionalInfo, relevance, updateLengthOnValidate);
			return jspProposal;
		} else {
			return null;
		}
	}

//	protected void acceptMethod(char[] declaringTypePackageName, char[] declaringTypeName, char[] name, char[][] parameterPackageNames, char[][] parameterTypeNames, char[][] parameterNames, char[] returnTypePackageName, char[] returnTypeName, char[] completionName, int modifiers, int start, int end, int relevance) {
//		String rawName = String.valueOf(name);
//		if(parameterNames.length == 0 && rawName.length() > 3 && rawName.startsWith("get"))
//		{
//			String mangledName = rawName.substring(3,4).toLowerCase() + rawName.substring(4, rawName.length());
//			super.acceptField(declaringTypePackageName, declaringTypeName, mangledName.toCharArray(), returnTypePackageName, returnTypeName, mangledName.toCharArray(), modifiers, start, end, relevance);
//		}
//	}
}

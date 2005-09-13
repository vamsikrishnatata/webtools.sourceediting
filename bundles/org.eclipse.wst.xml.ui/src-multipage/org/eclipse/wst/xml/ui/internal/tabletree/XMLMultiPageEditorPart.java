/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.common.ui.provisional.editors.PostMultiPageEditorSite;
import org.eclipse.wst.common.ui.provisional.editors.PostMultiPageSelectionProvider;
import org.eclipse.wst.common.ui.provisional.editors.PostSelectionMultiPageEditorPart;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.IXMLPreferenceNames;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;

public class XMLMultiPageEditorPart extends PostSelectionMultiPageEditorPart {

	/**
	 * Internal part activation listener, copied from AbstractTextEditor
	 */
	class ActivationListener implements IPartListener, IWindowListener {

		/** Cache of the active workbench part. */
		private IWorkbenchPart fActivePart;
		/** Indicates whether activation handling is currently be done. */
		private boolean fIsHandlingActivation = false;
		/**
		 * The part service.
		 * 
		 * @since 3.1
		 */
		private IPartService fPartService;

		/**
		 * Creates this activation listener.
		 * 
		 * @param partService
		 *            the part service on which to add the part listener
		 * @since 3.1
		 */
		public ActivationListener(IPartService partService) {
			fPartService = partService;
			fPartService.addPartListener(this);
			PlatformUI.getWorkbench().addWindowListener(this);
		}

		/**
		 * Disposes this activation listener.
		 * 
		 * @since 3.1
		 */
		public void dispose() {
			fPartService.removePartListener(this);
			PlatformUI.getWorkbench().removeWindowListener(this);
			fPartService = null;
		}

		/*
		 * @see IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			fActivePart = part;
			handleActivation();
		}

		/*
		 * @see IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		/*
		 * @see IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
		}

		/*
		 * @see IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			fActivePart = null;
		}

		/*
		 * @see IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
		}

		/**
		 * Handles the activation triggering a element state check in the
		 * editor.
		 */
		private void handleActivation() {
			if (fIsHandlingActivation)
				return;

			if (fActivePart == XMLMultiPageEditorPart.this) {
				fIsHandlingActivation = true;
				try {
					getTextEditor().safelySanityCheckState(getEditorInput());
				}
				finally {
					fIsHandlingActivation = false;
				}
			}
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowActivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow()) {
				/*
				 * Workaround for problem described in
				 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11731 Will be
				 * removed when SWT has solved the problem.
				 */
				window.getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						handleActivation();
					}
				});
			}
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowClosed(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowOpened(IWorkbenchWindow window) {
		}
	}

	/**
	 * Listens for selection from the source page, applying it to the design
	 * viewer.
	 */
	private class TextEditorPostSelectionAdapter extends UIJob implements ISelectionChangedListener {
		boolean forcePostSelection = false;
		ISelection selection = null;

		public TextEditorPostSelectionAdapter() {
			super(getTitle());
			setUser(true);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (selection != null) {
				fDesignViewer.getSelectionProvider().setSelection(selection);
			}
			return Status.OK_STATUS;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			if (fDesignViewer != null && !fDesignViewer.getControl().isFocusControl()) {
				if (forcePostSelection) {
					selection = event.getSelection();
					schedule(200);
				}
				else {
					fDesignViewer.getSelectionProvider().setSelection(event.getSelection());
				}
			}
		}
	}

	/**
	 * Internal IPropertyListener
	 */
	class PropertyListener implements IPropertyListener {
		public void propertyChanged(Object source, int propId) {
			switch (propId) {
				// had to implement input changed "listener" so that
				// StructuredTextEditor could tell it containing editor that
				// the input has change, when a 'resource moved' event is
				// found.
				case IEditorPart.PROP_INPUT :
				case IEditorPart.PROP_DIRTY : {
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
							/*
							 * title should always change when input changes.
							 * create runnable for following post call
							 */
							Runnable runnable = new Runnable() {
								public void run() {
									_firePropertyChange(IWorkbenchPart.PROP_TITLE);
								}
							};
							/*
							 * Update is just to post things on the display
							 * queue (thread). We have to do this to get the
							 * dirty property to get updated after other
							 * things on the queue are executed.
							 */
							((Control) getTextEditor().getAdapter(Control.class)).getDisplay().asyncExec(runnable);
						}
					}
					break;
				}
				case IWorkbenchPart.PROP_TITLE : {
					// update the input if the title is changed
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
						}
					}
					break;
				}
				default : {
					// propagate changes. Is this needed? Answer: Yes.
					if (source == getTextEditor()) {
						_firePropertyChange(propId);
					}
					break;
				}
			}

		}
	}

	class TextInputListener implements ITextInputListener {
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (fDesignViewer != null && newInput != null)
				fDesignViewer.setDocument(newInput);
		}
	}

	/** The design page index. */
	private int fDesignPageIndex;

	/** The design viewer */
	IDesignViewer fDesignViewer;

	private ActivationListener fActivationListener;

	IPropertyListener fPropertyListener = null;

	/** The source page index. */
	private int fSourcePageIndex;

	/** The text editor. */
	private StructuredTextEditor fTextEditor;

	private TextEditorPostSelectionAdapter fTextEditorSelectionListener;

	/**
	 * StructuredTextMultiPageEditorPart constructor comment.
	 */
	public XMLMultiPageEditorPart() {
		super();
	}

	/*
	 * This method is just to make firePropertyChanged accessible from some
	 * (anonomous) inner classes.
	 */
	void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	/**
	 * Adds the source page of the multi-page editor.
	 */
	private void addSourcePage() throws PartInitException {
		fSourcePageIndex = addPage(fTextEditor, getEditorInput());
		setPageText(fSourcePageIndex, XMLEditorMessages.XMLMultiPageEditorPart_0);
		// the update's critical, to get viewer selection manager and
		// highlighting to work
		fTextEditor.update();

		firePropertyChange(PROP_TITLE);

		// Changes to the Text Viewer's document instance should also
		// force an
		// input refresh
		fTextEditor.getTextViewer().addTextInputListener(new TextInputListener());
	}

	/**
	 * Connects the design viewer with the viewer selection manager. Should be
	 * done after createSourcePage() is done because we need to get the
	 * ViewerSelectionManager from the TextEditor. setModel is also done here
	 * because getModel() needs to reference the TextEditor.
	 */
	private void connectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setDocument(getDocument());
		}

		/*
		 * Connect selection from the design viewer to the selection provider
		 * for the XMLMultiPageEditorPart so that selection in the design
		 * viewer will propogate across the workbench.
		 */
		if (fDesignViewer.getSelectionProvider() instanceof IPostSelectionProvider) {
			((IPostSelectionProvider) fDesignViewer.getSelectionProvider()).addPostSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					((PostMultiPageSelectionProvider) getSite().getSelectionProvider()).firePostSelectionChanged(event);
				}
			});
		}
		fDesignViewer.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				((MultiPageSelectionProvider) getSite().getSelectionProvider()).fireSelectionChanged(event);
			}
		});

		/*
		 * Connect selection from the design viewer to the selection provider
		 * of the source page so that selection in the design viewer will be
		 * applied to the source page. Prefer post selection.
		 */
		if (fDesignViewer.getSelectionProvider() instanceof IPostSelectionProvider) {
			((IPostSelectionProvider) fDesignViewer.getSelectionProvider()).addPostSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					getTextEditor().getSelectionProvider().setSelection(event.getSelection());
				}
			});
		}
		else {
			fDesignViewer.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					getTextEditor().getSelectionProvider().setSelection(event.getSelection());
				}
			});
		}

		/**
		 * Drive the design viewer from selection in the source page
		 */
		ISelectionProvider provider = getTextEditor().getSelectionProvider();
		if (fTextEditorSelectionListener == null) {
			fTextEditorSelectionListener = new TextEditorPostSelectionAdapter();
		}
		if (provider instanceof IPostSelectionProvider) {
			fTextEditorSelectionListener.forcePostSelection = false;
			((IPostSelectionProvider) provider).addPostSelectionChangedListener(fTextEditorSelectionListener);
		}
		else {
			fTextEditorSelectionListener.forcePostSelection = true;
			provider.addSelectionChangedListener(fTextEditorSelectionListener);
		}
	}

	/**
	 * Create and Add the Design Page using a registered factory
	 * 
	 */
	private void createAndAddDesignPage() {
		IDesignViewer designViewer = createDesignPage();

		fDesignViewer = designViewer;
		// note: By adding the design page as a Control instead of an
		// IEditorPart, page switches will indicate
		// a "null" active editor when the design page is made active
		fDesignPageIndex = addPage(designViewer.getControl());
		setPageText(fDesignPageIndex, designViewer.getTitle());
	}

	protected IDesignViewer createDesignPage() {
		XMLTableTreeViewer tableTreeViewer = new XMLTableTreeViewer(getContainer());
		// Set the default infopop for XML design viewer.
		XMLUIPlugin.getInstance().getWorkbench().getHelpSystem().setHelp(tableTreeViewer.getControl(), XMLTableTreeHelpContextIds.XML_DESIGN_VIEW_HELPID);
		return tableTreeViewer;
	}

	/**
	 * Creates the pages of this multi-page editor.
	 * <p>
	 * Subclasses of <code>MultiPageEditor</code> must implement this
	 * method.
	 * </p>
	 */
	protected void createPages() {
		try {
			// source page MUST be created before design page, now
			createSourcePage();

			createAndAddDesignPage();
			addSourcePage();
			connectDesignPage();

			int activePageIndex = getPreferenceStore().getInt(IXMLPreferenceNames.LAST_ACTIVE_PAGE);
			if (activePageIndex >= 0 && activePageIndex < getPageCount()) {
				setActivePage(activePageIndex);
			}
			else {
				setActivePage(fSourcePageIndex);
			}
		}
		catch (PartInitException e) {
			Logger.logException(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 */
	protected IEditorSite createSite(IEditorPart editor) {
		IEditorSite site = null;
		if (editor == fTextEditor) {
			site = new PostMultiPageEditorSite(this, editor) {
				/**
				 * @see org.eclipse.ui.part.MultiPageEditorSite#getActionBarContributor()
				 */
				public IEditorActionBarContributor getActionBarContributor() {
					IEditorActionBarContributor contributor = super.getActionBarContributor();
					IEditorActionBarContributor multiContributor = XMLMultiPageEditorPart.this.getEditorSite().getActionBarContributor();
					if (multiContributor instanceof XMLMultiPageEditorActionBarContributor) {
						contributor = ((XMLMultiPageEditorActionBarContributor) multiContributor).sourceViewerActionContributor;
					}
					return contributor;
				}
			};
		}
		else {
			site = super.createSite(editor);
		}
		return site;
	}

	/**
	 * Creates the source page of the multi-page editor.
	 */
	protected void createSourcePage() throws PartInitException {
		fTextEditor = createTextEditor();
		fTextEditor.setEditorPart(this);

		if (fPropertyListener == null) {
			fPropertyListener = new PropertyListener();
		}
		fTextEditor.addPropertyListener(fPropertyListener);
	}

	/**
	 * Method createTextEditor.
	 * 
	 * @return StructuredTextEditor
	 */
	private StructuredTextEditor createTextEditor() {
		return new StructuredTextEditorXML();
	}

	private void disconnectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setDocument(null);
		}
	}

	public void dispose() {
		Logger.trace("Source Editor", "XMLMultiPageEditorPart::dispose entry"); //$NON-NLS-1$ //$NON-NLS-2$

		disconnectDesignPage();

		if (fActivationListener != null) {
			fActivationListener.dispose();
			fActivationListener = null;
		}

		if (fTextEditor != null && fPropertyListener != null) {
			fTextEditor.removePropertyListener(fPropertyListener);
		}

		// moved to last when added window ... seems like
		// we'd be in danger of losing some data, like site,
		// or something.
		super.dispose();

		Logger.trace("Source Editor", "StructuredTextMultiPageEditorPart::dispose exit"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		fTextEditor.doSave(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		fTextEditor.doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		Object result = null;
		if (key == IDesignViewer.class) {
			result = fDesignViewer;

		}
		else if (key.equals(IGotoMarker.class)) {
			result = new IGotoMarker() {
				public void gotoMarker(IMarker marker) {
					XMLMultiPageEditorPart.this.gotoMarker(marker);
				}
			};
		}
		else {
			// DMW: I'm bullet-proofing this because
			// its been reported (on IBM WSAD 4.03 version) a null pointer
			// sometimes
			// happens here on startup, when an editor has been left
			// open when workbench shutdown.
			if (fTextEditor != null) {
				result = fTextEditor.getAdapter(key);
			}
		}
		return result;
	}

	private IDocument getDocument() {
		IDocument document = null;
		if (fTextEditor != null)
			document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput());
		return document;
	}

	private IPreferenceStore getPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}

	StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#getTitle()
	 */
	public String getTitle() {
		String title = null;
		if (getTextEditor() == null) {
			if (getEditorInput() != null) {
				title = getEditorInput().getName();
			}
		}
		else {
			title = getTextEditor().getTitle();
		}
		if (title == null) {
			title = getPartName();
		}
		return title;
	}

	void gotoMarker(IMarker marker) {
		setActivePage(fSourcePageIndex);
		IDE.gotoMarker(fTextEditor, marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		try {
			super.init(site, input);
			site.setSelectionProvider(new PostMultiPageSelectionProvider(this));
			// we want to listen for our own activation
			fActivationListener = new ActivationListener(site.getWorkbenchWindow().getPartService());
		}
		catch (Exception e) {
			Logger.logException("exception initializing " + getClass().getName(), e);
		}
		setPartName(input.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return fTextEditor != null && fTextEditor.isSaveAsAllowed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		// overriding super class since it does a lowly isDirty!
		if (fTextEditor != null)
			return fTextEditor.isSaveOnCloseNeeded();
		return isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		saveLastActivePageIndex(newPageIndex);

		IEditorPart newlyActiveEditor = getEditor(newPageIndex);
		if (newPageIndex == fDesignPageIndex) {
			ISelectionProvider selectionProvider = fDesignViewer.getSelectionProvider();
			if (selectionProvider != null) {
				SelectionChangedEvent event = new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection());
				((MultiPageSelectionProvider) getSite().getSelectionProvider()).fireSelectionChanged(event);
				((PostMultiPageSelectionProvider) getSite().getSelectionProvider()).firePostSelectionChanged(event);
			}
		}
	}

	void safelySanityCheckState() {
		// If we're called before editor is created, simply ignore since we
		// delegate this function to our embedded TextEditor
		if (getTextEditor() != null) {
			getTextEditor().safelySanityCheckState(getEditorInput());
		}
	}

	private void saveLastActivePageIndex(int newPageIndex) {
		// save the last active page index to preference manager
		getPreferenceStore().setValue(IXMLPreferenceNames.LAST_ACTIVE_PAGE, newPageIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		// If driven from the Source page, it's "model" may not be up to date
		// with the input just yet. We'll rely on later notification from the
		// TextViewer to set us straight
		super.setInput(input);
		if (fDesignViewer != null)
			fDesignViewer.setDocument(getDocument());
		setPartName(input.getName());
	}
}
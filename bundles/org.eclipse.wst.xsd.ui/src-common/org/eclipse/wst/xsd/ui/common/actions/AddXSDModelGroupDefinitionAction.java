/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.common.commands.AddXSDModelGroupDefinitionCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class AddXSDModelGroupDefinitionAction extends XSDBaseAction
{
  public static final String MODELGROUPDEFINITION_ID = "AddXSDModelGroupDefinitionAction";
  public static final String MODELGROUPDEFINITIONREF_ID = "AddXSDModelGroupDefinitionRefAction";
  boolean isReference;

  public AddXSDModelGroupDefinitionAction(IWorkbenchPart part, boolean isReference)
  {
    super(part);
    this.isReference = isReference;
    if (isReference)
    {
      setText("Add Group Ref");
      setId(MODELGROUPDEFINITION_ID);
    }
    else
    {
      setText("Add Group");
      setId(MODELGROUPDEFINITIONREF_ID);
    }
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
    XSDConcreteComponent xsdConcreteComponent = null;
    if (selection instanceof XSDBaseAdapter)
    {
      xsdConcreteComponent = (XSDConcreteComponent) ((XSDBaseAdapter) selection).getTarget();
    }
    if (xsdConcreteComponent != null)
    {
      AddXSDModelGroupDefinitionCommand command = new AddXSDModelGroupDefinitionCommand(getText(), xsdConcreteComponent, isReference);
      getCommandStack().execute(command);
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(command.getAddedComponent());
      if (adapter != null)
        provider.setSelection(new StructuredSelection(adapter));
    }
  }
}

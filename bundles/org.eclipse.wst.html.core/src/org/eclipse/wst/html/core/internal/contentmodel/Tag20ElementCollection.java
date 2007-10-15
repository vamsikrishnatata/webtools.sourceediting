/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.lang.reflect.Field;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations of JSP 2.0 Tag files.
 */
final class Tag20ElementCollection extends JSPElementCollection implements JSP20Namespace.ElementName {
	// element IDs
	private static class TagIds20 {
		public static final int ID_SCRIPTLET = 0;
		public static final int ID_EXPRESSION = 1;
		public static final int ID_DECLARATION = 2;
		public static final int ID_DIRECTIVE_TAG = 3;
		public static final int ID_DIRECTIVE_INCLUDE = 4;
		public static final int ID_DIRECTIVE_TAGLIB = 5;
		public static final int ID_USEBEAN = 6;
		public static final int ID_SETPROPERTY = 7;
		public static final int ID_GETPROPERTY = 8;
		public static final int ID_INCLUDE = 9;
		public static final int ID_FORWARD = 10;
		public static final int ID_PLUGIN = 11;
		public static final int ID_PARAMS = 12;
		public static final int ID_FALLBACK = 13;
		public static final int ID_PARAM = 14;
		public static final int ID_ROOT = 15;
		public static final int ID_TEXT = 16;
		public static final int ID_DIRECTIVE_VARIABLE = 17;
		public static final int ID_DIRECTIVE_ATTRIBUTE = 18;
		public static final int ID_BODY = 19;
		public static final int ID_ATTRIBUTE = 20;
		public static final int ID_ELEMENT = 21;
		public static final int ID_DOBODY = 22;
		public static final int ID_INVOKE = 23;
		public static final int ID_OUTPUT = 24;

		// chache the result of the reflection.
		private static int numofids = -1;

		public static int getNumOfIds() {
			if (numofids != -1)
				return numofids;

			// NOTE: If the reflection is too slow, this method should
			// just return the literal value, like 105.
			// -- 5/25/2001
			Class clazz = TagIds20.class;
			Field[] fields = clazz.getFields();
			numofids = 0;
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				if (name.startsWith("ID_"))//$NON-NLS-1$
					numofids++;
			}
			return numofids;
		}
	}

	// attribute creater
	private class TACreater20 extends JACreater {

		public TACreater20() {
			super();
		}

		private void createForAttribute() {
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_TRIM);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_TRIM, adec);
		}

		private void createForBody() {
		}

		private void createForDirAttribute() {
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_REQUIRED);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_REQUIRED, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_FRAGMENT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_FRAGMENT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_RTEXPRVALUE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_RTEXPRVALUE, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_TYPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_TYPE, adec);

			adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DESCRIPTION);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DESCRIPTION, adec);
		}

		private void createForDirTag() {
			// ("import" URI optional)
			AttrDecl adec = new AttrDecl(ATTR_NAME_IMPORT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(ATTR_NAME_IMPORT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DISPLAY_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ID);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DISPLAY_NAME, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_BODY_CONTENT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {JSP20Namespace.ATTR_VALUE_EMPTY, JSP20Namespace.ATTR_VALUE_TAGDEPENDENT, JSP20Namespace.ATTR_VALUE_SCRIPTLESS};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_BODY_CONTENT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_SMALL_ICON);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_SMALL_ICON, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_LARGE_ICON);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_LARGE_ICON, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DESCRIPTION);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DESCRIPTION, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_EXAMPLE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_EXAMPLE, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_LANGUAGE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_LANGUAGE, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_IMPORT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_IMPORT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_PAGEENCODING);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_PAGEENCODING, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_ISELIGNORED);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_ISELIGNORED, adec);
		}

		/**
		 * Changed in 2.0 (tagdir added)
		 */
		void createForDirTaglib() {
			// ("uri" URI OPTIONAL)
			AttrDecl adec = new AttrDecl(ATTR_NAME_URI);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(ATTR_NAME_URI, adec);

			// ("tagdir" URI OPTIONAL)
			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_TAGDIR);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_TAGDIR, adec);

			// ("prefix" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PREFIX);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PREFIX, adec);
		}

		private void createForDirVariable() {
			AttrDecl adec = new AttrDecl(JSP20Namespace.ATTR_NAME_NAME_GIVEN);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_NAME_GIVEN, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_NAME_FROM_ATTRIBUTE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_NAME_FROM_ATTRIBUTE, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_ALIAS);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_ALIAS, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_VARIABLE_CLASS);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_VARIABLE_CLASS, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DECLARE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DECLARE, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DESCRIPTION);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DESCRIPTION, adec);

			adec = new AttrDecl(JSP11Namespace.ATTR_NAME_SCOPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			values = new String[]{JSP20Namespace.ATTR_VALUE_SCOPE_NESTED, JSP20Namespace.ATTR_VALUE_SCOPE_AT_BEGIN, JSP20Namespace.ATTR_VALUE_SCOPE_AT_END};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_SCOPE_NESTED);
			declarations.putNamedItem(JSP11Namespace.ATTR_NAME_SCOPE, adec);
		}

		private void createForDoBody() {
			AttrDecl adec = new AttrDecl(JSP20Namespace.ATTR_NAME_VAR);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_VAR, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_VARREADER);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_VARREADER, adec);

			adec = new AttrDecl(JSP11Namespace.ATTR_NAME_SCOPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_PAGE, ATTR_VALUE_REQUEST, ATTR_VALUE_SESSION, ATTR_VALUE_APPLICATION};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP11Namespace.ATTR_NAME_SCOPE, adec);
		}

		private void createForElement() {
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);
		}

		private void createForInvoke() {
			AttrDecl adec = new AttrDecl(JSP20Namespace.ATTR_NAME_FRAGMENT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_FRAGMENT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_VAR);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_VAR, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_VARREADER);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_VARREADER, adec);

			adec = new AttrDecl(JSP11Namespace.ATTR_NAME_SCOPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_PAGE, ATTR_VALUE_REQUEST, ATTR_VALUE_SESSION, ATTR_VALUE_APPLICATION};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP11Namespace.ATTR_NAME_SCOPE, adec);
		}

		private void createForOutput() {
			AttrDecl adec = new AttrDecl(JSP20Namespace.ElementName.OUTPUT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE, JSP20Namespace.ATTR_VALUE_YES, JSP20Namespace.ATTR_VALUE_NO};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_NO);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ElementName.OUTPUT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC, adec);
		}

		public CMNamedNodeMapImpl getDeclarations(int eid) {
			switch (eid) {
				case TagIds20.ID_DIRECTIVE_TAG :
					createForDirTag();
					break;
				case TagIds20.ID_DIRECTIVE_VARIABLE :
					createForDirVariable();
					break;
				case TagIds20.ID_DIRECTIVE_ATTRIBUTE :
					createForDirAttribute();
					break;
				case TagIds20.ID_DIRECTIVE_TAGLIB :
					createForDirTaglib();
					break;
				case TagIds20.ID_ATTRIBUTE :
					createForAttribute();
					break;
				case TagIds20.ID_ELEMENT :
					createForElement();
					break;
				case TagIds20.ID_BODY :
					createForBody();
					break;
				case TagIds20.ID_DOBODY :
					createForDoBody();
					break;
				case TagIds20.ID_INVOKE :
					createForInvoke();
					break;
				case TagIds20.ID_OUTPUT :
					createForOutput();
					break;
				default :
					super.getDeclarations(eid);
					break;
			}
			return declarations;
		}
	}

	private static String[] names = null;

	static {
		names = new String[TagIds20.getNumOfIds()];
		names[Ids.ID_SCRIPTLET] = JSP11Namespace.ElementName.SCRIPTLET;
		names[Ids.ID_EXPRESSION] = JSP11Namespace.ElementName.EXPRESSION;
		names[Ids.ID_DECLARATION] = JSP11Namespace.ElementName.DECLARATION;
		names[TagIds20.ID_DIRECTIVE_TAG] = JSP20Namespace.ElementName.DIRECTIVE_TAG;
		names[Ids.ID_DIRECTIVE_INCLUDE] = JSP11Namespace.ElementName.DIRECTIVE_INCLUDE;
		names[Ids.ID_DIRECTIVE_TAGLIB] = JSP11Namespace.ElementName.DIRECTIVE_TAGLIB;
		names[Ids.ID_USEBEAN] = JSP11Namespace.ElementName.USEBEAN;
		names[Ids.ID_SETPROPERTY] = JSP11Namespace.ElementName.SETPROPERTY;
		names[Ids.ID_GETPROPERTY] = JSP11Namespace.ElementName.GETPROPERTY;
		names[Ids.ID_INCLUDE] = JSP11Namespace.ElementName.INCLUDE;
		names[Ids.ID_FORWARD] = JSP11Namespace.ElementName.FORWARD;
		names[Ids.ID_PLUGIN] = JSP11Namespace.ElementName.PLUGIN;
		names[Ids.ID_PARAMS] = JSP11Namespace.ElementName.PARAMS;
		names[Ids.ID_FALLBACK] = JSP11Namespace.ElementName.FALLBACK;
		names[Ids.ID_PARAM] = JSP11Namespace.ElementName.PARAM;
		names[Ids.ID_ROOT] = JSP11Namespace.ElementName.ROOT;
		names[Ids.ID_TEXT] = JSP11Namespace.ElementName.TEXT;
		names[TagIds20.ID_DIRECTIVE_VARIABLE] = JSP20Namespace.ElementName.DIRECTIVE_VARIABLE;
		names[TagIds20.ID_DIRECTIVE_ATTRIBUTE] = JSP20Namespace.ElementName.DIRECTIVE_ATTRIBUTE;
		names[TagIds20.ID_BODY] = JSP20Namespace.ElementName.BODY;
		names[TagIds20.ID_ATTRIBUTE] = JSP20Namespace.ElementName.ATTRIBUTE;
		names[TagIds20.ID_ELEMENT] = JSP20Namespace.ElementName.ELEMENT;
		names[TagIds20.ID_DOBODY] = JSP20Namespace.ElementName.DOBODY;
		names[TagIds20.ID_INVOKE] = JSP20Namespace.ElementName.INVOKE;
		names[TagIds20.ID_OUTPUT] = JSP20Namespace.ElementName.OUTPUT;
	}

	/**
	 */
	public Tag20ElementCollection() {
		super(Tag20ElementCollection.names, TOLERANT_CASE);
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 * @param elementName
	 *            java.lang.String
	 */
	protected CMNode create(String elementName) {
		return createElemDecl(getID(elementName));
	}

	/**
	 * @param eid
	 *            int
	 */
	CMGroupImpl createContent(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		CMGroupImpl content = null;
		CMNode child = null;

		switch (eid) {
			case Ids.ID_ROOT :
				content = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
				int validChildren[] = {
				// %Directives;
							Ids.ID_TEXT, Ids.ID_DIRECTIVE_PAGE, Ids.ID_DIRECTIVE_INCLUDE, TagIds20.ID_DIRECTIVE_TAG, TagIds20.ID_DIRECTIVE_VARIABLE, TagIds20.ID_DIRECTIVE_ATTRIBUTE, TagIds20.ID_BODY, TagIds20.ID_ATTRIBUTE,
							// %Scripts;
							Ids.ID_SCRIPTLET, Ids.ID_DECLARATION, Ids.ID_EXPRESSION,
							// %Actions;
							Ids.ID_USEBEAN, Ids.ID_SETPROPERTY, Ids.ID_GETPROPERTY, Ids.ID_INCLUDE, Ids.ID_FORWARD, Ids.ID_PLUGIN};
				for (int i = 0; i < validChildren.length; i++) {
					child = item(validChildren[i]);
					if (child != null)
						content.appendChild(child);
				}
				break;
			default :
				content = super.createContent(eid);
				break;
		}

		return content;
	}

	/**
	 * @param eid
	 *            int
	 */
	HTMLElementDeclaration createElemDecl(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		TypePacket packet = new TypePacket();
		switch (eid) {
			case TagIds20.ID_DIRECTIVE_TAG :
				// directive.taglib
				packet.name = DIRECTIVE_TAG;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case TagIds20.ID_DIRECTIVE_VARIABLE :
				// directive.taglib
				packet.name = DIRECTIVE_VARIABLE;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case TagIds20.ID_DIRECTIVE_ATTRIBUTE :
				// directive.taglib
				packet.name = DIRECTIVE_ATTRIBUTE;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case TagIds20.ID_DIRECTIVE_TAGLIB :
				// directive.taglib
				packet.name = DIRECTIVE_TAGLIB;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case TagIds20.ID_BODY :
				// declaration
				packet.name = BODY;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case TagIds20.ID_ELEMENT :
				// declaration
				packet.name = JSP20Namespace.ElementName.ELEMENT;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case TagIds20.ID_ATTRIBUTE :
				// declaration
				packet.name = JSP20Namespace.ElementName.ATTRIBUTE;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case TagIds20.ID_DOBODY :
				// declaration
				packet.name = JSP20Namespace.ElementName.DOBODY;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case TagIds20.ID_INVOKE :
				// declaration
				packet.name = JSP20Namespace.ElementName.INVOKE;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case TagIds20.ID_OUTPUT :
				// declaration
				packet.name = JSP20Namespace.ElementName.OUTPUT;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			default :
				return super.createElemDecl(eid);
		}
		ElemDecl decl = new ElemDecl(packet);

		CMGroupImpl content = createContent(eid);
		if (content != null)
			decl.setContent(content);

		TACreater20 creater = new TACreater20();
		decl.setAttributes(creater.getDeclarations(eid));

		return decl;
	}
}

package org.learningregistry.stdsalign.ns;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class DCNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if (prefix == null) throw new NullPointerException("Null prefix");
		else if ("dct".equals(prefix)) return "http://purl.org/dc/terms/";
		else return XMLConstants.NULL_NS_URI;
	}

	public String getPrefix(String arg0) {
		
		throw new UnsupportedOperationException();
	}

	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}
	
}
/*
QuiXProc: efficient evaluation of XProc Pipelines.
Copyright (C) 2011-2012 Innovimax
All rights reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package innovimax.quixproc.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

public abstract class QuixEvent implements IEvent{
  
  // Here is the grammar of events
  // sequence := START_SEQUENCE, document*, END_SEQUENCE
  // document := START_DOCUMENT, (PROCESSING-INSTRUCTION|COMMENT)*, element, (PROCESSING-INSTRUCTION|COMMENT)*, END_DOCUMENT
  // element := START_ELEMENT, NAMESPACE*, ATTRIBUTE*, (TEXT|element|PROCESSING-INSTRUCTION|COMMENT)*, END_ELEMENT

  // TODO : store namespacecontext
  // TODO : store type information for PSVI
  
  // to enable CACHING for specific kind of type
  private final static boolean SEQUENCE_CACHING_ENABLED = false;
  private final static boolean DOCUMENT_CACHING_ENABLED = false;
  private final static boolean ELEMENT_CACHING_ENABLED  = false;
  private final static boolean NAME_CACHING_ENABLED     = false;

  public static enum Token {
    START_SEQUENCE, END_SEQUENCE, START_DOCUMENT, END_DOCUMENT, NAMESPACE, START_ELEMENT, END_ELEMENT, ATTRIBUTE, TEXT, PI, COMMENT
  }

  private static long createCount     = 0;
  private static long createCallCount = 0;
  private static long createDocCount  = 0;
  private static long createAttrCount = 0;

  protected final Token     type;

  /* constructors */
  private QuixEvent(Token type) {
    this.type = type;
    createCount++;
  }
  
  public static class StartSequence extends QuixEvent {
    private StartSequence() {
      super(Token.START_SEQUENCE);
    }

    public String toString() {
      return type.toString();
    }
  }

  public static class EndSequence extends QuixEvent {
    private EndSequence() {
      super(Token.END_SEQUENCE);
    }

    public String toString() {
      return type.toString();
    }
  }

  public static class StartDocument extends QuixEvent {
    private final String uri;

    private StartDocument(String uri) {
      super(Token.START_DOCUMENT);
      this.uri = uri;
      createDocCount++;
//      System.out.println("START DOCUMENT"+uri);
    }

    public String getURI() {
      return this.uri;
    }

    public String toString() {
      return type + " " + this.uri;
    }
  }

  public static class EndDocument extends QuixEvent {
    private final String uri;

    private EndDocument(String uri) {
      super(Token.END_DOCUMENT);
      this.uri = uri;
      createDocCount++;
//      System.out.println("END DOCUMENT"+uri);
    }

    public String getURI() {
      return this.uri;
    }

    public String toString() {
      return type + " " + this.uri;
    }
  }

  public static class Namespace extends QuixEvent {
    private final String prefix;
    private final String uri;
    private Namespace(String prefix, String uri) {
      super(Token.NAMESPACE);
      this.prefix = prefix;
      this.uri = uri;
    }
    public String getPrefix() {
      return this.prefix;
    }
    public String getURI() {
      return this.uri;
    }
    public String toString() {
      return type + " " + this.prefix + ":" + this.uri;
    }
  }
  
  public static abstract class NamedEvent extends QuixEvent {
    private final QName qname;

    private NamedEvent(QName qname, Token type) {
      super(type);
      this.qname = qname;
    }

    public String getLocalName() {
      return this.qname.getLocalPart();
    }
    
    public String getFullName() {
      return (this.qname.getPrefix().length() > 0 ? this.qname.getPrefix()+":":"")+this.qname.getLocalPart();
    }

    public String getURI() {
      return this.qname.getNamespaceURI();
    }

    public String getPrefix() {
      return this.qname.getPrefix();
    }

    public QName getQName() {
      return qname;
    }
  }

  public static class StartElement extends NamedEvent {
    private StartElement(QName qname) {
      super(qname, Token.START_ELEMENT);
      // System.out.println("START ELEMENT"+localName);
    }

    public String toString() {
      return type + " " + getLocalName();
    }
  }

  public static class EndElement extends NamedEvent {
    private EndElement(QName qname) {
      super(qname,Token.END_ELEMENT);
      // System.out.println("END ELEMENT" + localName);
    }

    public String toString() {
      return type + " " + getLocalName();
    }
  }

  public static class Attribute extends NamedEvent {
    private final String value;

    private Attribute(QName qname, String value) {
      super(qname, Token.ATTRIBUTE);
      this.value = value;
      createAttrCount++;
      // System.out.println("ATTRIBUTE");
    }

    public String getValue() {
      return this.value;
    }

    public String toString() {
      return type + " " + getLocalName();
    }

  }

  public static class Text extends QuixEvent {
    private final String data;

    private Text(String data) {
      super(Token.TEXT);
      this.data = data;
      // System.out.println("TEXT");
    }

    public String getData() {
      return this.data;
    }

    public String toString() {
      return type + " " + getData();
    }
  }

  public static class PI extends QuixEvent {
    private final String target;
    private final String data;

    private PI(String target, String data) {
      super(Token.PI);
      this.target = target;
      this.data = data;
    }

    public String getTarget() {
      return this.target;
    }

    public String getData() {
      return this.data;
    }

    public String toString() {
      return type + " " + getTarget();
    }
  }

  public static class Comment extends QuixEvent {
    private final String data;

    private Comment(String data) {
      super(Token.COMMENT);
      this.data = data;
    }

    public String getData() {
      return this.data;
    }

    public String toString() {
      return type + " " + getData();
    }
  }

  public NamedEvent asNamedEvent() {
    return (NamedEvent) this;
  }

  public StartSequence asStartSequence() {
    return (StartSequence) this;
  }

  public EndSequence asEndSequence() {
    return (EndSequence) this;
  }

  public Namespace asNamespace() {
    return (Namespace) this;
  }
  
  public StartElement asStartElement() {
    return (StartElement) this;
  }

  public EndElement asEndElement() {
    return (EndElement) this;
  }

  public StartDocument asStartDocument() {
    return (StartDocument) this;
  }

  public EndDocument asEndDocument() {
    return (EndDocument) this;
  }

  public Text asText() {
    return (Text) this;
  }

  public Comment asComment() {
    return (Comment) this;
  }

  public PI asPI() {
    return (PI) this;
  }

  public Attribute asAttribute() {
    return (Attribute) this;
  }

  public Token getType() {
    return this.type;
  }

  /* get typed event */

  private static StartSequence newStartSequence = SEQUENCE_CACHING_ENABLED ? new StartSequence() : null;

  public static QuixEvent getStartSequence() {
    createCallCount++;
    StartSequence result;
    if (SEQUENCE_CACHING_ENABLED) {
      result = newStartSequence;
    } else {
      result = new StartSequence();
    }
    return result;
  }

  private static EndSequence newEndSequence = SEQUENCE_CACHING_ENABLED ? new EndSequence() : null;

  public static QuixEvent getEndSequence() {
    createCallCount++;
    EndSequence result;
    if (SEQUENCE_CACHING_ENABLED) {
      result = newEndSequence;
    } else {
      result = new EndSequence();
    }
    return result;
  }

  private static Map<String, StartDocument> startDocumentMap = DOCUMENT_CACHING_ENABLED ? new HashMap<String, StartDocument>() : null;

  public static QuixEvent getStartDocument(String uri) {
    createCallCount++;
    StartDocument result;
    if (DOCUMENT_CACHING_ENABLED) {
      synchronized (startDocumentMap) {
        if (startDocumentMap.containsKey(uri)) {
          result = startDocumentMap.get(uri);
        } else {
          result = new StartDocument(uri);
          startDocumentMap.put(uri, result);
        }
      }
    } else {
      result = new StartDocument(uri);
    }
    return result;
  }

  private static Map<String, EndDocument> endDocumentMap = DOCUMENT_CACHING_ENABLED ? new HashMap<String, EndDocument>() : null;

  public static QuixEvent getEndDocument(String uri) {
    createCallCount++;
    EndDocument result;
    if (DOCUMENT_CACHING_ENABLED) {
      synchronized (endDocumentMap) {
        if (endDocumentMap.containsKey(uri)) {
          result = endDocumentMap.get(uri);
        } else {
          result = new EndDocument(uri);
          endDocumentMap.put(uri, result);
        }
      }
    } else {
      result = new EndDocument(uri);
    }
    return result;
  }

  public static Namespace getNamespace(String prefix, String uri) {
    return new Namespace(prefix==null?"":prefix,uri);
  }
  
  private static Map<String, QName> qNameMap = NAME_CACHING_ENABLED ? new HashMap<String, QName>() : null;

  private static QName getQName(String localName, String namespace, String pref) {
    QName result;
    String uri = namespace == null ? "" : namespace;
    String prefix = pref == null ? "" : pref;
    if (NAME_CACHING_ENABLED) {
      String key = localName;
      // here the prefix plays no role
      // if (prefix.length() > 0) key = prefix + ":" + localName;
      if (uri.length() > 0) key = "{" + uri + "}" + key;
      synchronized (qNameMap) {
        if (qNameMap.containsKey(key)) {
          result = qNameMap.get(key);
        } else {
          result = new QName(uri, localName, prefix);
          qNameMap.put(key, result);
        }
      }
    } else {
      result = new QName(uri, localName, prefix);
    }
    return result;
  }

  private static Map<QName, StartElement> startElementMap = ELEMENT_CACHING_ENABLED ? new HashMap<QName, StartElement>() : null;

  public static QuixEvent getStartElement(String qName, String namespace) {
    String localName = qName;
    String prefix = null;
    if (qName.contains(":")) {
      StringTokenizer st = new StringTokenizer(qName, ":");
      prefix = st.nextToken();
      localName = st.nextToken();
    }
    return getStartElement(localName, namespace, prefix);
  }

  public static QuixEvent getStartElement(String localName, String namespace, String prefix) {
    createCallCount++;
    StartElement result;
    QName qname = getQName(localName, namespace, prefix);
    if (ELEMENT_CACHING_ENABLED) {
      synchronized (startElementMap) {
        if (startElementMap.containsKey(qname)) {
          result = startElementMap.get(qname);
        } else {
          result = new StartElement(qname);
          startElementMap.put(qname, result);
        }
      }
    } else {
      result = new StartElement(qname);
    }
    return result;
  }

  private static Map<QName, EndElement> endElementMap = ELEMENT_CACHING_ENABLED ? new HashMap<QName, EndElement>() : null;

  public static QuixEvent getEndElement(String qName, String namespace) {
    String localName = qName;
    String prefix = null;
    if (qName.contains(":")) {
      StringTokenizer st = new StringTokenizer(qName, ":");
      prefix = st.nextToken();
      localName = st.nextToken();
    }
    return getEndElement(localName, namespace, prefix);
  }

  public static QuixEvent getEndElement(String localName, String namespace, String prefix) {
    createCallCount++;
    EndElement result;
    QName qname = getQName(localName, namespace, prefix);
    if (ELEMENT_CACHING_ENABLED) {
      synchronized (endElementMap) {
        if (endElementMap.containsKey(qname)) {
          result = endElementMap.get(qname);
        } else {
          result = new EndElement(qname);
          endElementMap.put(qname, result);
        }
      }
    } else {
      result = new EndElement(qname);
    }
    return result;
  }

  public static QuixEvent getAttribute(String qName, String namespace, String value) {
    String localName = qName;
    String prefix = null;
    if (qName.contains(":")) {
      StringTokenizer st = new StringTokenizer(qName, ":");
      prefix = st.nextToken();
      localName = st.nextToken();
    }
    return getAttribute(localName, namespace, prefix, value);
  }

  public static QuixEvent getAttribute(String localName, String namespace, String prefix, String value) {
    createCallCount++;
    return new Attribute(getQName(localName, namespace, prefix), value);
  }

  public static QuixEvent getText(String text) {
    createCallCount++;
    return new Text(text);
  }

  public static QuixEvent getPI(String target, String data) {
    createCallCount++;
    return new PI(target, data);
  }

  public static QuixEvent getComment(String comment) {
    createCallCount++;
    return new Comment(comment);
  }

  /* utilities */

  public boolean isStartSequence() {
    return (this.type == Token.START_SEQUENCE);
  }

  public boolean isEndSequence() {
    return (this.type == Token.END_SEQUENCE);
  }

  public boolean isStartDocument() {
    return (this.type == Token.START_DOCUMENT);
  }

  public boolean isEndDocument() {
    return (this.type == Token.END_DOCUMENT);
  }

  public boolean isStartElement() {
    return (this.type == Token.START_ELEMENT);
  }

  public boolean isEndElement() {
    return (this.type == Token.END_ELEMENT);
  }

  public boolean isAttribute() {
    return (this.type == Token.ATTRIBUTE);
  }

  public boolean isText() {
    return (this.type == Token.TEXT);
  }

  public boolean isPI() {
    return (this.type == Token.PI);
  }

  public boolean isComment() {
    return (this.type == Token.COMMENT);
  }
  
  public boolean isNamespace() {
    return (this.type == Token.NAMESPACE);
  }
  
  public QuixEvent getEvent() { return this; }

  /* debuging */

  public static long getCreateCount() {
    return createCount;
  }

  public static long getCreateDocCount() {
    return createDocCount;
  }

  public static long getCreateAttrCount() {
    return createAttrCount;
  }

  public static long getCreateCallCount() {
    return createCallCount;
  }

  public static void resetCreateCount() {
    createCount = 0;
    createDocCount = 0;
    createAttrCount = 0;
    createCallCount = 0;
  }

}

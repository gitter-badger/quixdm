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
package innovimax.quixproc.datamodel.stream;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;


import innovimax.quixproc.datamodel.IEvent;
import innovimax.quixproc.datamodel.IStream;
import innovimax.quixproc.datamodel.QuixEvent;

public class NamespaceContextFilter<T extends IEvent> extends AStreamFilter<T> {

  private LinkedList<Map<String, String>> namespaces;
  public NamespaceContextFilter(IStream<T> stream) {
    super(stream);
    // TODO Auto-generated constructor stub
    this.namespaces = new LinkedList<Map<String, String>>();
  }

  private boolean needCleaning = false;
  @Override
  public T process(T item) {
    QuixEvent qevent = item.getEvent();
    if (needCleaning) {
      this.namespaces.pollLast();
      needCleaning = false;
    }
    switch(qevent.getType()) {
      case START_ELEMENT :
        this.namespaces.add(new TreeMap<String, String>());
        break;
      case END_ELEMENT :
        // differ the cleaning to the next event
        needCleaning = true;
        break;
      case NAMESPACE :        
        this.namespaces.getLast().put(qevent.asNamespace().getPrefix(), qevent.asNamespace().getURI());
        break;
    }
    return item;
  }
  /**
   * Check at the current moment if the prefix is mapped
   * It returns null if the prefix is not mapped at this time
   * @param prefix
   * @return
   */
  public String getURI(String prefix) {
    for(Iterator<Map<String, String>> iter = this.namespaces.descendingIterator();iter.hasNext();) {
       Map<String, String> map = iter.next();
       if (map.containsKey(prefix)) return map.get(prefix);
    }
    return null;
  }
}

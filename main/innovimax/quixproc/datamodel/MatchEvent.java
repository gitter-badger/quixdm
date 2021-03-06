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

public class MatchEvent implements IEvent
{           
  
  /* properties */       
  
  private final QuixEvent event;
  private boolean matched = true;  
  private String channels = null;    
  
  /* constructor */ 
      
  public MatchEvent(QuixEvent event) {
    this.event = event;
  }
  
  public MatchEvent(QuixEvent event, boolean matched) {
    this.event = event;
    this.matched = matched;
  }

  /* set/get properties */  
  
  public QuixEvent getEvent() {    
    return event;
  }  
    
  public MatchEvent setMatched(boolean matched) {
    this.matched = matched;
    return this;
  }


  public boolean isMatched() {
    return matched;
  }  

  @Override
  public String toString() {
    return this.event.toString() + ";"+this.matched;
  }
}

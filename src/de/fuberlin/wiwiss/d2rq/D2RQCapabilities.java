/*
  (c) Copyright 2004 by Chris Bizer (chris@bizer.de)
*/

package de.fuberlin.wiwiss.d2rq;

import com.hp.hpl.jena.graph.Capabilities;

/**
    Description of the capabilities of a GraphD2RQ.
    
 * <BR>History: 06-10-2004   : Initial version of this class.
 * @author Chris Bizer chris@bizer.de
 * @version V0.1
 */

class D2RQCapabilities implements Capabilities
    {
    public boolean sizeAccurate() { return true; }
    public boolean addAllowed() { return addAllowed( false ); }
    public boolean addAllowed( boolean every ) { return false; }
    public boolean deleteAllowed() { return deleteAllowed( false ); }
    public boolean deleteAllowed( boolean every ) { return false; }
    public boolean canBeEmpty() { return true; }
    public boolean iteratorRemoveAllowed() { return false; }
    public boolean findContractSafe() { return false; } // TODO check semantic of this in Jena 2.2.    
    // TODO check handlesLiteralTyping() for correctness. 
    // SQL does it for numbers, but currently not for dates.
    public boolean handlesLiteralTyping() { return false; } // required by Jena 2.3
    }





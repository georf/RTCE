/*
 * $Id: Value.java 76 2008-09-30 06:13:48Z parzy $
 * 
 *  This file is part of the REBECA System.
 *  See http://www.gkec.informatik.tu-darmstadt.de/rebeca (broken)
 *
 *  Authors: Gero   Muehl <gmuehl@cs.tu-berlin.de>
 *           Ludger Fiege <fiege@gkec.tu-darmstadt.de>
 *
 *  Copyright (C) 2000-2001
 *
 *  You can redistribute it and/or modify this software under the
 *  terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2, or (at your option) any
 *  later version.
 */


package rebeca.namevalue;

import java.io.Serializable;

public interface Value extends Serializable {

    public String toString();

}

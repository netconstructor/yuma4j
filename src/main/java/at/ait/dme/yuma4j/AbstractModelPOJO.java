package at.ait.dme.yuma4j;

import java.io.Serializable;

abstract class AbstractModelPOJO implements Serializable {

	private static final long serialVersionUID = -413997795983989115L;

	protected boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}

}

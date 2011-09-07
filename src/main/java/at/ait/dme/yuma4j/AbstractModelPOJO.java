package at.ait.dme.yuma4j;

class AbstractModelPOJO {
	
	protected boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}

}

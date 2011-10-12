package at.ait.dme.yuma4j;

/**
 * An RDF Plain Literal (see http://www.w3.org/TR/rdf-concepts/#dfn-plain-literal)
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class PlainLiteral extends AbstractModelPOJO {

	private static final long serialVersionUID = 7253408804885981918L;

	/**
	 * The value (MANDATORY)
	 */
	private String value;

	/**
	 * The language (OPTIONAL)
	 */
	private String lang = null;
	
	// Required for JSON mapping
	public PlainLiteral() { }

	public PlainLiteral(String value) {
		this.value = value;
	}

	public PlainLiteral(String value, String lang) {
		this.value = value;
		this.lang = lang.toLowerCase();
	}

	public String getValue() {
		return value;
	}
	
	// Required for JSON mapping
	void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}
	
	// Required for JSON mapping
	void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PlainLiteral))
			return false;

		PlainLiteral l = (PlainLiteral) other;

		if (!l.getValue().equals(this.getValue()))
			return false;

		if (!equalsNullable(this.getLang(), l.getLang()))
			return false;

		return true;
	}

}

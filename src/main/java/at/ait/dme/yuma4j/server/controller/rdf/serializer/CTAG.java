package at.ait.dme.yuma4j.server.controller.rdf.serializer;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

class CTAG {

	protected static final String uri="http://commontag.org/ns#";

    protected static final Resource resource( String local )
        { return ResourceFactory.createResource( uri + local ); }

    protected static final Property property( String local )
        { return ResourceFactory.createProperty( uri, local ); }

    public static final Resource Tag = resource( "Tag");
    public static final Property tagged = property( "tagged");
    public static final Property label = property( "label");
    public static final Property means = property( "means");
    
}

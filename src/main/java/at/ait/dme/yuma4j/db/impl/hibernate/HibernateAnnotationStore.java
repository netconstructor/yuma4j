package at.ait.dme.yuma4j.db.impl.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.DeleteNotAllowedException;
import at.ait.dme.yuma4j.db.exception.InvalidAnnotationException;
import at.ait.dme.yuma4j.db.impl.hibernate.entities.AnnotationEntity;
import at.ait.dme.yuma4j.db.impl.hibernate.entities.SemanticTagEntity;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.model.tags.SemanticTag;

/**
 * DB implementation for relational databases using the
 * Hibernate persistence layer. 
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class HibernateAnnotationStore extends AnnotationStore {
	
	public static final String PARAM_PERSISTENCE_UNIT = "persistence.unit";
	
	private static final String EXCEPTION_ROOT_NOT_FOUND = "Annotation is a reply to an annotation that does not exist: ";
	private static final String EXCEPTION_NOT_ROOT = "Cannot reply to an annotation that is a reply itself: ";
	private static final String EXCEPTION_HIJACK_ATTEMPT = "Update attempt made by a different user - may be a hijacking attempt: ";
	private static final String EXCEPTION_INVALID_PROPERTY_UPDATE = "Update attempted on a property that should never change: ";
	
	private static EntityManagerFactory emf;	
	
	private EntityManager em = null;
	
	@Override
	public synchronized void init(Map<Object, Object> params) {	
		emf = Persistence.createEntityManagerFactory((String) params.get(PARAM_PERSISTENCE_UNIT));
	}
	
	@Override
	public void connect() throws AnnotationStoreException {
		if (emf == null) 
			throw new AnnotationStoreException("Hibernate annotation store not initialized!");
		
		em = emf.createEntityManager();		
	}
	
	@Override
	public void disconnect() {
		if ((em != null) && (em.isOpen()))
			em.close();	
	}

	@Override
	public void shutdown() {
		if (emf != null)
			emf.close();
	}

	@Override
	public Annotation createAnnotation(Annotation annotation)
			throws AnnotationStoreException, InvalidAnnotationException {

		EntityTransaction tx = em.getTransaction();
		try {			
			tx.begin();
			AnnotationEntity entity = new AnnotationEntity(annotation);
			
			// If this annotation is a reply, we have to make sure that the root
			// annotation (a) exists and (b) is indeed a root annotation, not a reply itself
			if (annotation.getIsReplyTo() != null) {
				Long rootID = Long.parseLong(annotation.getIsReplyTo());
				AnnotationEntity root = em.find(AnnotationEntity.class, rootID);				
				
				if (root == null)
					throw new InvalidAnnotationException(EXCEPTION_ROOT_NOT_FOUND + annotation.getIsReplyTo());
				
				if (root.getIsReplyTo() != null)
					throw new InvalidAnnotationException(EXCEPTION_NOT_ROOT + annotation.getIsReplyTo());
			}

			em.persist(entity);														
			tx.commit();

			annotation.setID(Long.toString(entity.getID()));
			return annotation;
		} catch(Throwable t) {
			tx.rollback();
			
			if (t instanceof InvalidAnnotationException)
				throw (InvalidAnnotationException) t;
			
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public Annotation updateAnnotation(String annotationID, Annotation update)
			throws AnnotationStoreException, AnnotationNotFoundException, InvalidAnnotationException {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Long id = Long.parseLong(annotationID);
			AnnotationEntity original = em.find(AnnotationEntity.class, id);
			if (original == null)
				throw new AnnotationNotFoundException();
			
			// Verify 'unchangeable' properties
			if (!original.getCreator().toUser().equals(update.getCreator()))
				throw new InvalidAnnotationException(EXCEPTION_HIJACK_ATTEMPT + update.getCreator());
			
			if (!original.getObjectURI().equals(update.getObjectURI()))
				throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "objectURI");
			
			if (!original.getContext().toContext().equals(update.getContext()))
				throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "context");
			
			if (original.getCreated().getTime() != update.getCreated().getTime())
				throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "created");
			
			if (!original.getMediatype().equals(update.getMediatype()))
				throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "mediatype");
				
			if (original.getIsReplyTo() == null) {
				if (update.getIsReplyTo() != null) 
					throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "isReplyTo");
			} else {
				if (!original.getIsReplyTo().toString().equals(update.getIsReplyTo()))
					throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "isReplyTo");
				
				if (update.getFragment() != null)
					throw new InvalidAnnotationException(EXCEPTION_INVALID_PROPERTY_UPDATE + "fragment");
			}
			
			// Update changeable properties
			original.setModified(update.getModified());
			original.setText(update.getText());
			original.setFragment(update.getFragment());
			original.setScope(update.getScope());

			List<SemanticTagEntity> tagEntities = new ArrayList<SemanticTagEntity>();
			for (SemanticTag t : update.getTags()) {
				tagEntities.add(new SemanticTagEntity(original, t));
			}
			original.setTags(tagEntities);
			
			tx.commit();
			return original.toAnnotation();
		} catch (Throwable t) {
			tx.rollback();
			
			if (t instanceof AnnotationNotFoundException)
				throw (AnnotationNotFoundException) t;
			
			if (t instanceof InvalidAnnotationException)
				throw (InvalidAnnotationException) t;
			
			throw new AnnotationStoreException(t);
		}
	}
	
	@Override
	public void deleteAnnotation(String annotationID)
			throws AnnotationStoreException, AnnotationNotFoundException, DeleteNotAllowedException {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Long id = Long.parseLong(annotationID);
			AnnotationEntity entity = em.find(AnnotationEntity.class, id);
			if (entity == null)
				throw new AnnotationNotFoundException();
			
			if (entity.getIsReplyTo() == null) {
				// If this is a root annotation, make sure no-one has replied
				// in the mean time
				em.lock(entity, LockModeType.PESSIMISTIC_WRITE);			
				em.refresh(entity);
				
				if (getReplyThread(annotationID).size() > 0)
					throw new DeleteNotAllowedException();
			}
			
			em.remove(entity);	
			tx.commit();
		} catch (Throwable t) {
			tx.rollback();
			
			if (t instanceof AnnotationNotFoundException)
				throw (AnnotationNotFoundException) t;
			
			if (t instanceof DeleteNotAllowedException)
				throw (DeleteNotAllowedException) t;
			
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public List<Annotation> listAnnotationsForObject(String objectURI) throws AnnotationStoreException {
		try {
			Query query = em.createNamedQuery("annotationentity.find.for.object");
			query.setParameter("objectURI", objectURI);
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> allAnnotations = query.getResultList();
			
			return toAnnotations(allAnnotations);
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public long countAnnotationsForObject(String objectURI) throws AnnotationStoreException {
		int count = 0;
		try {
			Query query = em.createNamedQuery("annotationentity.count.for.object");
			query.setParameter("objectURI", objectURI);
			count = ((Long) query.getSingleResult()).intValue();
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
		return count;
	}

	@Override
	public List<Annotation> listAnnotationsForUser(String username) throws AnnotationStoreException {
		try {
			Query query = em.createNamedQuery("annotationentity.find.for.user");
			query.setParameter("username", username);
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> allAnnotations = query.getResultList();
			
			return toAnnotations(allAnnotations);
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}	
	
	@Override
	public long countAnnotationsForUser(String username) throws AnnotationStoreException {
		int count = 0;
		try {
			Query query = em.createNamedQuery("annotationentity.count.for.user");
			query.setParameter("username", username);
			count = ((Long) query.getSingleResult()).intValue();
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
		return count;
	}
	
	@Override
	public Annotation getAnnotation(String annotationID) 
		throws AnnotationStoreException, AnnotationNotFoundException {

		try {
			Long id = Long.parseLong(annotationID);
			AnnotationEntity entity = em.find(AnnotationEntity.class, id);
			if (entity == null)
				throw new AnnotationNotFoundException();
			
			return entity.toAnnotation();					
		} catch(AnnotationNotFoundException e) {
			throw e;
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}
	
	@Override
	public List<Annotation> getReplyThread(String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException {
		
		try {
			Annotation a = getAnnotation(annotationId);
			String isReplyTo;
			if (a.getIsReplyTo() == null) {
				isReplyTo = a.getID();
			} else {
				isReplyTo = a.getIsReplyTo();
			}
			
			Query query = em.createNamedQuery("annotationentity.find.thread");	
			query.setParameter("isReplyTo", Long.parseLong(isReplyTo));
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> thread = query.getResultList();		
			return toAnnotations(thread);
		} catch (Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public List<Annotation> getMostRecent(int n, boolean publicOnly) throws AnnotationStoreException {
		try {
			Query query;
			if (publicOnly) {
				query = em.createNamedQuery("annotationentity.mostrecent.public");
			} else {
				query = em.createNamedQuery("annotationentity.mostrecent.all");
			}
			query.setMaxResults(n);
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> mostRecent = query.getResultList();
			return toAnnotations(mostRecent);
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public List<Annotation> findAnnotations(String q) throws AnnotationStoreException {
		try {
			Query query = em.createNamedQuery("annotationentity.searchTextAndTags");
			query.setParameter("term", q.toLowerCase());
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> entities = 
				new ArrayList<AnnotationEntity>(new HashSet<AnnotationEntity>(query.getResultList()));
			
			Collections.sort(entities, new Comparator<AnnotationEntity>() {
				@Override
				public int compare(AnnotationEntity a, AnnotationEntity b) {
					if (a.getModified().before(b.getModified()))
						return 1;
					
					return -1;
				}
				
			});
			
			return toAnnotations(entities);
		} catch(Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}
	
	private List<Annotation> toAnnotations(List<AnnotationEntity> entities) throws AnnotationStoreException {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (AnnotationEntity entity : entities) {
			annotations.add(entity.toAnnotation());
		}
		return annotations;
	}

}

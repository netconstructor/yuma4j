package at.ait.dme.yuma4j.db.impl.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;

import org.hibernate.cfg.Configuration;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.AnnotationTree;
import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.impl.hibernate.entities.AnnotationEntity;

/**
 * DB implementation for relational databases using the
 * Hibernate persistence layer. 
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class HibernateAnnotationStore extends AnnotationStore {
	
	private static EntityManagerFactory emf;	
	
	private EntityManager em = null;

	public synchronized void init(String persistenceUnit) {	
		emf = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	@Override
	public void connect() {
		if (emf == null) 
			init("production");
		
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
	public String createAnnotation(Annotation annotation)
			throws AnnotationStoreException, AnnotationModifiedException {

		EntityTransaction tx = em.getTransaction();
		try {			
			tx.begin();
			
			// In case of a reply we have to ensure that the parent is unchanged
			AnnotationEntity entity = new AnnotationEntity(annotation);
			if (entity.getParentID() != null) {
				// An annotation gets a new ID on every update. Therefore, checking for
				// existence is sufficient here.			
				AnnotationEntity parent = em.find(AnnotationEntity.class, entity.getParentID());				
				if (parent == null) 
					throw new AnnotationModifiedException(entity.getParentID());

				// Parent unchanged - lock it and make sure it wasn't modified concurrently
				try {
					em.lock(parent, LockModeType.PESSIMISTIC_WRITE);
				} catch(PessimisticLockException e) {
					throw new AnnotationModifiedException(annotation.getParentID());
				}				
				em.refresh(parent);				
			}

			em.persist(entity);														
			tx.commit();
			
			String id = Long.toString(entity.getAnnotationID());
			annotation.setAnnotationID(id);
			return id;
		} catch(AnnotationModifiedException e) {
			throw e;
		} catch(Throwable t) {
			tx.rollback();
			System.out.println("Failed to save annotation: " + t.getMessage());
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public String updateAnnotation(String annotationID, Annotation annotation)
			throws AnnotationStoreException, AnnotationNotFoundException, AnnotationHasReplyException {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			delete(annotationID);
			AnnotationEntity entity = new AnnotationEntity(annotation);
			em.persist(entity);
			tx.commit();
			
			String id = Long.toString(entity.getAnnotationID());
			annotation.setAnnotationID(id);
			return id;	
		} catch (Throwable t) {
			tx.rollback();
			
			if (t instanceof AnnotationNotFoundException)
				throw (AnnotationNotFoundException) t;
			
			if (t instanceof AnnotationHasReplyException)
				throw (AnnotationHasReplyException) t;
			
			throw new AnnotationStoreException(t);
		}
	}
	
	@Override
	public void deleteAnnotation(String annotationID)
			throws AnnotationStoreException, AnnotationNotFoundException, AnnotationHasReplyException {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			delete(annotationID);
			tx.commit();
		} catch (Throwable t) {
			tx.rollback();
			
			if (t instanceof AnnotationNotFoundException)
				throw (AnnotationNotFoundException) t;
			
			if (t instanceof AnnotationHasReplyException)
				throw (AnnotationHasReplyException) t;
			
			throw new AnnotationStoreException(t);
		}
	}
	
	private void delete(String annotationID)
		throws AnnotationNotFoundException, AnnotationStoreException, AnnotationHasReplyException {
		
		Long id = Long.parseLong(annotationID);
	
		AnnotationEntity entity = em.find(AnnotationEntity.class, id);
		if (entity == null)
			throw new AnnotationNotFoundException();
		
		em.lock(entity, LockModeType.PESSIMISTIC_WRITE);			
		em.refresh(entity);
		
		if (countRepliesToAnnotation(annotationID) > 0)
			throw new AnnotationHasReplyException();
		
		em.remove(entity);	
	}

	@Override
	public AnnotationTree listAnnotationsForObject(String objectURI) throws AnnotationStoreException {
		Configuration c = new Configuration();
		c.buildSessionFactory().openSession();
		try {
			Query query = em.createNamedQuery("annotationentity.find.for.object");
			query.setParameter("objectURI", objectURI);
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> allAnnotations = query.getResultList();
			
			return new AnnotationTree(toAnnotations(allAnnotations));
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
	public AnnotationTree listRepliesToAnnotation(String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException {
		
		try {
			Annotation a = getAnnotation(annotationId);
			String rootId;
			if (a.getRootID() == null) {
				rootId = a.getAnnotationID();
			} else {
				rootId = a.getRootID();
			}
			
			Query query = em.createNamedQuery("annotationentity.find.thread");	
			query.setParameter("rootID", Long.parseLong(rootId));
			
			@SuppressWarnings("unchecked")
			List<AnnotationEntity> thread = query.getResultList();		
			return new AnnotationTree(toAnnotations(filterReplies(thread, annotationId)));
		} catch (Throwable t) {
			throw new AnnotationStoreException(t);
		}
	}

	@Override
	public long countRepliesToAnnotation(String annotationId) throws AnnotationStoreException {
		int count = 0;
		try {
			Query query = em.createNamedQuery("annotationentity.count.replies");
			query.setParameter("id", Long.parseLong(annotationId));
			count = ((Long) query.getSingleResult()).intValue();
		} catch (Throwable t) {
			throw new AnnotationStoreException(t);
		}
		return count;
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
	
	/**
	* Filters an entire annotation thread so that only (direct or indirect)
	* children of the annotation with the specified ID are returned.
	* @param thread the entire annotation thread
	* @param parentId the ID of the parent
	* @return the filtered thread
	*/
	private List<AnnotationEntity> filterReplies(List<AnnotationEntity> thread, String parentId) {
		List<AnnotationEntity> replies = new ArrayList<AnnotationEntity>();
		getChildren(thread, replies, Long.parseLong(parentId));
		return replies;
	}

	private void getChildren(List<AnnotationEntity> all, List<AnnotationEntity> children, Long parentId) {
		for (AnnotationEntity a : all) {
			if (a.getParentID().equals(parentId)) {
				children.add(a);
				getChildren(all, children, a.getAnnotationID());
			}
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

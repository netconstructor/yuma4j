# TODOs

* Reply functionality:
  * Annotations are either "root annotations" or replies
  * Replies have a "isReplyTo" property that holds the ID of the root annotation
  * Root annotations have a null "isReplyTo" value
  * A root annotation that has replies MAY NOT be deleted
  * A root annotation that has replies MAY be edited (text + bounding box)
  * A reply DOES NOT have a "fragment" value
  * A reply MAY always be deleted by the owner
  * A reply MAY always be edited by the owner
  * In the UI, replies are sorted by creation date
* The whole project could really use a code review
* Review/revise JavaDoc!
* Make search results page URL bookmarkable
* Pagination in annotation list views
* Revise feed API
* 'Dump database' and 'Dump to RDF' feature (in the admin section)
* Implement index-based operations in HibernateAnnotationStore using Hibernate Search
* Unit testing: Hibernate tests are now all in one method - split up into one test method per
  HibernateAnnotationStore method
* Wicket tooling for Web tests?

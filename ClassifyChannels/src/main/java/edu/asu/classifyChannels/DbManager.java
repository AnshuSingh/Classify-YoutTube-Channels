package edu.asu.classifyChannels;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.exception.LockTimeoutException;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import edu.asu.classifyChannels.model.A_Channels;
import edu.asu.classifyChannels.util.HibernateUtil;

public class DbManager {

	    public static void main(String[] args) {
	    	DbManager mgr = new DbManager();
	    	
	    	List<SearchResult> searchResultList = Search.searchEntities("videos");
	    	if (searchResultList != null){
	    		mgr.storeResult(searchResultList.iterator());
	    	}
	    	HibernateUtil.getSessionFactory().close();
	    }
	        
	    
	    private void storeResult(Iterator<SearchResult> iteratorSearchResults) {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
	        
	        if (!iteratorSearchResults.hasNext()) {
	            System.out.println(" There aren't any results for your query.");
	        }

	        while (iteratorSearchResults.hasNext()) {

	            SearchResult singleVideo = iteratorSearchResults.next();
	            ResourceId rId = singleVideo.getId();

	            // Confirm that the result represents a video. Otherwise, the
	            // item will not contain a video ID.
	            if (rId.getKind().equals("youtube#video")) {
	            	SearchResultSnippet theSnippet = singleVideo.getSnippet();
	            	A_Channels theChannel = new A_Channels();
	            	theChannel.setId(rId.getVideoId());
	            	try {
						theChannel.setSnippet(theSnippet.toPrettyString());
					} catch (IOException e) {
						e.printStackTrace();
					}
	     	        theChannel.setChannelTitle(theSnippet.getChannelTitle());
	     	        theChannel.setDescription(theSnippet.getDescription());
	     	        theChannel.setTitle(theSnippet.getTitle());
	     	        theChannel.setUnknownKeys(theSnippet.getUnknownKeys().toString());
	     	        session.save(theChannel);

	     	        //System.out.println(" Video Id" + rId.getVideoId());
	                System.out.println(" Title: " + theSnippet.getTitle());
	            }
	        }
		    try{
		        session.getTransaction().commit();
		    }
	        catch (LockTimeoutException e){
	        	e.printStackTrace();
	        }
		    catch (HibernateException e){
	        	e.printStackTrace();
	        }
		    catch (Exception e){
	        	e.printStackTrace();
	        }
	    }

	    /*
	    private static List listA_Channels() {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
	        List result = session.createQuery("from A_Channels").list();
	        session.getTransaction().commit();
	        return result;
	    }
	    
	    private void addA_VideosToEvent(Long personId, Long eventId) {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();

	        A_Videos aA_Videos = (A_Videos) session
	                .createQuery("select p from A_Videos p left join fetch p.events where p.id = :pid")
	                .setParameter("pid", personId)
	                .uniqueResult(); // Eager fetch the collection so we can use it detached
	        A_Channels anEvent = (A_Channels) session.load(A_Channels.class, eventId);

	        session.getTransaction().commit();

	        // End of first unit of work

	        aA_Videos.getA_Channels().add(anEvent); // aA_Videos (and its collection) is detached

	        // Begin second unit of work

	        Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
	        session2.beginTransaction();
	        session2.update(aA_Videos); // Reattachment of aA_Videos

	        session2.getTransaction().commit();
	    }*/

	}

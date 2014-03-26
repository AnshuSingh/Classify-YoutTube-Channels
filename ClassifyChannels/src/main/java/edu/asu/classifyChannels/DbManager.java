package edu.asu.classifyChannels;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockTimeoutException;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import edu.asu.classifyChannels.model.A_Channels;
import edu.asu.classifyChannels.util.HibernateUtil;

public class DbManager {

	    public static void main(String[] args) {
	    	DbManager mgr = new DbManager();
	    	String [] categories = {"country", "rock", "hip hop", "jazz", "blues"};
	    	for (int i = 0; i < categories.length; i++){
	    		List<SearchResult> searchResultList = Search.searchEntities("videos", categories[i]);
		    	if (searchResultList != null){
		    		mgr.storeResult(searchResultList.iterator(),categories[i] );
		    	}
	    	}
	    	HibernateUtil.getSessionFactory().close();
	    }
	        
	    
	    private void storeResult(Iterator<SearchResult> iteratorSearchResults, String category) {
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
	     	        theChannel.setChannelTitle(theSnippet.getChannelTitle());
	     	        theChannel.setDescription(theSnippet.getDescription());
	     	        theChannel.setTitle(theSnippet.getTitle());
	     	        theChannel.setCategory(category);
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
		    catch(ConstraintViolationException e){
		    	e.printStackTrace();
		    }
		    catch (HibernateException e){
	        	e.printStackTrace();
	        }
		    catch (Exception e){
	        	e.printStackTrace();
	        }
	    }

	}

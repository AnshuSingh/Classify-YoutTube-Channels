package edu.asu.classifyChannels;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import edu.asu.classifyChannels.util.Auth;

public class Search {
	
	    private static final String PROPERTIES_FILENAME = "youtube.properties";

	    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
	    
	    static Set<String> resultSet = new HashSet<String>();

	    private static YouTube youtube;
	    
	    private static int setMaxResults(int max){
			return  (int) NUMBER_OF_VIDEOS_RETURNED;
	    }

	    public static List<SearchResult> searchEntities(String type, String queryTerm) {
	        // Read the developer key from the properties file.
	        Properties properties = new Properties();
	        try {
	            InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	            properties.load(in);

	        } catch (IOException e) {
	            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	                    + " : " + e.getMessage());
	            System.exit(1);
	        }

	        List<SearchResult> searchResultList = null;
	        try {
	            // This object is used to make YouTube Data API requests. The last
	            // argument is required, but since we don't need anything
	            // initialized when the HttpRequest is initialized, we override
	            // the interface and provide a no-op function.
	            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
	                public void initialize(HttpRequest request) throws IOException {
	                }
	            }).setApplicationName("youtube-channel-search").build();

	            // Prompt the user to enter a query term.

	            YouTube.Search.List search = youtube.search().list("id,snippet");

	            String apiKey = properties.getProperty("youtube.apikey");
	            search.setKey(apiKey);
	            search.setQ(queryTerm);

	            search.setType(type);
	            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

	            // Call the API and print results.
	            for (int i =0; i<10;i++){
	            SearchListResponse searchResponse = search.execute();
	            searchResultList = searchResponse.getItems();
		            if (searchResultList != null) {
		            	
		               // prettyPrint(searchResultList.iterator(), queryTerm);
		            }
	            }
	            int total = 0;
	            for (String a : resultSet){
	            	System.out.println (total+": " +a);
	            	total++;
	            }
	        } catch (GoogleJsonResponseException e) {
	            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	                    + e.getDetails().getMessage());
	        } catch (IOException e) {
	            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }
	        return searchResultList;
	    }

	}


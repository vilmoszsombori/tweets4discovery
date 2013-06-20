package uk.ac.gold.tweets4discovery.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.google.gson.Gson;

/**
 * Servlet implementation class SessionsInfo
 */
@WebServlet("/json")
public class GetTweetsInJson extends HttpServlet {       
	
	private static Logger LOG = Logger.getLogger(GetTweetsInJson.class);
	private static final long serialVersionUID = -702440476935775080L;
	private Gson gson = new Gson();

	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetTweetsInJson() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// create JSON response map
		Map<String, Object> jsonResp = new HashMap<String, Object>();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		String queryString = request.getParameter("query");
		String since = request.getParameter("since");
		String until = request.getParameter("until");
		jsonResp.put("query", queryString);
		jsonResp.put("since", since);
		jsonResp.put("until", until);
		
		LOG.info("Query: [" + queryString + "]; Since: [" + since + "]; Until: [" + until + "].");
		
		long startTime = System.currentTimeMillis();
		String status = "failed"; 
		
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			Query query = new Query(queryString);
			query.setSince(since);
			query.setUntil(until);
			QueryResult result;
			List<Status> _tweets = new Vector<Status>();
			do {
				result = twitter.search(query);
				_tweets.addAll(result.getTweets());
				/*
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}
				*/
			} while ((query = result.nextQuery()) != null);
			
			if ( !_tweets.isEmpty() ) {
				jsonResp.put("tweets", _tweets);
				status = "successful";
			} else {
				throw new Exception("Sorry, there are no tweets matching your search [" + queryString + "] between those dates (from [" + since + "] to [" + until + "]).");
			}
		} catch (TwitterException e) {
			jsonResp.put("exception", e.getMessage());
			e.printStackTrace();
		} catch ( Exception e ) {
			jsonResp.put("exception", e.getMessage());
			request.setAttribute ("javax.servlet.jsp.jspException", e);
			e.printStackTrace();
		} finally {
			// return the JSON 
			LOG.info("Response time: " + (System.currentTimeMillis() - startTime) + "ms.");
			LOG.info("Status: " + status);
			response.getWriter().write(gson.toJson(jsonResp));			
		}		
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}	
}

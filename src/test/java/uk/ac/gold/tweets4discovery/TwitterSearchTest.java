package uk.ac.gold.tweets4discovery;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterSearchTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("java twitter4j.examples.search.SearchTweets [query]");
			System.exit(-1);
		}
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(args[0]);
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName()
							+ " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}

}

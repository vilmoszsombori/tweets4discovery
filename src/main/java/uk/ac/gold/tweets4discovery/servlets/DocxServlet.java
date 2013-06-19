package uk.ac.gold.tweets4discovery.servlets;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.google.gson.Gson;

/**
 * Servlet implementation class DocxServlet
 */
//@WebServlet("/json")
public class DocxServlet extends HttpServlet {       
	
	private static final long serialVersionUID = 5177529122176911436L;
	private static Logger LOG = Logger.getLogger(DocxServlet.class);
	private Gson gson = new Gson();

	/**
     * @see HttpServlet#HttpServlet()
     */
    public DocxServlet() {
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
			
        	//long timestamp = System.currentTimeMillis();
        	String rootPath = System.getProperty("tweets4discovery.rootPath") + "download";
        	LOG.info("The DOCX download folder is [" + rootPath + "].");
        	
        	//create "logs" folder if necessary
        	File directory = new File(rootPath);
        	if (directory.isDirectory() == false) {
        		directory.mkdir();
        	}
        	
			//WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
			wordMLPackage = WordprocessingMLPackage.createPackage();
			
			
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Tweets 4 Discovery");
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", "by Vilmos and Michel");
			wordMLPackage.getMainDocumentPart().addParagraphOfText("Query: " + queryString);
			wordMLPackage.getMainDocumentPart().addParagraphOfText("Since: " + since);
			wordMLPackage.getMainDocumentPart().addParagraphOfText("Until: " + until);
			
			factory = Context.getWmlObjectFactory();
			Tbl table = createTableWithContent();
			
			addBorders(table);
			wordMLPackage.getMainDocumentPart().addObject(table);
			
			wordMLPackage.save(new java.io.File(rootPath + "/HelloWord2.docx"));
			
			Twitter twitter = new TwitterFactory().getInstance();
			Query query = new Query(queryString);
			query.setSince(since);
			query.setUntil(until);
			QueryResult result;
			/*
			List<Status> _tweets = new Vector<Status>();
			do {
				result = twitter.search(query);
				_tweets.addAll(result.getTweets());
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			
			if ( !_tweets.isEmpty() ) {
				jsonResp.put("tweets", _tweets);
				status = "successful";
			} else {
				throw new Exception("No results for query [" + queryString + "].");
			}
		} catch (TwitterException e) {
			jsonResp.put("exception", "Twitter exception. " + e.getMessage());
			e.printStackTrace();
			*/			
		} catch (Docx4JException e) {
			jsonResp.put("exception", "Docx4JException: " + e.getMessage());
			e.printStackTrace();
		} catch ( Exception e ) {
			jsonResp.put("exception", "General excpetion. " + e.getMessage());
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
		String prefix =  getServletContext().getRealPath("/");
		System.setProperty("tweets4discovery.rootPath", prefix);
	}				

	private  void addBorders(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);
 
        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        table.getTblPr().setTblBorders(borders);
    }
 
    private Tbl createTableWithContent() {
        Tbl table = factory.createTbl();
        Tr tableRow = factory.createTr();
 
        addTableCell(tableRow, "Field 1");
        addTableCell(tableRow, "Field 2");
 
        table.getContent().add(tableRow);
        return table;
    }
 
    private void addTableCell(Tr tableRow, String content) {
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
        wordMLPackage.getMainDocumentPart().
            createParagraphOfText(content));
        tableRow.getContent().add(tableCell);
    }
    
    private WordprocessingMLPackage  wordMLPackage;
    private ObjectFactory factory;    
}

package uk.ac.gold.tweets4discovery.servlets;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.google.gson.Gson;

/**
 * Servlet implementation class SearchServlet
 */
//@WebServlet("/search")
public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = -657016811446300443L;  	
	private static Logger LOG = Logger.getLogger(SearchServlet.class);
	private static int fileCounter = 0;
	private static final String FILE_NAME = "Tweets";
	private Gson gson = new Gson();

	/**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
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
        	
        	//create "download" folder if necessary
        	File directory = new File(rootPath);
        	if (directory.isDirectory() == false) {
        		directory.mkdir();
        	}
        	
			factory = Context.getWmlObjectFactory();
			wordMLPackage = WordprocessingMLPackage.createPackage();
	        
			// add credits
			Relationship relationship = createFooterPart();
	        createFooterReference(relationship);			
						
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Tweets 4 Discovery");
			//wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", "by Vilmos and Michel");
			/*
			P p = factory.createP();
			R r = factory.createR();
			
			Text text = factory.createText();
			text.setValue("Query: " + queryString);
			r.getContent().add(text);
			
			r.getContent().add(factory.createBr());
			
			text = factory.createText();
			text.setValue("Since: " + since);
			r.getContent().add(text);
			
			r.getContent().add(factory.createBr());
			
			text = factory.createText();
			text.setValue("Until: " + until);
			r.getContent().add(text);
			*/
			
			addQueryParams(queryString, since, until);
						
			//r.getContent().add(p);
			//wordMLPackage.getMainDocumentPart().addObject(r);
						
			Tbl table = factory.createTbl();
						
			Twitter twitter = new TwitterFactory().getInstance();
			Query query = new Query(queryString);
			query.setSince(since);
			query.setUntil(until);
			QueryResult result;
			List<Status> _tweets = new Vector<Status>();			

			do {
		        result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				
				// accumulate results for the JSON response 
				_tweets.addAll(tweets);
				
				for (Status tweet : tweets) {
			        Tr tr = factory.createTr();				   	 			        
			        addTableCell(tr, "@" + tweet.getUser().getScreenName());
			        addTableCell(tr, tweet.getUser().getName());
			        addTableCellWithWidth(tr, tweet.getText(), 4500);
			        addTableCell(tr, tweet.getCreatedAt().toString());
					//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
			        table.getContent().add(tr);			
				}				
			} while ((query = result.nextQuery()) != null);
			
			if ( !_tweets.isEmpty() ) {
				// JSON results
				jsonResp.put("tweets", _tweets);
				status = "successful";
				
				// DOCX results
				addBorders(table);
				wordMLPackage.getMainDocumentPart().addObject(table);
				
				String file = getFileName();
				wordMLPackage.save(new java.io.File(rootPath + "/" + file));
				
				jsonResp.put("docx", file);							
			} else {
				throw new Exception("Sorry, there are no tweets matching your search [" + queryString + "] between those dates (from [" + since + "] to [" + until + "]).");
			}
						
		} catch (TwitterException e) {
			jsonResp.put("exception", e.getMessage());
			e.printStackTrace();
		} catch (Docx4JException e) {
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
	
	private synchronized String getFileName() {
		return FILE_NAME + (fileCounter++) + ".docx";
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
		
    	String logsFolder = System.getProperty("tweets4discovery.rootPath") + "logs";
    	
    	//create "download" folder if necessary
    	File directory = new File(logsFolder);
    	if (directory.isDirectory() == false) {
    		directory.mkdir();
    		LOG.info("The logs folder [" + logsFolder + "] has been created.");    		
    	} else {
        	LOG.info("The logs folder is [" + logsFolder + "].");    		
    	}
		
	}				

    /**
     *  In this method we'll add the borders to the table.
     */
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
 
    private void addTableCell(Tr tableRow, String content) {
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
        wordMLPackage.getMainDocumentPart().
            createParagraphOfText(content));
        tableRow.getContent().add(tableCell);
    }
    
    /**
     *  In this method we create a cell and add the given content to it.
     *  If the given width is greater than 0, we set the width on the cell.
     *  Finally, we add the cell to the row.
     */
    private void addTableCellWithWidth(Tr row, String content, int width){
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(
                content));
 
        if (width > 0) {
            setCellWidth(tableCell, width);
        }
        row.getContent().add(tableCell);
    }
 
    /**
     *  In this method we create a table cell properties object and a table width
     *  object. We set the given width on the width object and then add it to
     *  the properties object. Finally we set the properties on the table cell.
     */
    private void setCellWidth(Tc tableCell, int width) {
        TcPr tableCellProperties = new TcPr();
        TblWidth tableWidth = new TblWidth();
        tableWidth.setW(BigInteger.valueOf(width));
        tableCellProperties.setTcW(tableWidth);
        tableCell.setTcPr(tableCellProperties);
    }
    
    /**
     *  This method creates a footer part and set the package on it. Then we add some
     *  text and add the footer part to the package. Finally we return the
     *  corresponding relationship.
     *
     *  @return
     *  @throws InvalidFormatException
     */
    private Relationship createFooterPart() throws InvalidFormatException {
        FooterPart footerPart = new FooterPart();
        footerPart.setPackage(wordMLPackage);
 
        footerPart.setJaxbElement(createFooter("by Vilmos Zsombori and Michael Frantzis"));
 
        return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
    }
 
    /**
     *  First we create a footer, a paragraph, a run and a text. We add the given
     *  given content to the text and add that to the run. The run is then added to
     *  the paragraph, which is in turn added to the footer. Finally we return the
     *  footer.
     *
     *  @param content
     *  @return
     */
    private Ftr createFooter(String content) {
        Ftr footer = factory.createFtr();
        P paragraph = factory.createP();
        R run = factory.createR();
        Text text = new Text();
        text.setValue(content);
        run.getContent().add(text);
        paragraph.getContent().add(run);
        footer.getContent().add(paragraph);
        return footer;
    }
 
    /**
     *  First we retrieve the document sections from the package. As we want to add
     *  a footer, we get the last section and take the section properties from it.
     *  The section is always present, but it might not have properties, so we check
     *  if they exist to see if we should create them. If they need to be created,
     *  we do and add them to the main document part and the section.
     *  Then we create a reference to the footer, give it the id of the relationship,
     *  set the type to header/footer reference and add it to the collection of
     *  references to headers and footers in the section properties.
     *
     * @param relationship
     */
    private void createFooterReference(Relationship relationship) {
        List<SectionWrapper> sections =
            wordMLPackage.getDocumentModel().getSections();
 
        SectPr sectionProperties = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectionProperties==null ) {
            sectionProperties = factory.createSectPr();
            wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
            sections.get(sections.size() - 1).setSectPr(sectionProperties);
        }
 
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectionProperties.getEGHdrFtrReferences().add(footerReference);
    }
    
    private void addQueryParams(String query, String since, String until) {
	    /*
		wordMLPackage.getMainDocumentPart().addParagraphOfText("Search term: " + queryString);
		wordMLPackage.getMainDocumentPart().addParagraphOfText("From: " + since);
		wordMLPackage.getMainDocumentPart().addParagraphOfText("To: " + until);
		*/
    	
	    Tbl table = factory.createTbl();	    
	    Tr tr;
	    
	    tr = factory.createTr();	 
	    addTableCell(tr, "Query string: ");
	    addTableCell(tr, query);	 
	    table.getContent().add(tr);

	    tr = factory.createTr();	 
	    addTableCell(tr, "From: ");
	    addTableCell(tr, since);	 
	    table.getContent().add(tr);

	    tr = factory.createTr();	 
	    addTableCell(tr, "To: ");
	    addTableCell(tr, until);	 
	    table.getContent().add(tr);
	    
	    wordMLPackage.getMainDocumentPart().addObject(table);		    	
    }
     
    private WordprocessingMLPackage  wordMLPackage;
    private ObjectFactory factory;
}
package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicPropertyDescriptor;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Puts some helper buttons around that RQL query box
 */
public class SwoleGSAAdminServlet extends GSAAdminServlet {
	private transient final GSARepository repo;

    //The strategy used here is to sniff for the output we want to inject our stuff right before
    private static final String RQL_TEXT_AREA_MARKUP = "<p><textarea rows=\"12\" cols=\"80\" name=\"xmltext\">";
    private static final String RQL_TEXT_AREA_MARKUP_BIGGER = "<p><textarea rows=\"20\" style=\"width: 100%\" name=\"xmltext\">";



    private static final String RQL_TOOLBAR_MARKUP = new Scanner(
        GSAAdminServlet.class.getClassLoader().getResourceAsStream("com/vitalize/atg/dynadmin/SwoleGSAAdminServletRQLToolBar.html"),
        "UTF-8"
    )
        .useDelimiter("\\A")
        .next();




	public SwoleGSAAdminServlet(
	    GSARepository service,
        ApplicationLogging logger,
        Nucleus nucleus,
        TransactionManager txManager
    ) {
		super(service, logger, nucleus, txManager);

		this.repo = service;
	}

    private void outputRQLToolbar(ServletOutputStream o) throws IOException {

        o.println("<script>");

        //Output a itemDescriptor taxonomy for the toolbars to use
        o.println("var itemDescriptors = {};");

        for(String n : repo.getItemDescriptorNames()){

            RepositoryItemDescriptor descriptor = null;

            try {

                descriptor = repo.getItemDescriptor(n);

            } catch (RepositoryException e){
                //TODO: log a warning (ATG stubs doesn't have logging signatures yet
            }

            //only output non error descriptors
            if(descriptor != null){
                o.println("itemDescriptors['" + n + "'] = {" );

                o.println("\tprops : {" );
                boolean firstProp = true;

                for(DynamicPropertyDescriptor property : descriptor.getPropertyDescriptors()){
                    o.print("\t\t");

                    if(!firstProp){
                        o.print(",");
                    }

                    firstProp = false;

                    o.println("'" + property.getName() + "' : {");

                    if(property instanceof RepositoryPropertyDescriptor){
                        RepositoryPropertyDescriptor repoProp = (RepositoryPropertyDescriptor)property;

                        o.println("\t\t\tqueryable : " + repoProp.isQueryable() + ",");
                        o.println("\t\t\tderived : " + repoProp.isDerived() + ",");
                        o.println("\t\t\tdefault : '" + repoProp.getDefaultValueString() + "'");
                    }

                    o.println("\t\t}");

                }

                o.println("\t}");

                o.println("};" );
            }

        }

        o.println("</script>");



        o.println(RQL_TOOLBAR_MARKUP);

    }



    class Query {
	    final String rql;
	    final String itemType;

	    Query(String rql, String itemType){
	        this.rql = rql;
	        this.itemType = itemType;
        }
    }

    /**
     * Overrides the printAdmin and injects and RQL toolbar above the RQL box. Also makes the RQL box a little bigger by default.
     * @param req
     * @param res
     * @param out
     * @throws ServletException
     * @throws IOException
     */
	@Override
	protected void printAdmin(
	    final HttpServletRequest req,
        HttpServletResponse res,
        final ServletOutputStream out
    ) throws ServletException, IOException {


	    //We use a hash set incase they have duplicate queries for some reason
	    final Queue<Query> queries = new LinkedList<Query>();

        String incomingQuery = req.getParameter("xmltext");


        //see if there's a query in the QSPs
        if(incomingQuery == null){
            String itemTypeQSP = req.getParameter("item-type");
            String rqlQueryQSP = req.getParameter("rql-query");

            if(itemTypeQSP != null && rqlQueryQSP != null){
                incomingQuery = "<query-items item-descriptor=\"" + itemTypeQSP + "\">" + rqlQueryQSP + "</query-items>";
            }
        }

        if(incomingQuery != null){

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><x>" + incomingQuery + "</x>").getBytes("UTF-8")));

                doc.normalizeDocument();


                NodeList childNodes = doc.getDocumentElement().getChildNodes();

                for(int i = 0; i < childNodes.getLength(); i++){
                    org.w3c.dom.Node child = childNodes.item(i);
                    if(child instanceof Element){
                        Element ele = (Element)child;

                        if("query-items".equals(ele.getTagName()) && ele.hasAttribute("item-descriptor")){
                            out.println( ">>>" + i + ": " + ele.getTextContent() + "<<<");
                            queries.add(new Query(ele.getTextContent().trim(), ele.getAttribute("item-descriptor").trim()));
                        }
                    }

                }


            } catch (ParserConfigurationException e) {

            } catch (SAXException e) {

            }
        }


        //TODO: Is it safe to assume the path is = service name?
        final String pathToThisComponent = this.formatServiceName(req.getPathInfo(), req);

        //I tried use HttpsRequestWrapper but it seems ATG has some funcitonality somewhere that
        //treats non DynamoHttpServlerRequests (or maybe JUST HttpRequestWrapper's) as some
        //special case and then seesm to call swapRequest on the actual DynamoHttpServletRequest
        //(not sure how it gets a handle to that since it's been wrapped..?
        //The dynamohttpservlet request seems to be a request wrapper itself..so
        //i used it and it worked..but might be causing some unexpected issues since it may not
        //be a fully compliant wrapper...i dunno.
        DynamoHttpServletRequest wrappedRequest = this.wrapRequest(req, res, incomingQuery);



		printAdminInternal(
            wrappedRequest,
            res,
            new DelegatingServletOutputStream(out){

                private boolean inPreCodeBlock = false;

                @Override
                public void println(String s) throws IOException {

                    if(RQL_TEXT_AREA_MARKUP.equals(s)) {

                        outputRQLToolbar(out);
                        //While we're at it...make that a bit bigger
                        s = RQL_TEXT_AREA_MARKUP_BIGGER;

                    } else if("</a><br>".equals(s)) {
                        s = "</a>, <a href=\"#RQL_TOOLBAR\">Jump to Query Box</a><br>";
                    } else if("<form method=\"POST\">".equals(s)){
                        s = "<form method=\"POST\" action=\"#RQL_RESULTS\">";
                    } else if("<pre><code>".equals(s)){
                        inPreCodeBlock = true;
                        out.println("<span id=\"RQL_RESULTS\"></span>");
                    } else if("</code></pre><p>".equals(s)){
                        inPreCodeBlock = false;
                    } else if(inPreCodeBlock && !queries.isEmpty()){
                        //this isn't the best way to do this i'm sure..but it's tricky because the text we link does not include
                        //the item descriptor...and we have to be sure to only link 1 line per query
                        //for example imagine two ALL RANGE 0+10 queries on 2 distinct items..if we ONLY matched on rql
                        //then both would match so how would we know which to link?
                        //This pattern works as long as there is only 1 query per line..and they are returned in order found

                        //To make order not matter we'd have to loop through the list of queries for each line
                        //

                        Query queryToMatch = queries.remove();

                        String[] lines = s.split("\n");
                        StringBuilder sb = new StringBuilder();

                        for(String  l : lines){
                            if(queryToMatch == null || l.trim().startsWith("<")){
                                //A lot of lines of data results start with < and never have queries
                                //so let's ignore those
                                //also this projects us in the unlikley case a piece of data has text matching our query RQL
                                sb.append(l);
                            } else if(l.contains(queryToMatch.rql)){
                                //it's in this line
                                sb.append(l.replace(queryToMatch.rql, "<a href=\"" + pathToThisComponent + "?item-type=" + URLEncoder.encode(queryToMatch.itemType, "UTF-8") + "&rql-query=" + URLEncoder.encode(queryToMatch.rql, "UTF-8") + "#RQL_RESULTS\">" + queryToMatch.rql + "</a>"));
                                //start looking for the next query

                                //last time through this will be empty
                                if(!queries.isEmpty()) {
                                    queryToMatch = queries.remove();
                                }
                            } else {
                                //not found in the line, just pass it through
                                sb.append(l);
                            }

                            //put that newline we split on back
                            sb.append("\n");
                        }

                        s = sb.toString();

                    }

                    out.println(s);


                }
            }
        );
	}



    /**
     * This is just here for testing so we can mock up the super interface
     * @param req
     * @param res
     * @param out
     * @throws ServletException
     * @throws IOException
     */
    protected void printAdminInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        ServletOutputStream out
    ) throws ServletException, IOException {

        super.printAdmin(
            req,
            res,
            out
        );
    }



    @Override
    protected void insertStyle(
        HttpServletRequest req,
        HttpServletResponse res,
        ServletOutputStream out
    ) throws ServletException, IOException {
        //add jquery
        out.println("<script src=\"https://code.jquery.com/jquery-1.12.4.min.js\" integrity=\"sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ=\" crossorigin=\"anonymous\"></script>");

        //add select2
        out.println("<link href=\"https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/css/select2.min.css\" rel=\"stylesheet\" />");
        out.println("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/js/select2.min.js\"></script>");

        //add fontawesome
        out.println("<link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\" rel=\"stylesheet\" />");

        super.insertStyle(req, res, out);
    }



    protected DynamoHttpServletRequest wrapRequest(
        final HttpServletRequest req,
        final HttpServletResponse res,
        final String overriddenXmlParamValue
    ){
        DynamoHttpServletRequest r = new DynamoHttpServletRequest(){

            @Override
            public boolean isBrowserType(String pFeature) {
                //this is super chatty in the logs, so let's just clear it out..i guess...
                return false;
            }

            @Override
            public String getParameter(String s) {
                if("xmltext".equals(s)){
                    return overriddenXmlParamValue;
                }
                return req.getParameter(s);
            }
        };
        r.setRequest(req);

        if(res instanceof DynamoHttpServletResponse){
            r.setResponse((DynamoHttpServletResponse) res);
        } else {
            DynamoHttpServletResponse wrappedResponse = new DynamoHttpServletResponse(res);
            wrappedResponse.setRequest(r);
            r.setResponse(wrappedResponse);
        }

        return r;

    }


}

package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.StringUtils;
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
import javax.servlet.http.HttpServletRequest;
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

    private void outputRQLToolbar(
        Map<String, RepositoryItemDescriptor> itemDescriptorLookup,
        ServletOutputStream o
    ) throws IOException {

        o.println("<script>");

        //Output a itemDescriptor taxonomy for the toolbars to use
        o.println("var itemDescriptors = {};");

        for(Map.Entry<String,RepositoryItemDescriptor> entry : itemDescriptorLookup.entrySet()){

            RepositoryItemDescriptor descriptor = entry.getValue();

            //only output non error descriptors
            if(descriptor != null){
                o.println("itemDescriptors['" + entry.getKey() + "'] = {" );

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
                        o.println("\t\t\tdefault : '" + repoProp.getDefaultValueString() + "',");
                        o.println("\t\t\ttypeName : '" + repoProp.getTypeName() + "',");
                        o.println("\t\t\ttypeClass : '" + repoProp.getPropertyType().getName() + "',");
                        o.println("\t\t\ttypeComponentClass : '" + (repoProp.getComponentPropertyType() == null ? "" : repoProp.getComponentPropertyType().getName()) + "',");
                        o.println("\t\t\titemDescriptorName : '" + (repoProp.getPropertyItemDescriptor() == null ? "" : repoProp.getPropertyItemDescriptor().getItemDescriptorName()) + "',");
                        o.println("\t\t\tcomponentItemDescriptorName : '" + (repoProp.getComponentItemDescriptor() == null ? "" : repoProp.getComponentItemDescriptor().getItemDescriptorName()) + "'");

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

        /**
         * In the pre code a few escapes are performed
         */
	    final String rqlInPreCode;
	    final String itemType;

	    Query(String rql, String itemType){
	        this.rql = rql;
	        this.itemType = itemType;

	        //TODO: not sure how to unit test this is actually happening..maybe a protected method on SwoleGASAdminServler that i can override in tests?
	        this.rqlInPreCode = StringUtils.escapeHtmlString(rql);
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


        final Map<String, RepositoryItemDescriptor> itemDescriptorLookup = new HashMap<String, RepositoryItemDescriptor>();

        for(String n : repo.getItemDescriptorNames()) {

            try {

                itemDescriptorLookup.put(
                    n,
                    repo.getItemDescriptor(n)
                );

            } catch (RepositoryException e) {
                //TODO: log a warning (ATG stubs doesn't have logging signatures yet
            }
        }

            //We use a hash set in case they have duplicate queries for some reason
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
                            queries.add(new Query(ele.getTextContent(), ele.getAttribute("item-descriptor")));
                        }
                    }

                }


            } catch (ParserConfigurationException e) {

            } catch (SAXException e) {

            }
        }


        //TODO: Is it safe to assume the path is = service name?
        final String pathToThisComponent = this.formatServiceName(req.getPathInfo(), req);


		printAdminInternal(
            this.wrapRequest(req, res, incomingQuery),
            res,
            new DelegatingServletOutputStream(out){

                private boolean inPreCodeBlock = false;
                private Map<String, RepositoryPropertyDescriptor> currentItemPropertyDescriptors = Collections.emptyMap();

                @Override
                public void println(String s) throws IOException {

                    if(s == null){
                        out.println(s);
                        return;
                    }

                    if(RQL_TEXT_AREA_MARKUP.equals(s)) {

                        outputRQLToolbar(
                            itemDescriptorLookup,
                            out
                        );

                        //While we're at it...make that a bit bigger
                        s = RQL_TEXT_AREA_MARKUP_BIGGER;

                    } else if("</a><br>".equals(s)) {
                        s = "</a>, <a href=\"#RQL_TOOLBAR\">Jump to Query Box</a><br>";
                    } else if("<form method=\"POST\">".equals(s)){
                        s = "<form method=\"POST\" action=\"#RQL_RESULTS\">";
                    } else if("<pre><code>".equals(s)){
                        inPreCodeBlock = true;
                        out.println("<span id=\"RQL_RESULTS\"></span>");
                    } else if("</code></pre><p>".equals(s)) {
                        inPreCodeBlock = false;
                    } else if (inPreCodeBlock && s.trim().startsWith("&lt;add-item item-descriptor=&quot;")) {
                        String itemType = s.trim().replace("&lt;add-item item-descriptor=&quot;", "").split("&quot;")[0];

                        if(itemDescriptorLookup.containsKey(itemType)) {
                            currentItemPropertyDescriptors = new HashMap<String, RepositoryPropertyDescriptor>();

                            for(DynamicPropertyDescriptor p : itemDescriptorLookup.get(itemType).getPropertyDescriptors()){
                                if(p instanceof RepositoryPropertyDescriptor) {
                                    currentItemPropertyDescriptors.put(
                                        p.getName(),
                                        (RepositoryPropertyDescriptor)p
                                    );
                                }
                            }
                        }


                    } else if (inPreCodeBlock && s.trim().startsWith("&lt;/add-item&gt;")) {
                        //clear it out when we get out of an add item
                        currentItemPropertyDescriptors = Collections.emptyMap();
                    } else if (s.contains("<set-property name=\"")){


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
                                //also this projects us in the unlikely case a piece of data has text matching our query RQL
                                sb.append(l);
                            } else if(l.contains(queryToMatch.rqlInPreCode)){
                                //it's in this line
                                sb.append(l.replace(queryToMatch.rqlInPreCode, "<a href=\"" + pathToThisComponent + "?item-type=" + URLEncoder.encode(queryToMatch.itemType, "UTF-8") + "&rql-query=" + URLEncoder.encode(queryToMatch.rql, "UTF-8") + "#RQL_RESULTS\">" + queryToMatch.rqlInPreCode + "</a>"));
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

        //The simple case
        if(req instanceof DynamoHttpServletRequest){
            return new DelegatingDynamoServletRequest((DynamoHttpServletRequest)req) {

                @Override
                public String getParameter(String s) {
                    if ("xmltext".equals(s)) {
                        return overriddenXmlParamValue;
                    }
                    return req.getParameter(s);
                }
            };
        }


        //This generally works..but i've seen issues where some urls are creates as /dyn/admin/dyn/admin
        //this only comes up if someone wrapped the request in the servlet chain..so let's wait till someone does that
        //and complains
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

package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.Scanner;

/**
 * Puts some helper buttons around that RQL query box
 */
public class BestGSAAdminServletOfAllTime extends GSAAdminServlet {
	private transient final GSARepository repo;

    //The strategy used here is to sniff for the output we want to inject our stuff right before
    private static final String RQL_TEXT_AREA_MARKUP = "<p><textarea rows=\"12\" cols=\"80\" name=\"xmltext\">";
    private static final String WIDER_TALLER_RQL_TEXT_AREA_MARKUP = "<p><textarea rows=\"20\" cols=\"160\" name=\"xmltext\">";

    private static final String[] RQL_ACTION_TYPES = {
        "query-items",
        "add-item",
        "remove-item",
        "update-item"
    };


    //TODO: make this an external script?
    private static final String RQL_TOOLBAR_SCRIPT = new Scanner(
            GSAAdminServlet.class.getClassLoader().getResourceAsStream("com/vitalize/atg/dynadmin/BestGSAAdminServletOfAllTime.js"),
            "UTF-8"
        )
        .useDelimiter("\\A")
        .next();




	public BestGSAAdminServletOfAllTime(GSARepository pService, ApplicationLogging pLogger, Nucleus pNucleus, TransactionManager pTransactionManager) {
		super(pService, pLogger, pNucleus, pTransactionManager);

		this.repo = pService;
	}

    private void outputRQLToolbar(ServletOutputStream o) throws IOException {

        o.println("<script>");
        o.println(RQL_TOOLBAR_SCRIPT);
        o.println("</script>");

        o.println("<div>");
        o.println("<a href=\"https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGRepositoryGuide/html/s0305rqloverview01.html\" target=\"_blank\">Y U FORGET RQL?</a>");
        o.println("</div>");

        o.println("<div>");
        o.println("<select id=\"RQL_ITEM_TYPE\" style=\"width: 40%\">");

        for(String d : repo.getItemDescriptorNames()){
            o.println("<option>" + d + "</option>");
        }
        o.println("</select>");


        o.println("<select id=\"RQL_ACTION_TYPE\" style=\"width: 20%\">");

        for(String action : RQL_ACTION_TYPES){
            o.println("<option>" + action + "</option>");
        }
        o.println("</select>");



        o.println("<button onclick=\"rqlAdd();return false;\">Add</button>");
        o.println("<button onclick=\"rqlClear();return false;\">Clear</button>");
        o.println("<input type=\"submit\" value=\"Go\"/>");
        o.println("<br/>");
        o.println("</div>");


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
	    HttpServletRequest req,
        HttpServletResponse res,
        final ServletOutputStream out
    ) throws ServletException, IOException {

		printAdminInternal(
            req,
            res,
            new DelegatingServletOutputStream(out){

                @Override
                public void println(String s) throws IOException {

                    if(RQL_TEXT_AREA_MARKUP.equals(s)) {

                        outputRQLToolbar(out);
                        //While we're at it...make that a bit bigger
                        out.println(WIDER_TALLER_RQL_TEXT_AREA_MARKUP);

                    } else {
                        //otherwise just pass it on through
                        out.println(s);
                    }



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


        super.insertStyle(req, res, out);
    }






}

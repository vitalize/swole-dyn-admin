package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicPropertyDescriptor;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;

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




	public SwoleGSAAdminServlet(GSARepository pService, ApplicationLogging pLogger, Nucleus pNucleus, TransactionManager pTransactionManager) {
		super(pService, pLogger, pNucleus, pTransactionManager);

		this.repo = pService;
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
                        out.println(RQL_TEXT_AREA_MARKUP_BIGGER);

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

        //add fontawesome
        out.println("<link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\" rel=\"stylesheet\" />");

        super.insertStyle(req, res, out);
    }






}

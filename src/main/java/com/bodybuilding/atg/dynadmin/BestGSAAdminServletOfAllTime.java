package com.bodybuilding.atg.dynadmin;

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

/**
 * Puts some helper buttons around that RQL query box
 */
public class BestGSAAdminServletOfAllTime extends GSAAdminServlet {
	private final GSARepository repo;

	public BestGSAAdminServletOfAllTime(Object pService, ApplicationLogging pLogger, Nucleus pNucleus, TransactionManager pTransactionManager) {
		super(pService, pLogger, pNucleus, pTransactionManager);

		this.repo = (GSARepository)pService;
	}

	@Override
	protected void printAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse, ServletOutputStream pOut) throws ServletException, IOException {



		super.printAdmin(pRequest, pResponse, new DelegatingServletOutputStream(pOut));
	}

	private static final String[] RQL_ACTION_TYPES = {
		"query-items",
		"add-item",
		"remove-item",
		"update-item"
	};


	//TODO: make this an external script?
	private static final String DO_RQL_ACTION_SCRIPT = "<script>" +
		"function doRQLAction(){" +
		"var typeSelector = document.getElementById('RQL_ITEM_TYPE');" +
		"var selectedType = typeSelector.options[typeSelector.selectedIndex].text;" +
		"" +
		"var actionSelector = document.getElementById('RQL_ACTION_TYPE');" +
		"var selectedAction = actionSelector.options[actionSelector.selectedIndex].text;" +
		"" +
		"" +
		//add,remove,update all have an id="" attribute...but query does not
		"var idAttr = selectedAction == 'query-items' ? '' : ' id=\"\"';" +
		"" +
		"var rql = '<' + selectedAction + ' item-descriptor=\"' + selectedType + '\"' + idAttr + '>';" +
		"rql += '</' + selectedAction + '>';" +
		"" +
		"document.getElementsByName('xmltext')[0].value = rql;" +
		"}" +
		"</script>";

	private void printRQLControls(ServletOutputStream o) throws IOException {
		o.println("<div>");
		o.println("<select id=\"RQL_ITEM_TYPE\">");

		for(String d : repo.getItemDescriptorNames()){
			o.println("<option>" + d + "</option>");
		}
		o.println("</select>");


		o.println("<select id=\"RQL_ACTION_TYPE\">>");

		for(String action : RQL_ACTION_TYPES){
			o.println("<option>" + action + "</option>");
		}
		o.println("</select>");

		o.println(DO_RQL_ACTION_SCRIPT);

		o.println("<button onclick=\"doRQLAction();return false;\">GO</button>");
		o.println("<br/>");
		o.println("</div>");

		o.println("<div>");
		o.println("<a href=\"https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGRepositoryGuide/html/s0305repositoryquerylanguage01.html\" target=\"_blank\">Y U FORGET RQL?</a>");
		o.println("</div>");
	}

	//The strategy used here is to sniff for the output we want to inject our stuff right before
	private static final String MARKER = "<p><textarea rows=\"12\" cols=\"80\" name=\"xmltext\">";

	private class DelegatingServletOutputStream extends ServletOutputStream {

		private final ServletOutputStream delegate;

		public DelegatingServletOutputStream(ServletOutputStream o){
			delegate = o;
		}

		@Override
		public void print(String s) throws IOException {
			delegate.print(s);
		}

		@Override
		public void print(boolean b) throws IOException {
			delegate.print(b);
		}

		@Override
		public void print(char c) throws IOException {
			delegate.print(c);
		}

		@Override
		public void print(int i) throws IOException {
			delegate.print(i);
		}

		@Override
		public void print(long l) throws IOException {
			delegate.print(l);
		}

		@Override
		public void print(float f) throws IOException {
			delegate.print(f);
		}

		@Override
		public void print(double d) throws IOException {
			delegate.print(d);
		}

		@Override
		public void println() throws IOException {
			delegate.println();
		}

		@Override
		public void println(String s) throws IOException {
			if(MARKER.equals(s)){
				printRQLControls(delegate);
			}

			delegate.println(s);
		}

		@Override
		public void println(boolean b) throws IOException {
			delegate.println(b);
		}

		@Override
		public void println(char c) throws IOException {
			delegate.println(c);
		}

		@Override
		public void println(int i) throws IOException {
			delegate.println(i);
		}

		@Override
		public void println(long l) throws IOException {
			delegate.println(l);
		}

		@Override
		public void println(float f) throws IOException {
			delegate.println(f);
		}

		@Override
		public void println(double d) throws IOException {
			delegate.println(d);
		}

		@Override
		public void write(int b) throws IOException {
			delegate.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			delegate.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			delegate.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			delegate.flush();
		}

		@Override
		public void close() throws IOException {
			delegate.close();
		}
	}
}

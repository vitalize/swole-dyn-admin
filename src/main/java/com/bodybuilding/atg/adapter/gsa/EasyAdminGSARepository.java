package com.bodybuilding.atg.adapter.gsa;

import atg.adapter.gsa.GSARepository;
import com.bodybuilding.atg.dynadmin.BestGSAAdminServletOfAllTime;

import javax.servlet.Servlet;

/**
 * Wraps the {@link GSARepository} exporting the {@link BestGSAAdminServletOfAllTime} as it's admin servlet
 */
public class EasyAdminGSARepository extends GSARepository {
	@Override
	protected Servlet createAdminServlet() {
		return new BestGSAAdminServletOfAllTime(this, this, this.getNucleus(), this.getTransactionManager());
	}
}

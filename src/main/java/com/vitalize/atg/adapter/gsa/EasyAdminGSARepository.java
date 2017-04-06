package com.vitalize.atg.adapter.gsa;

import atg.adapter.gsa.GSARepository;
import com.vitalize.atg.dynadmin.BestGSAAdminServletOfAllTime;

import javax.servlet.Servlet;

/**
 * Wraps the {@link GSARepository} exporting the {@link BestGSAAdminServletOfAllTime} as it's admin servlet.
 * Change your GSARepository nucleus component config so the $class value is com.vitalize.atg.adapter.gsa.EasyAdminGSARepository
 * instead of atg.adapter.gsa.GSARepository
 */
public class EasyAdminGSARepository extends GSARepository {
	@Override
	protected Servlet createAdminServlet() {
		return new BestGSAAdminServletOfAllTime(this, this, this.getNucleus(), this.getTransactionManager());
	}
}

package com.vitalize.atg.adapter.gsa;

import com.vitalize.atg.dynadmin.BestGSAAdminServletOfAllTime;
import org.junit.Test;
import static org.junit.Assert.*;

public class EasyAdminGSARepositoryTest {

    @Test
    public void testCreateAdminServlet(){

        EasyAdminGSARepository subject = new EasyAdminGSARepository();

        javax.servlet.Servlet actual = subject.createAdminServlet();

        assertTrue("Better be the best admin servlet of all time", actual instanceof BestGSAAdminServletOfAllTime);

        BestGSAAdminServletOfAllTime bestServlet = (BestGSAAdminServletOfAllTime)actual;

        //TODO: test that the repo, logger, nucleus, and transaction manager are those from the EasyAdminGSARepository
        //Could do that through reflection (yuck), or maybe add public accessors to our servlet (also yuck)

    }
}

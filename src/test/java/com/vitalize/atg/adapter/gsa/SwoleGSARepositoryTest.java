package com.vitalize.atg.adapter.gsa;

import com.vitalize.atg.dynadmin.SwoleGSAAdminServlet;
import org.junit.Test;
import static org.junit.Assert.*;

public class SwoleGSARepositoryTest {

    @Test
    public void testCreateAdminServlet(){

        SwoleGSARepository subject = new SwoleGSARepository();

        javax.servlet.Servlet actual = subject.createAdminServlet();

        assertTrue("Better be the best admin servlet of all time", actual instanceof SwoleGSAAdminServlet);

        SwoleGSAAdminServlet bestServlet = (SwoleGSAAdminServlet)actual;

        //TODO: test that the repo, logger, nucleus, and transaction manager are those from the EasyAdminGSARepository
        //Could do that through reflection (yuck), or maybe add public accessors to our servlet (also yuck)

    }
}

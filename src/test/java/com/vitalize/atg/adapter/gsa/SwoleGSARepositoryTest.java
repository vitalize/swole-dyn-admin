package com.vitalize.atg.adapter.gsa;

import atg.nucleus.Nucleus;
import com.vitalize.atg.dynadmin.SwoleGSAAdminServlet;
import org.junit.Test;

import javax.transaction.TransactionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SwoleGSARepositoryTest {

    @Test
    public void testCreateAdminServlet(){

        final Nucleus fakeNucleus = new Nucleus(null);

        final TransactionManager fakeTxMgr = mock(TransactionManager.class);

        SwoleGSARepository subject = new SwoleGSARepository(){

            @Override
            public Nucleus getNucleus() {
                return fakeNucleus;
            }

            @Override
            public TransactionManager getTransactionManager() {
                return fakeTxMgr;
            }
        };

        javax.servlet.Servlet actual = subject.createAdminServlet();

        assertTrue("Better be the best admin servlet of all time", actual instanceof SwoleGSAAdminServlet);

        SwoleGSAAdminServlet bestServlet = (SwoleGSAAdminServlet)actual;

        //TODO: test that the repo, logger, nucleus, and transaction manager are those from the EasyAdminGSARepository
        //Could do that through reflection (yuck), or maybe add public accessors to our servlet (also yuck)

    }
}

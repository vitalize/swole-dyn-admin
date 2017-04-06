package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


public class BestGSAAdminServletOfAllTimeTest {

    static String EXPECTED_SCRIPT = new Scanner(
            GSAAdminServlet.class.getClassLoader().getResourceAsStream("com/vitalize/atg/dynadmin/BestGSAAdminServletOfAllTime.js"),
            "UTF-8"
        )
        .useDelimiter("\\A")
        .next();

    @Mock
    GSARepository mockGSARepo;

    @Mock
    ApplicationLogging mockLogger;

    @Mock
    Nucleus mockNucleus;

    @Mock
    TransactionManager mockTxManager;

    @Mock
    HttpServletRequest mockRequest;

    @Mock
    HttpServletResponse mockResponse;

    @Mock
    ServletOutputStream mockOutputStream;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrintAdminRQLBoxNotPresent() throws ServletException, IOException {

        BestGSAAdminServletOfAllTime subject = new BestGSAAdminServletOfAllTime(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest pRequest, HttpServletResponse pResponse, ServletOutputStream pOut) throws ServletException, IOException {

                //instead of calling super pretend i did
                pOut.println("not the text we care about");
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );

        //Make sure nothing extra was added
        verify(mockOutputStream, times(1)).println(anyString());

    }



    @Test
    public void testPrintAdminRQLBoxPresent() throws ServletException, IOException {

        String[] descriptors = new String[]{
            "dogs",
            "cats"
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(descriptors);

        BestGSAAdminServletOfAllTime subject = new BestGSAAdminServletOfAllTime(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest req, HttpServletResponse res, ServletOutputStream out) throws ServletException, IOException {

                //instead of calling super pretend i did
                out.println("<p><textarea rows=\"12\" cols=\"80\" name=\"xmltext\">");

                out.println("some other content");
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );

        //Make sure the better rql box is output
        verify(mockOutputStream, times(1)).println("<p><textarea rows=\"20\" cols=\"160\" name=\"xmltext\">");

        for(String d: descriptors) {
            //make sure all the descriptors are out there
            verify(mockOutputStream, times(1)).println("<option>" + d + "</option>");
        }

        assertThat(
            EXPECTED_SCRIPT,
            not(
                isEmptyOrNullString()
            )
        );

        verify(mockOutputStream, times(1)).println(EXPECTED_SCRIPT);

        //make sure we ouput the RQL help
        verify(mockOutputStream, times(1)).println("<a href=\"https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGRepositoryGuide/html/s0305repositoryquerylanguage01.html\" target=\"_blank\">Y U FORGET RQL?</a>");


        verify(mockOutputStream, times(1)).println("some other content");
    }



}

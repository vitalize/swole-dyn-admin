package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicBeanDescriptor;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyDescriptor;
import atg.core.exception.ContainerException;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.repository.Repository;
import atg.repository.RepositoryItemDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


public class SwoleGSAAdminServletTest {

    static String EXPECTED_SCRIPT = new Scanner(
            GSAAdminServlet.class.getClassLoader().getResourceAsStream("com/vitalize/atg/dynadmin/SwoleGSAAdminServlet.js"),
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

        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
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
    public void testPrintAdminRQLBoxPresent() throws ServletException, IOException, ContainerException.RepositoryException {

        String[] descriptors = new String[]{
            "dogs",
            "cats"
        };

        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(descriptors);

        RepositoryItemDescriptor fakeCatsProp = new FakeRepositoryItemDescriptor("cats");
        when(mockGSARepo.getItemDescriptor("cats"))
            .thenReturn(fakeCatsProp);


        RepositoryItemDescriptor fakeDogsProp = new FakeRepositoryItemDescriptor("dogs");
        when(mockGSARepo.getItemDescriptor("dogs"))
            .thenReturn(fakeDogsProp);



        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
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


        String allOutput = allOutputBuilder.toString();

        assertThat(
            allOutput,
            containsString("<p><textarea rows=\"20\" cols=\"160\" name=\"xmltext\">")
        );


        for(String d: descriptors) {
            verify(mockOutputStream, times(1)).println("itemDescriptors['" + d + "'] = {\n");

        }

        assertThat(
            EXPECTED_SCRIPT,
            not(
                isEmptyOrNullString()
            )
        );

        assertThat(
            allOutput,
            containsString(EXPECTED_SCRIPT)
        );


        assertThat(
            allOutput,
            containsString("<a href=\"https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGRepositoryGuide/html/s0305rqloverview01.html\" target=\"_blank\">Y U FORGET RQL?</a>")
        );


        verify(mockOutputStream, times(1)).println("some other content");

    }



    class FakeRepositoryItemDescriptor implements RepositoryItemDescriptor {

        private final String name;
        private final HashMap<String, DynamicPropertyDescriptor> descriptors = new HashMap<String, DynamicPropertyDescriptor>();

        public FakeRepositoryItemDescriptor(String name, DynamicPropertyDescriptor... descriptors){
            this.name = name;
            for(DynamicPropertyDescriptor d : descriptors){
                this.descriptors.put(d.getName(), d);
            }
        }


        public String getItemDescriptorName() {
            return name;
        }


        public DynamicPropertyDescriptor[] getPropertyDescriptors() {
            return descriptors.values().toArray(new DynamicPropertyDescriptor[]{});
        }

        public boolean hasProperty(String s) {
            return descriptors.containsKey(s);
        }

        public String[] getPropertyNames() {
            return descriptors.keySet().toArray(new String[]{});
        }

        public DynamicPropertyDescriptor getPropertyDescriptor(String s) {
            return descriptors.get(s);
        }

        //Begin non-implemented things
        public DynamicBeanDescriptor getBeanDescriptor() {
            return null;
        }

        public Repository getRepository() {
            return null;
        }

        public boolean isInstance(Object o) {
            return false;
        }

        public boolean areInstances(DynamicBeanInfo dynamicBeanInfo) {
            return false;
        }
    }

}

package com.vitalize.atg.dynadmin;

import atg.adapter.gsa.GSAAdminServlet;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicBeanDescriptor;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyDescriptor;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.repository.*;
import atg.servlet.DynamoHttpServletRequest;
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
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


public class SwoleGSAAdminServletTest {

    static String EXPECTED_MARKUP = new Scanner(
            GSAAdminServlet.class.getClassLoader().getResourceAsStream("com/vitalize/atg/dynadmin/SwoleGSAAdminServletRQLToolBar.html"),
            "UTF-8"
        )
        .useDelimiter("\\A")
        .next();

    @Mock
    private GSARepository mockGSARepo;

    @Mock
    private ApplicationLogging mockLogger;

    @Mock
    private Nucleus mockNucleus;

    @Mock
    private TransactionManager mockTxManager;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private ServletOutputStream mockOutputStream;

    @Mock
    private DynamoHttpServletRequest mockDynamoRequest;


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

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );

        //Make sure nothing extra was added
        verify(mockOutputStream, times(1)).println(anyString());

    }



    @Test
    public void testPrintAdminRQLBoxPresent() throws ServletException, IOException, RepositoryException {

        String[] descriptors = new String[]{
            "dogs",
            "cats"
        };

        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(descriptors);

        RepositoryItemDescriptor fakeCatsProp = new FakeRepositoryItemDescriptor(
            "cats",
            mockGSARepo
        );

        when(mockGSARepo.getItemDescriptor("cats"))
            .thenReturn(fakeCatsProp);


        RepositoryItemDescriptor fakeDogsProp = new FakeRepositoryItemDescriptor(
            "dogs",
            mockGSARepo
        );

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

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
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
            containsString("<p><textarea rows=\"20\" style=\"width: 100%\" name=\"xmltext\">")
        );


        for(String d: descriptors) {
            verify(mockOutputStream, times(1)).println("itemDescriptors['" + d + "'] = {");

        }

        assertThat(
            EXPECTED_MARKUP,
            not(
                isEmptyOrNullString()
            )
        );

        assertThat(
            allOutput,
            containsString(EXPECTED_MARKUP)
        );


        assertThat(
            allOutput,
            containsString("<a href=\"https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGRepositoryGuide/html/s0305rqloverview01.html\" target=\"_blank\">")
        );


        verify(mockOutputStream, times(1)).println("some other content");

    }


    @Test
    public void testPrintLinkToRQLBox() throws ServletException, IOException, RepositoryException {


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
            .thenReturn(new String[0]);


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

                out.println("</a><br>");
            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();


        assertThat(
            "toolbar link should be in output",
            allOutput,
            containsString("</a>, <a href=\"#RQL_TOOLBAR\">Jump to Query Box</a><br>")
        );


    }


    @Test
    public void testFormPostsToResultsFragment() throws ServletException, IOException, RepositoryException {


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
            .thenReturn(new String[0]);


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

                out.println("<form method=\"POST\">");
            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();


        assertThat(
            "form must post to RQL_RESULTS fragment",
            allOutput,
            containsString("<form method=\"POST\" action=\"#RQL_RESULTS\">")
        );


    }


    @Test
    public void testLinkToQueriesNoXMLRequest() throws ServletException, IOException, RepositoryException {


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
            .thenReturn(new String[0]);



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
                out.println("<set-property\n<set-property");
            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
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
            containsString("<set-property\n<set-property")
        );

    }

    static final String PRECODE_MARKUP_ENTER = "<pre><code>";
    static final String PRECODE_MARKUP_EXIT = "</code></pre><p>";

    @Test
    public void testLinkToQueriesEnsureOnlyPreCodeStuffIsMatched() throws ServletException, IOException, RepositoryException {


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
            .thenReturn(new String[0]);



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
                //Once before
                out.println("before ALL RANGE 0+10 before");

                //Once during
                out.println(PRECODE_MARKUP_ENTER);
                out.println("during ALL RANGE 0+10 during");

                //Once after
                out.println(PRECODE_MARKUP_EXIT);
                out.println("after ALL RANGE 0+10 after");

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "/path/to/component/service";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockRequest.getParameter("xmltext"))
            .thenReturn("<query-items item-descriptor=\"dog\">ALL RANGE 0+10</query-items>");

        String fakePathInfo = "/path/to/stuff";
        when(mockRequest.getPathInfo())
            .thenReturn(fakePathInfo);

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();

        assertThat(
            "text before must NOT have been altered",
            allOutput,
            containsString("before ALL RANGE 0+10 before")
        );


        assertThat(
            "text after must NOT have been altered",
            allOutput,
            containsString("after ALL RANGE 0+10 after")
        );


        assertThat(
            "text during must have been altered",
            allOutput,
            not(containsString("during ALL RANGE 0+10 during"))
        );


        assertThat(
            "text during must have been altered",
            allOutput,
            containsString("during <a href=\"" + "/path/to/component/service" + "?item-type=dog&rql-query=ALL+RANGE+0%2B10#RQL_RESULTS\">ALL RANGE 0+10</a> during")
        );

        assertThat(
            "a RQL results target should be created",
            allOutput,
            containsString("<span id=\"RQL_RESULTS\"></span>")
        );

    }




    @Test
    public void testQueryQSPsAreRespected() throws ServletException, IOException, RepositoryException {


        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest req, HttpServletResponse res, ServletOutputStream out) {

                assertEquals(
                    "xmltext must be simulated from QSP params",
                    "<query-items item-descriptor=\"dog\">ALL RANGE 1+10</query-items>",
                    req.getParameter("xmltext")
                );
            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }


            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                assertEquals(
                    "xmltext must be simulated from QSP params",
                    "<query-items item-descriptor=\"dog\">ALL RANGE 1+10</query-items>",
                    xml
                );

                when(mockDynamoRequest.getParameter("xmltext"))
                    .thenReturn(xml);

                return mockDynamoRequest;
            }
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        when(mockRequest.getParameter("item-type"))
            .thenReturn("dog");

        when(mockRequest.getParameter("rql-query"))
            .thenReturn("ALL RANGE 1+10");

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


    }

    @Test
    public void testQueryQSPsAreIgnoredIfItemTypeIsMissing() throws ServletException, IOException, RepositoryException {


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
                assertNull(
                    "xmltext must not be simulated if item-type not present",
                    req.getParameter("xmltext")
                );

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };



        when(mockRequest.getParameter("rql-query"))
            .thenReturn("ALL RANGE 1+10");

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


    }


    @Test
    public void testQueryQSPsAreIgnoredIfRqlQueryIsMissing() throws ServletException, IOException, RepositoryException {


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
                assertNull(
                    "xmltext must not be simulated if rql-query not present",
                    req.getParameter("xmltext")
                );

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }


            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        when(mockRequest.getParameter("item-type"))
            .thenReturn("dog");


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


    }


    @Test
    public void testQueryQSPsAreIgnoredIfBothParamsAreMissing() throws ServletException, IOException, RepositoryException {


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
                assertNull(
                    "xmltext must not be simulated if item-type nad rql-query are not present",
                    req.getParameter("xmltext")
                );

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


    }



    @Test
    public void testQueryQSPsAreIgnoredIfXmlTextParamPresent() throws ServletException, IOException, RepositoryException {

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
                assertEquals(
                    "xmltext must pass through when present",
                    "<query-items item-descriptor=\"cat\">ALL RANGE 10+20</query-items>",
                    req.getParameter("xmltext")
                );

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }


            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                assertEquals(
                    "xmltext must pass through when present",
                    "<query-items item-descriptor=\"cat\">ALL RANGE 10+20</query-items>",
                    xml
                );

                when(mockDynamoRequest.getParameter("xmltext"))
                    .thenReturn(xml);

                return mockDynamoRequest;
            }
        };

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{});

        when(mockRequest.getParameter("xmltext"))
            .thenReturn("<query-items item-descriptor=\"cat\">ALL RANGE 10+20</query-items>");


        when(mockRequest.getParameter("item-type"))
            .thenReturn("dog");

        when(mockRequest.getParameter("rql-query"))
            .thenReturn("ALL RANGE 1+10");

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


    }



    @Test
    public void testLinkToQueriesEnsurePropertyValuesAreNotMatched() throws ServletException, IOException, RepositoryException {


        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[0]);



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

                out.println(PRECODE_MARKUP_ENTER);

                out.println("&lt;set-property name=\"seoTags\"><![CDATA[ALL RANGE 0+10]]></set-property>");
                out.println("&lt;!-- export is false   <set-property name=\"version\"><![CDATA[ALL RANGE 0+10]]></set-property>  -->");

                out.println(PRECODE_MARKUP_EXIT);

            }

            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }


            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockRequest.getParameter("xmltext"))
            .thenReturn("<query-items item-descriptor=\"dog\">ALL RANGE 0+10</query-items>");

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();


        assertThat(
            "matching property text after must NOT have been altered even though content matches",
            allOutput,
            containsString("&lt;set-property name=\"seoTags\"><![CDATA[ALL RANGE 0+10]]></set-property>")
        );


        assertThat(
            "matching property text before must NOT have been altered even though content matches",
            allOutput,
            containsString("&lt;!-- export is false   <set-property name=\"version\"><![CDATA[ALL RANGE 0+10]]></set-property>  -->")
        );


    }


    @Test
    public void testLinkToQueriesRQLInProperty() throws ServletException, IOException, RepositoryException {


        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());

        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[0]);


        final String someText = UUID.randomUUID().toString();


        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            @Override
            protected String formatServiceName(String serviceName, HttpServletRequest req) {
                return "";
            }

            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest req, HttpServletResponse res, ServletOutputStream out) throws ServletException, IOException {
                out.println(someText);

            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };

        when(mockRequest.getParameter("xmltext"))
            .thenReturn("<dog>i'm not valid xml </car>");

        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();

        assertThat(
            "invalid xml/rql should not stop output from happening",
            allOutput,
            containsString(someText)
        );

    }



    @Test
    public void testPropsOfRepoItemAreLinked() throws ServletException, IOException, RepositoryException {


        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());


        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{
                "theItemType"
            });

        final String fakeGSARepoPath = "/some/path/to/Repo";

        when(mockGSARepo.getAbsoluteName())
            .thenReturn(fakeGSARepoPath);



        RepositoryPropertyDescriptor fakePropWritableSingleValue = new FakeRepositoryPropertyDescriptor(
            "propWritableSingleValue",
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propWritableSingleValueItemType",
                mockGSARepo
            ),
            null,
            null
        );

        RepositoryPropertyDescriptor fakePropReadOnlySingleValue = new FakeRepositoryPropertyDescriptor(
            "propReadOnlySingleValue",
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propReadOnlySingleValueItemType",
                mockGSARepo
            ),
            null,
            null
        );

        RepositoryPropertyDescriptor fakePropWritableNotRepoItem = new FakeRepositoryPropertyDescriptor("propWritableNotRepoItem", String.class, null, null, null);
        RepositoryPropertyDescriptor fakePropReadOnlyNotRepoItem = new FakeRepositoryPropertyDescriptor("propReadOnlyNotRepoItem", String.class, null, null, null);

        RepositoryPropertyDescriptor fakePropSet = new FakeRepositoryPropertyDescriptor(
            "propSet",
            Set.class,
            null,
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propSetItemType",
                mockGSARepo
            )
        );

        RepositoryPropertyDescriptor fakePropList = new FakeRepositoryPropertyDescriptor(
            "propList",
            List.class,
            null,
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propListItemType",
                mockGSARepo
            )
        );

        RepositoryPropertyDescriptor fakePropMap = new FakeRepositoryPropertyDescriptor(
            "propMap",
            Map.class,
            null,
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propMapItemType",
                mockGSARepo
            )
        );

        RepositoryPropertyDescriptor fakePropMapReadOnly = new FakeRepositoryPropertyDescriptor(
            "propMapReadOnly",
            Map.class,
            null,
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propMapReadOnlyItemType",
                mockGSARepo
            )
        );




        RepositoryItemDescriptor fakeItemDescriptor = new FakeRepositoryItemDescriptor(
            "theItemType",
            mockGSARepo,
            fakePropWritableSingleValue,
            fakePropReadOnlySingleValue,
            fakePropWritableNotRepoItem,
            fakePropReadOnlyNotRepoItem,
            fakePropSet,
            fakePropList,
            fakePropMap,
            fakePropMapReadOnly
        );

        when(mockGSARepo.getItemDescriptor("theItemType"))
            .thenReturn(fakeItemDescriptor);


        final String fakeDynAdminRepoPathToMockGSARepo = "/dyn/admin/path/";

        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            @Override
            protected String formatServiceName(
                String serviceName,
                HttpServletRequest req
            ) {
                if(fakeGSARepoPath == serviceName){
                    return fakeDynAdminRepoPathToMockGSARepo;
                }

                return "";
            }

            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest req, HttpServletResponse res, ServletOutputStream out) throws ServletException, IOException {

                out.println( PRECODE_MARKUP_ENTER);

                //ATG seems to send all the multiline pre stuff in a single call to println
                out.println(
                    "  &lt;add-item item-descriptor=&quot;theItemType&quot; id=&quot;100217&quot;&gt;  "

                    //set property is commented out when it's read only
                    + "\n" + "&lt;set-property name=&quot;propWritableSingleValue&quot;>&lt;![CDATA[writableVal]]&gt;&lt;/set-property&gt;"
                    + "\n" + "&lt;!-- export is false   &lt;set-property name=&quot;propReadOnlySingleValue&quot;&gt;&lt;![CDATA[readOnlyVal]]&gt;&lt;/set-property&gt;  --&gt;"

                    //props that aren't repo item per property descriptors
                    + "\n" + "&lt;set-property name=&quot;propWritableNotRepoItem&quot;>&lt;![CDATA[notRepoItemRef]]&gt;&lt;/set-property&gt;"
                    + "\n" + "&lt;!-- export is false   &lt;set-property name=&quot;propReadOnlyNotRepoItem&quot;&gt;&lt;![CDATA[notRepoItemRef]]&gt;&lt;/set-property&gt;  --&gt;"

                    //list and set properties
                    + "\n" + "&lt;set-property name=&quot;propSet&quot;>&lt;![CDATA[val1,val2]]&gt;&lt;/set-property&gt;"
                    + "\n" + "&lt;!-- export is false   &lt;set-property name=&quot;propList&quot;&gt;&lt;![CDATA[roVal1,roVal2]]&gt;&lt;/set-property&gt;  --&gt;"

                    //map properties are weird
                    + "\n" + "&lt;set-property name=&quot;propMap&quot;>&lt;![CDATA[key1=val1,key2=val2]]&gt;&lt;/set-property&gt;"
                    + "\n" + "&lt;!-- export is false   &lt;set-property name=&quot;propMapReadOnly&quot;&gt;&lt;![CDATA[roKey1=roVal1,roKey2=roVal2]]&gt;&lt;/set-property&gt;  --&gt;"

                    + "\n" + "  &lt;/add-item&gt;  "


                );

                out.println(PRECODE_MARKUP_EXIT);

            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();


        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;set-property name=&quot;propWritableSingleValue&quot;>&lt;![CDATA[<a href=\"/dyn/admin/path/?item-type=propWritableSingleValueItemType&rql-query=ID+in+%7B%22writableVal%22%7D#RQL_RESULTS\">writableVal</a>]]&gt;&lt;/set-property&gt;"
            )
        );

        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;!-- export is false   &lt;set-property name=&quot;propReadOnlySingleValue&quot;&gt;&lt;![CDATA[<a href=\"/dyn/admin/path/?item-type=propReadOnlySingleValueItemType&rql-query=ID+in+%7B%22readOnlyVal%22%7D#RQL_RESULTS\">readOnlyVal</a>]]&gt;&lt;/set-property&gt;  --&gt;"
            )
        );


        //props that aren't repo item per property descriptors
        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;set-property name=&quot;propWritableNotRepoItem&quot;>&lt;![CDATA[notRepoItemRef]]&gt;&lt;/set-property&gt;"
            )
        );


        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;!-- export is false   &lt;set-property name=&quot;propReadOnlyNotRepoItem&quot;&gt;&lt;![CDATA[notRepoItemRef]]&gt;&lt;/set-property&gt;  --&gt;"
            )
        );


        //list and set properties
        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;set-property name=&quot;propSet&quot;>&lt;![CDATA[" +
                    "<a href=\"/dyn/admin/path/?item-type=propSetItemType&rql-query=ID+in+%7B%22val1%22%7D#RQL_RESULTS\">val1</a>" +
                    ",<a href=\"/dyn/admin/path/?item-type=propSetItemType&rql-query=ID+in+%7B%22val2%22%7D#RQL_RESULTS\">val2</a>" +
                "]]&gt;&lt;/set-property&gt;"
            )
        );


        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;!-- export is false   &lt;set-property name=&quot;propList&quot;&gt;&lt;![CDATA["
                    + "<a href=\"/dyn/admin/path/?item-type=propListItemType&rql-query=ID+in+%7B%22roVal1%22%7D#RQL_RESULTS\">roVal1</a>" +
                    ",<a href=\"/dyn/admin/path/?item-type=propListItemType&rql-query=ID+in+%7B%22roVal2%22%7D#RQL_RESULTS\">roVal2</a>" +
                "]]&gt;&lt;/set-property&gt;  --&gt;"
            )
        );


        //map properties are weird
        assertThat(
            "",
            allOutput,
            containsString(

                "&lt;set-property name=&quot;propMap&quot;>&lt;![CDATA[" +
                    "key1=<a href=\"/dyn/admin/path/?item-type=propMapItemType&rql-query=ID+in+%7B%22val1%22%7D#RQL_RESULTS\">val1</a>" +
                    ",key2=<a href=\"/dyn/admin/path/?item-type=propMapItemType&rql-query=ID+in+%7B%22val2%22%7D#RQL_RESULTS\">val2</a>" +
                "]]&gt;&lt;/set-property&gt;"

            )
        );


        assertThat(
            "",
            allOutput,
            containsString(
                "&lt;!-- export is false   &lt;set-property name=&quot;propMapReadOnly&quot;&gt;&lt;![CDATA[" +
                    "roKey1=<a href=\"/dyn/admin/path/?item-type=propMapReadOnlyItemType&rql-query=ID+in+%7B%22roVal1%22%7D#RQL_RESULTS\">roVal1</a>," +
                    "roKey2=<a href=\"/dyn/admin/path/?item-type=propMapReadOnlyItemType&rql-query=ID+in+%7B%22roVal2%22%7D#RQL_RESULTS\">roVal2</a>" +
                "]]&gt;&lt;/set-property&gt;  --&gt;"
            )
        );

    }

    @Test
    public void testMapPropsOfRepoItemWithInterestingValuesAreLinked() throws ServletException, IOException, RepositoryException {


        final StringBuilder allOutputBuilder = new StringBuilder();

        doAnswer(
            new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    allOutputBuilder.append(invocation.getArguments()[0]);
                    return null;
                }
            }
        ).when(mockOutputStream).println(anyString());


        when(mockGSARepo.getItemDescriptorNames())
            .thenReturn(new String[]{
                "theItemType"
            });

        final String fakeGSARepoPath = "/some/path/to/Repo";

        when(mockGSARepo.getAbsoluteName())
            .thenReturn(fakeGSARepoPath);



        RepositoryPropertyDescriptor fakePropMap = new FakeRepositoryPropertyDescriptor(
            "propMap",
            Map.class,
            null,
            RepositoryItem.class,
            new FakeRepositoryItemDescriptor(
                "propMapItemType",
                mockGSARepo
            )
        );




        RepositoryItemDescriptor fakeItemDescriptor = new FakeRepositoryItemDescriptor(
            "theItemType",
            mockGSARepo,
            fakePropMap
        );

        when(mockGSARepo.getItemDescriptor("theItemType"))
            .thenReturn(fakeItemDescriptor);


        final String fakeDynAdminRepoPathToMockGSARepo = "/dyn/admin/path/";

        SwoleGSAAdminServlet subject = new SwoleGSAAdminServlet(
            mockGSARepo,
            mockLogger,
            mockNucleus,
            mockTxManager
        ){
            @Override
            protected String formatServiceName(
                String serviceName,
                HttpServletRequest req
            ) {
                if(fakeGSARepoPath == serviceName){
                    return fakeDynAdminRepoPathToMockGSARepo;
                }

                return "";
            }

            //This is a bit tricky to test because we are relying on the stubs impl for super class
            //but that doesn't actually do anything...anyone have a better idea?
            @Override
            protected void printAdminInternal(HttpServletRequest req, HttpServletResponse res, ServletOutputStream out) throws ServletException, IOException {

                out.println( PRECODE_MARKUP_ENTER);

                //ATG seems to send all the multiline pre stuff in a single call to println
                out.println(
                    "  &lt;add-item item-descriptor=&quot;theItemType&quot; id=&quot;100217&quot;&gt;  "

                        //map properties are weird, expecially if they contain , in values
                        + "\n" + "&lt;set-property name=&quot;propMap&quot;>&lt;![CDATA[name=last, first,date=val2,dog,cat=foo,bar]]&gt;&lt;/set-property&gt;"

                        + "\n" + "  &lt;/add-item&gt;  "


                );

                out.println(PRECODE_MARKUP_EXIT);

            }

            @Override
            protected DynamoHttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse res, String xml) {
                return mockDynamoRequest;
            }
        };


        subject.printAdmin(
            mockRequest,
            mockResponse,
            mockOutputStream
        );


        String allOutput = allOutputBuilder.toString();


        //map properties are weird
        assertThat(
            "",
            allOutput,
            containsString(

                "&lt;set-property name=&quot;propMap&quot;>&lt;![CDATA[" +
                    "name=last, first" +
                    ",date=val2" +
                    ",dog,cat=foo,bar" +
                    "]]&gt;&lt;/set-property&gt;"

            )
        );


    }


    class FakeRepositoryItemDescriptor implements RepositoryItemDescriptor {

        private final String name;
        private final Repository repo;
        private final Map<String, DynamicPropertyDescriptor> descriptors = new HashMap<String, DynamicPropertyDescriptor>();


        public FakeRepositoryItemDescriptor(
            String name,
            Repository repo,
            DynamicPropertyDescriptor... descriptors
        ){
            this.name = name;
            this.repo = repo;
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
            return repo;
        }

        public boolean isInstance(Object o) {
            return false;
        }

        public boolean areInstances(DynamicBeanInfo dynamicBeanInfo) {
            return false;
        }
    }



    class FakeRepositoryPropertyDescriptor extends RepositoryPropertyDescriptor {

        private final String name;
        private final Class<?> propertyType;
        private final RepositoryItemDescriptor propItemDescriptor;
        private final Class<?> componentPropertyType;
        private final RepositoryItemDescriptor componentItemDescriptor;

        FakeRepositoryPropertyDescriptor(
            String name,
            Class<?> propertyType,
            RepositoryItemDescriptor propItemDescriptor,
            Class<?> componentPropertyType,
            RepositoryItemDescriptor componentItemDescriptor
        ){
            this.name = name;
            this.propertyType = propertyType;
            this.propItemDescriptor = propItemDescriptor;
            this.componentPropertyType = componentPropertyType;
            this.componentItemDescriptor = componentItemDescriptor;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Class getPropertyType() {
            return this.propertyType;
        }

        @Override
        public Class getComponentPropertyType() {
            return this.propertyType;
        }

        @Override
        public RepositoryItemDescriptor getComponentItemDescriptor() {
            return componentItemDescriptor;
        }

        @Override
        public RepositoryItemDescriptor getPropertyItemDescriptor() {
            return propItemDescriptor;
        }
    }


}

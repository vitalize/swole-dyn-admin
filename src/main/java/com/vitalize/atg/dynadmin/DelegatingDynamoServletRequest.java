package com.vitalize.atg.dynadmin;

import atg.droplet.TagConverter;
import atg.naming.NameContext;
import atg.nucleus.*;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.naming.ComponentName;
import atg.nucleus.naming.ParameterName;
import atg.security.UserAuthenticator;
import atg.servlet.*;
import atg.servlet.exittracking.ExitTrackingHandler;
import atg.servlet.minimal.WebApplicationInterface;
import atg.servlet.pipeline.RedirectURLValidatorService;


import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * I had no interest in building this...but I tried to use HttpsRequestWrapper but it seems ATG has some functionality somewhere that
 //treats non DynamoHttpServlerRequests (or maybe JUST HttpRequestWrapper's) as some
 //special case and then seesm to call swapRequest on the actual DynamoHttpServletRequest
 //(not sure how it gets a handle to that since it's been wrapped..?
 //The dynamohttpservlet request seems to be a request wrapper itself..so
 //i tried to use it that wasy and it MOSTLY worked..but caused some weird issues were things linked as /dyn/admin/dyn/admin/path/to/component
 //so i am resolved to delegate this thing and it's 500M methods (approximately)
 */
public class DelegatingDynamoServletRequest extends DynamoHttpServletRequest {

    private final DynamoHttpServletRequest delegate;

    public DelegatingDynamoServletRequest(DynamoHttpServletRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }


    @Override
    protected void swapRequest(HttpServletRequest pRequest) {
        ServletPackageHelpers.swapRequest(delegate, pRequest);
    }

    @Override
    public HttpServletRequest getRequest() {
        return delegate.getRequest();
    }

    @Override
    public int getContentLength() {
        return delegate.getContentLength();
    }

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public String getProtocol() {
        return delegate.getProtocol();
    }

    @Override
    public String getScheme() {
        return delegate.getScheme();
    }

    @Override
    public String getServerName() {
        return delegate.getServerName();
    }

    @Override
    public int getServerPort() {
        return delegate.getServerPort();
    }

    @Override
    public String getRemoteAddr() {
        return delegate.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return delegate.getRemoteHost();
    }

    @Override
    public void setRequest(HttpServletRequest pRequest) {
        delegate.setRequest(pRequest);
    }

    @Override
    public void endRequest() {
        delegate.endRequest();
    }

    @Override
    public void setResponse(DynamoHttpServletResponse pResponse) {
        delegate.setResponse(pResponse);
    }

    @Override
    public DynamoHttpServletResponse getResponse() {
        return delegate.getResponse();
    }

    @Override
    public void setUserAuthenticator(UserAuthenticator pUserAuthenticator) {
         delegate.setUserAuthenticator(pUserAuthenticator);
    }

    @Override
    public UserAuthenticator getUserAuthenticator() {
        return delegate.getUserAuthenticator();
    }

    @Override
    public void setLog(ApplicationLogging pLog) {
        delegate.setLog(pLog);
    }

    @Override
    public ApplicationLogging getLog() {
        return delegate.getLog();
    }

    @Override
    public WebApplicationInterface getWebApplication() {
        return delegate.getWebApplication();
    }

    @Override
    public String getURLParameterString() {
        return delegate.getURLParameterString();
    }

    @Override
    public void setParameterDelimiter(String pParameterDelimiter) {
        delegate.setParameterDelimiter(pParameterDelimiter);
    }

    @Override
    public String getParameterDelimiter() {
        return delegate.getParameterDelimiter();
    }

    @Override
    public void setUseXmlParamDelimiter(Boolean pUseXmlParamDelimiter) {
        delegate.setUseXmlParamDelimiter(pUseXmlParamDelimiter);
    }

    @Override
    public Boolean isUseXmlParamDelimiter() {
        return delegate.isUseXmlParamDelimiter();
    }

    @Override
    public void setScrambleKey(byte[] pScrambleKey) {
        delegate.setScrambleKey(pScrambleKey);
    }

    @Override
    public byte[] getScrambleKey() {
        return delegate.getScrambleKey();
    }

    @Override
    public void setMimeTyper(MimeTyper pMimeTyper) {
        delegate.setMimeTyper(pMimeTyper);
    }

    @Override
    public MimeTyper getMimeTyper() {
        return delegate.getMimeTyper();
    }

    @Override
    public void setNameResolverFactory(MultiRootNameResolverFactory pNameResolverFactory) {
        delegate.setNameResolverFactory(pNameResolverFactory);
    }

    @Override
    public MultiRootNameResolverFactory getNameResolverFactory() {
        return delegate.getNameResolverFactory();
    }

    @Override
    public void setMultiNucleusNameResolverFactory(MultiRootNameResolverFactory pMultiNucleusNameResolverFactory) {
        delegate.setMultiNucleusNameResolverFactory(pMultiNucleusNameResolverFactory);
    }

    @Override
    public MultiRootNameResolverFactory getMultiNucleusNameResolverFactory() {
        return delegate.getMultiNucleusNameResolverFactory();
    }

    @Override
    public boolean isDelayedRequest() {
        return delegate.isDelayedRequest();
    }

    @Override
    public void setParameterHandler(ParameterHandler pParameterHandler) {
        delegate.setParameterHandler(pParameterHandler);
    }

    @Override
    public void setExitTrackingHandler(ExitTrackingHandler pExitTrackingHandler) {
        delegate.setExitTrackingHandler(pExitTrackingHandler);
    }

    @Override
    public ExitTrackingHandler getExitTrackingHandler() {
        return delegate.getExitTrackingHandler();
    }

    @Override
    public HttpSessionRequest getSessionRequest() {
        return delegate.getSessionRequest();
    }

    @Override
    public HttpSessionRequest getSessionRequest(boolean pCreate) {
        return delegate.getSessionRequest();
    }

    @Override
    public void setSessionRequest(HttpSessionRequest pSessionRequest) {
        delegate.setSessionRequest(pSessionRequest);
    }

    @Override
    public void removeSessionFromRequest() {
        delegate.removeSessionFromRequest();
    }

    @Override
    public void setRequestScopeManager(RequestScopeManager pRequestScopeManager) {
        delegate.setRequestScopeManager(pRequestScopeManager);
    }

    @Override
    public RequestScopeManager getRequestScopeManager() {
        return delegate.getRequestScopeManager();
    }

    @Override
    public void setWindowScopeManager(WindowScopeManager pScopeManager) {
        delegate.setWindowScopeManager(pScopeManager);
    }

    @Override
    public WindowScopeManager getWindowScopeManager() {
        return delegate.getWindowScopeManager();
    }

    @Override
    public void setRequestScope(NameContext pRequestScope) {
        delegate.setRequestScope(pRequestScope);
    }

    @Override
    public NameContext getRequestScope() {
        return delegate.getRequestScope();
    }

    @Override
    public boolean hasRequestScope() {
        return delegate.hasRequestScope();
    }

    @Override
    public NameContext getWindowScope() {
        return delegate.getWindowScope();
    }

    @Override
    public DynamoHttpServletRequest getRequestForComparison() {
        return delegate.getRequestForComparison();
    }

    @Override
    public Nucleus getNucleus() {
        return delegate.getNucleus();
    }

    @Override
    public void setNucleus(Nucleus pNucleus) {
        delegate.setNucleus(pNucleus);
    }

    @Override
    public BrowserTyper getBrowserTyper() {
        return delegate.getBrowserTyper();
    }

    @Override
    public void setBrowserTyper(BrowserTyper pBrowserTyper) {
        delegate.setBrowserTyper(pBrowserTyper);
    }

    @Override
    public boolean getGenerateRequestLocales() {
        return delegate.getGenerateRequestLocales();
    }

    @Override
    public void setGenerateRequestLocales(boolean pValue) {
        delegate.setGenerateRequestLocales(pValue);
    }

    @Override
    public ComponentName getRequestLocalePath() {
        return delegate.getRequestLocalePath();
    }

    @Override
    public void setRequestLocalePath(ComponentName pValue) {
        delegate.setRequestLocalePath(pValue);
    }

    @Override
    public RequestLocale getRequestLocale() {
        return delegate.getRequestLocale();
    }

    @Override
    public void setRequestLocale(RequestLocale pLocale) {
        delegate.setRequestLocale(pLocale);
    }

    @Override
    public void setInitialized(boolean pInitialized) {
        delegate.setInitialized(pInitialized);
    }

    @Override
    public boolean isInitialized() {
        return delegate.isInitialized();
    }

    @Override
    public String getRequestURI() {
        return delegate.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return delegate.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return delegate.getServletPath();
    }

    @Override
    public String getPathInfo() {
        return delegate.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return delegate.getPathTranslated();
    }

    @Override
    public String getContextPath() {
        return delegate.getContextPath();
    }

    @Override
    public String getQueryString() {
        return delegate.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return delegate.getRemoteUser();
    }

    @Override
    public String getAuthType() {
        return delegate.getAuthType();
    }

    @Override
    public String getHeader(String p0) {
        return delegate.getHeader(p0);
    }

    @Override
    public int getIntHeader(String p0) {
        return delegate.getIntHeader(p0);
    }

    @Override
    public long getDateHeader(String p0) {
        return delegate.getDateHeader(p0);
    }

    @Override
    public Enumeration getHeaderNames() {
        return delegate.getHeaderNames();
    }

    @Override
    public Enumeration getHeaders(String pName) {
        return delegate.getHeaders(pName);
    }

    @Override
    public void setContentLength(int pContentLength) {
        delegate.setContentLength(pContentLength);
    }

    @Override
    public void setContentType(String pContentType) {
        delegate.setContentType(pContentType);
    }

    @Override
    public void setProtocol(String pProtocol) {
        delegate.setProtocol(pProtocol);
    }

    @Override
    public void setScheme(String pScheme) {
        delegate.setScheme(pScheme);
    }

    @Override
    public void setServerName(String pServerName) {
        delegate.setServerName(pServerName);
    }

    @Override
    public void setServerPort(int pServerPort) {
        delegate.setServerPort(pServerPort);
    }

    @Override
    public void setRemoteAddr(String pRemoteAddr) {
        delegate.setRemoteAddr(pRemoteAddr);
    }

    @Override
    public void setRemoteHost(String pRemoteHost) {
        delegate.setRemoteHost(pRemoteHost);
    }

    @Override
    public void setInputStream(ServletInputStream pInputStream) {
        delegate.setInputStream(pInputStream);
    }

    @Override
    public void setMethod(String pMethod) {
        delegate.setMethod(pMethod);
    }

    @Override
    public void setRequestURI(String pRequestURI) {
        delegate.setRequestURI(pRequestURI);
    }

    @Override
    public void setServletPath(String pServletPath) {
        delegate.setServletPath(pServletPath);
    }

    @Override
    public void setPathInfo(String pPathInfo) {
        delegate.setPathInfo(pPathInfo);
    }

    @Override
    public void setPathTranslated(String pPathTranslated) {
        delegate.setPathTranslated(pPathTranslated);
    }

    @Override
    public void setQueryString(String pQueryString) {
        delegate.setQueryString(pQueryString);
    }

    @Override
    public void setRemoteUser(String pRemoteUser) {
        delegate.setRemoteUser(pRemoteUser);
    }

    @Override
    public void setAuthType(String pAuthType) {
        delegate.setAuthType(pAuthType);
    }

    @Override
    public void setContextPath(String pContextPath) {
        delegate.setContextPath(pContextPath);
    }

    @Override
    public synchronized Object getPermanentAttribute(AttributeFactory pKey) {
        return delegate.getPermanentAttribute(pKey);
    }

    @Override
    public void setAttribute(String pName, Object pValue) {
        delegate.setAttribute(pName, pValue);
    }

    @Override
    public void removeAttribute(String pName) {
        delegate.removeAttribute(pName);
    }

    @Override
    public void setAttributeFactory(String pName, AttributeFactory pFactory) {
        delegate.setAttributeFactory(pName, pFactory);
    }

    @Override
    public String getRequestURIWithQueryString() {
        return delegate.getRequestURIWithQueryString();
    }

    @Override
    public Enumeration getParameterNamesInStack() {
        return delegate.getParameterNamesInStack();
    }

    @Override
    public String getQueryParameter(String pKey) {
        return delegate.getQueryParameter(pKey);
    }

    @Override
    public String getQueryParameter(String pKey, int pIndex) {
        return delegate.getQueryParameter(pKey, pIndex);
    }

    @Override
    public String[] getQueryParameterValues(String pKey) {
        return delegate.getQueryParameterValues(pKey);
    }

    @Override
    public Enumeration getQueryParameterNames() {
        return delegate.getQueryParameterNames();
    }

    @Override
    public int getQueryParameterCount(String pKey) {
        return delegate.getQueryParameterCount(pKey);
    }

    @Override
    public String getPostParameter(String pKey) {
        return delegate.getPostParameter(pKey);
    }

    @Override
    public String getPostParameter(String pKey, int pIndex) {
        return delegate.getPostParameter(pKey, pIndex);
    }

    @Override
    public String[] getPostParameterValues(String pKey) {
        return delegate.getPostParameterValues(pKey);
    }

    @Override
    public Enumeration getPostParameterNames() {
        return delegate.getPostParameterNames();
    }

    @Override
    public int getPostParameterCount(String pKey) {
        return delegate.getPostParameterCount(pKey);
    }

    @Override
    public String getURLParameter(String pKey) {
        return delegate.getURLParameter(pKey);
    }

    @Override
    public String getURLParameter(String pKey, int pIndex) {
        return delegate.getURLParameter(pKey, pIndex);
    }

    @Override
    public Object getObjectURLParameter(String pKey, int pIndex) {
        return delegate.getObjectURLParameter(pKey, pIndex);
    }

    @Override
    public String[] getURLParameterValues(String pKey) {
        return delegate.getURLParameterValues(pKey);
    }

    @Override
    public Enumeration getURLParameterNames() {
        return delegate.getURLParameterNames();
    }

    @Override
    public int getURLParameterCount(String pKey) {
        return delegate.getURLParameterCount(pKey);
    }

    @Override
    public boolean tamperedURLParameters() {
        return delegate.tamperedURLParameters();
    }

    @Override
    public boolean getEncodeServletPath() {
        return delegate.getEncodeServletPath();
    }

    @Override
    public void setEncodeServletPath(boolean pEncode) {
        delegate.setEncodeServletPath(pEncode);
    }

    @Override
    public int getEncodeContextPathMode() {
        return delegate.getEncodeContextPathMode();
    }

    @Override
    public void setEncodeContextPathMode(int pEncodeMode) {
        delegate.setEncodeContextPathMode(pEncodeMode);
    }

    @Override
    public String getCookieParameter(String pKey) {
        return delegate.getCookieParameter(pKey);
    }

    @Override
    public String getCookieParameter(String pKey, int pIndex) {
        return delegate.getCookieParameter(pKey, pIndex);
    }

    @Override
    public String[] getCookieParameterValues(String pKey) {
        return delegate.getCookieParameterValues(pKey);
    }

    @Override
    public Enumeration getCookieParameterNames() {
        return delegate.getCookieParameterNames();
    }

    @Override
    public int getCookieParameterCount(String pKey) {
        return delegate.getCookieParameterCount(pKey);
    }

    @Override
    public boolean isBrowserType(String pFeature) {
        return delegate.isBrowserType(pFeature);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }


    @Override
    public String getMimeType() {
        return delegate.getMimeType();
    }

    @Override
    public void setMimeType(String pMimeType) {
        delegate.setMimeType(pMimeType);
    }

    @Override
    public String getWorkingDirectory() {
        return delegate.getWorkingDirectory();
    }

    @Override
    public void setLinkEncoding(String pLinkEncoding) {
        delegate.setLinkEncoding(pLinkEncoding);
    }

    @Override
    public String getLinkEncoding() {
        return delegate.getLinkEncoding();
    }

    @Override
    public void addQueryParameter(String pKey, String pValue) {
        delegate.addQueryParameter(pKey, pValue);
    }

    @Override
    public void addURLParameter(String pKey, String pValue) {
        delegate.addURLParameter(pKey, pValue);
    }

    @Override
    public void addPersistentQueryParameter(String pKey, String pValue) {
        delegate.addPersistentQueryParameter(pKey, pValue);
    }

    @Override
    public void removePersistentQueryParameter(String pKey) {
        delegate.removePersistentQueryParameter(pKey);
    }

    @Override
    public String encodeURL(String pURL) {
        return delegate.encodeURL(pURL);
    }

    @Override
    public String encodeURL(String pURL, boolean pClearParameters) {
        return delegate.encodeURL(pURL, pClearParameters);
    }

    @Override
    public String performExitTracking(String pURL) {
        return delegate.performExitTracking(pURL);
    }

    @Override
    public boolean shouldExitTrack(String pURL) {
        return delegate.shouldExitTrack(pURL);
    }

    @Override
    public String getExitTrackingParameterName() {
        return delegate.getExitTrackingParameterName();
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL);
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL, boolean pInterpretURIs) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL, pInterpretURIs);
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL, boolean pInterpretURIs, boolean pDoExitTracking) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL, pInterpretURIs, pDoExitTracking);
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL, boolean pInterpretURIs, boolean pDoExitTracking, boolean pUseReverseMap) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL, pInterpretURIs, pDoExitTracking, pUseReverseMap);
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL, boolean pInterpretURIs, boolean pDoExitTracking, int pPrependMode) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL, pInterpretURIs, pDoExitTracking, pPrependMode);
    }

    @Override
    public String encodeURL(String pURL, boolean pEncodeParameters, boolean pClearParameters, boolean pIsImageURL, boolean pInterpretURIs, boolean pDoExitTracking, int pPrependMode, boolean pUseReverseMap) {
        return delegate.encodeURL(pURL, pEncodeParameters, pClearParameters, pIsImageURL, pInterpretURIs, pDoExitTracking, pPrependMode, pUseReverseMap);
    }

    @Override
    public String reverseMapContextRootForLayering(String strFinalURL) {
        return delegate.reverseMapContextRootForLayering(strFinalURL);
    }

    @Override
    public void setEncodeURL(boolean pEncodeURL) {
        delegate.setEncodeURL(pEncodeURL);
    }

    @Override
    public boolean getEncodeURL() {
        return delegate.getEncodeURL();
    }

    @Override
    public void setURLSessionIdSpecifier(String pURLSessionIdSpecifier) {
        delegate.setURLSessionIdSpecifier(pURLSessionIdSpecifier);
    }

    @Override
    public String getURLSessionIdSpecifier() {
        return delegate.getURLSessionIdSpecifier();
    }

    @Override
    public void setRestorableSessionIdFromURL(String pRestorableSessionIdFromURL) {
        delegate.setRestorableSessionIdFromURL(pRestorableSessionIdFromURL);
    }

    @Override
    public String getRestorableSessionIdFromURL() {
        return delegate.getRestorableSessionIdFromURL();
    }

    @Override
    public void setWebApplication(WebApplicationInterface pWebApplication) {
        delegate.setWebApplication(pWebApplication);
    }

    @Override
    public void setRedirectURLValidator(RedirectURLValidatorService pRedirectURLValidator) {
        delegate.setRedirectURLValidator(pRedirectURLValidator);
    }

    @Override
    public RedirectURLValidatorService getRedirectURLValidator() {
        return delegate.getRedirectURLValidator();
    }

    @Override
    public boolean isLoggingInfo() {
        return delegate.isLoggingInfo();
    }

    @Override
    public void setLoggingInfo(boolean pLogging) {
        delegate.setLoggingInfo(pLogging);
    }

    @Override
    public boolean isLoggingWarning() {
        return delegate.isLoggingWarning();
    }

    @Override
    public void setLoggingWarning(boolean pLogging) {
        delegate.setLoggingWarning(pLogging);
    }

    @Override
    public boolean isLoggingError() {
        return delegate.isLoggingError();
    }

    @Override
    public void setLoggingError(boolean pLogging) {
        delegate.setLoggingError(pLogging);
    }

    @Override
    public boolean isLoggingDebug() {
        return delegate.isLoggingDebug();
    }

    @Override
    public void setLoggingDebug(boolean pLogging) {
        delegate.setLoggingDebug(pLogging);
    }

    @Override
    public void logInfo(String pMessage) {
        delegate.logInfo(pMessage);
    }

    @Override
    public void logInfo(Throwable pThrowable) {
        delegate.logInfo(pThrowable);
    }

    @Override
    public void logInfo(String pMessage, Throwable pThrowable) {
        delegate.logInfo(pMessage, pThrowable);
    }

    @Override
    public void logWarning(String pMessage) {
        delegate.logWarning(pMessage);
    }

    @Override
    public void logWarning(Throwable pThrowable) {
        delegate.logWarning(pThrowable);
    }

    @Override
    public void logWarning(String pMessage, Throwable pThrowable) {
        delegate.logWarning(pMessage, pThrowable);
    }

    @Override
    public void logError(String pMessage) {
        delegate.logError(pMessage);
    }

    @Override
    public void logError(Throwable pThrowable) {
        delegate.logError(pThrowable);
    }

    @Override
    public void logError(String pMessage, Throwable pThrowable) {
        delegate.logError(pMessage, pThrowable);
    }

    @Override
    public void logDebug(String pMessage) {
        delegate.logDebug(pMessage);
    }

    @Override
    public void logDebug(Throwable pThrowable) {
        delegate.logDebug(pThrowable);
    }

    @Override
    public void logDebug(String pMessage, Throwable pThrowable) {
        delegate.logDebug(pMessage, pThrowable);
    }

    @Override
    public void printRequest(PrintStream pOut) {
        delegate.printRequest(pOut);
    }

    @Override
    public void pushFrame() {
        delegate.pushFrame();
    }

    @Override
    public void popFrame() {
        delegate.popFrame();
    }

    @Override
    public int getCountOfFrames() {
        return delegate.getCountOfFrames();
    }

    @Override
    public void pushParameters(Dictionary pDict) {
        delegate.pushParameters(pDict);
    }

    @Override
    public void pushDefaultParameters(Dictionary pDict) {
        delegate.pushDefaultParameters(pDict);
    }

    @Override
    public void popDefaultParameters() {
        delegate.popDefaultParameters();
    }

    @Override
    public void popParameters() {
        delegate.popParameters();
    }

    @Override
    public Map getMapForCurrentFrame() {
        return delegate.getMapForCurrentFrame();
    }

    @Override
    public Map getMapCopyOfCurrentFrame() {
        return delegate.getMapCopyOfCurrentFrame();
    }

    @Override
    public Map getCurrentFrameWithoutNulls(boolean pCopy) {
        return delegate.getCurrentFrameWithoutNulls(pCopy);
    }

    @Override
    public void setBaseDirectory(String pBaseDir) {
        delegate.setBaseDirectory(pBaseDir);
    }

    @Override
    public String getBaseDirectory() {
        return delegate.getBaseDirectory();
    }

    @Override
    public void setParameter(String pName, Object pValue) {
        delegate.setParameter(pName, pValue);
    }

    @Override
    public void setParameter(String pName, Object pValue, TagConverter pCvt, Properties pCvtArgs) throws ServletException {
        delegate.setParameter(pName, pValue, pCvt, pCvtArgs);
    }

    @Override
    public Object removeParameter(String pName) {
        return delegate.removeParameter(pName);
    }

    @Override
    public Object resolveGlobalName(ComponentName pName) {
        return delegate.resolveGlobalName(pName);
    }

    @Override
    public Object resolveSessionName(ComponentName pName) {
        return delegate.resolveSessionName(pName);
    }

    @Override
    public Object resolveRequestName(ComponentName pName) {
        return delegate.resolveRequestName(pName);
    }

    @Override
    public Object resolveName(ComponentName pName) {
        return delegate.resolveName(pName);
    }

    @Override
    public Object resolveName(ComponentName pName, boolean pCreate) {
        return delegate.resolveName(pName, pCreate);
    }

    @Override
    public ComponentName getContextNamePath() {
        return delegate.getContextNamePath();
    }

    @Override
    public Object resolveGlobalName(String pName) {
        return delegate.resolveGlobalName(pName);
    }

    @Override
    public Object resolveSessionName(String pName) {
        return delegate.resolveSessionName(pName);
    }

    @Override
    public Object resolveRequestName(String pName) {
        return delegate.resolveRequestName(pName);
    }

    @Override
    public Object resolveName(String pName) {
        return delegate.resolveName(pName);
    }

    @Override
    public Object resolveName(String pName, boolean pCreate) {
        return delegate.resolveName(pName, pCreate);
    }

    @Override
    public String getParameter(ParameterName pName) {
        return delegate.getParameter(pName);
    }

    @Override
    public Object getLocalParameter(ParameterName pName) {
        return delegate.getLocalParameter(pName);
    }

    @Override
    public Object getObjectParameter(ParameterName pName) {
        return delegate.getObjectParameter(pName);
    }

    @Override
    public boolean serviceParameter(ParameterName pName, ServletRequest pReq, ServletResponse pRes) throws ServletException, IOException {
        return delegate.serviceParameter(pName, pReq, pRes);
    }

    @Override
    public boolean serviceParameter(ParameterName pName, ServletRequest pReq, ServletResponse pRes, TagConverter pCvt, Properties pCvtArgs) throws ServletException, IOException {
        return delegate.serviceParameter(pName, pReq, pRes, pCvt, pCvtArgs);
    }

    @Override
    public boolean serviceLocalParameter(ParameterName pName, ServletRequest pReq, ServletResponse pRes) throws ServletException, IOException {
        return delegate.serviceLocalParameter(pName, pReq, pRes);
    }

    @Override
    public Object getObjectParameter(String pName) {
        return delegate.getObjectParameter(pName);
    }

    @Override
    public String getParameter(String pName) {
        return delegate.getParameter(pName);
    }

    @Override
    public Object getLocalParameter(String pName) {
        return delegate.getLocalParameter(pName);
    }

    @Override
    public String[] getParameterValues(String pName) {
        return delegate.getParameterValues(pName);
    }

    @Override
    public Enumeration getParameterNames() {
        return delegate.getParameterNames();
    }

    @Override
    public Map getParameterMap() {
        return delegate.getParameterMap();
    }

    @Override
    public Object getAttribute(String p0) {
        return delegate.getAttribute(p0);
    }

    @Override
    public Object getAttribute(String p0, boolean pCreate) {
        return delegate.getAttribute(p0);
    }

    @Override
    public Enumeration getAttributeNames() {
        return delegate.getAttributeNames();
    }

    @Override
    public String getMethod() {
        return delegate.getMethod();
    }

    @Override
    public boolean serviceParameter(String pName, ServletRequest pReq, ServletResponse pRes) throws ServletException, IOException {
        return delegate.serviceParameter(pName, pReq, pRes);
    }

    @Override
    public boolean serviceParameter(String pName, ServletRequest pReq, ServletResponse pRes, TagConverter pCvt, Properties pCvtArgs) throws ServletException, IOException {
        return delegate.serviceParameter(pName, pReq, pRes, pCvt, pCvtArgs);
    }

    @Override
    public boolean serviceLocalParameter(String pName, ServletRequest pReq, ServletResponse pRes) throws ServletException, IOException {
        return delegate.serviceLocalParameter(pName, pReq, pRes);
    }

    @Override
    public String getRealPath(String pPath) {
        return delegate.getRealPath(pPath);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public void setDocRootServicePrefix(String pDocRootServicePrefix) {
        delegate.setDocRootServicePrefix(pDocRootServicePrefix);
    }

    @Override
    public String getDocRootServicePrefix() {
        return delegate.getDocRootServicePrefix();
    }

    @Override
    public Cookie[] getCookies() {
        return delegate.getCookies();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return delegate.getSession();
    }

    @Override
    public HttpSession getSession() {
        return delegate.getSession();
    }

    @Override
    public String getRequestedSessionId() {
        return delegate.getRequestedSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return delegate.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return delegate.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return delegate.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return delegate.isRequestedSessionIdFromURL();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return delegate.getReader();
    }

    @Override
    public String getCharacterEncoding() {
        return delegate.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String pEncoding) throws UnsupportedEncodingException {
        delegate.setCharacterEncoding(pEncoding);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String pPath) {
        return delegate.getRequestDispatcher(pPath);
    }

    @Override
    public String getLocalAddr() {
        return delegate.getLocalAddr();
    }

    @Override
    public String getLocalName() {
        return delegate.getLocalName();
    }

    @Override
    public int getLocalPort() {
        return delegate.getLocalPort();
    }

    @Override
    public int getRemotePort() {
        return delegate.getRemotePort();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(ServletContext pContext, String pPath) {
        return delegate.getRequestDispatcher(pContext, pPath);
    }

    @Override
    public void setEventFlags(int pEventFlags) {
        delegate.setEventFlags(pEventFlags);
    }

    @Override
    public int getEventFlags() {
        return delegate.getEventFlags();
    }

    @Override
    public void setDisableExitTracking(boolean pDisableExitTracking) {
        delegate.setDisableExitTracking(pDisableExitTracking);
    }

    @Override
    public boolean getDisableExitTracking() {
        return delegate.getDisableExitTracking();
    }

    @Override
    public void setupLoopbackTemplateEmailRequest() {
        delegate.setupLoopbackTemplateEmailRequest();
    }

    @Override
    public void setInTemplatePage(boolean pInTemplatePage) {
        delegate.setInTemplatePage(pInTemplatePage);
    }

    @Override
    public boolean isInTemplatePage() {
        return delegate.isInTemplatePage();
    }

    @Override
    public void setFormEventsSent(boolean pFormEventsSent) {
        delegate.setFormEventsSent(pFormEventsSent);
    }

    @Override
    public boolean getFormEventsSent() {
        return delegate.getFormEventsSent();
    }

    @Override
    public void setRequestURIHasQueryString(boolean pRequestURIHasQueryString) {
        delegate.setRequestURIHasQueryString(pRequestURIHasQueryString);
    }

    @Override
    public ServletRequestWrapper getWrapper() {
        return delegate.getWrapper();
    }

    @Override
    public void setWrapper(ServletRequestWrapper pWrapper) {
        delegate.setWrapper(pWrapper);
    }

    @Override
    public boolean isUserInRole(String pRole) {
        return delegate.isUserInRole(pRole);
    }

    @Override
    public Principal getUserPrincipal() {
        return delegate.getUserPrincipal();
    }

    @Override
    public boolean isSecure() {
        return delegate.isSecure();
    }

    @Override
    public Locale getLocale() {
        return delegate.getLocale();
    }

    @Override
    public Enumeration getLocales() {
        return delegate.getLocales();
    }

    @Override
    public void setDynamoPipeline(boolean pDynamoPipeline) {
        delegate.setDynamoPipeline(pDynamoPipeline);
    }

    @Override
    public boolean isDynamoPipeline() {
        return delegate.isDynamoPipeline();
    }

    @Override
    public void setAfterGetsClaimed(boolean pAfterGetsClaimed) {
        delegate.setAfterGetsClaimed(pAfterGetsClaimed);
    }

    @Override
    public boolean isAfterGetsClaimed() {
        return delegate.isAfterGetsClaimed();
    }

    @Override
    public ResolveNameHelper getResolveNameHelper() {
        return delegate.getResolveNameHelper();
    }

    @Override
    public void setResolveNameHelper(ResolveNameHelper pResolveNameHelper) {
        delegate.setResolveNameHelper(pResolveNameHelper);
    }

    @Override
    public ResolveNameHelper getMultiNucleusResolveNameHelper() {
        return delegate.getMultiNucleusResolveNameHelper();
    }

    @Override
    public Map getParamMapForTopFrame() {
        return delegate.getParamMapForTopFrame();
    }

    @Override
    public NameContext getSessionNameContext() {
        return delegate.getSessionNameContext();
    }

    @Override
    public long getSessionConfirmationNumber() {
        return delegate.getSessionConfirmationNumber();
    }
}

package atg.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Util class for working with package protected methods.
 */
public final class ServletPackageHelpers {
    public static void swapRequest(DynamoHttpServletRequest swapper, HttpServletRequest swapee){
        swapper.swapRequest(swapee);
    }
}

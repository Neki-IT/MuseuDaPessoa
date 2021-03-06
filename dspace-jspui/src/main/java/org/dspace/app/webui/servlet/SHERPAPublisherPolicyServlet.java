/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.sherpa.v2.SHERPAResponse;
import org.dspace.app.sherpa.submit.SHERPASubmitService;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;

/**
 * This servlet uses the SHERPASubmitService to build an html page with the
 * publisher policy for the journal referred in the specified Item
 * 
 * @author Andrea Bollini
 * 
 */
public class SHERPAPublisherPolicyServlet extends DSpaceServlet
{
    private SHERPASubmitService sherpaSubmitService = new DSpace()
            .getServiceManager().getServiceByName(
                    SHERPASubmitService.class.getCanonicalName(),
                    SHERPASubmitService.class);

    /** log4j logger */
    private static Logger log = Logger.getLogger(SHERPAPublisherPolicyServlet.class);
    
    @Override
    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
        int itemID = UIUtil.getIntParameter(request, "item_id");
        Item item = Item.find(context, itemID);
        if (item == null)
        {
            return;
        }
        List<SHERPAResponse> responses = sherpaSubmitService.searchRelatedJournals(context, item);
        List<SHERPAResponse> sherpaResponses = new LinkedList<>();
        // The new structure means we should handle multiple *responses*, (the API 'item' object) not just
        // multiple journals within a single response.
        // Only return responses with valid results, unless there are only errors

        boolean all_errors = true;
        boolean some_errors = false;
        for (SHERPAResponse sherpaResponse : responses) {
            if (sherpaResponse.isError()) {
                some_errors = true;
            } else {
                all_errors = false;
            }
            sherpaResponses.add(sherpaResponse);
        }
        request.setAttribute("sherpaResponses", sherpaResponses);
        request.setAttribute("error", all_errors);
        request.setAttribute("some_errors", some_errors);

        // Simply forward to the plain form
        JSPManager.showJSP(request, response, "/sherpa/sherpa-policy.jsp");
    }

    @Override
    protected void doDSPost(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}

package org.dspace.app.webui.cris.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.dspace.app.cris.model.ResearchObject;
import org.dspace.app.cris.model.ResearcherPage;
import org.dspace.app.cris.service.ApplicationService;
import org.dspace.app.cris.util.Researcher;
import org.dspace.app.webui.json.JSONRequest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.eperson.AccountManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.PasswordHash;
import org.dspace.kernel.ServiceManager;
import org.dspace.utils.DSpace;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class ClaimRPJSONServlet extends JSONRequest{
	Logger log = Logger.getLogger(ClaimRPJSONServlet.class);
	
	private static SearchService searcher;
	 
	private int validateCreateEperson(Context context,String mail, String rpKey) {
		
		ApplicationService applicationservice = new DSpace().getServiceManager().getServiceByName("applicationService", ApplicationService.class);
		
		ResearcherPage r=  applicationservice.getEntityByCrisId(rpKey, ResearcherPage.class);
		int status=1;
		if(StringUtils.equals(mail, r.getEmail().getValue() )){
            context.setIgnoreAuthorization(true);
            EPerson eperson;
			try {
				eperson = EPerson.create(context);
				eperson.setEmail(mail);
				eperson.setCanLogIn(true);
				eperson.setFirstName("");
				eperson.setLanguage("en");
				eperson.setLastName("");
				eperson.setRequireCertificate(false);
				eperson.setSelfRegistered(false);
				eperson.update();
                log.info(LogManager.getHeader(context,
                        "sendtoken_forgotpw", "email=" + mail));
                AccountManager.sendForgotPasswordInfo(context, mail);
    			
			} catch (SQLException e) {
				status = -1;
				log.error(e.getMessage(), e);
			} catch (AuthorizeException e) {
				status = -1;
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				status = -1;
				log.error(e.getMessage(), e);
			} catch (MessagingException e) {
				status = -1;
				log.error(e.getMessage(), e);
			}finally{
				context.setIgnoreAuthorization(false);
				if(status<0){
					context.abort();
				}else{
					try {
						context.commit();
					} catch (SQLException e) {
						status =-1;
						log.error(e.getMessage(), e);
					}
				}
			}
			return status;
		}
		status =-2;
		return status;
	}

	@Override
	public void doJSONRequest(Context context, HttpServletRequest req,
			HttpServletResponse resp) throws AuthorizeException, IOException {


		String mail = req.getParameter("mailUser");
		String rpKey = req.getParameter("rpKey");
		
		int status =0;
		if(StringUtils.isNotBlank(mail) && StringUtils.isNotBlank(rpKey)){
			status = validateCreateEperson(context, mail, rpKey);
		}
        JsonObject jo = new JsonObject();
        jo.addProperty("result", status);
        resp.getWriter().write(jo.toString());
	}
	
	

}

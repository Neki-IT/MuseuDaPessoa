/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.dspace.browse.BrowseDSpaceObject;
import org.dspace.content.IMetadataValue;
import org.dspace.content.Item;
import org.dspace.core.Utils;
import org.dspace.discovery.IGlobalSearchResult;

public class TitleDisplayStrategy implements IDisplayMetadataValueStrategy
{

    public String getMetadataDisplay(HttpServletRequest hrq, int limit,
            boolean viewFull, String browseType, int colIdx, String field,
            List<IMetadataValue> metadataArray, BrowseDSpaceObject item, boolean disableCrossLinks, boolean emph)
    {
        return getDisplay(hrq, metadataArray, item.isWithdrawn(), item.getHandle(), emph);
    }

    public String getMetadataDisplay(HttpServletRequest hrq, int limit,
            boolean viewFull, String browseType, int colIdx, String field,
            List<IMetadataValue> metadataArray, Item item, boolean disableCrossLinks, boolean emph)
    {
        return getDisplay(hrq, metadataArray, item.isWithdrawn(), item.getHandle(), emph);
    }
    

    private String getDisplay(HttpServletRequest hrq, List<IMetadataValue> metadataArray,
            boolean isWithdrawn, String handle, boolean emph)
    {
        String metadata = "-";
        if (metadataArray.size() > 0)
        {
            if (isWithdrawn)
            {
                metadata = Utils.addEntities(metadataArray.get(0).getValue());
            }
            else
            {
                metadata = "<a href=\"" + hrq.getContextPath() + "/handle/"
                + handle + "\">"
                + Utils.addEntities(metadataArray.get(0).getValue())
                + "</a>";
            }
        }
        metadata = (emph? "<strong>":"") + metadata + (emph? "</strong>":"");
        return metadata;
    }

    public String getExtraCssDisplay(HttpServletRequest hrq, int limit,
            boolean b, String string, int colIdx, String field,
            List<IMetadataValue> metadataArray, BrowseDSpaceObject browseItem,
            boolean disableCrossLinks, boolean emph)
    {
        return null;
    }

    public String getExtraCssDisplay(HttpServletRequest hrq, int limit,
            boolean b, String browseType, int colIdx, String field,
            List<IMetadataValue> metadataArray, Item item, boolean disableCrossLinks,
            boolean emph) throws JspException
    {
        return null;
    }
    
	@Override
	public String getMetadataDisplay(HttpServletRequest hrq, int limit, boolean viewFull, String browseType,
			int colIdx, String field, List<IMetadataValue> metadataArray, IGlobalSearchResult item, boolean disableCrossLinks,
			boolean emph) throws JspException {
        String metadata = "-";
        if (metadataArray.size()>0)
        {
            if (item.isWithdrawn())
            {
                metadata = Utils.addEntities(metadataArray.get(0).getValue());
            }
            else
            {
                metadata = "<a href=\"" + hrq.getContextPath() + "/handle/"
                + item.getHandle() + "\">"
                + Utils.addEntities(metadataArray.get(0).getValue())
                + "</a>";
            }
        }
        metadata = (emph? "<strong>":"") + metadata + (emph? "</strong>":"");
        return metadata;		
	}
}
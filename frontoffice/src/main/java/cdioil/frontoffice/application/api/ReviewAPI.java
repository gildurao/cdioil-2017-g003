package cdioil.frontoffice.application.api;

import javax.ws.rs.core.Response;

/**
 * Interface that represents the FeedbackMonkey Review API.
 *
 * @author <a href="1160912@isep.ipp.pt">Rita Gonçalves</a>
 * @author <a href="1161380@isep.ipp.pt">Joana Pinheiro</a>
 */
public interface ReviewAPI {

    /**
     * Produces an HTTP Response whereby the response's body contains an XML
     * representation of a Review.
     *
     * @param authenticationToken user's session authentication token
     * @param surveyID database identifier of the survey being answered
     * @return HTTP Response with an XML representation of a new Review if no
     * errors have occured.
     */
    public Response newReview(String authenticationToken, String surveyID);

}

package view.endpoints;

import com.google.gson.Gson;
import logic.StudentController;
import security.Digester;
import security.UserSecurityModel;
import shared.ReviewDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by Kasper on 19/10/2016.
 */

@Path("/api/student")
public class StudentEndpoint extends UserEndpoint {

    @OPTIONS
    @Path("/review/{sessionId}")
    public Response optionsGetReview(@PathParam("sessionId") String sessionId) {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .header("Access-Control-Allow-Methods", "POST, DELETE")
                .build();
    }

    @POST
    @Consumes("application/json")
    @Path("/review/{sessionId}")
    public Response addReview(@PathParam("sessionId") String sessionId, String json) {
        UserSecurityModel user = Digester.VerifySession(sessionId);
        int userId = user.id;
        if (userId <= 0) {
            return errorResponse(401, "User is not authenticated.");
        }
        Gson gson = new Gson();
        try {
            ReviewDTO review = new Gson().fromJson(json, ReviewDTO.class);
            review.setUserId(userId);

            StudentController studentCtrl = new StudentController();
            boolean isAdded = studentCtrl.addReview(review);

            if (isAdded) {
                String toJson = gson.toJson(gson.toJson(isAdded));
                return successResponse(200, toJson);
            } else {
                return errorResponse(500, "Failed. Couldn't create review.");
            }
        } catch (Exception ex) {
            return errorResponse(500, "Failed. Couldn't create review. Reason: " + ex.getMessage());
        }
    }

    @DELETE
    @Consumes("application/json")
    @Path("/review/{sessionId}")
    public Response deleteReview(@PathParam("sessionId") String sessionId, String json) {
        UserSecurityModel user = Digester.VerifySession(sessionId);
        int userId = user.id;
        if (userId < 0) {
            return errorResponse(401, "User is not authenticated.");
        }
        Gson gson = new Gson();

        ReviewDTO review = gson.fromJson(json, ReviewDTO.class);
        review.setUserId(userId);
        StudentController studentCtrl = new StudentController();

        boolean isDeleted = studentCtrl.softDeleteReview(review.getUserId(), review.getId(), user);

        if (isDeleted) {
            String toJson = gson.toJson(Digester.encrypt(gson.toJson(isDeleted)));

            return successResponse(200, toJson);
        } else {
            return errorResponse(404, "Failed. Couldn't delete the chosen review.");
        }
    }
}
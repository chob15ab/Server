package view.endpoints;

import com.google.gson.Gson;
import logic.UserController;
import security.Digester;
import security.UserSecurityModel;
import service.DBWrapper;
import shared.CourseDTO;
import shared.LectureDTO;
import shared.ReviewDTO;
import shared.UserDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;


@Path("/api")
public class UserEndpoint {

    @OPTIONS
    @Path("/lecture")
    public Response optionsGetLectures() {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }

    /**
     * En metode til at hente lektioner for et enkelt kursus i form af en JSON String.
     *
     * @param sessionId User session id
     * @param course_id Fagkoden på det kursus man ønsker at hente.
     * @return En JSON String
     */
    @GET
    @Path("/lecture/{sessionId}/{course_id}") //Working
    public Response getLectures(@PathParam("sessionId") String sessionId, @PathParam("course_id") String course_id) {
        Gson gson = new Gson();
        UserSecurityModel user = Digester.VerifySession(sessionId);
        int userId = user.id;
        if (userId < 0) {
            return errorResponse(401, gson.toJson("User is not authenticated."));
        }

        UserController userCtrl = new UserController();
        ArrayList<LectureDTO> lectures = userCtrl.getLectures(course_id);

        if (!lectures.isEmpty()) {
            return successResponse(200, gson.toJson(lectures));
        } else {
            return errorResponse(200, gson.toJson("Failed. Couldn't get reviews." + userId));
        }
    }

    @OPTIONS
    @Path("/course")
    public Response optionsGetCourse() {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }

    /**
     * En metode til at hente de kurser en bruger er tilmeldt.
     *
     * @param sessionId Id'et på den bruger man ønsker at hente kurser for. TODO
     * @return De givne kurser i form af en JSON String.
     */
    @GET
    @Path("/course/{sessionId}")
    public Response getCourses(@PathParam("sessionId") String sessionId) {
        UserSecurityModel user = Digester.VerifySession(sessionId);
        int userId = user.id;
        if (userId <= 0) {
            return errorResponse(401, "User is not authenticated.");
        }

        UserController userCtrl = new UserController();
        ArrayList<CourseDTO> courses = userCtrl.getCourses(userId);

        if (!courses.isEmpty()) {
            return successResponse(200, courses);
        } else {
            return errorResponse(404, "Failed. Couldn't get reviews.");
        }
    }

    @GET
    @Path("/review/{sessionId}/{lectureId}")
    public Response getReviews(@PathParam("sessionId") String sessionId, @PathParam("lectureId") int lectureId) {
        UserSecurityModel user = Digester.VerifySession(sessionId);
        int userId = user.id;
        if (userId < 0) {
            return errorResponse(401, "User is not authorized.");
        }
        Gson gson = new Gson();
        UserController userCtrl = new UserController();
        ArrayList<ReviewDTO> reviews = userCtrl.getReviews(lectureId, user);

        if (!reviews.isEmpty()) {
            return successResponse(200, gson.toJson(reviews));
        } else {
            return errorResponse(404, "Failed. Couldn't get reviews.");
        }
    }

    @OPTIONS
    @Path("/login")
    public Response optionsLogin() {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }

    @POST
    @Consumes("application/json")
    @Path("/login")
    public Response login(String data) {

        Gson gson = new Gson();
        UserDTO user = new Gson().fromJson(data, UserDTO.class);

        if (user != null) {
            UserController userCtrl = new UserController();
            String[] userResult = userCtrl.login(user.getCbsMail(), user.getPassword());
            if (userResult != null){
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("sessionId", userResult[0]);
                map.put("type", userResult[1]);
                return successResponse(200, map);
            }
        }
        return errorResponse(401, "Couldn't login. Try again!");
    }

    @OPTIONS
    @Path("/logout/{sessionId}")
    public Response optionsLogout() {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }
    @GET
    @Path("/logout/{sessionId}")
    public Response logout(@PathParam("sessionId") String sessionId) {
        Digester.DeleteSession(sessionId);
        return errorResponse(200, "Logout complete");
    }

    protected Response errorResponse(int status, String message) {

        return Response.status(status).entity(new Gson().toJson("{\"message\": \"" + message + "\"}")).build();
        //return Response.status(status).entity(new Gson().toJson("{\"message\": \"" + message + "\"}")).build();
    }

    protected Response successResponse(int status, Object data) {
        Gson gson = new Gson();

        //Pt. udkommenteret for testing.
        //return Response.status(status).entity(gson.toJson(Digester.encrypt(gson.toJson(data)))).build();

        //Adding response headers to enable CORS in the Chrome browser
        return Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, PUT, POST").header("Access-Control-Allow-Headers", "Content-Type").entity(gson.toJson(data)).build();
    }
}

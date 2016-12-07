package logic;

import security.Digester;
import shared.LectureDTO;
import shared.Logging;
import shared.ReviewDTO;

import java.sql.Timestamp;
import java.util.ArrayList;

import service.DBWrapper;
import shared.CourseDTO;
import shared.UserDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserController {

    public static void main(String[] args) {
        UserController controller = new UserController();
        controller.getCourses(1);
    }

    public UserController() {
    }

    public UserDTO login(String cbs_email, String password) {

        UserDTO user = new UserDTO();

        try {
            Map<String, String> params = new HashMap();
            params.put("cbs_mail", String.valueOf(cbs_email));

            password = Digester.hashWithSalt(password);
            params.put("password", String.valueOf(password));

            String[] attributes = {"id, type"};
            ResultSet rs = DBWrapper.getRecords("user", attributes, params, null, 0);

            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setType(rs.getString("type"));
                System.out.print("User found");
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("User not found");
        return null;
    }

    public ArrayList<ReviewDTO> getReviews(int lectureId) {

        ArrayList<ReviewDTO> reviews = new ArrayList<ReviewDTO>();

        try {
            Map<String, String> params = new HashMap();
            params.put("lecture_id", String.valueOf(lectureId));
            params.put("is_deleted", "0");
            String[] attributes = {"id", "user_id", "lecture_id", "rating", "comment"};

            ResultSet rs = DBWrapper.getRecords("review", attributes, params, null, 0);

            while (rs.next()) {
                ReviewDTO review = new ReviewDTO();
                review.setId(rs.getInt("id"));
                review.setUserId(rs.getInt("user_id"));
                review.setLectureId(rs.getInt("lecture_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getReviews");
        }
        return reviews;
    }

    public ArrayList<LectureDTO> getLectures(String code) {

        ArrayList<LectureDTO> lectures = new ArrayList<LectureDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("course_id", code);

            ResultSet rs = DBWrapper.getRecords("lecture", null, params, null, 0);

            while (rs.next()) {
                LectureDTO lecture = new LectureDTO();

                lecture.setStartDate(rs.getTimestamp("start"));
                lecture.setEndDate(rs.getTimestamp("end"));
                lecture.setId(rs.getInt("id"));
                lecture.setType(rs.getString("type"));
                lecture.setDescription(rs.getString("description"));

                lectures.add(lecture);
            }


        }
        catch (SQLException e){
            e.printStackTrace();
        Logging.log(e,2,"Kunne ikke hente getLecture");

        }
        return lectures;
    }


    //Metode der softdeleter et review fra databasen - skal ind i AdminControlleren, da dette er moden for at slette et review uafhængigt af brugertype.
    public boolean softDeleteReview(int userId, int reviewId) {
        boolean isSoftDeleted = true;

        try {
            Map<String, String> isDeleted = new HashMap();

            isDeleted.put("is_deleted", "1");

            Map<String, String> whereParams = new HashMap();

            if(userId != 0) {
                whereParams.put("user_id", String.valueOf(userId));
            }

            whereParams.put("id", String.valueOf(reviewId));

            DBWrapper.updateRecords("review", isDeleted, whereParams);
            return isSoftDeleted;

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Softdelete kunne ikke slette review, SoftDeleteReview.");
            isSoftDeleted = false;
        }
        return isSoftDeleted;
    }

    public ArrayList<CourseDTO> getCourses(int userId) {

        ArrayList<CourseDTO> courses = new ArrayList<CourseDTO>();

        try {
            Map<String, String> params = new HashMap();
            Map<String, String> joins = new HashMap();

            //params.put("course_attendant.user_id", String.valueOf(userId));
            //joins.put("course_attendant", "course_id");

            params.put("usercourse.user_id", String.valueOf(userId));
            joins.put("usercourse", "course_id");

            String[] attributes = new String[]{"name", "code", "course.id"};
            ResultSet rs = DBWrapper.getRecords("course", attributes, params, joins, 0);

            while (rs.next()) {
                CourseDTO course = new CourseDTO();

                course.setDisplaytext(rs.getString("name"));
                course.setCode(rs.getString("code"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getCourses");
        }
        return courses;
    }

}
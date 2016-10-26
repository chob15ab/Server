package logic;

import shared.LectureDTO;
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

    public ArrayList<ReviewDTO> getReviews(int lectureId) {

        ArrayList<ReviewDTO> reviews = new ArrayList<ReviewDTO>();

        try {
            Map<String, String> params = new HashMap();
            params.put("id", String.valueOf(lectureId));
            params.put("comment_is_deleted", "0");
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
        }

        return reviews;
    }

    public ArrayList<LectureDTO> getLectures(String course) {

        ArrayList<LectureDTO> lectures = new ArrayList<LectureDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("course_id", String.valueOf(course));

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


        } catch (SQLException e) {

        }
        return lectures;
    }


    //Metode der softdeleter et review fra databasen
    public boolean deleteReview(ReviewDTO reviewDTO) {
        boolean isSoftDeleted = true;

        try {
            Map<String, String> isDeleted = new HashMap();

            if (isSoftDeleted) {
                isDeleted.put("comment_is_deleted", "1");

                Map<String, String> id = new HashMap();
                id.put("id", String.valueOf(reviewDTO.getId()));

                DBWrapper.updateRecords("review", isDeleted, id);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addReview(ReviewDTO reviewDTO) {

        try {
            Map<String, String> review = new HashMap();
            review.put("user_id", String.valueOf(reviewDTO.getUserId()));
            review.put("lecture_id", String.valueOf(reviewDTO.getLectureId()));
            review.put("rating", String.valueOf(reviewDTO.getRating()));
            review.put("comment", String.valueOf(reviewDTO.getComment()));

            DBWrapper.insertIntoRecords("review",review);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<CourseDTO> getCourses(int userId) {

        ArrayList<CourseDTO> courses = new ArrayList<CourseDTO>();

        try {
            Map<String, String> params = new HashMap();
            Map<String, String> joins = new HashMap();

            params.put("course_attendant.user_id", String.valueOf(userId));
            joins.put("course_attendant", "course_id");

            String[] attributes = new String[]{"name", "code", "course.id"};
            ResultSet rs = DBWrapper.getRecords("course", attributes, params, joins, 0);


            while (rs.next()) {
                CourseDTO course = new CourseDTO();

                course.setDisplaytext(rs.getString("name"));
                course.setCode(rs.getString("code"));
                //course.setId(rs.getInt("id"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public UserDTO login(String cbs_email, String password) {

        UserDTO user = new UserDTO();

        try {
            Map<String, String> params = new HashMap();
            params.put("cbs_mail", String.valueOf(cbs_email));
            params.put("password", String.valueOf(password));

            String[] attributes = {"id"};
            ResultSet rs = DBWrapper.getRecords("user", attributes, params, null, 0);

            while (rs.next()) {
                user.setId(rs.getInt("id"));
                System.out.print("User found");
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("User not found");
        return null;
    }
}
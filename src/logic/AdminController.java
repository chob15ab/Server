package logic;

import security.Digester;
import service.DBWrapper;
import shared.*;
import view.TUIAdminMenu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Admin klassen er logikken bag administratorens funktioner, denne klasse hører sammen med TUIAdminMenu,
 * som er UI siden af klassen.
 **/
public class AdminController extends UserController {

    private TUIAdminMenu tuiAdminMenu;


    /**
     * AdminControlleren har nedarvet fra UserControlleren dvs. den har adgang til de samme metoder.
     **/
    public AdminController() {
        super();
    }

    /**
     * deleteComment metoden sørger for at slette en kommentar udfra et indtastet id af admin brugeren (indtastes i TUI)
     **/
    public void deleteReview(AdminDTO adminDTO) {

        for (StudyDTO studyDTO : getStudies()) {
            System.out.println("id: " + studyDTO.getId() + " - forkortelse: " + studyDTO.getShortname() + " - name: " + studyDTO.getName());
        }
        Scanner input1 = new Scanner(System.in);
        System.out.println("Indtast id for det ønskede studie: ");
        int idStudyChoice = input1.nextInt();

        /**
         * Dette er en foreach løkke som printer alle lectures ud med tilhørende id
         **/
        for (CourseDTO courseDTO : getCourses(idStudyChoice)) {
            System.out.println("Id: " + courseDTO.getDisplaytext() + " - Name: " + courseDTO.getCode());
        }
                /**
                 * Her kaldes tuiAdminMenuen, som spørger admin efter et id på den Course admin ønsker og se tilhørende lectures til
                 */
                Scanner input2 = new Scanner(System.in);
                System.out.println("Indtast id for ønskede kursus: ");
                String idCourseChoice = input2.nextLine();

                /**
                 * Dette er en foreach løkke som printer alle lectures ud med deres id
                 **/

                for (LectureDTO lectureDTO : getLectures(idCourseChoice)) {
                    System.out.println("id: " + lectureDTO.getId() + " " + lectureDTO.getDescription() + " - " + lectureDTO.getType());
                }

                System.out.println("Indtast id for ønskede lecture: ");
                Scanner input3 = new Scanner(System.in);
                int idLectureChoice = input3.nextInt();

                /**
                 * Dette er en foreach løkke som printer alle reviews ud på baggrund af den givne id, som admin har skrevet
                 * ind i tuiAdminMenu.
                 */
                for (ReviewDTO reviewDTO : getReviews(idLectureChoice)) {
                    System.out.println("id: " + reviewDTO.getId() + " - Rating: " + reviewDTO.getRating() + " - Comment: " + reviewDTO.getComment() + " - Soft delete: " + reviewDTO.isDeleted());

                }

                System.out.println("Indtast id for ønskede review der skal slettes: ");
                Scanner input4 = new Scanner(System.in);
                int idReviewChoice = input4.nextInt();

                /**
                 * Nu har vi fået et id på et review fra TUIChooseReviewId, den indtastede id findes i review database-tabelen
                 * og her ændres værdien "is_deleted" til 1 dvs. den "soft deletes"
                 */

                UserController u = new UserController();
                u.softDeleteReview(0,idReviewChoice);

                TUIAdminMenu tuiAdminMenu = new TUIAdminMenu();
                tuiAdminMenu.menu(adminDTO);
            }

    /**
     * Denne metode er til at slette en bruger, hvor alle brugerne listes op i terminalen (med id)
     * og derefter kan der tastes et id ind af den bruger som skal slettes.
     **/
    public void deleteUser(AdminDTO adminDTO) {

        /**
         * Viser alle brugere i databasen.
         */
        for (UserDTO user : getUsers())
        {
            System.out.println("Id: " + user.getId() +  " Type: " + user.getType() + "  CBS mail: " + user.getCbsMail()) ;
        }

        //spørg efter id der skal slettes
        Scanner input = new Scanner(System.in);
        System.out.println("Indtast id på bruger der skal slettes: ");

        int userIdDelete = input.nextInt();
            /**
             * Her slettes brugeren fra databasen ved hjælp af den id som der er blevet skrevet ind af admin.
             */

            try{
                Map<String, String> course_attendant = new HashMap<String, String>();

                course_attendant.put("user_id", String.valueOf(userIdDelete));

                Map<String, String> id = new HashMap<String, String>();

                id.put("id", String.valueOf(userIdDelete));

                DBWrapper.deleteRecords("course_attendant", course_attendant);
                DBWrapper.deleteRecords("user", id);

                System.out.println("Brugeren er slettet" + "\n");
                TUIAdminMenu tuiAdminMenu = new TUIAdminMenu();
                tuiAdminMenu.menu(adminDTO);

            } catch(SQLException e) {
                e.printStackTrace();
                Logging.log(e, 1, "Brugeren kunne ikke slettes" + "\n");
            }
}

    /**
     * Denne metode er til at lave en ny bruger, admin skal indtaste de tilhørende parametre: mail, password og type
     * hvorefter brugeren bliver oprettet i databasen.
     **/
    public void createUser(AdminDTO adminDTO, AdminDTO newUser) {

        String mail = newUser.getCbsMail();
        String password = newUser.getPassword();
        String type = newUser.getType();

        //Hash password ved opret bruger.
        String securePw2 = Digester.hashWithSalt(password);

        /**
         * For løkke der tjekker om brugeren findes i forvejen
         */
        for (AdminDTO user : getUsers()) {
            if (mail.equals(user)) {
                System.out.println("brugeren findes allerede" + "\n");
                tuiAdminMenu = new TUIAdminMenu();
                tuiAdminMenu.menu(adminDTO);

            }
        }
        boolean hasNumber = false;
        boolean hasUppercaseLetter = false;
        boolean hasLength = false;
        if (password.matches(".*?[0-9].*?")) {
            hasNumber = true;
        }
        if (password.matches(".*?[A-Z].*?")) {
            hasUppercaseLetter = true;
        }
        if (password.length() > 2) {
            hasLength = true;
        }

                //tjekker passwordet for tal og bogstaver, om det opfylder et normalt krav til et password
                if (hasNumber && hasUppercaseLetter && hasLength) {
                        try {
                            //oprettelse af bruger i DB
                            Map<String, String> userCreate = new HashMap<String, String>();

                            userCreate.put("cbs_mail", String.valueOf(mail));
                            userCreate.put("password", String.valueOf(securePw2));
                            userCreate.put("type", String.valueOf(type));

                            DBWrapper.insertIntoRecords("user", userCreate);
                            System.out.println("Brugeren " + mail + " er nu oprettet." + "\n");
                            TUIAdminMenu tUIAdminMenu = new TUIAdminMenu();
                            tUIAdminMenu.menu(adminDTO);

                        } catch (SQLException e) {
                            e.printStackTrace();
                            Logging.log(e, 1, "Brugeren kunne ikke oprettes" + "\n");
                        }
                } else {
                    System.out.println("Forkert værdi i password. Må indebære et tal og stort bogstav. Prøv igen " + "\n");
                    tuiAdminMenu = new TUIAdminMenu();
                    tuiAdminMenu.menu(adminDTO);
                }
        }

    public void courseAssign (AdminDTO adminDTO){

        for (UserDTO user : getUsers())
        {
            System.out.println("Id: " + user.getId() +  " Type: " + user.getType() + "  CBS mail: " + user.getCbsMail()) ;
        }

        Scanner input = new Scanner(System.in);
        System.out.println("Indtast id for bruger som skal tildeles kursus: ");
        int idUserChoice = input.nextInt();

        /**
         * Dette er en foreach løkke som printer alle lectures ud med tilhørende id
         **/

        for (StudyDTO studyDTO: getStudies()){
            System.out.println("id: " + studyDTO.getId() + " - forkortelse: " + studyDTO.getShortname() + " - name: " + studyDTO.getName() );
        }

        Scanner input1 = new Scanner(System.in);
        System.out.println("Indtast id for det studie brugeren skal tilknyttet: ");
        int idStudyChoice = input1.nextInt();

        for (CourseDTO courseDTO : getCourseStudy(idStudyChoice)) {
            System.out.println("Id: " + courseDTO.getId() + " - Name: " + courseDTO.getCode() + " " + courseDTO.getDisplaytext());
        }

        /**
         * Her kaldes tuiAdminMenuen, som spørger admin efter et id på den Course admin ønsker og se tilhørende lectures til
         */
        Scanner input2 = new Scanner(System.in);
        System.out.println("Indtast id for ønskede kursus brugeren skal tildeles: ");
        int idCourseChoice = input2.nextInt();

        setUserCourse(idCourseChoice, idUserChoice);
        System.out.println("Brugeren er tilknyttet " + idCourseChoice);

        TUIAdminMenu tuiAdminMenu = new TUIAdminMenu();
        tuiAdminMenu.menu(adminDTO);

    }

    public ArrayList<CourseDTO> getCourseStudy(int idStudyChoice){
        ArrayList<CourseDTO> courses = new ArrayList<CourseDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("study_id", String.valueOf(idStudyChoice));
            String[] attributes = new String[]{"name", "code", "id"};

            ResultSet rs = DBWrapper.getRecords("course", attributes, params, null, 0);

            while (rs.next()) {
                CourseDTO courses1 = new CourseDTO();
                courses1.setId(rs.getString("id"));
                courses1.setCode(rs.getString("code"));
                courses1.setDisplaytext(rs.getString("name"));

                courses.add(courses1);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getCouses");

        }
        return courses;
    }

    private ArrayList<UserDTO> setUserCourse(int idCourseChoice, int userId) {

        ArrayList<UserDTO> userscourse = new ArrayList<UserDTO>();
        try {
            Map<String, String> params = new HashMap();

            params.put("course_id", Integer.toString(idCourseChoice));
            params.put("user_id", Integer.toString(userId));
            DBWrapper.insertIntoRecords("usercourse", params);

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e, 1, "Funktionen kunne ikke hente kurserne" + "\n");
        }

        return userscourse;

    }

        private ArrayList<AdminDTO> getUsers() {

            ArrayList<AdminDTO> users = new ArrayList<AdminDTO>();
            try {
                String[] attributes = {"id", "cbs_mail", "type"};

                ResultSet rs = DBWrapper.getRecords("user", attributes, null, null, 0);

                while (rs.next()) {
                    AdminDTO user = new AdminDTO();
                    user.setId(rs.getInt("id"));
                    user.setCbsMail(rs.getString("cbs_mail"));
                    user.setType(rs.getString("type"));
                    users.add(user);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Logging.log(e, 1, "Funktionen getUsers kunne ikke hente Brugerne" + "\n");
            }

            return users;

        }


    private ArrayList<CourseDTO> getCourses() {

        ArrayList<CourseDTO> courses = new ArrayList<CourseDTO>();

        try {
            String[] attributes = new String[]{"name", "code", "id"};
            ResultSet rs = DBWrapper.getRecords("course", attributes, null, null, 0);


            while (rs.next()) {
                CourseDTO course = new CourseDTO();

                course.setDisplaytext(rs.getString("name"));
                course.setCode(rs.getString("code"));
                //course.setId(rs.getInt("id"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getCourses");
        }
        return courses;
    }

    public ArrayList<LectureDTO> getLectures(String course) {

        ArrayList<LectureDTO> lectures = new ArrayList<LectureDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("course_id", String.valueOf(course));
            String[] attributes = new String[]{"description", "type", "id"};

            ResultSet rs = DBWrapper.getRecords("lecture", attributes, params, null, 0);

            while (rs.next()) {
                LectureDTO lecture = new LectureDTO();

                lecture.setLectureId(rs.getInt("id"));
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


    public ArrayList<ReviewDTO> getReviews(int lectureId) {

        ArrayList<ReviewDTO> reviews = new ArrayList<ReviewDTO>();

        try {
            Map<String, String> params = new HashMap();
            params.put("lecture_id", String.valueOf(lectureId));
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
            Logging.log(e, 2, "Kunne ikke hente getReviews");
        }

        return reviews;
    }


    private ArrayList<StudyDTO> getStudies() {

        ArrayList<StudyDTO> studies = new ArrayList<StudyDTO>();

        try {


            ResultSet rs = DBWrapper.getRecords("study", null, null, null, 0);


            while (rs.next()) {
                StudyDTO study = new StudyDTO();

                study.setName(rs.getString("name"));
                study.setShortname(rs.getString("shortname"));
                study.setId(rs.getInt("id"));
                studies.add(study);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente Studies");
        }
        return studies;
    }

}
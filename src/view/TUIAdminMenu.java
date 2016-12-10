package view;

import logic.AdminController;
import logic.UserController;
import shared.AdminDTO;
import shared.StudentDTO;
import shared.UserDTO;
import view.endpoints.UserEndpoint;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Her ses TUIAdminMenuen, som er admin's text-baseret user interface menu.
 **/
public class TUIAdminMenu {

    private AdminController adminController;
    private StudentDTO studentDTO;
    private AdminDTO adminDTO;
    private UserDTO userDTO;

    public void menu(AdminDTO adminDTO) {
        adminController = new AdminController();
        Scanner input = new Scanner(System.in);
        try {

            /**
             * Her ses de muligheder som admin har i en menu.
             **/

            System.out.println("Velkommen til Undervisningsevaluering for CBS administratorer!");
            System.out.println("Du er logget ind som: " +  adminDTO.getCbsMail());
            System.out.println("Tast 0 for at stoppe programmet og log ud.");
            System.out.println("Tast 1 for at oprette en ny bruger. ");
            System.out.println("Tast 2 for slet en bruger. ");
            System.out.println("Tast 3 for slet et review.");
            System.out.println("Tast 4 for tildeling af kursus til bruger.");

            //UserEndpoint u = new UserEndpoint();
            //u.getCourses("YhMnOTGCrGrMbcYxeILKPgboBNiinkPcL-GlemuqbBRTsvKZSzGadaVFlhhisixYAqbeqgGkSFxGlasEOViyPMlTFNMzHzqIIKUaFMsHhUvsIVGPGatVjAmO-cFBHGjwrSebvNVXKgdhVNzcgrLebfyKaZZebQPlcyJnEqAE-YmIxSJbxAMGfOU-qcVGSidXukXlniUJ");

            /**
             * Her modtager vi admins respons til ovenstående menu, og sender dem i gennem en switch som sender
             * admin videre til den valgte metode. fx. 1 starter adminController.createUser();
             **/

            int choice = input.nextInt();

            switch (choice) {
                case 0:
                    System.out.println("Programmet er stoppet og du er logget ud.");
                    System.exit(0);
                    break;

                case 1:
                    AdminDTO newUser = new AdminDTO();
                    TUICreateUser(adminDTO, newUser);
                    adminController = new AdminController();
                    adminController.createUser(adminDTO, newUser);

                    break;

                case 2:
                    int userId = adminDTO.getId();
                    adminController = new AdminController();
                    adminController.deleteUser(adminDTO);
                    break;

                case 3:
                    adminController = new AdminController();
                    adminController.deleteReview(adminDTO);
                    break;
                case 4:
                    adminController = new AdminController();
                    adminController.courseAssign(adminDTO);

                default:
                    System.out.println("Du indtastede en forkert vaerdi, proev igen.\n");
                    menu(adminDTO);
            }

	/**
     * Her er en catch som træder i kraft, hvis brugeren taster en invalid vaerdi.
     **/
        } catch (InputMismatchException e) {
            System.out.printf("Systemet fandt fejlen: %s \n", e);
            System.out.println("Du indtastede ikke et heltal, menuen kører forfra");
            menu(adminDTO);
        }
    }

    /**
     * TUIChooseCourseId er bruges som "søgekriterie" til at finde frem til det review der ønskes slettet.
     * Logikken bag denne metode ses i AdminControlleren
     **/
    public int TUIChooseCourseId(int idCourseChoice) {

        Scanner input = new Scanner(System.in);
        System.out.println("Indtast id for ønskede kursus: ");

        idCourseChoice = input.nextInt();

        return  idCourseChoice;
        }

    /**
     * TUICreateUser tager admins input som den nye brugers parametre: CBS mail, Password og Type
     * og sender variablerne videre til logik laget (AdminController)
     **/
    public Object TUICreateUser(AdminDTO adminDTO, AdminDTO newUser){

        Scanner mail_input = new Scanner(System.in);
        System.out.println("Indtast CBS mail: ");
        String mail = mail_input.nextLine();

        Scanner password_input = new Scanner(System.in);
        System.out.println("Indtast password: ");
        String password = password_input.nextLine();

        Scanner type_input = new Scanner(System.in);
        System.out.println("Indtast type (student, teacher, admin): ");
        String type = type_input.nextLine();

        newUser.setCbsMail(mail);
        newUser.setPassword(password);
        newUser.setType(type);

        return newUser;
    }

}


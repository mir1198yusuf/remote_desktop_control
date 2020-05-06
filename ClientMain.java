
import java.awt.*;	// robot class
import java.awt.event.*;	
import javax.swing.*;	//joptionpane
import java.net.*;	//socket
import java.io.*;	//stream
import java.awt.image.*;	// not used
import javax.imageio.*;		//
import java.util.Scanner;





// main class of client
public class ClientMain {

    Socket socket = null;

    public static void main(String[] args){
		
		//joptionpane is small window with a label
		//showInoputDialog accepts user input and returns as string
        String ip = JOptionPane.showInputDialog("Please enter server IP");
        String port = JOptionPane.showInputDialog("Please enter server port");
        new ClientMain().initialize(ip, Integer.parseInt(port));
    }

    public void initialize(String ip, int port ){

        Robot robot ; //Used to capture the screen
        Rectangle rectangle; //Used to represent screen dimensions

        try {
            System.out.println("Connecting to server ..");
            socket = new Socket(ip, port);
            System.out.println("Connection done.");

          
            //Get screen dimensions
			// dimension n rectangel class is present in awt package
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            rectangle = new Rectangle(dim);

            //Prepare Robot object
            robot = new Robot();

            
            //ScreenSender sends screenshots of the client screen
            new ScreenSender(socket,robot,rectangle);
			
			
            //ServerCmdExecution recieves server commands and execute them
            new ServerCmdExecution(socket,robot);
        }

		catch (Exception ex) {
            ///
        } 
    }
}
 






//send sshots
class ScreenSender extends Thread {

    Socket socket ; 
    Robot robot; // Used to capture screen
    Rectangle rectangle; //Used to represent screen dimensions
    boolean continueLoop = true; //Used to exit the program
    
    public ScreenSender(Socket socket, Robot robot,Rectangle rect) {
        this.socket = socket;
        this.robot = robot;
        rectangle = rect;
        start();	//to start the thread
    }

	// overriding run() of Thread class
    public void run()
	{
        ObjectOutputStream oos = null ; //Used to write an object to the streem


        try{
            //Prepare ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            /*
             * Send screen size to the server in order to calculate correct mouse
             * location on the server's panel
             */
            oos.writeObject(rectangle);
        }catch(Exception ex){
            ////
        }

       while(continueLoop){
            //Capture screen
            BufferedImage image = robot.createScreenCapture(rectangle);
           
		   // we acnnot send BufferedImage in stream
		   // so we converted it to imageicon
		   
            ImageIcon imageIcon = new ImageIcon(image);

            //Send captured screen to the server
            try {
                System.out.println("before sending image");
                oos.writeObject(imageIcon);
                oos.reset(); //Clear ObjectOutputStream cache
                System.out.println("New screenshot sent");
            } 
			catch (Exception ex) {
               ////
            }

            //wait for 100ms to reduce network traffic
            try{
                Thread.sleep(100);
            }
			catch(Exception e){
                /////
            }
        }

    }

}



class ServerCmdExecution extends Thread {

    Socket socket = null;
    Robot robot = null;
    boolean continueLoop = true;

    public ServerCmdExecution(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start(); //Start the thread and hence calling run method
    }

    public void run(){
        Scanner scanner = null;
        try {
            //prepare Scanner object
            System.out.println("Preparing InputStream");
            scanner = new Scanner(socket.getInputStream());

            while(continueLoop){
                //recieve commands and respond accordingly
                System.out.println("Waiting for command");
                int command = scanner.nextInt();
                System.out.println("New command: " + command);
                switch(command){
                    case -1:
                        robot.mousePress(scanner.nextInt());
                    break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
                    break;
                    case -3:
                        robot.keyPress(scanner.nextInt());
                    break;
                    case -4:
                        robot.keyRelease(scanner.nextInt());
                    break;
                    case -5:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                    break;
                }
            }
        } catch (Exception e) {
            ///
        }
    }

}

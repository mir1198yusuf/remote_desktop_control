
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * This is the entry class of the server
 */
public class ServerMain {
    //Main server frame
    private JFrame frame = new JFrame("SERVER MAIN");
    //JDesktopPane represents the main container that will contain all
    //connected clients' screens
    private JDesktopPane desktop = new JDesktopPane();
	
    public static void main(String args[]){
		// showinputdialog will return user input as string
        String port = JOptionPane.showInputDialog("Please enter listening port");
        new ServerMain().initialize(Integer.parseInt(port));
    }

    public void initialize(int port){

        try {
            ServerSocket sc = new ServerSocket(port);
            //Show Server GUI
            drawGUI();
            //Listen to server port and accept clients connections
            while(true){
                Socket client = sc.accept();
                System.out.println("New client Connected to the server");
                //Per each client create a ClientCreation
                new ClientCreation(client,desktop);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * Draws the main server GUI
     */
    public void drawGUI(){
            frame.add(desktop,BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //Show the frame in a maximized state
            frame.setExtendedState(frame.getExtendedState());		/// |JFrame.MAXIMIZED_BOTH
            frame.setVisible(true);
    }
	
	
	
	
}

class ClientCreation extends Thread {

   
    JInternalFrame interFrame = new JInternalFrame("Client Screen",true, true, true);
     JPanel cPanel = new JPanel();
	 Socket client;
	 JDesktopPane desktop;
    
    ClientCreation(Socket client, JDesktopPane desktop) {
        
		this.client = client;
		this.desktop = desktop;
        start();
    }

    /*
     * Draw GUI per each connected client
     */
    public void drawGUI(){
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel,BorderLayout.CENTER);
        interFrame.setSize(100,100);
        desktop.add(interFrame);
        try {
            //Initially show the internal frame maximized
            interFrame.setMaximum(true);
        } catch (Exception e) {
           ///
        }
        //this allows to handle KeyListener events
        cPanel.setFocusable(true);
        interFrame.setVisible(true);
    }

    public void run(){

        //used to represent client screen size
        Rectangle clientScreenDim = null;
        //Used to read screenshots and client screen dimension
        ObjectInputStream ois = null;
        //start drawing GUI
        drawGUI();

        try{
            //Read client screen dimension
            ois = new ObjectInputStream(client.getInputStream());
            clientScreenDim =(Rectangle) ois.readObject();
        }catch(Exception e){
            ////
        }
        //Start recieveing screenshots
        new ClientScreenReciever(ois,cPanel);
        //Start sending events to the client
        new ClientCommandsSender(client,cPanel,clientScreenDim);
    }

}




class ClientScreenReciever extends Thread {

     ObjectInputStream ois;
     JPanel p;
     boolean continueLoop = true;

    public ClientScreenReciever(ObjectInputStream ois, JPanel p) {
        this.ois = ois;
        this.p = p;
        //start the thread and thus call the run method
        start();
    }

    public void run(){
        
            try {
                
                //Read screenshots of the client then draw them
                while(continueLoop){
                    //Recieve client screenshot and resize it to the current panel size
                    ImageIcon imageIcon = (ImageIcon) ois.readObject();
                    System.out.println("New image recieved");
                    Image image = imageIcon.getImage();
                    image = image.getScaledInstance(p.getWidth(),p.getHeight()
                                                        ,Image.SCALE_SMOOTH);
                    //Draw the recieved screenshot
                    Graphics graphics = p.getGraphics();
                    graphics.drawImage(image, 0, 0, p.getWidth(),p.getHeight(),p);
                }
            } catch (Exception e) {
               ////
          } 
     }
}



class ClientCommandsSender implements KeyListener,
        MouseMotionListener,MouseListener {

     Socket s;
     JPanel p ;
     PrintWriter writer ;
     Rectangle r ;

    ClientCommandsSender(Socket s, JPanel p, Rectangle r) {
        this.s = s;
        this.p = p;
        this.r = r;
        //Associate event listners to the panel
        p.addKeyListener(this);
        p.addMouseListener(this);
        p.addMouseMotionListener(this);
        try {
             //Prepare PrintWriter which will be used to send commands to
             //the client
            writer = new PrintWriter(s.getOutputStream());
        } catch (Exception e) {
           /////
        }
        
    }

    //Not implemeted yet
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
		
		// we find ratio between client screen n server screen size by dividing them
        double xScale = r.getWidth()/p.getWidth();
        System.out.println("xScale: " + xScale);
        double yScale = r.getHeight()/p.getHeight();
        System.out.println("yScale: " + yScale);
        System.out.println("Mouse Moved");
        writer.println(-5);
        writer.println((int)(e.getX() * xScale));
        writer.println((int)(e.getY() * yScale));
        writer.flush();
    }

    //this is not implemented
    public void mouseClicked(MouseEvent e) {
    }

	
	/*	for getButton() methiod left mouse click = 1
									right mouse click = 3
									
			for robot class left mouse click = 16
							right mouse click = 4
							
							*/
	public void mousePressed(MouseEvent e) {
        System.out.println("Mouse Pressed");
        writer.println(-1);
        int button = e.getButton();
		//first we assume left button is clicked
        int xButton = 16;
        if (button == 3) // if right button is clciked
		{
            xButton = 4;
        }
		
		// xbutton is value used to tell robot class which mouse button is pressed
        writer.println(xButton);
        writer.flush();
    }

    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse Released");
        writer.println(-2);
        int button = e.getButton();
		System.out.println(button);
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    //not implemented
    public void mouseEntered(MouseEvent e) {
    }

    //not implemented
    public void mouseExited(MouseEvent e) {

    }

    //not implemented
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("Key Pressed");
        writer.println(-3);
        writer.println(e.getKeyCode());
        writer.flush();
    }

    public void keyReleased(KeyEvent e) {
        System.out.println("Mouse Released");
        writer.println(-4);
        writer.println(e.getKeyCode());
        writer.flush();
    }  

}

/*

    PRESS_MOUSE(-1),
    RELEASE_MOUSE(-2),
    PRESS_KEY(-3),
    RELEASE_KEY(-4),
    MOVE_MOUSE(-5);

   



*/
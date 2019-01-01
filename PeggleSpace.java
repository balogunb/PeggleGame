import acm.program.*;
import acm.graphics.*;
import acm.util.*;
import java.awt.*;
import java.util.*;
/**
 * PeggleSpace
 * This class draws the graphics,checks collision and controls the direction the cannon shoots at  
 * @Basit Balogun
 * 
 * Testing Hypothesis - 
 * I ended up needing a lot more variables like cannonAngle, peg, hitCount and making my labels
 * instant variables for my game to work the way I wanted it to. I also removed some of the variables 
 * and static constants like SPEED,WALL_W, because they did not seem necessary for the implementation 
 * of this program. Most importantly  I drew my peg in drawPegs method rather than in draw graphics 
 * because it allowed me to let my game restart with minimal complications. other changes are explained
 * in CannonBall class comment section and HitBall class comment section.
 */
public class PeggleSpace extends GraphicsProgram {
    // initial size of the window 
    public static int 
    APPLICATION_WIDTH = 800,
    APPLICATION_HEIGHT = 800;

    // constants
    private static final double
    BASE_SIZE = 100,
    CANNON_SIZE = BASE_SIZE * 0.7;

    // instance variables 
    private double width, height;
    private GOval base;
    private GLabel scoreBoard,result;
    private GPolygon cannon;
    private int scoreNum;
    private GLine aimLine; // a line to aim the cannon
    private double cannonAngle = -90; // current angle of the cannon
    private RandomGenerator rand = new RandomGenerator();
    private boolean gameIsOver = false ;
    private HitBall[] peg = new HitBall[50];   // an array of 50 pegs 
    private CannonBall[] ballArray = new CannonBall[100]; //an array of cannon balls
    int pegNum = 50;

    int hitCount = 0;   //used to know when there is no more pegs left 

    /** the run method, draw the inital graphics */
    public void run() {
        drawGraphics();

    }

    /** initially draw the background, a ball using HitBall class and a label */
    private void drawGraphics() {
        // draw background 
        GImage background = new GImage("background.jpg");
        background.setSize(800,800);
        add(background,0,0);

        // GRect gameBackground = new GRect(0,0,getWidth(),getHeight());
        // gameBackground.setFilled(true);
        // gameBackground.setColor(Color.BLACK);
        // add(gameBackground);

        //draw stars in background 
        int starLimit = 1000;
        while ( starLimit-- > 0)  {
            GOval star = new GOval(rand.nextDouble(0,getWidth()),//random horizontal point
                    rand.nextDouble(0,getHeight()),//random vertical point
                    2,2);
            star.setColor(rand.nextColor());
            star.setFilled(true);
            add(star);
        }

        // draw a message to give instructions to restart the game
        result = new GLabel("Game Over! " + scoreNum + " Shots fired. Click to restart!");
        result.setColor(Color.WHITE);
        result.setFont(new Font("SANS_SERIF", Font.BOLD,25));
        add(result,getWidth()/2 - result.getWidth()/2,getHeight()/2);
        result.setVisible(false);

        //draw a message to show number of times the cannonBall was shot
        scoreBoard = new GLabel("Shots fired: "+ scoreNum);
        scoreBoard.setColor(Color.WHITE);
        scoreBoard.setFont(new Font("SANS_SERIF", Font.BOLD,15));
        add(scoreBoard,getWidth()-200,20);

        // draw a blue base at the top as the base of the cannon 
        GOval cannonBase = new GOval(BASE_SIZE,BASE_SIZE);
        cannonBase.setFilled(true);
        cannonBase.setColor(Color.BLUE);
        add(cannonBase,getWidth()/2 - BASE_SIZE/2,-BASE_SIZE/2);

        // draw a GPolygon as the cannon
        createCannon(CANNON_SIZE);
        add(cannon,getWidth()/2,0);
        cannon.setFilled(true);
        cannon.setFillColor(Color.GREEN);

        // draw a line from center of star to mouse point
        // the mouse point is unknown at this time
        aimLine = new GLine(cannon.getX(),cannon.getY(),
            cannon.getX(),cannon.getY());

        aimLine.setColor(Color.red);
        add(aimLine);

        // draw a cap on the top of the base of the cannon
        GOval cannonCap = new GOval(BASE_SIZE/2,BASE_SIZE/2);
        cannonCap.setFilled(true);
        cannonCap.setColor(Color.BLACK);
        cannonCap.setFillColor(Color.YELLOW);
        add(cannonCap,getWidth()/2 - BASE_SIZE/4, -BASE_SIZE/4);

        drawPegs();

    }

    /** draw a polygon as the barrel */
    private void createCannon(double size) {
        cannon = new GPolygon();
        //add the four corners of the cannon 
        cannon.addVertex(-size/4,0);
        cannon.addVertex(size/4,0);
        cannon.addVertex(size/6,size);
        cannon.addVertex(-size/6,size);

    }

    public void drawPegs(){
        // use array to draw multiple instances of the hit ball 
        for (int i= 0; i < pegNum; i++) {
            double    pegSize = 20*1.3;
            peg[i] = new HitBall(rand.nextDouble(5,15), this);
            add(peg[i],rand.nextDouble(pegSize/2,getWidth()-pegSize/2),rand.nextDouble(getHeight()/4,0.75*getHeight()-pegSize));
            new Thread(peg[i]).start();
        }

    }

    /** create a Cannon when mouse is pressed */
    public void mousePressed(GPoint point) {
        //if gameIsOver is true redraw graphics 
        if (gameIsOver) {

            drawPegs();
            gameIsOver = false;
            scoreNum = 0;
            result.setVisible(false);
            scoreBoard.setLabel("Shots fired: "+ scoreNum);

        }
        else{

            // the tip of the cannon is where the projectile appears
            GPoint cannonTip = getCannonTip();

            // create a cannon ball
            CannonBall ball = new CannonBall(0.05*getLength(aimLine),getAngle(aimLine),this);

            // add to the tip of the cannon barrel
            add(ball,cannonTip.getX(), cannonTip.getY());

            // start the animation of the cannonball
            new Thread(ball).start();
            //increase the score 
            scoreNum = scoreNum + 1 ;
            scoreBoard.setLabel("Shots fired: "+ scoreNum);
        }

    }
    // get the point at the tip of the cannon
    private GPoint getCannonTip() {
        // use a pen to find the tip
        GPen pen = new GPen(cannon.getX(), cannon.getY());
        pen.movePolar(CANNON_SIZE, getAngle(aimLine));
        return new GPoint(pen.getX(), pen.getY());
    }

    /** rotate the cannon as mouse moves */
    public void mouseMoved(GPoint point) {  
        //use the mouse pointer's point to move the cannon
        if(gameIsOver) return;
        if(cannon != null) rotateCannonToPoint(point);
    }

    /** check if the Cannonball hits anything */
    public void checkCollision(CannonBall ball) {
        //check if the cannon ball hit left or right wall to bounce the ball of the wall
        if (ball.getX() - ball.getWidth()/2 < 0 ||
        ball.getX() + ball.getWidth()/2 > getWidth())
            ball.setAngle(180-ball.getAngle()); 

        //check if cannon ball hits bottom boundary, if true kill the ball 
        if( ball.getY() + ball.getHeight() > getHeight() ){
            //ball.setLocation(ball.getX(),getHeight());
            ball.die();
        }

        //check if cannon ball colllides with the Hitball, set run method die which sets isHit to true 
        for (int i= 0; i < pegNum; i++) {
            if (peg[i].isHit()== false && ball.getBounds().intersects(peg[i].getBounds())){

                ball.setAngle(-ball.getAngle()); 
                peg[i].die();
                hitCount++;

                // //allow 3 max collisions for cannon ball limits peg to 100
                // for (int k= 0; k < 100; k++) {

                    // for( int l = 0 ; l > l++){
                        // ballArray[k].explode();
                        // ballArray[k].die();
                    // }
                    // k = 100;
                // }

                if(peg[i].getIsExplosive()==true){
                    CannonBall ball1 = new CannonBall(ball.getSpeed(),ball.getAngle()+ 30,this);
                    add(ball1,ball.getX(), ball.getY());
                    new Thread(ball1).start();

                    CannonBall ball2 = new CannonBall(ball.getSpeed(),ball.getAngle()- 30,this);
                    add(ball2,ball.getX(), ball.getY());
                    new Thread(ball2).start();

                }
                checkStatus();

            }
        }
    }

    // rotate the cannon to align with a line from center of the cannon to the point
    private void rotateCannonToPoint(GPoint point) {

        // change the end point of the line
        aimLine.setEndPoint(point.getX(), point.getY());
        // calculate the current angle of the line
        double lineAngle = getAngle(aimLine); 
        // rotate the cannon by the difference 
        cannon.rotate(lineAngle - cannonAngle); 
        // update the lastAngle
        cannonAngle = lineAngle;
    }

    // calculate the angle of the line and return the angle 
    private double getAngle(GLine line) {

        return GMath.angle(line.getStartPoint().getX(),
            line.getStartPoint().getY(),
            line.getEndPoint().getX(), 
            line.getEndPoint().getY());
    }  

    // calculate the length of the line
    private double getLength(GLine line) {
        // calculate the length of the line and return the length 
        return GMath.distance(line.getStartPoint().getX(),
            line.getStartPoint().getY(),
            line.getEndPoint().getX(), 
            line.getEndPoint().getY());
    }

    private boolean checkStatus(){
        //if all the balls in the array  have been hit, set gameIsOver to true
        if(hitCount == pegNum){
            gameIsOver = true;
            result.setLabel("Game Over! " + scoreNum + " Shots fired. Click to restart!");
            result.setVisible(true);
            hitCount = 0;
        }
        return gameIsOver;
    }

}

import acm.program.*;
import acm.graphics.*;
import java.awt.*;
/**
 * CannonBall
 * This class is for the balls shot from the cannon in the PeggleGame.
 * @Basit Balogun
 * Testing Hypothesis-
 * Added a die method to allow me to remove the cannonBall in the PeggleSpace class. Also added 
 * a getSpeed method which allowed me to use the balls speed to create new instance of Cannon ball
 * with the same speed in the PeggleSpace class.
 */
public class CannonBall extends GCompound implements Runnable {
    // constants
    private static final double 
    DELAY = 20,
    GRAVITY = 0.6; // gravity co-ef, added to ySpeed in every step

    // instance variables 
    private PeggleSpace game; // the main game 
    private double speed, angle; // speed and direction of movement
    private boolean isAlive = true;
    private double size = 16;

    /** the constructor, create the ball */
    public CannonBall( double speed, double angle, 
    PeggleSpace game) {
        // save the parameters in instance variables
        this.game = game;
        this.speed = speed;
        this.angle = angle;

        //create a ball centered at the origin of the local coordinate system 
        GOval ball  = new GOval(-size/2,-size/2,size,size);
        ball.setFilled(true);
        ball.setColor(Color.GREEN);
        add(ball);
    } 

    /** the run method, to animate the ball */
    public void run() {
        while (isAlive) {
            oneTimeStep();
            pause(DELAY);      
        }   
        explode(); // explode and disappear if is alive is false 
    }
    
     /** return the angle of movement */ 
    public double getSpeed() {
        return speed;
    }

    /** return the angle of movement */ 
    public double getAngle() {
        return angle;
    }

    /** change the angle of movement when bounce */ 
    public void setAngle(double angle) {
        this.angle = angle;

    }

    // in each time step, move the ball and bounce if hit the wall
    private void oneTimeStep() {
        // move the ball using polar coordinates 
        movePolar(speed, angle); 
        game.checkCollision(this);// check if hit anything using check collision 
        applyGravity(); // apply the effect of gravity
    }
    // apply the effect of gravity
    private void applyGravity() {
        // calculate xSpeed and ySpeed of tbe ball
        double xSpeed = speed*GMath.cosDegrees(angle);
        double ySpeed = -speed*GMath.sinDegrees(angle);

        // apply gravity  by increasing the ySpeed
        ySpeed += GRAVITY; 

        // calculate new speed and angle
        speed = GMath.distance(xSpeed, ySpeed);
        angle = GMath.angle(xSpeed, ySpeed);
    }

    /** stop the animation */
    public void die() {
        isAlive = false;
    }
    

    // show explosion and disappear
    public void explode() {
        // remove the ball 
        removeAll();
        // draw an explosion using Gimage and set the location to the location of the ball
        GImage explosion = new GImage("bigexplosion.gif");
        explosion.setSize(2*size, 2*size);
        add(explosion, -size, -size);
        pause(500);
        removeAll();
    }

}

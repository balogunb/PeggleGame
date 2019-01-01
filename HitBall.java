import acm.program.*;
import acm.graphics.*;
import java.awt.*;
import acm.util.*;
/**
 * HitBall
 * A class for balls that would be hit by the CannonBall in the PeggleGame
 * Testing Hypothesis-
 * Added method explode to allow the ball explode when hit by a cannon ball 
 * 
 */
public class HitBall extends GCompound implements Runnable {
    // constants
    private static final double DELAY = 70;
    

    // instance variables
    private PeggleSpace game; // the main game
    private double size = 28;
    private boolean isHit = false; //if the ball has not been hit
    private boolean isExplosive; //checks if the ball is explosive
    private double speed;//should be random speed so they dont move at the same speed 
    private RandomGenerator rand = new RandomGenerator();
    Color color;
    int count;
    GOval smallCircle;

    /** the constructor, create the hit ball */
    public HitBall( double speed  ,PeggleSpace game){
        //save the parameters in the instance variable 
        this.game = game;
        this.speed = speed;//the speed at which the ball moves up and down 

        //create a ball using two GOval centered at the local origin 

        GOval largeCircle = new GOval(-size*1.3/2,-size*1.3/2,size*1.3,size*1.3);
        largeCircle.setFilled(true);
        largeCircle.setFillColor(Color.WHITE);
        largeCircle.setColor(Color.BLACK);
        add(largeCircle);

        smallCircle  = new GOval(-size/2,-size/2,size,size);
        smallCircle.setFilled(true);
        smallCircle.setColor(Color.BLACK);
        add(smallCircle);

        //use if statement to give the ball a 10% chance of being explosive 
        int x = rand.nextInt(1,10);
        if (x % 10 == 2) {
            isExplosive = true;
        }

        if (isExplosive){
            //smallCircle.setFillColor(Color.RED);
            color  = Color.RED;
        }
        else{
            //smallCircle.setFillColor(Color.BLUE);
            color  = Color.BLUE;
        }

        smallCircle.setFillColor(color);
        count = rand.nextInt(0,13);
    }

    /** the run method, to animate the ball*/
    public void run() {
        while (isHit == false) {
            oneTimeStep();
            pause(DELAY);
        }   
         explode();//remove the ball if isHit is true 
    }

    private void oneTimeStep() {
        move(0,speed);
        //switch between orginal color and black
        count++;

        if (count % 3 == 0){
            smallCircle.setFillColor(Color.BLACK);

        }
        else {
            smallCircle.setFillColor(color);

        }
        
        //move up and down 
        if (count%4== 0){
            speed = -speed;
        }
    }

    /** kill the HitBall  */
    public void die() {
        isHit = true;
    }

    public boolean isHit() {
        return isHit;
    }
    
    public boolean getIsExplosive() {
    return isExplosive;
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

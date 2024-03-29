import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;
import processing.core.PApplet;


public class MainFinal extends PApplet
{
    //when in doubt, consult the Processsing reference: https://processing.org/reference/

    int margin = 200; //set the margin around the squares
    final int padding = 50; // padding between buttons and also their width/height
    final int buttonSize = 40; // padding between buttons and also their width/height
    ArrayList<Integer> trials = new ArrayList<Integer>(); //contains the order of buttons that activate in the test
    int trialNum = 0; //the current trial number (indexes into trials array above)
    int startTime = 0; // time starts when the first click is captured
    float prevClickTime = 0;
    int finishTime = 0; //records the time of the final click
    int hits = 0; //number of successful clicks
    int misses = 0; //number of missed clicks
    Robot robot; //initialized in setup
    int prevCursorX = 0;
    int prevCursorY = 0;
    int participantID = 1;

    int numRepeats = 20; //sets the number of times each button repeats in the test


    public static void main(String[] args) {
        PApplet.main("MainFinal");
    }

    @Override
    public void settings() {
        size(700,700); // set the size of the window
    }

    public void setup()
    {
        //noCursor(); // hides the system cursor if you want
        cursor(CROSS);
        noStroke(); //turn off all strokes, we're just using fills here (can change this if you want)
        textFont(createFont("Arial",16)); //sets the font to Arial size 16
        textAlign(CENTER);
        frameRate(60); //normally you can't go much higher than 60 FPS.
        ellipseMode(CENTER); //ellipses are drawn from the center (BUT RECTANGLES ARE NOT!)
        //rectMode(CENTER); //enabling will break the scaffold code, but you might find it easier to work with centered rects

        try {
            robot = new Robot(); //create a "Java Robot" class that can move the system cursor
        } catch (AWTException e) {
            e.printStackTrace();
        }

        //===DON'T MODIFY MY RANDOM ORDERING CODE==
        for (int i = 0; i < 16; i++) //generate list of targets and randomize the order
            // number of buttons in 4x4 grid
            for (int k = 0; k < numRepeats; k++)
                // number of times each button repeats
                trials.add(i);

        Collections.shuffle(trials); // randomize the order of the buttons
        System.out.println("trial order: " + trials); //print out order for reference

        surface.setLocation(0,0);// put window in top left corner of screen (doesn't always work)
    }


    public void draw()
    {
        background(0); //set background to black

        if (trialNum >= trials.size()) //check to see if test is over
        {
            float timeTaken = (finishTime-startTime) / 1000f;
            float penalty = constrain(((95f-((float)hits*100f/(float)(hits+misses)))*.2f),0,100);
            fill(255); //set fill color to white
            //write to screen (not console)
            text("Finished!", width / 2, height / 2);
            text("Hits: " + hits, width / 2, height / 2 + 20);
            text("Misses: " + misses, width / 2, height / 2 + 40);
            text("Accuracy: " + (float)hits*100f/(float)(hits+misses) +"%", width / 2, height / 2 + 60);
            text("Total time taken: " + timeTaken + " sec", width / 2, height / 2 + 80);
            text("Average time for each button: " + nf((timeTaken)/(float)(hits+misses),0,3) + " sec", width / 2, height / 2 + 100);
            text("Average time for each button + penalty: " + nf(((timeTaken)/(float)(hits+misses) + penalty),0,3) + " sec", width / 2, height / 2 + 140);
            return; //return, nothing else to do now test is over
        }

        fill(255); //set fill color to white
        text((trialNum + 1) + " of " + trials.size(), 40, 20); //display what trial the user is on

        for (int i = 0; i < 16; i++) {// for all button
            Rectangle bounds = getButtonLocation(i);
            if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width) && (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) {
                fill(161, 252, 255);
                rect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            else {
                drawButton(i); //draw button
            }
        }
    }

    public void mousePressed() // test to see if hit was in target!
    {
        if (trialNum >= trials.size()) //check if task is done
            return;

        if (trialNum == 0) //check if first click, if so, record start time
            startTime = millis();

        if (trialNum == trials.size() - 1) //check if final click
        {
            finishTime = millis();
            //write to terminal some output:
            System.out.println("we're all done!");
        }

        Rectangle bounds = getButtonLocation(trials.get(trialNum));
        
        int success = 0;
        //check to see if cursor was inside button
        if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width) && (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
        {
            // System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
            success = 1;
            hits++;
        } else
        {
            // System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
            misses++;
        }

        float currTime = millis();
        float timeTaken = currTime - prevClickTime;
        prevClickTime = currTime;
        
        System.out.println(trialNum + "," + participantID + "," + prevCursorX + "," + prevCursorY + "," + 
                          (bounds.x + bounds.width)/2 + "," + (bounds.y + bounds.height)/2 + "," + buttonSize + "," + timeTaken/1000.0 + "," + success);
        trialNum++; // Increment trial number
        prevCursorX = mouseX;
        prevCursorY = mouseY;

        //in this example design, I move the cursor back to the middle after each click
        //robot.mouseMove(width/2, (height)/2); //on click, move cursor to roughly center of window!
    }

    //probably shouldn't have to edit this method
    public Rectangle getButtonLocation(int i) //for a given button ID, what is its location and size
    {
        int x = (i % 4) * (padding + buttonSize) + margin;
        int y = (i / 4) * (padding + buttonSize) + margin;

        return new Rectangle(x, y, buttonSize, buttonSize);
    }

    //you can edit this method to change how buttons appear
    public void drawButton(int i)
    {
        Rectangle bounds = getButtonLocation(i);

        if (trials.get(trialNum) == i) // see if current button is the target
            fill(255,0,0); // if so, fill red
        else
            fill(200); // if not, fill gray

        rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void mouseMoved()
    {
        //can do stuff everytime the mouse is moved (i.e., not clicked)
        //https://processing.org/reference/mouseMoved_.html
    }

    public void mouseDragged()
    {
        //can do stuff everytime the mouse is dragged
        //https://processing.org/reference/mouseDragged_.html
    }

}

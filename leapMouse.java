package leapWatch;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

class CustomListener extends Listener {

    public Robot robot;

    public void onConnect(Controller c) {
    	c.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES); // allows the program to run in places other than the terminal
        c.enableGesture(Gesture.Type.TYPE_CIRCLE);
        c.enableGesture(Gesture.Type.TYPE_SWIPE);
        c.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
    }

    public void onFrame(Controller c){
        try {robot = new Robot();} catch (Exception e) {}


        
        Frame frame = c.frame();
        InteractionBox box = frame.interactionBox();
        
        for (Hand h: frame.hands()) {
			int fCount = 0;
			String handType = h.isLeft() ? "left" : "right";
			// get the normal vector of the hand. Used to detect tilt
			Vector normal = h.palmNormal();
			Vector direction = h.direction();
			Vector palmPos = h.stabilizedPalmPosition();
			Vector palmVel = h.palmVelocity();
			Vector boxPalmPos = box.normalizePoint(palmPos);
			
			double grab = h.grabStrength();
			double roll = Math.toDegrees(normal.roll());
			double pitch = Math.toDegrees(direction.pitch());
			double hand_angle = Math.toDegrees(h.grabStrength());
			double yaw = Math.toDegrees(direction.yaw());
			Vector handSpeed = h.palmVelocity();
			if (h.isRight()) {
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				//System.out.print(screen.width * (boxPalmPos.getX() - 0.4));
				//System.out.print("\n");
                // move hand's center to the right for better controd
				// vanilla game pad ctrl
				robot.mouseMove((int)(screen.width * (boxPalmPos.getX() - 0.4)), (int) (screen.height - boxPalmPos.getY() * screen.height));
				
				// natural mouse control
				PointerInfo a = MouseInfo.getPointerInfo();
				Point b = a.getLocation();
				int x = (int) b.getX();
				int y = (int) b.getY();
				//robot.mouseMove((int)(x + palmVel.getX()), (int) (y - palmVel.getY()));
				
				
				//robot.mouseMove((int)(screen.width * (yaw  + 180)/360), (int) (screen.height - (pitch+180)lll * screen.height));
				
				if (grab >= 0.95){
					//System.out.print(grab);
					//System.out.print("\n");
					robot.mousePress(InputEvent.BUTTON1_MASK); //press the left mouse button
					robot.mouseRelease(InputEvent.BUTTON1_MASK);  //release the right mouse button
					
				}else if (roll > -60 && roll <-30) {
					System.out.print(roll);
					System.out.print("\n");
					robot.keyPress(KeyEvent.VK_L); //press the right mouse button
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
	                robot.keyRelease(KeyEvent.VK_L); //release the right mouse button
	                
				} 
			}
	
			else if (h.isLeft()) {
				
				if(grab >=0.95) {
					robot.keyPress(KeyEvent.VK_Q); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_Q); //release the right mouse button
					
				}
				
				
				if(roll > 30 + 90) {
					//System.out.print(roll);

					//System.out.print("\n");
					robot.keyPress(KeyEvent.VK_A); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_A); //release the right mouse button
				}
				if(roll < -30 + 90) {

					//System.out.print(roll);
					//System.out.print("\n");
					robot.keyPress(KeyEvent.VK_D); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_D); //release the right mouse button
				
				}
				
				if(pitch > 20) {
					//System.out.print(roll);
					System.out.print(pitch);
					System.out.print("\n");
					//System.out.print("\n");
					robot.keyPress(KeyEvent.VK_S); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_S); //release the right mouse button
				}
				if(pitch < -10) {

					System.out.print(pitch);
					System.out.print("\n");
					robot.keyPress(KeyEvent.VK_W); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_W); //release the right mouse button
				
				}
				/**
				if(palmPos.getY() > 10) {
					robot.keyPress(KeyEvent.VK_W); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_W); //release the right mouse button
				}
				if(palmPos.getY() < -10) {
					robot.keyPress(KeyEvent.VK_S); //press the right mouse button
	                robot.keyRelease(KeyEvent.VK_S); //release the right mouse button
				
				}
				**/
			}
	

        }
        /**
        for (Finger f: frame.fingers()){
            if (f.type() == Finger.Type.TYPE_INDEX) {
                // maybe better with tipVelo
                Vector fingerPos = f.stabilizedTipPosition();
                Vector boxFingerPos = box.normalizePoint(fingerPos);
                Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                robot.mouseMove((int)(screen.width * boxFingerPos.getX()), (int) (screen.height - boxFingerPos.getY() * screen.height));


            }
           


        }
         **/
        for (Gesture g: frame.gestures()) {
        	if (g.type() == Type.TYPE_CIRCLE) {
        		CircleGesture circle = new CircleGesture(g);
        		if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
        			System.out.print("R");
                    robot.keyPress(KeyEvent.VK_R); //press r
                    robot.keyRelease(KeyEvent.VK_R); //release r
        		}
        	}else if (g.type() == Type.TYPE_SWIPE && g.state() == State.STATE_START) {
        		robot.keyPress(KeyEvent.VK_SEMICOLON);
        		robot.keyRelease(KeyEvent.VK_SEMICOLON);
        	}
        }
        
        
    }
}

public class leapMouse {
    public static void main(String[] args) {
        CustomListener l = new CustomListener();
        Controller c = new Controller();
        c.addListener(l);
        try {
            System.in.read();

        } catch (Exception e){

        }

        c.removeListener(l);
    }
}


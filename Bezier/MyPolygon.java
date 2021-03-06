import java.awt.*;
import java.lang.*;
import java.util.*;
import java.awt.geom.Line2D;

public class MyPolygon extends Polygon {

    public static final long serialVersionUID = 24362462L;
    public Polygon Elevated;
    public boolean elevated = false;
    private double tempX[] = new double[4];
    private double tempY[] = new double[4];
    private int np;

    public MyPolygon() {
    }

    public MyPolygon(int x[],int y[],int n) {
    }

    /////////////////////////////////////////////////////////////

    //This is a convenience method for ensuring that tempX and tempY
    //grow in an extensible manner.  If newSize is greater than the
    //current capacity then tempX and tempY are reallocated to double the
    //until they reach the required capacity.  This reallocation includes
    //copying all data to the new location.
    private void resizeTemp(int newSize)
    {
        assert(tempX.length == tempY.length);
        while (newSize > tempX.length)
        {
            //Double the capacity of the array
            //Make more space
            double [] copy_x=new double[tempX.length*2];
            double [] copy_y=new double[tempX.length*2];
            //Copy data across
            System.arraycopy(tempX, 0, copy_x, 0, tempX.length);
            System.arraycopy(tempY, 0, copy_y, 0, tempX.length);
            //Swing the references back to the original object
            tempX=copy_x;
            tempY=copy_y;

            //System.out.println("Capacity increased to "+tempX.length);
        }
        assert(tempX.length == tempY.length);
    }

    public void elevateOnce() {
      //Input comes in the form of a Java Polygon (points are stored as integers)
      //Output is also in the form of a Java Polygon called Elevated
      //See http://download.oracle.com/javase/1.5.0/docs/api/java/awt/Polygon.html for Polygon details
      //As degree elevation progresses, the working data is kept in
      //double-precision floating point

      //Begin new elevated polygon
      Elevated = new Polygon();

      // First time elevateOnce is run
      if (!elevated){
        System.out.println("First degree elevation...");
        np=npoints;
        resizeTemp(np+1);
        //Make vector(s) of points which are of size npoints+1 (to allow room for elevation).
        for (int i=0; i<np; i++) {
          tempX[i] =  xpoints[i];
          tempY[i] =  ypoints[i];
        }
        elevated = true;
      }

      // All other runs
      resizeTemp(np+1);
      // Outer control points stay fixed...
      // ...temp[0] remains unmoved
      tempX[np] = tempX[np-1];
      tempY[np] = tempY[np-1];
      // Compute new inner control points in place
      for(int i = (np-1); i>0; i--){
        double n = (double) np; //To avoid integer division
        tempX[i] = (i/n)*tempX[i-1] + ((np-i)/n)*tempX[i];
        tempY[i] = (i/n)*tempY[i-1] + ((np-i)/n)*tempY[i];
      }
      //Copy all npoints+1 points back into the Elevated polygon
      Elevated.reset();
      for(int i=0;i<(np+1); i++) {
        // +0.5 for rounding the correct way, as (int) truncates
        Elevated.addPoint((int)(tempX[i]+0.5),
                          (int)(tempY[i]+0.5));
      }
      np++;
    }

    public void elevateLots() {
      for (int i=0; i<100; i++){
        elevateOnce();
      }
    }
}

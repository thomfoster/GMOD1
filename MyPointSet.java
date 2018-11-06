import java.awt.*;
import java.lang.*;
import java.util.*;
import java.awt.geom.Line2D;

public class MyPointSet extends Vector<MyPoint> {

    private int imin, imax, xmin, xmax;
    private boolean xySorted;
    private Vector<MyPoint>  theHull;
    public static final long serialVersionUID = 24362462L;
    public MyPointSet() {
	     xySorted = false;
    }

    public void addPoint(int x, int y) {
    	MyPoint p = new MyPoint(x,y);
    	addElement(p);
    	xySorted = false;
    }

    private int next(int i) {
	     return (i = (i+1) % size());
    }

    private int previous(int i) {
	     return (i = (i-1+size()) % size());
    }

    private int hullnext(int i) {
	     return (i = (i+1) % theHull.size());
    }

    private int hullprevious(int i) {
	     return (i = (i-1+theHull.size()) % theHull.size());
    }

    public void sortByXY() {
    	int i;
    	MyPoint p, q;
    	boolean no_swaps;

    	// Task 1
        // YOUR CODE GOES HERE
    	// use your favourite sorting algorithm
      // used bubble sort
      no_swaps = false;
      while (no_swaps == false){
        i = 1;
        no_swaps = true;
        while (i<size()) {
          p = elementAt(i-1);
          q = elementAt(i);
          if ((q.x < p.x) || ((p.x == q.x ) && (q.y < p.y))){
            //Perform swap
            setElementAt(q, i-1);
            setElementAt(p, i);
            no_swaps = false;
          }
          i += 1;
        }
      }

    	xySorted = true;

    	//Here's some code that is useful for debugging
        for (i=0; i<size(); i++) {
    	    p = elementAt(i);
    	    System.out.println(i+": "+p.x+" "+p.y);
    	}

    	return;
    }

    private void enumerateHull() {
    	System.out.println("");
    	System.out.print("Current chain is: ");
    	for (int index=0; index<theHull.size(); index++) {
    		MyPoint tmppoint;
    	    tmppoint = theHull.elementAt(index);
    	    System.out.print(" ("+tmppoint.x+", "+tmppoint.y+")");
    	}
    	System.out.println("");
    }

    private int removeChain(int bottom, int top) {
    	// removes the chain between bottom+1 and top-1 inclusive
    	// N.B. the size of the hull decreases by 1 at each step
    	// returns the index of the last valid element

    	int i, howmany;
    	MyPoint q;

    	System.out.println("  Removing chain between "+bottom+" and "+top+
    			   " in hull of size "+theHull.size());

    	if (bottom == top) return bottom; // nothing to remove

    	if (bottom < top) {
    	    howmany = top-bottom-1;
    	    System.out.println("   0 I want "+howmany+" elements");
    	    for (i=0; i<howmany; i++) {
    		q = theHull.elementAt(bottom+1);
    		System.out.println("   0 Removing element at "+bottom+1+": ("+q.x+", "+q.y+")");
    		theHull.removeElementAt(bottom+1);
    	    }
	    }

  	else { // top < bottom so wrap along chain end
  	    System.out.println(" \n  WRAPPING AROUND THE END \n");
  	    howmany = theHull.size()-bottom-1;
  	    System.out.println("   1 I want "+howmany+" elements between "+(bottom+1)+" and "+(theHull.size()-1)+" inclusive");
  	    for (i=0; i<howmany; i++) {
  		q = theHull.elementAt(bottom+1);
  		System.out.println("   1 Removing element at "+(bottom+1)+": ("+q.x+", "+q.y+")");
  		theHull.removeElementAt(bottom+1);
  	    }
  	    howmany = top;
  	    System.out.println("   plus "+howmany+" elements between "+0+" and "+(top-1)+" inclusive");
  	    for (i=0; i<howmany; i++) {
  		// could remove top-1 but then need to change top
  		q = theHull.elementAt(0);
  		System.out.println("   2 Removing element at "+0+": ("+q.x+", "+q.y+")");
  		theHull.removeElementAt(0);
  	    }

  	    if (bottom >= theHull.size()) bottom = theHull.size()-1;
  	}

  	return bottom; // index of last valid element
  }




  private void hullIncremental() {
    	int k,i,n, howmany;
    	MyPoint a, b, c, d, r;
    	MyPoint topelem, nextelem, botelem, prevelem;
    	int top, bottom;

    	n = size();

    	if (n<3) {
    	    System.out.println("\u0007Can't compute convex hull");
    	    return;
    	}

    	theHull   = new Vector<MyPoint>(n,n);

    	if (!xySorted) sortByXY();

    	// provisionally add the first three non-identical non-collinear points

      a = elementAt(0);
    	theHull.insertElementAt(a,0);
    	// prepare first three points

      // TASK 4 // If collinear skip to using points 3 and 4 and so on.



      // TASK 2 //
      //        // If left A-B-C no swap needed
      //        // If not left A-B-C swap A and B.
      //        // Due to sort on XY ABC now guaranteed to be both anticlockwise
      //        // and C visible from D, since C is the current rightmost point.

      k = 1;
      b = elementAt(k);
      while ((a.x == b.x) && (a.y == b.y)){
        k += 1;
        b = elementAt(k-1);
      } // B now guaranteed distinct from a

      k += 1;
      c = elementAt(k);
      while (c.collinear(a,b)){
        k += 1;
        //theHull.insertElementAt(b,theHull.size()); //Uncomment for collinear points to be on hull
        b = c;
        c = elementAt(k);
      }

      theHull.insertElementAt(b,theHull.size());
      theHull.insertElementAt(c,theHull.size());

      if (!c.left(a,b)){
        theHull.setElementAt(b,0);
        theHull.setElementAt(a,theHull.size()-2);
      }
      enumerateHull();

    	// initialise the ends of the chain-to-be-removed
    	// as the rightmost point in the hull
      // That is, top=bottom= "index of rightmost point in hull"
    	top=bottom=(theHull.size()-1);

      //Loop through the remaining points
      c = theHull.elementAt(theHull.size()-1); // Most recently added point

      for (i=k+1; i<n; i++){

        d = elementAt(i); // Point to consider

        System.out.println("Rightmost point: ("+c.x+","+c.y);
        System.out.println("Considering adding: ("+d.x+","+d.y);

        // Invariant: theHull.elementAt(top) always lit
        topelem = theHull.elementAt(top);
        nextelem = theHull.elementAt(hullnext(top)); // looks one ahead
        while (d.leftOn(nextelem,topelem)){ // If you want collinear points on hull this should be just "left"
          System.out.println("top: R is: ("+topelem.x+","+topelem.y+")");
          top = hullnext(top);
          topelem = theHull.elementAt(top);
          nextelem = theHull.elementAt(hullnext(top));
        }

        // Invariant: theHull.elementAt(bottom) always lit
        botelem = theHull.elementAt(bottom);
        prevelem = theHull.elementAt(hullprevious(bottom)); // looks one behind
        while (d.leftOn(botelem, prevelem)){
          System.out.println("bottom: R is: ("+prevelem.x+","+prevelem.y+")");
          bottom = previous(bottom);
          botelem = theHull.elementAt(bottom);
          prevelem = theHull.elementAt(hullprevious(bottom));
        }

        // remove lit chain

        //Add the next point
        if (top != bottom){
          System.out.println("decided to add");
          bottom = removeChain(bottom, top);
          theHull.insertElementAt(d,(bottom+1));
          c = d; // d becomes the most recently inserted element
          top = bottom + 1;
          bottom = bottom + 1;
        }
        else {
          System.out.println("decided not to add");
        }

      }
    }

    public Polygon hullDraw() {
    	int i;
    	MyPoint q;
    	Polygon p;

    	p = new Polygon();
    	System.out.println("The Hull has size "+theHull.size());
    	System.out.println("The Hull is:");
    	for (i=0; i<theHull.size(); i++) {
    	    q = theHull.elementAt(i);
    	    System.out.println("-> ("+q.x+", "+q.y+")");
    	    p.addPoint(q.x,q.y);
    	}
    	System.out.println("===================================");
    	return p;
    }

    public Polygon hullThePolygon() {
    	int i;
    	Polygon chp;

    	sortByXY();
    	hullIncremental();
    	chp = hullDraw();

    	return chp;
    }
}

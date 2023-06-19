import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> points;

    public PointSET() { // construct an empty set of points
        points = new TreeSet<>();
    }

    public boolean isEmpty() { // is the set empty?
        return points.isEmpty();
    }

    public int size() { // number of points in the set
        return points.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null) throw new IllegalArgumentException("insert point must not be null");

        points.add(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null) throw new IllegalArgumentException("search point must not be null");

        return points.contains(p);
    }

    public void draw() { // draw all points to standard draw
        for (Point2D p : points) {
            StdDraw.point(p.x(), p.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        if (rect == null) throw new IllegalArgumentException("search rectangle must not be null");

        ArrayList<Point2D> pointsInRect = new ArrayList<>();
        for (Point2D point : points) {
            if (
                    point.x() >= rect.xmin() &&
                    point.x() <= rect.xmax() &&
                    point.y() >= rect.ymin() &&
                    point.y() <= rect.ymax()
            ) pointsInRect.add(point);
        }

        return pointsInRect;
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) throw new IllegalArgumentException("search point must not be null");
        double minDist = -1.0;
        Point2D nearestPoint = null;

        for (Point2D point : points) {
            double dist = Math.abs(p.distanceTo(point));
            if (nearestPoint == null || dist < minDist) {
                minDist = dist;
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }


    public static void main(String[] args) { // unit testing of the methods (optional)
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        int counter = 1000;
        while (!in.isEmpty() && counter > 0) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
            counter--;
        }

        // process nearest neighbor queries
        StdDraw.enableDoubleBuffering();
        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw in red the nearest neighbor (using brute-force algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            brute.nearest(query).draw();
            StdDraw.setPenRadius(0.02);

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.show();
            StdDraw.pause(40);
        }
    }

}
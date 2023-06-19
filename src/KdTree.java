import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

public class KdTree {
//    private final TreeSet<Point2D> points;
    private KdNode rootNode;

    public KdTree() { // construct an empty set of points
//        points = new TreeSet<>();
    }

    public boolean isEmpty() { // is the set empty?
        return rootNode == null;
    }

    public int size() { // number of points in the set
//        return points.size();
        if (isEmpty()) return 0;
        return rootNode.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null) throw new IllegalArgumentException("insert point must not be null");

        if (contains(p)) return;
        if (isEmpty()) rootNode = new KdNode(p);
        else rootNode.insert(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null) throw new IllegalArgumentException("search point must not be null");

//        return points.contains(p);
        if (isEmpty()) return false;
        else return rootNode.contains(p);
    }

    public void draw() { // draw all points to standard draw
        for (KdNode node : rootNode.iterable()) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            StdDraw.point(node.point.x(), node.point.y());

            StdDraw.setPenRadius(0.001);
            if (node.isVertical()) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(node.point.x(), node.getMinBound(), node.point.x(), node.getMaxBound());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(node.getMinBound(), node.point.y(), node.getMaxBound(), node.point.y());
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        if (rect == null) throw new IllegalArgumentException("search rectangle must not be null");

        ArrayList<Point2D> pointsInRect = new ArrayList<>();
        if (isEmpty()) return pointsInRect;
//        for (KdNode node : rootNode.iterable()) {
//            if (
//                    node.point.x() >= rect.xmin() &&
//                    node.point.x() <= rect.xmax() &&
//                    node.point.y() >= rect.ymin() &&
//                    node.point.y() <= rect.ymax()
//            ) pointsInRect.add(node.point);
//        }
//
//        return pointsInRect;
        rootNode.rangeSearch(rect, pointsInRect);
        return pointsInRect;
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) throw new IllegalArgumentException("search point must not be null");
        if (isEmpty()) return null;

        KdNode nearestNode = rootNode.searchNearestNode(p, rootNode);
        return nearestNode.point;
    }


    public static void main(String[] args) { // unit testing of the methods (optional)
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdTree = new KdTree();
        int counter = 10000;
//        while (!in.isEmpty()) {
        while (!in.isEmpty() && counter > 0) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdTree.insert(p);
            counter--;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        kdTree.draw();

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
            kdTree.draw();

            // draw in red the nearest neighbor (using kdTree-force algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            kdTree.nearest(query).draw();
            StdDraw.setPenRadius(0.02);

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.show();
            StdDraw.pause(40);
        }
    }

    private class KdNode {
        private Point2D point;
        private KdNode parent;
        private boolean vertical;
        private KdNode leftChild;
        private KdNode rightChild;
        private int numberOfChild;

        public KdNode(Point2D p) {
            this(p, null);
        }
        public KdNode(Point2D p, KdNode parentNode) {
            point = p;
            parent = parentNode;
            if (parentNode == null) vertical = true;
            else vertical = !parentNode.vertical;

            leftChild = null;
            rightChild = null;
            numberOfChild = 0;
        }

        public void insert(Point2D p) {
            if (point.equals(p)) return;

            if (
                    (vertical && p.x() < point.x()) ||
                    (!vertical && p.y() < point.y())
            ) {
                if (leftChild == null) leftChild = new KdNode(p, this);
                else leftChild.insert(p);
            } else {
                if (rightChild == null) rightChild = new KdNode(p, this);
                else rightChild.insert(p);
            }
            numberOfChild++;
        }

        public boolean contains(Point2D p) {
            if (point.equals(p)) return true;

            if (
                    (vertical && p.x() < point.x()) ||
                    (!vertical && p.y() < point.y())
            ) {
                if (leftChild == null) return false;
                return leftChild.contains(p);
            } else {
                if (rightChild == null) return false;
                return rightChild.contains(p);
            }
        }

        public boolean isVertical() {
            return vertical;
        }

        public int size() {
            return numberOfChild + 1;
        }

        public double getMaxBound() {
            if (parent == null) return 1.0;
            if (this.equals(parent.leftChild)) {
                if (isVertical()) return parent.point.y();
                return parent.point.x();
            }

            if (parent.parent == null) return 1.0;
            return parent.parent.getMaxBound();
        }

        public double getMinBound() {
            if (parent == null) return 0.0;
            if (this.equals(parent.rightChild)) {
                if (isVertical()) return parent.point.y();
                return parent.point.x();
            }

            if (parent.parent == null) return 0.0;
            return parent.parent.getMinBound();
        }

        public Iterable<KdNode> iterable() {
            ArrayList<KdNode> nodes = new ArrayList<>();
            nodes.add(this);
            if (numberOfChild == 0) {
                return nodes;
            }
            if (leftChild != null) {
                for (KdNode node : leftChild.iterable()) {
                    nodes.add(node);
                }
            }
            if (rightChild != null) {
                for (KdNode node : rightChild.iterable()) {
                    nodes.add(node);
                }
            }
            return nodes;
        }

        public double distanceSquaredTo(Point2D p) {
            return point.distanceSquaredTo(p);
        }

        private KdNode searchPriorSide(Point2D p, KdNode nearestSoFar, KdNode firstNode, KdNode secondNode) {
            if (firstNode == null) return secondNode.searchNearestNode(p, nearestSoFar);

            nearestSoFar = firstNode.searchNearestNode(p, nearestSoFar);
            if (nearestSoFar.distanceSquaredTo(p) < Math.pow(nearestSoFar.perpendicularDist(p), 2))
                return nearestSoFar;

            if (secondNode == null) return nearestSoFar;
            return secondNode.searchNearestNode(p, nearestSoFar);
        }

        public KdNode searchNearestNode(Point2D p, KdNode nearestSoFar) {
            if (this.point.equals(p)) return this;

            double thisDist = distanceSquaredTo(p);
            double thisPos = point.x();
            double pPos = p.x();
            if (!isVertical()) {
                thisPos = point.y();
                pPos = p.y();
            }

            if (thisDist < nearestSoFar.distanceSquaredTo(p)) nearestSoFar = this;

            if (leftChild == null && rightChild == null) return nearestSoFar;

            KdNode firstNode = leftChild;
            KdNode secondNode = rightChild;
            if (pPos >= thisPos) {
                firstNode = rightChild;
                secondNode = leftChild;
            }
            return searchPriorSide(p, nearestSoFar, firstNode, secondNode);
        }

        private boolean inRange(RectHV rect) {
            if (point.x() < rect.xmin()) return false;
            if (point.x() > rect.xmax()) return false;
            if (point.y() < rect.ymin()) return false;
            if (point.y() > rect.ymax()) return false;
            return true;
        }

        public void rangeSearch(RectHV rect, ArrayList<Point2D> points) {
            double thisPos = point.x();
            double rectMax = rect.xmax();
            double rectMin = rect.xmin();
            if (!isVertical()) {
                thisPos = point.y();
                rectMax = rect.ymax();
                rectMin = rect.ymin();
            }

            if (thisPos > rectMax) {
                if (leftChild != null) leftChild.rangeSearch(rect, points);
                return;
            } else if (thisPos < rectMin) {
                if (rightChild != null) rightChild.rangeSearch(rect, points);
                return;
            }

            if (inRange(rect)) {
                points.add(point);
            }
            if (leftChild != null) leftChild.rangeSearch(rect, points);
            if (rightChild != null) rightChild.rangeSearch(rect, points);
        }

        private double perpendicularDist(Point2D p) {
            if (isVertical()) {
                return Math.abs(p.x() - point.x());
            }
            return Math.abs(p.y() - point.y());
        }
    }
}
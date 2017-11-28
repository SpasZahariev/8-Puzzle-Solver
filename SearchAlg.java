import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

//parent class for all search algorithms
public abstract class SearchAlg {

    private HashSet<Integer> obstacles;

    private JTextField passedTextField;
    private JTextField depthTextField;

    protected static long nodesPassed;
    protected static int solutionDepth;

    private Stack<Integer[]> solutionRoute;

    public SearchAlg(HashSet<Integer> obstacles) {
        this.obstacles = obstacles;
        solutionRoute = new Stack<>();
    }

    protected abstract class BasicNode {
        protected Block agent;
        protected BasicNode parent;
        protected ArrayList<Block> towerBlocks;
    }

    public Stack<Integer[]> getSolutionRoute() {
        return solutionRoute;
    }

    public HashSet<Integer> getObstacles() {
        return obstacles;
    }

    //records the route to the solution
    protected void traceRouteFrom(BasicNode node) {
        //number of blocks + agent is the same throughout the whole solution
        int numberOfMovables = node.towerBlocks.size() + 1;

        //the first iteration is not a movement of the agent so -1 to compensate
        //(it is just the start configuration before the agent has started moving)
        solutionDepth = -1;
        while (node != null) {
            Integer[] posOnBoard = new Integer[numberOfMovables];
            posOnBoard[0] = node.agent.getCurrPos();
            int k = 1;
            for (Block i : node.towerBlocks) {
                posOnBoard[k] = i.getCurrPos();
                k++;
            }
            solutionRoute.push(posOnBoard);
            solutionDepth++;
            node = node.parent;
        }
        System.err.println("NODES PASSED: " + nodesPassed);
        System.err.println("Solution Depth: " + solutionDepth);
        updateTextFields(String.valueOf(nodesPassed), String.valueOf(solutionDepth));
    }

    protected abstract void startSearch();

    //check for collision if there is an obstacle in the way
    protected boolean noObstacle(int futurePos) {
        return !obstacles.contains(futurePos);
    }

    //references to the GUI text fields, so we can write answers on them later
    protected void setTextFields(JTextField nodesPassedRef, JTextField solutionDepthRef) {
        passedTextField = nodesPassedRef;
        depthTextField = solutionDepthRef;
    }

    //writes the output to the text fields
    private void updateTextFields(String nodesPassed, String solutionDepth) {
        passedTextField.setText(nodesPassed);
        depthTextField.setText(solutionDepth);
    }
}

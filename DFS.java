import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DFS extends SearchAlg implements Searchable<DFS.Node> {

    private static Node root;
    private static Stack<Node> stack;
    private static int lengthAcross;
    private static int area;
    private static boolean isSolution;

    protected class Node extends BasicNode {

        private ArrayList<Node> children;

        public Node() {
            towerBlocks = new ArrayList<>(3);
            children = new ArrayList<>(4);
        }
    }

    public DFS(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        stack = new Stack<>();
        nodesPassed = 0;
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;
        isSolution = false;
    }

    //adds to the stack randomly until a solution is found
    public void startSearch() {
        stack.add(root);
        while (!stack.empty() && !isSolution)
            doDFS(stack.peek());
    }

    //true when all movable blocks are in their goal position
    public boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        return true;
    }

    //finds possible movements (of agent) in current position
    //chooses a random move to do
    private void doDFS(Node current) {
        nodesPassed++;

        if (checkSolution(current)) {
            isSolution = true;
            traceRouteFrom(current);
            return;
        }
        int agentPos = current.agent.getCurrPos();

        //checks for when node is next to borders or an obstacle
        //up
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
        }
        //left
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            makeSwitches(new Node(), current, agentPos - 1, agentPos);
        }
        //down
        if (agentPos < area - lengthAcross && noObstacle(agentPos + lengthAcross)) {
            makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
        }
        //right
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            makeSwitches(new Node(), current, agentPos + 1, agentPos);
        }
        //reached leaf in tree (should never happen with this algorithm) - tree is infinite
        //in a tree with leaf nodes, algorithm would backtrack one level and then expand other branches
        if (current.children.isEmpty()) {
            stack.pop();
            doDFS(stack.peek());
            return;
        }
        Collections.shuffle(current.children);
        Node next = current.children.get(0);
        //add one random child -> add one of its children and so on
        stack.add(next);
    }

    //sets up board state for child node, does swaps between Agent and Blocks too
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        child.agent = new Block(futureAgentPos);
        child.parent = parent;

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
        parent.children.add(child);
    }
}

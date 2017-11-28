import java.util.ArrayList;
import java.util.LinkedList;

public class BFS extends SearchAlg implements Searchable<BFS.Node> {

    private static Node root;
    private static LinkedList<Node> queue;
    private static int lengthAcross;
    private static int area;
    private static boolean isSolution;

    protected class Node extends BasicNode {

        //nodes for possible children
        private Node up;
        private Node right;
        private Node down;
        private Node left;

        public Node() {
            towerBlocks = new ArrayList<>();
        }
    }

    public BFS(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;
        nodesPassed = 0;
        queue = new LinkedList<>();
        isSolution = false;
    }

    //sees if all movable blocks have reached their goal positions
    //when finished, traces path from solution node to beginning
    public boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        isSolution = true;
        traceRouteFrom(current);
        return true;
    }

    //will expand queue tail
    //later expanded nodes are added to queue head
    //this way every node at level K is expanded before a node in level K+1
    public void startSearch() {
        queue.add(root);
        while (!isSolution)
            doBFS(queue.poll());
    }

    //add nodes from all possible adjacent positions to queue
    private void doBFS(Node current) {
        nodesPassed++;
        int agentPos = current.agent.getCurrPos();

        //is next to wall check or obstacle
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            current.left = new Node();
            makeSwitches(current.left, current, agentPos - 1, agentPos);
            if (checkSolution(current.left))
                return;
            queue.add(current.left);
        }
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            current.right = new Node();
            makeSwitches(current.right, current, agentPos + 1, agentPos);
            if (checkSolution(current.right))
                return;
            queue.add(current.right);
        }
        if (agentPos < (area - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
            current.down = new Node();
            makeSwitches(current.down, current, agentPos + lengthAcross, agentPos);
            if (checkSolution(current.down))
                return;
            queue.add(current.down);
        }
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            current.up = new Node();
            makeSwitches(current.up, current, agentPos - lengthAcross, agentPos);
            if (checkSolution(current.up))
                return;
            queue.add(current.up);
        }
    }

    //checks for the need to do swaps between blocks and agent and does them
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
    }
}

import java.util.ArrayList;
import java.util.LinkedList;

//every node remembers the direction of its parent => does not need to check it
//one less check done for each node compared to regular BFS
public class GraphBFS extends SearchAlg implements Searchable<GraphBFS.Node> {

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

        //optimise redundant moves
        private boolean parentRight;
        private boolean parentLeft;
        private boolean parentUp;
        private boolean parentDown;

        public Node() {
            towerBlocks = new ArrayList<>();
        }
    }

    public GraphBFS(PuzzleBoard newBoard) {
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
            doGraphBFS(queue.poll());
    }

    //add nodes from all possible adjacent positions to queue
    private void doGraphBFS(Node current) {
        nodesPassed++;
        int agentPos = current.agent.getCurrPos();

        //is next to wall check or obstacle
        //if expanding in this direction will get me in the parent node's position, skip it
        //up
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross) && !current.parentUp) {
            current.up = new Node();
            current.up.parentDown = true;
            makeSwitches(current.up, current, agentPos - lengthAcross, agentPos);
            if (checkSolution(current.up))
                return;
            queue.add(current.up);
        }
        //left
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1) && !current.parentLeft) {
            current.left = new Node();
            current.left.parentRight = true;
            makeSwitches(current.left, current, agentPos - 1, agentPos);
            if (checkSolution(current.left))
                return;
            queue.add(current.left);
        }
        //down
        if (agentPos < (area - lengthAcross) && noObstacle(agentPos + lengthAcross) && !current.parentDown) {
            current.down = new Node();
            current.down.parentUp = true;
            makeSwitches(current.down, current, agentPos + lengthAcross, agentPos);
            if (checkSolution(current.down))
                return;
            queue.add(current.down);
        }
        //right
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1) && !current.parentRight) {
            current.right = new Node();
            current.right.parentLeft = true;
            makeSwitches(current.right, current, agentPos + 1, agentPos);
            if (checkSolution(current.right))
                return;
            queue.add(current.right);
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

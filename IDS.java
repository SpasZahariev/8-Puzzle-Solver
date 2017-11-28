import java.util.ArrayList;
import java.util.Stack;

//class for iterative deepening search
public class IDS extends SearchAlg implements Searchable<IDS.Node> {

    private static Node root;
    private static Stack<Node> stack;
    private static int maxLevel;
    private static int lengthAcross;
    private static int boardArea;
    private static boolean isSolution;

    protected class Node extends BasicNode {

        private long level;
        private Stack<Node> children;
        private boolean givenChildren;

        public Node() {
            towerBlocks = new ArrayList<>();
            children = new Stack<>();
        }
    }

    public IDS(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        root.level = 0;
        stack = new Stack<>();
        nodesPassed = 0;
        lengthAcross = newBoard.getLengthAcross();
        boardArea = lengthAcross * lengthAcross;
        isSolution = false;
    }

    //when all movable blocks are in their goal position => solution
    public boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        return true;
    }

    //does DFS up to a max level. Max level increases.
    //this is repeated until a solution is found
    //it is like DFS on a tree where all nodes on "max level" are leaf nodes
    public void startSearch() {
        maxLevel = 1;
        stack.add(root);
        while (!isSolution) {
            doIDS(stack.peek());
            if (stack.empty()) {
                root.givenChildren = false;
                stack.add(root);
                maxLevel++;
            }
        }
    }

    //adds a node to the stack. This node has a stack of children.
    //expands one child following the same rule. (pop's it from Node's stack)
    //if a node does not have any children left, pop it from the stack
    private void doIDS(Node current) {
        nodesPassed++;

        //if current has been given children then it has gone through this check before
        if (!current.givenChildren && checkSolution(current)) {
            isSolution = true;
            traceRouteFrom(current);
            return;
        }

        int agentPos = current.agent.getCurrPos();

        //if a node is at the max level it can not be expanded; it is not given children
        if (current.level == maxLevel) {
            stack.pop();
            return;
        } else if (current.level < maxLevel && !current.givenChildren) {
            current.givenChildren = true;
            //checks for when node is next to borders
            //up
            if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
                makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
            }
            //left
            if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
                makeSwitches(new Node(), current, agentPos - 1, agentPos);
            }
            //down
            if (agentPos < (boardArea - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
                makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
            }
            //right
            if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
                makeSwitches(new Node(), current, agentPos + 1, agentPos);
            }
        }

        //all of this node's children have been looked at
        if (current.children.isEmpty()/* && current.givenChildren*/) {
            stack.pop();
            return;
        }
        Node next = current.children.pop();
        next.level = current.level + 1;
        stack.add(next);
    }

    //sets up board state for child node, does swaps between Agent and Blocks too
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        child.level = parent.level + 1;
        child.agent = new Block(futureAgentPos);

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
        parent.children.add(child);
        child.parent = parent;
    }
}


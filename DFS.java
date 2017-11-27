import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

public class DFS extends SearchAlg implements Searchable<DFS.Node>{

    private static Node root;
    private static Stack<Node> stack;
//    private static long nodesPassed;
    private static int lengthAcross;
    private static int area;

    private static boolean isSolution;

    protected class Node extends BasicNode{

//        private Block agent;
        //private Block blockA;
        //private Block blockB;
        //private Block blockC;

//        private ArrayList<Block> towerBlocks;
        private ArrayList<Node> children;

        /*private Node up;
        private Node right;
        private Node down;
        private Node left;*/

        /*//optimise redundant moves
        public boolean parentRight;
        public boolean parentLeft;
        public boolean parentUp;
        public boolean parentDown;*/

        public Node() {
            towerBlocks = new ArrayList<>(3);
            children = new ArrayList<>(4);
            //parentDown = parentUp = parentRight = parentLeft = false;
        }
    }

    /*public DFS(Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        stack = new Stack<>();
        nodesPassed = 0;
    }*/

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
//        super.obstacles = newBoard.getObstacles();
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
//    public void insertBlock(Block newBlock) {
//        root.towerBlocks.add(newBlock);
//    }

    public void startSearch() {
        stack.add(root);
        while (!stack.empty() && !isSolution)
        doDFS(stack.peek());
    }

    public boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        return true;
    }

    private void doDFS(Node current) {
        nodesPassed++;

        if (checkSolution(current)) {
//            System.out.println("\nyey DFS mofo\nnodes passed: " + nodesPassed);
            isSolution = true;
            //TODO cleanup code after trace route
            traceRouteFrom(current);
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println("Final Position is: " + i.getCurrPos()));
            return;
        }

        int agentPos = current.agent.getCurrPos();

        //checks for when node is next to borders
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            makeSwitches(new Node(), current, agentPos - 1, agentPos);
        }
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            makeSwitches(new Node(), current, agentPos + 1, agentPos);
        }
        if (agentPos < area - lengthAcross && noObstacle(agentPos + lengthAcross)) {
            makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
        }
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
        }

        //reached leaf in tree (should never happen)
        if(current.children.isEmpty()) {
            stack.pop();
            doDFS(stack.peek());
            return;
        }
        Collections.shuffle(current.children);
        Node next = current.children.get(0);
        //add one random child -> add one of its children -
        stack.add(next);
        //todo might not need next line
        //doDFS(next);
    }

    //todo choose random child and do dfs on it


    //sets up board state for child node, does swaps between Agent and Blocks too
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        child.agent = new Block(futureAgentPos);
        child.parent = parent;

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                //System.out.println("bug check: agent " + currentAgentPos + " moved to " + futureAgentPos);
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
        parent.children.add(child);
    }
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class BFS extends SearchAlg implements Searchable<BFS.Node>{

    private static Node root;
    private static LinkedList<Node> queue;

    private static int lengthAcross;
    private static int area;
//    private static HashSet<Integer> obstacles;
//    private static long nodesPassed;

    private static boolean isSolution = false;

    protected class Node extends BasicNode{

//        private Block agent;
        //private Block blockA;
        //private Block blockB;
        //private Block blockC;

//        private ArrayList<Block> towerBlocks;


        private Node up;
        private Node right;
        private Node down;
        private Node left;

//        private Node parent;
        //optimise redundant moves
        /*public boolean parentRight;
        public boolean parentLeft;
        public boolean parentUp;
        public boolean parentDown;*/

        public Node() {
            towerBlocks = new ArrayList<>(3);
            //parentDown = parentUp = parentRight = parentLeft = false;
        }
    }

    /*public BFS (Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        queue = new LinkedList<>();
        nodesPassed = 0;
    }*/

    public BFS (PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;
//        super.obstacles = newBoard.getObstacles();
        nodesPassed = 0;
        queue = new LinkedList<>();
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    /*public void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }*/

    public boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        //TODO cleanup the rest of this to have only traceroute
        System.out.println("BFS reached answer after checking: " + nodesPassed + " nodes");
        isSolution = true;
        //<3 lambda
        /*current.towerBlocks.forEach(i -> System.out.println("Block Position; " + i.getCurrPos()));
        System.out.println("   AGENT: " + current.agent.getCurrPos() + "\n\n");*/
//        Node path = current.parent;
        traceRouteFrom(current);
        /*while (current != null) {
            current.towerBlocks.forEach(i -> System.out.println("Block Position; " + i.getCurrPos()));
            System.out.println("   AGENT: " + current.agent.getCurrPos() + "\n\n");
            current = current.parent;
        }*/
        return true;
    }

    public void startSearch() {
        queue.add(root);
        while (!isSolution)
        doBFS(queue.poll());
    }

    //todo optimise nodes to not go to their parents places
    private void doBFS(Node current) {
        nodesPassed++;

        /*boolean reachedSolution = true;
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                reachedSolution = false;
                break;
            }
        }*/
        //TODO DELETE LATER
        /*if (checkSolution(current)) {
            System.out.println("yey mofo nodes Passed: " + nodesPassed);
            isSolution = true;
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println(i.getCurrPos()));
            return;
        }*/

        int agentPos = current.agent.getCurrPos();

        //is next to wall check
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            current.left = new Node();
            //current.left.parentRight = true;
            makeSwitches(current.left, current, agentPos - 1, agentPos);
            if(checkSolution(current.left))
                return;
            queue.add(current.left);
        }
        //is next to wall check
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            current.right = new Node();
            //current.right.parentLeft = true;
            makeSwitches(current.right, current, agentPos + 1, agentPos);
            if(checkSolution(current.right))
                return;
            queue.add(current.right);
        }
        //is next to wall check
        if (agentPos < (area - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
            current.down = new Node();
            //current.up.parentDown = true;
            makeSwitches(current.down, current, agentPos + lengthAcross, agentPos);
            if(checkSolution(current.down))
                return;
            queue.add(current.down);
        }
        //is next to wall check
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            current.up = new Node();
            //current.down.parentUp = true;
            makeSwitches(current.up, current, agentPos - lengthAcross, agentPos);
            if(checkSolution(current.up))
                return;
            queue.add(current.up);
        }

        //todo check if reached solution
    }

    //checks for the need to do swaps between blocks and agent and does them
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        //child = new Node();
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

    }
}

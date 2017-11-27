import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class IDS extends SearchAlg implements Searchable<IDS.Node>{

    //TODO should i try to make global vars as few as possible?
    private static Node root;
    private static Stack<Node> stack;
    private static int maxLevel;

    private static int lengthAcross;
    private static int boardArea;
//    private static HashSet<Integer> obstacles;
//    private static long nodesPassed;

    private static boolean isSolution;

    protected class Node extends BasicNode{

//        private Block agent;
        private long level;
//        private ArrayList<Block> towerBlocks;
        private Stack<Node> children;
        private boolean givenChildren;
//        private Node parent;
        

        public Node() {
            towerBlocks = new ArrayList<>();
            children = new Stack<>();
        }
    }

    /*public IDS(Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        stack = new Stack<>();
        root.level = 0;
        nodesPassed = 0;
    }*/

    public IDS(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        root.level = 0;


        stack = new Stack<>();
        nodesPassed = 0;
//        super.obstacles = newBoard.getObstacles();
        lengthAcross = newBoard.getLengthAcross();
        boardArea = lengthAcross * lengthAcross;
        isSolution = false;
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
        return true;
    }

    //todo iteration
    public void startSearch() {
        maxLevel = 1;
        stack.add(root);
        while (!isSolution) {
            doIDS(stack.peek());

            if (stack.empty()) {
                root.givenChildren = false;
                stack.add(root);
                maxLevel++;
                //System.out.println("Max Level: " + maxLevel);
            }
        }
    }

    //self explanatory
    private void doIDS(Node current) {
        nodesPassed++;
        //System.out.println(current.agent.getCurrPos());
        //method called on empty stack;
        //todo might not need this
//        if(current == null)
//            return;

        //if current has been given children than it has gone through this check before
        if (!current.givenChildren && checkSolution(current)) {
            System.out.println("IDS completed!" + "\nNodesPassed: " + nodesPassed);
            isSolution = true;
            //TODO cleanUp code after trace route
            traceRouteFrom(current);
            //<3 lambda
            /*while (current != null) {
                current.towerBlocks.forEach(i -> System.out.println("Final Position: " + i.getCurrPos()));
                System.out.println("   Agent: " + current.agent.getCurrPos() + "\n");
                current = current.parent;
            }*/
            return;
        }

        int agentPos = current.agent.getCurrPos();

        if (current.level == maxLevel) {
            stack.pop();
            return;
        } else if(current.level < maxLevel && !current.givenChildren) {
            current.givenChildren = true;
            //checks for when node is next to borders
            if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
                makeSwitches(new Node(), current, agentPos - 1, agentPos);
            }
            if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
                makeSwitches(new Node(), current, agentPos + 1, agentPos);
            }
            if (agentPos < (boardArea - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
                makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
            }
            if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
                makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
            }
        }

        //all of this node's children have been looked at
        if(current.children.isEmpty()/* && current.givenChildren*/) {
            stack.pop();
            //doIDS(stack.peek());
            return;
        }

        //Collections.shuffle(current.children);
        Node next = current.children.pop();
        next.level = current.level + 1;
        //add one random child -> add one of its children -
        stack.add(next);
    }

    //sets up board state for child node, does swaps between Agent and Blocks too
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        child.level = parent.level + 1;
        child.agent = new Block(futureAgentPos);

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                //System.out.println("bug check: agent " + currentAgentPos + " moved to " + futureAgentPos);
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
        parent.children.add(child);
        child.parent = parent;
    }
}


import java.util.ArrayList;
import java.util.LinkedList;

public class BFS {

    private static Node root;
    private static LinkedList<Node> queue;

    private static boolean isSolution = false;

    private class Node {

        private Block agent;
        //private Block blockA;
        //private Block blockB;
        //private Block blockC;

        private ArrayList<Block> towerBlocks;


        private Node up;
        private Node right;
        private Node down;
        private Node left;

        //optimise redundant moves
        public boolean parentRight;
        public boolean parentLeft;
        public boolean parentUp;
        public boolean parentDown;

        public Node() {
            towerBlocks = new ArrayList<>(3);
            parentDown = parentUp = parentRight = parentLeft = false;
        }
    }

    public BFS (Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        queue = new LinkedList<>();
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    public void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }

    public void doBFS() {
        queue.add(root);
        while (!isSolution)
        doBFS(queue.poll());
    }

    //todo optimise nodes to not go to their parents places
    private void doBFS(Node current) {

        boolean reachedSolution = true;
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                reachedSolution = false;
                break;
            }
        }
        if (reachedSolution) {
            System.out.println("yey mofo");
            isSolution = true;
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println(i.getCurrPos()));
            return;
        }

        int agentPos = current.agent.getCurrPos();

        //is next to wall check
        if (agentPos % 4 != 0 && !current.parentLeft) {
            current.left = new Node();
            current.left.parentRight = true;
            makeSwitches(current.left, current, agentPos - 1, agentPos);
            queue.add(current.left);
        }
        //is next to wall check
        if (agentPos % 4 != 3 && !current.parentRight) {
            current.right = new Node();
            current.right.parentLeft = true;
            makeSwitches(current.right, current, agentPos + 1, agentPos);
            queue.add(current.right);
        }
        //is next to wall check
        if (agentPos < 12 && !current.parentUp) {
            current.up = new Node();
            current.up.parentDown = true;
            makeSwitches(current.up, current, agentPos + 4, agentPos);
            queue.add(current.up);
        }
        //is next to wall check
        if (agentPos > 3 && !current.parentDown) {
            current.down = new Node();
            current.down.parentUp = true;
            makeSwitches(current.down, current, agentPos - 4, agentPos);
            queue.add(current.down);
        }

        //todo check if reached solution
    }

    //checks and does swaps of blocks and agent
    private void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        child.agent = new Block(futureAgentPos);

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                System.out.println("bug check: agent " + currentAgentPos + " moved to " + futureAgentPos);
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
    }
}

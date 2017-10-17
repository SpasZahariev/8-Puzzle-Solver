import java.util.ArrayList;
import java.util.PriorityQueue;

public class BFS {

    private static Node root;
    private static PriorityQueue<Node> queue;

    private static boolean isSolution = false;

    private class Node {

        private Block agent;
        //private Block blockA;
        //private Block blockB;
        //private Block blockC;

        private ArrayList<Block> towerBlocks = new ArrayList<>(3);
        //todo can change towerBlocks to arrayList for scalability

        private Node up;
        private Node right;
        private Node down;
        private Node left;
    }

    public BFS () {
        root = new Node();
        queue = new PriorityQueue<>();
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    private void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }

    private void doBFS() {
        queue.add(root);
        while (!isSolution)
        doBFS(queue.poll());
    }

    //todo optimise nodes to not go to their parents places
    private void doBFS(Node current) {
        int agentPos = current.agent.getCurrPos();

        //is next to wall check
        if (agentPos % 4 != 0) {
            current.left = new Node();
            makeSwitches(current.left, current, agentPos - 1, agentPos);
            queue.add(current.left);
        }
        //is next to wall check
        if (agentPos % 4 != 3) {
            current.right = new Node();
            makeSwitches(current.right, current, agentPos + 1, agentPos);
            queue.add(current.right);
        }
        //is next to wall check
        if (agentPos < 12) {
            current.up = new Node();
            makeSwitches(current.up, current, agentPos + 4, agentPos);
            queue.add(current.up);
        }
        //is next to wall check
        if (agentPos > 3) {
            current.down = new Node();
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
                System.out.println("bug check: " + futureAgentPos + " moved to " + currentAgentPos);
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
    }
}

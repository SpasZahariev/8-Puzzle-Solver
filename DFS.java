import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DFS {

    private static Node root;
    private static Stack<Node> stack;
    private static long nodesPassed;

    private static boolean isSolution;

    private class Node {

        private Block agent;
        //private Block blockA;
        //private Block blockB;
        //private Block blockC;

        private ArrayList<Block> towerBlocks;
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

    public DFS(Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        stack = new Stack<>();
        nodesPassed = 0;
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    public void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }

    public void doDFS() {
        stack.add(root);
        while (!stack.empty() && !isSolution)
        doDFS(stack.peek());
    }

    private boolean checkSolution(Node current) {
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
            System.out.println("\nyey DFS mofo\nnodes passed: " + nodesPassed);
            isSolution = true;
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println("Final Position is: " + i.getCurrPos()));
            return;
        }

        int agentPos = current.agent.getCurrPos();

        //checks for when node is next to borders
        if (agentPos % 4 != 0) {
            makeSwitches(current, agentPos - 1, agentPos);
        }
        if (agentPos % 4 != 3) {
            makeSwitches(current, agentPos + 1, agentPos);
        }
        if (agentPos < 12) {
            makeSwitches(current, agentPos + 4, agentPos);
        }
        if (agentPos > 3) {
            makeSwitches(current, agentPos - 4, agentPos);
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
    private void makeSwitches(Node parent, int futureAgentPos, int currentAgentPos) {
        Node child = new Node();
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
    }
}

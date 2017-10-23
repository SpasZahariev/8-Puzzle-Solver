import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DFS {

    private static Node root;
    private static Stack<Node> stack;

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

        if (checkSolution(current)) {
            System.out.println("yey DFS mofo");
            isSolution = true;
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println(i.getCurrPos()));
            return;
        }

        int agentPos = current.agent.getCurrPos();

        if (agentPos % 4 != 0) {
            Node temp = new Node();
            //current.left.parentRight = true;
            makeSwitches(temp, current, agentPos - 1, agentPos);
            current.children.add(temp);
        }
        //is next to wall check
        if (agentPos % 4 != 3) {
            Node temp = new Node();
            //current.right.parentLeft = true;
            makeSwitches(temp, current, agentPos + 1, agentPos);
            current.children.add(temp);
        }
        //is next to wall check
        if (agentPos < 12) {
            Node temp = new Node();
            //current.up.parentDown = true;
            makeSwitches(temp, current, agentPos + 4, agentPos);
            current.children.add(temp);
        }
        //is next to wall check
        if (agentPos > 3) {
            Node temp = new Node();
            //current.down.parentUp = true;
            makeSwitches(temp, current, agentPos - 4, agentPos);
            current.children.add(temp);
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

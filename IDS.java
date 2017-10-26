import java.util.ArrayList;
import java.util.Stack;

public class IDS {

    //TODO should i try to make global vars as few as possible?
    private static Node root;
    private static Stack<Node> stack;
    private static int maxLevel;
    private static long nodesPassed;

    private static boolean isSolution;

    private class Node {

        private Block agent;
        private long level;
        private ArrayList<Block> towerBlocks;
        private Stack<Node> children;
        private boolean givenChildren;
        

        public Node() {
            towerBlocks = new ArrayList<>(3);
            children = new Stack<>();
        }
    }

    public IDS(Block rootAgent) {
        root = new Node();
        root.agent = rootAgent;
        stack = new Stack<>();
        root.level = 0;
        nodesPassed = 0;
    }

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    public void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }


    private boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        return true;
    }

    //todo iteration
    public void doIDS() {
        maxLevel = 1;
        stack.add(root);
        while (!isSolution) {
            doIDS(stack.peek());

            if (stack.empty()) {
                root.givenChildren = false;
                stack.add(root);
                maxLevel++;
                System.out.println("Max Level: " + maxLevel);
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
            //<3 lambda
            current.towerBlocks.forEach(i -> System.out.println("Final Position: " + i.getCurrPos()));
            return;
        }

        int agentPos = current.agent.getCurrPos();

        if (current.level == maxLevel) {
            stack.pop();
            return;
        } else if(current.level < maxLevel && !current.givenChildren) {
            current.givenChildren = true;
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
    private void makeSwitches(Node parent, int futureAgentPos, int currentAgentPos) {
        Node child = new Node();
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
    }
}


public class Block {

    private int currPos;
    private int goalPos;

    //this constructor is called for making obstacles and agents
    //agent does not need a goal state
    public Block(int startPos) {
        currPos = startPos;
    }

    //constructor for making a movable block
    public Block(int currPos, int goalPos) {
        this.currPos = currPos;
        this.goalPos = goalPos;
    }

    public int getCurrPos() {
        return currPos;
    }

    public int getGoalPos() {
        return goalPos;
    }
}

public class Block {

    private int currPos;
    private int goalPos;

    //agent does not need a goal state
    public Block(int startPos) {
        currPos = startPos;
    }

    public Block(int currPos, int goalPos) {
        this.currPos = currPos;
        this.goalPos = goalPos;
    }

    public int getCurrPos() {
        return currPos;
    }

    public void setCurrPos(int currPos) {
        this.currPos = currPos;
    }

    public int getGoalPos() {
        return goalPos;
    }

    //todo may be useless
    public void setGoalPos(int goalPos) {
        this.goalPos = goalPos;
    }
}

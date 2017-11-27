public interface Searchable<T> {

    boolean checkSolution (T current);
    void startSearch();
    void makeSwitches(T child, T parent, int futureAgentPos, int currentAgentPos);
}

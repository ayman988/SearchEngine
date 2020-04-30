package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;

public class SearchResult implements ISearchResult {
    private String id ;
    private int rank ;
    public SearchResult(String id,int rank) {
        if (id == null||rank<0) throw new RuntimeErrorException(new Error());
        this.id = id ;
        this.rank = rank;
    }

    public SearchResult() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        if (id == null) throw new RuntimeErrorException(new Error());
        this.id = id ;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) {
        if (rank<0) throw new RuntimeErrorException(new Error());
        this.rank = rank;
    }
}

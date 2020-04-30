package eg.edu.alexu.csd.filestructure.btree;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

	private int numOfKeys;
	private int numOfChilds;
	private Boolean isLeaf;
	private List<K> keys = new ArrayList<K>();
	private List<V> values = new ArrayList<V>();;
	private List<IBTreeNode<K, V>> children = new ArrayList<IBTreeNode<K,V>>();
    private IBTreeNode<K, V> parent;
	
	public BTreeNode(int numOfChilds) {
		// TODO Auto-generated constructor stub
		this.numOfChilds = numOfChilds;
		this.numOfKeys = numOfChilds-1;
		this.isLeaf = true;
	}
	@Override
	public int getNumOfKeys() {
		// TODO Auto-generated method stub
		return this.keys.size();
	}

	@Override
	public void setNumOfKeys(int numOfKeys) {
		// TODO Auto-generated method stub
		this.numOfKeys = numOfKeys;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return this.isLeaf;
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		// TODO Auto-generated method stub
		this.isLeaf = isLeaf;
	}

	@Override
	public List<K> getKeys() {
		// TODO Auto-generated method stub
		return this.keys;
	}

	@Override
	public void setKeys(List<K> keys) {
		// TODO Auto-generated method stub
		this.keys = keys;
	}

	@Override
	public List<V> getValues() {
		// TODO Auto-generated method stub
		return this.values;
	}

	@Override
	public void setValues(List<V> values) {
		// TODO Auto-generated method stub
		this.values = values;
	}

	@Override
	public List<IBTreeNode<K, V>> getChildren() {
		// TODO Auto-generated method stub
		return this.children;
	}

	@Override
	public void setChildren(List<IBTreeNode<K, V>> children) {
		// TODO Auto-generated method stub
		this.children = children;
		for(IBTreeNode<K, V> child : children) {
			((BTreeNode<K, V>)child).setparent(this);
		}
	}
    public void setparent(IBTreeNode<K, V> parent) {
    	this.parent=parent;
    }
    public IBTreeNode<K, V> getparent(){
    	return this.parent;
    }
	

}

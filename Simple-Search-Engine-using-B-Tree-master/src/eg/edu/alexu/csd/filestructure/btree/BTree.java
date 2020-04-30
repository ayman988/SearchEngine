package eg.edu.alexu.csd.filestructure.btree;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.management.RuntimeErrorException;


public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
	
	private int mindegree;
	private int maxdegree;
	private int maxKeys;
	private int minKeys;
	private IBTreeNode<K, V> root = null;
	private IBTreeNode<K, V> NodeToInsertIn = null;
	private IBTreeNode<K, V> NodeToSearchIn = null;
	private Vector<IBTreeNode<K, V>> parents = new Vector<>();
	private Boolean keyFound = false; //This value is used when we insert an existed key, so if we found it we set it to true in order to not to insert it in the insert function
									 //it's important to reset this value.
	private Vector<Integer> NodeAsAchildIndx = new Vector<Integer>();
	int c =0;
	public BTree(int mindegree) {

		if(mindegree < 2) {
			Error e = null;
			throw new RuntimeErrorException(e);
		}
		// TODO Auto-generated constructor stub
		this.mindegree = mindegree;
		this.maxdegree = 2*mindegree;
		this.maxKeys = maxdegree-1;
		this.minKeys = mindegree-1;
	}
	
	@Override
	public int getMinimumDegree() {
		// TODO Auto-generated method stub
		return this.mindegree;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		// TODO Auto-generated method stub
		if(this.root == null) {
			Error e = new Error();
			throw new RuntimeErrorException(e);
		}
		return this.root;
	}

	@Override
	public void insert(K key, V value) {
		// TODO Auto-generated method stub
		if(key == null || value ==null) {
			Error e = new Error();
			throw new RuntimeErrorException(e);
		}
		if(this.root == null) {
			root = new BTreeNode<K,V>(mindegree);
		}
		keyFound = false;
		searchForInsert(this.root, key);
		if(keyFound == true) {
			keyFound = false;
			return;
		}
		NodeToInsertIn.getKeys().add(key);
		NodeToInsertIn.getValues().add(value);
		
		//Inserting at Leaf.
		for(int i = NodeToInsertIn.getKeys().size()-1 ; i > 0 ; i--) {
			
			if( NodeToInsertIn.getKeys().get(i).compareTo(NodeToInsertIn.getKeys().get(i-1)) < 0 ) {
				
				K temp =  NodeToInsertIn.getKeys().get(i);
				NodeToInsertIn.getKeys().set(i,  NodeToInsertIn.getKeys().get(i-1));
				NodeToInsertIn.getKeys().set(i-1, temp);
				
				V temp2 =  NodeToInsertIn.getValues().get(i);
				NodeToInsertIn.getValues().set(i,  NodeToInsertIn.getValues().get(i-1));
				NodeToInsertIn.getValues().set(i-1, temp2);
				
			}
			else {
				break;
			}
			
		}
		
		int size = NodeToInsertIn.getKeys().size();
		if( size  > maxKeys) {
			int median = ( size) / 2 ;
			K medianKey = NodeToInsertIn.getKeys().get(median);
			V medianValue = NodeToInsertIn.getValues().get(median);			
			List<K> leftKeys = new ArrayList<K>();
			List<V> leftValues = new ArrayList<V>();
			List<K> rightKeys = new ArrayList<K>();
			List<V> rightValues = new ArrayList<V>();
			
			for(int k = 0 ; k < median ; k++) {
				leftKeys.add(NodeToInsertIn.getKeys().get(k));
				leftValues.add(NodeToInsertIn.getValues().get(k));
			}
			for(int k = median + 1 ; k < size ; k++) {
				rightKeys.add(NodeToInsertIn.getKeys().get(k));
				rightValues.add(NodeToInsertIn.getValues().get(k));
			}
			IBTreeNode<K, V> leftNode = new BTreeNode<>(mindegree);
			IBTreeNode<K, V> rightNode = new BTreeNode<>(mindegree);
			leftNode.setLeaf(true);
			rightNode.setLeaf(true);
			leftNode.setKeys(leftKeys);
			leftNode.setValues(leftValues);
			rightNode.setKeys(rightKeys);
			rightNode.setValues(rightValues);
			insert_fixup( medianKey, medianValue, leftNode, rightNode	);
		}
		parents.clear();
	}
	
	
	private void insert_fixup(K key , V value , IBTreeNode<K, V> left , IBTreeNode<K, V> right ) {
		
		IBTreeNode<K, V> currentParent = null;
		Boolean flag = true;
		//check if parent is null , its root
		//if(parents.size() == 0) {
		if(((BTreeNode<K, V>)NodeToInsertIn).getparent()==null) {
			currentParent = new BTreeNode<K,V>(this.mindegree);
			this.root = currentParent;
			currentParent.setLeaf(false);
			flag = false;
		} 
		else {
			//currentParent = parents.get(parents.size()-1);
			//parents.remove(parents.size()-1);
			currentParent=((BTreeNode<K, V>)NodeToInsertIn).getparent();
		}
		currentParent.getKeys().add(key);
		currentParent.getValues().add(value);
		int i;
		for( i = currentParent.getKeys().size()-1 ; i > 0 ; i--) {
			
			if( currentParent.getKeys().get(i).compareTo(currentParent.getKeys().get(i-1)) < 0 ) {
				K temp =  currentParent.getKeys().get(i);
				currentParent.getKeys().set(i,  currentParent.getKeys().get(i-1));
				currentParent.getKeys().set(i-1, temp);
				V temp2 =  currentParent.getValues().get(i);
				currentParent.getValues().set(i,  currentParent.getValues().get(i-1));
				currentParent.getValues().set(i-1, temp2);
			}
			else {
				break;
			}
			
		}
		if(flag) {
			currentParent.getChildren().set(i, left);
			currentParent.getChildren().add(right);
			((BTreeNode<K, V>)left).setparent(currentParent);
			((BTreeNode<K, V>)right).setparent(currentParent);
			for(int j = currentParent.getChildren().size()-1 ; j>=i+2 ; j--) {
				IBTreeNode<K, V> temp = currentParent.getChildren().get(j-1);
				currentParent.getChildren().set(j-1, currentParent.getChildren().get(j));
				currentParent.getChildren().set(j, temp);
			}
		}
		else {
			currentParent.getChildren().add(left);
			currentParent.getChildren().add(right);
			((BTreeNode<K, V>)left).setparent(currentParent);
			((BTreeNode<K, V>)right).setparent(currentParent);
		}

		//check if it overloaded
		int size = currentParent.getKeys().size();
		//Don't forget to insert children at splitting!.
		if( size  > maxKeys) {
			int median = ( size ) / 2 ;		
			K medianKey = currentParent.getKeys().get(median);
			V medianValue = currentParent.getValues().get(median);		
			List<K> leftKeys = new ArrayList<K>();
			List<V> leftValues = new ArrayList<V>();
			List<K> rightKeys = new ArrayList<K>();
			List<V> rightValues = new ArrayList<V>();
			List<IBTreeNode<K, V>> leftChildren = new ArrayList<IBTreeNode<K,V>>();
			List<IBTreeNode<K, V>> rightChildren = new ArrayList<IBTreeNode<K,V>>();
			for(int k = 0 ; k < median ; k++) {
				leftKeys.add(currentParent.getKeys().get(k));
				leftValues.add(currentParent.getValues().get(k));
			}
			for(int k = median + 1 ; k < size ; k++) {
				rightKeys.add(currentParent.getKeys().get(k));
				rightValues.add(currentParent.getValues().get(k));
			}
			for(int k = 0 ; k <= median ; k++) {
				leftChildren.add(currentParent.getChildren().get(k) );
			}
			for(int k = median+1 ; k < currentParent.getChildren().size() ; k++) {
				rightChildren.add(currentParent.getChildren().get(k) );
			}
			IBTreeNode<K, V> leftNode = new BTreeNode<>(mindegree);
			IBTreeNode<K, V> rightNode = new BTreeNode<>(mindegree);
			leftNode.setLeaf(false);
			rightNode.setLeaf(false);
			leftNode.setKeys(leftKeys);
			leftNode.setValues(leftValues);
			leftNode.setChildren(leftChildren);
			rightNode.setKeys(rightKeys);
			rightNode.setValues(rightValues);
			rightNode.setChildren(rightChildren);
			NodeToInsertIn=((BTreeNode<K, V>)NodeToInsertIn).getparent();
			insert_fixup(medianKey, medianValue, leftNode, rightNode);
		}
		return;
	}
	
	private void searchForInsert(IBTreeNode<K, V> node , K key )  {
		// && node == this.root && node!= null
		if( (node.getKeys() == null || node.getKeys().size() == 0)) {	//Insert at root.
			NodeToInsertIn = this.root;		
			return;
		}
		for(int i = 0 ; i < node.getKeys().size() ; i++) {
			//Continue searching into left.
			if(node.getKeys().get(i).compareTo(key) == 0) {
				NodeToSearchIn = node;
				keyFound = true;
				return;
			}
			if(node.isLeaf() == false  && node.getKeys().get(i).compareTo(key) > 0) {
				
				//currentParent = node;
				parents.add(node);
				NodeAsAchildIndx.add(i);
//				if(node.getChildren().get(i) != null) {
					searchForInsert(node.getChildren().get(i), key);
//				}			
				return;
				
			}//Continue searching into right.
			else if ( node.isLeaf() == false && i == node.getKeys().size()-1 && node.getKeys().get(i).compareTo(key) < 0 ) {
				
				//currentParent = node;
				parents.add(node);
				NodeAsAchildIndx.add(i+1);
//				if(  i+1< node.getChildren().size() && node.getChildren().get(i+1)!= null ) {
					searchForInsert(node.getChildren().get(i+1), key);
//				}		
				return;
				
			}//Key will be inserted in this node, Insert instead of this key and fix-up if necessary.
			else if (node.isLeaf() == true && node.getKeys().get(i).compareTo(key) > 0 ) {
				
				NodeToInsertIn = node;
				return;
				
			}//Insert at the end cause all the elements are smaller than it and it's the end.
			else if (node.isLeaf() == true && i == node.getKeys().size()-1 && node.getKeys().get(i).compareTo(key) < 0 ) {
				
				NodeToInsertIn = node;
				return;
				
			}
		}
	}
	
	@Override
	public V search(K key) {
		// TODO Auto-generated method stub
		if(key == null) {
			Error e = null;
			throw new RuntimeErrorException(e);
		}
		if(root==null) {return null;}
		NodeToSearchIn = null;
		searchForInsert(this.root, key);
		if(NodeToSearchIn == null) {
			return null;
		}
		for(int i = 0 ; i < NodeToSearchIn.getKeys().size() ; i++) {
			if(NodeToSearchIn.getKeys().get(i).compareTo(key) == 0) {
				parents.clear();
				return NodeToSearchIn.getValues().get(i);
			}
		}
		parents.clear();
		return null;
	}
	
	
	private void fix_delete(IBTreeNode<K, V> node) {
		
			if(node == this.root) {
				return ;
			}
			IBTreeNode<K, V> currentParent = null;
		/*	if(parents.size()-1 >=0 ) {
				currentParent = parents.get( parents.size()-1 );
			}*/
			currentParent=((BTreeNode<K,V>)node).getparent();
			int indx = 0;
			/*if(NodeAsAchildIndx.size()-1 >=0) {
				indx = NodeAsAchildIndx.get( NodeAsAchildIndx.size()-1 );
			}*/
			if(currentParent!=null) {
				indx=currentParent.getChildren().indexOf(node);
			}
				if( indx-1 >= 0 && currentParent.getChildren().get(indx-1).getKeys().size() > minKeys ) {
					
					int indexLast = currentParent.getChildren().get(indx-1).getKeys().size()-1;
					
					K lastKey =  currentParent.getChildren().get(indx-1).getKeys().get(indexLast);
					V lastValue =  currentParent.getChildren().get(indx-1).getValues().get(indexLast);
					IBTreeNode<K, V> child = null;
					
					if(currentParent.getChildren().get(indx-1).isLeaf()==false && !(currentParent.getChildren().get(indx-1).getChildren() == null || currentParent.getChildren().get(indx-1).getChildren().size()==0)) {
						 child = currentParent.getChildren().get(indx-1).getChildren().get(indexLast+1);
							currentParent.getChildren().get(indx-1).getChildren().remove(indexLast+1);
					}

					
					currentParent.getChildren().get(indx-1).getKeys().remove(indexLast);
					currentParent.getChildren().get(indx-1).getValues().remove(indexLast);
					
					K parentKey = currentParent.getKeys().get(indx-1);
					V parentValue = currentParent.getValues().get(indx-1);
					
					currentParent.getKeys().set(indx-1, lastKey);
					currentParent.getValues().set(indx-1, lastValue);
					
					//insert parent.
					node.getKeys().add(parentKey);
					node.getValues().add(parentValue);
					
					if(currentParent.getChildren().get(indx-1).isLeaf()==false && !(currentParent.getChildren().get(indx-1).getChildren() == null || currentParent.getChildren().get(indx-1).getChildren().size()==0)) {
						//node.getChildren().add(child);
						 List<IBTreeNode<K, V>> habd=new ArrayList<IBTreeNode<K, V>>();
						 habd.add(child);
						 for(IBTreeNode<K, V> C: node.getChildren()) {
							 habd.add(C);
						 }
						((BTreeNode<K, V>)child).setparent(node);
						node.setChildren(habd);
						/*IBTreeNode<K, V> tmp =node.getChildren().get(0);
						node.getChildren().set(0, node.getChildren().get( node.getChildren().size()-1));
						node.getChildren().set(node.getChildren().size()-1, tmp);*/
					}
					//adjust its place.
					for(int i = node.getKeys().size()-1 ; i > 0 ; i--) {
						if( node.getKeys().get(i).compareTo(node.getKeys().get(i-1)) < 0 ) {
							K temp =  node.getKeys().get(i);
							node.getKeys().set(i,  node.getKeys().get(i-1));
							node.getKeys().set(i-1, temp);
							V temp2 =  node.getValues().get(i);
							node.getValues().set(i, node.getValues().get(i-1));
							node.getValues().set(i-1, temp2);
						}
						else {
							break;
						}
						
					}
					return;
				}
				//Try to borrow from right.		
				else if(  currentParent!=null && indx+1 <  currentParent.getChildren().size() && currentParent.getChildren().get(indx+1).getKeys().size() > minKeys ) {

						K FirstKey =  currentParent.getChildren().get(indx+1).getKeys().get(0);
						V FirstValue =  currentParent.getChildren().get(indx+1).getValues().get(0);
						IBTreeNode<K, V> child = null;
						if(currentParent.getChildren().get(indx+1).isLeaf()==false &&   !(currentParent.getChildren().get(indx+1).getChildren() == null || currentParent.getChildren().get(indx+1).getChildren().size()==0)) {
							 child = currentParent.getChildren().get(indx+1).getChildren().get(0);
							 currentParent.getChildren().get(indx+1).getChildren().remove(0);
						}
						currentParent.getChildren().get(indx+1).getKeys().remove(0);
						currentParent.getChildren().get(indx+1).getValues().remove(0);
						K parentKey = currentParent.getKeys().get(indx);
						V parentValue = currentParent.getValues().get(indx);
						currentParent.getKeys().set(indx, FirstKey);
						currentParent.getValues().set(indx, FirstValue);
						//insert parent.
						node.getKeys().add(parentKey);
						node.getValues().add(parentValue);
						//adjust its place.
						for(int i = node.getKeys().size()-1 ; i > 0 ; i--) {
							if( node.getKeys().get(i).compareTo(node.getKeys().get(i-1)) < 0 ) {
								K temp =  node.getKeys().get(i);
								node.getKeys().set(i,  node.getKeys().get(i-1));
								node.getKeys().set(i-1, temp);
								V temp2 = node.getValues().get(i);
								node.getValues().set(i, node.getValues().get(i-1));
								node.getValues().set(i-1, temp2);
							}
							else {
								break;
							}
						}
						if(currentParent.getChildren().get(indx+1).isLeaf()==false && !(currentParent.getChildren().get(indx+1).getChildren() == null || currentParent.getChildren().get(indx+1).getChildren().size()==0)) {
							node.getChildren().add(child);
							((BTreeNode<K, V>)child).setparent(node);
						}
						return;
				}
				//Merge. The default is merging with left if it exists. else ..> Merge with right.
				else {
					//Merge with left.
					if(indx-1 >= 0) {
						IBTreeNode<K, V> mergedNode = new BTreeNode<K, V>(this.mindegree);
						List<K> mergedKeys = new ArrayList<K>();
						List<V> mergedValues = new ArrayList<V>();
						List<IBTreeNode<K, V>> mergedChilds = new ArrayList<>();					
						//Merge two nodes with the exception of the desired one to be deleted and add the parent
						for(int i = 0 ; i < currentParent.getChildren().get(indx-1).getKeys().size();i++) {
							
						
								mergedKeys.add(currentParent.getChildren().get(indx-1).getKeys().get(i));
								mergedValues.add(currentParent.getChildren().get(indx-1).getValues().get(i));
						}
						if( !(currentParent.getChildren().get(indx-1).getChildren().size() ==0 || currentParent.getChildren().get(indx-1).getChildren() == null ) ) {
							for(int i = 0 ; i < currentParent.getChildren().get(indx-1).getChildren().size();i++) {
							
								mergedChilds.add(currentParent.getChildren().get(indx-1).getChildren().get(i));

							}
						}
						//add what is at the parent then delete it.
						mergedKeys.add(currentParent.getKeys().get(indx-1));
						mergedValues.add(currentParent.getValues().get(indx-1));
						currentParent.getKeys().remove(indx-1);
						currentParent.getValues().remove(indx-1);
						for(int i = 0 ; i < currentParent.getChildren().get(indx).getKeys().size();i++) {
							mergedKeys.add(currentParent.getChildren().get(indx).getKeys().get(i));
							mergedValues.add(currentParent.getChildren().get(indx).getValues().get(i));					
						}
						if( !(currentParent.getChildren().get(indx).getChildren().size() ==0  || currentParent.getChildren().get(indx).getChildren() == null) ) {
							for(int i = 0 ; i < currentParent.getChildren().get(indx).getChildren().size();i++) {
								mergedChilds.add(currentParent.getChildren().get(indx).getChildren().get(i));							
							}
						}
						currentParent.getChildren().remove(indx-1);
						mergedNode.setKeys(mergedKeys);
						mergedNode.setValues(mergedValues);
						mergedNode.setChildren(mergedChilds);
						if(node.isLeaf() == false) {
							mergedNode.setLeaf(false);
						}
						currentParent.getChildren().set(indx-1, mergedNode );
						((BTreeNode<K, V>)mergedNode).setparent(currentParent);
						if(currentParent.getKeys().size() == 0 && currentParent == this.root) {
							this.root = mergedNode;
							return;
						}
						if(currentParent.getKeys().size() < minKeys) {
							parents.remove(parents.size()-1);
							NodeAsAchildIndx.remove(NodeAsAchildIndx.size()-1);
							fix_delete(currentParent);
						}	
					}
					//Merge with right.
					else {
						
						if(indx+1 < currentParent.getChildren().size()) {
							IBTreeNode<K, V> mergedNode = new BTreeNode<K, V>(this.mindegree);
							List<K> mergedKeys = new ArrayList<K>();
							List<V> mergedValues = new ArrayList<V>();
							List<IBTreeNode<K, V>> mergedChilds = new ArrayList<>();
							//Merge two nodes with the exception of the desired one to be deleted and add the parent
							for(int i = 0 ; i < currentParent.getChildren().get(indx).getKeys().size();i++) {
									mergedKeys.add(currentParent.getChildren().get(indx).getKeys().get(i));
									mergedValues.add(currentParent.getChildren().get(indx).getValues().get(i));
							}
							if( !(currentParent.getChildren().get(indx).getChildren().size() ==0 || currentParent.getChildren().get(indx).getChildren() == null ) ) {
								for(int i = 0 ; i < currentParent.getChildren().get(indx).getChildren().size();i++) {
									
								
									mergedChilds.add(currentParent.getChildren().get(indx).getChildren().get(i));  
								}
							}				
							//add what is at the parent then delete it.
							mergedKeys.add(currentParent.getKeys().get(indx));
							mergedValues.add(currentParent.getValues().get(indx));
							currentParent.getKeys().remove(indx);
							currentParent.getValues().remove(indx);
							currentParent.getChildren().remove(indx);
							for(int i = 0 ; i < currentParent.getChildren().get(indx).getKeys().size();i++) {
								mergedKeys.add(currentParent.getChildren().get(indx).getKeys().get(i));
								mergedValues.add(currentParent.getChildren().get(indx).getValues().get(i));
							}
							if( !(currentParent.getChildren().get(indx).getChildren().size() ==0 || currentParent.getChildren().get(indx).getChildren() == null ) ) {
								for(int i = 0 ; i < currentParent.getChildren().get(indx).getChildren().size();i++) {
									mergedChilds.add(currentParent.getChildren().get(indx).getChildren().get(i));
								}
							}
							mergedNode.setKeys(mergedKeys);
							mergedNode.setValues(mergedValues);
							mergedNode.setChildren(mergedChilds);
							if(node.isLeaf() == false) {
								mergedNode.setLeaf(false);
							}
							currentParent.getChildren().set(indx, mergedNode );
							((BTreeNode<K, V>)mergedNode).setparent(currentParent);
							if(currentParent.getKeys().size() == 0 && currentParent == this.root) {
								this.root = mergedNode;
								return;
							}
							if(currentParent.getKeys().size() < minKeys) {
								parents.remove(parents.size()-1);
								NodeAsAchildIndx.remove(NodeAsAchildIndx.size()-1);
								fix_delete(currentParent);
							}
					}
				}			
		}
	}

	@Override
	public boolean delete(K key) {
		// TODO Auto-generated method stub
		if(key == null) {
			Error e = null;
			throw new RuntimeErrorException(e);
		}
		keyFound = false;
		searchForInsert(this.root, key);
		if(keyFound == false) {
			//System.out.println(this.search(key));
			//System.out.println(key + "a7ooooo");
			return false;
		}
		//Node is leaf.
		if(NodeToSearchIn.isLeaf()== true)
		{
			int index = 0;
			for(int i = 0 ; i < NodeToSearchIn.getKeys().size() ; i++) {
				if(NodeToSearchIn.getKeys().get(i).compareTo(key) == 0) {
					index = i;
					break;
				}
			}
			NodeToSearchIn.getKeys().remove(index);
			NodeToSearchIn.getValues().remove(index);
			if(NodeToSearchIn.getKeys().size() < minKeys) {
				fix_delete(NodeToSearchIn);
			}
			return true;
		}
		//Node is internal.
		else {			
			int index = 0;
			for(int i = 0 ; i < NodeToSearchIn.getKeys().size() ; i++) {
				if(NodeToSearchIn.getKeys().get(i).compareTo(key) == 0) {
					index = i;
					break;
				}
			}
			//replace with predecessor or successor.
			IBTreeNode<K, V> pre = NodeToSearchIn.getChildren().get(index);
			while(pre.isLeaf() == false ) {
				int lastIndex = pre.getChildren().size()-1;
				pre = pre.getChildren().get( lastIndex );
			}
			if(pre.getKeys().size() > minKeys) {
				K preKey = pre.getKeys().get( pre.getKeys().size()-1 );
				V preValue = pre.getValues().get( pre.getValues().size()-1 );
				pre.getKeys().remove(pre.getKeys().size()-1);
				pre.getValues().remove(pre.getValues().size()-1);
				NodeToSearchIn.getKeys().set(index, preKey);
				NodeToSearchIn.getValues().set(index, preValue);
				return true;
			}
			IBTreeNode<K, V> succ = NodeToSearchIn.getChildren().get(index+1);
			while(succ != null && succ.isLeaf() == false ) {
				succ  = succ.getChildren().get( 0);
			}
			if(succ.getKeys().size() > minKeys) {
				K preKey = succ.getKeys().get( 0 );
				V preValue = succ.getValues().get( 0 );
				succ.getKeys().remove(0);
				succ.getValues().remove(0);
				NodeToSearchIn.getKeys().set(index, preKey);
				NodeToSearchIn.getValues().set(index, preValue);	
				return true;
			}	
			//merge then delete element and check for the node if underflow..and also take into consideration merging of children of two nodes.
			//merge if the pre and succ are the children
			if(NodeToSearchIn.getChildren().get(0).isLeaf() == true) {
				
				IBTreeNode<K, V> mergedNode = new BTreeNode<K,V>(this.mindegree);
				List<K> mergedKeys = new ArrayList<K>();
				List<V> mergedValues = new ArrayList<V>();
				
				for(int i = 0 ; i < pre.getKeys().size();i++) {
					mergedKeys.add(pre.getKeys().get(i));
					mergedValues.add(pre.getValues().get(i));
				}
				
				for(int i = 0 ; i < succ.getKeys().size() ; i++) {
					mergedKeys.add(succ.getKeys().get(i));
					mergedValues.add(succ.getValues().get(i));
				}
				mergedNode.setKeys(mergedKeys);
				mergedNode.setValues(mergedValues);
				mergedNode.setLeaf(true);
				NodeToSearchIn.getKeys().remove(index);
				NodeToSearchIn.getValues().remove(index);
				NodeToSearchIn.getChildren().set(index, mergedNode);
				((BTreeNode<K,V>)mergedNode).setparent(NodeToSearchIn);
				NodeToSearchIn.getChildren().remove(index+1);
				//check for the node of underflow
				if(NodeToSearchIn.getKeys().size() == 0 && NodeToSearchIn == this.root) {
					this.root = mergedNode;
					return true;
				}
				if(NodeToSearchIn.getKeys().size() < minKeys) {
					fix_delete(NodeToSearchIn);
				}
				return true;
				
			}
			else  {
				IBTreeNode<K, V> pre2 = NodeToSearchIn.getChildren().get(index);
				parents.add(NodeToSearchIn);
				NodeAsAchildIndx.add(index);
				while(pre2.isLeaf() == false ) {
					int lastIndex = pre2.getChildren().size()-1;
					parents.add(pre2);
					NodeAsAchildIndx.add(lastIndex);				
					pre2 = pre2.getChildren().get( lastIndex );
				}
				K keyH = pre.getKeys().get( pre.getKeys().size()-1 );
				V valueH = pre.getValues().get( pre.getValues().size()-1 );
				IBTreeNode<K, V> h = NodeToSearchIn;
				pre2.getKeys().remove( pre2.getKeys().size()-1 );
				pre2.getValues().remove( pre2.getValues().size()-1 );
				h.getKeys().set(index, keyH );
				h.getValues().set(index, valueH );
				this.fix_delete(pre2);
				return true;
			}
		}
	}
	
	public void adjustvalue(K key,V value) {
		search(key);
		for(int i=0;i<NodeToSearchIn.getNumOfKeys();i++) {
			if(key.compareTo(NodeToSearchIn.getKeys().get(i))==0) {
				NodeToSearchIn.getValues().set(i, value);
				break;
			}
		}
	}

}

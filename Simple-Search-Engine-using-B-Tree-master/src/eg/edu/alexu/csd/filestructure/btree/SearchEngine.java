package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.io.*;
import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.Queue;
public class SearchEngine implements ISearchEngine {
	
		
    Vector<Page> pages=new Vector<Page>();
    public int min(int x,int y) {
    	if(x<y) {return x;}
    	return y;
    }
	@Override
	public void indexWebPage(String filePath) {
		
		if(filePath == null||filePath.equals("")) {
			Error e = null;			
			throw new RuntimeErrorException(e);		
		}
	   File file=new File(filePath);
	   if(!file.exists()) {return;}
		
		// TODO Auto-generated method stub
       try {
		pages.add(new Page(filePath));
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}

	@Override
	public void indexDirectory(String directoryPath) {
		
		if(directoryPath == null||directoryPath.equals("")) {
			Error e = null;			
			throw new RuntimeErrorException(e);		
		}
	   File file=new File(directoryPath);
	   if(!file.exists()) {return;}
		// TODO Auto-generated method stub
		File folder=new File(directoryPath);
		Vector<String> files=new Vector<String>();
		  for ( File fileEntry : folder.listFiles()) {
		            try {
		            	if(fileEntry.isDirectory()) {
		            	    for(File subfile: fileEntry.listFiles()) {
		            	    	if(files.contains(fileEntry.getName())) {continue;}
		            	    	pages.add(new Page(directoryPath+"\\"+fileEntry.getName()+"\\"+subfile.getName()));
		            	    	files.add(subfile.getName());
		            	    }
		            		continue;
		            	}
		            	if(files.contains(fileEntry.getName())) {continue;}
		            	files.add(fileEntry.getName());
						pages.add(new Page(directoryPath+"\\"+fileEntry.getName()));
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		
	}

	@Override
	public void deleteWebPage(String filePath) {
		// TODO Auto-generated method stub
		if(filePath == null||filePath.equals("")) {
			Error e = null;			
			throw new RuntimeErrorException(e);		
		}
	   File file=new File(filePath);
	   if(!file.exists()) {return;}
		Page temp=null;
		for(Page p:pages) {
			if(p.getpath().compareTo(filePath)==0) {
				temp=p;
				break;
			}
		}
		if(temp!=null) {
			pages.remove(temp);
		}
	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		// TODO Auto-generated method stub
		if(word == null) {
			Error e = null;			
			throw new RuntimeErrorException(e);		
		}
		if(word.compareTo("") == 0) {
			return new ArrayList<>();
		}
		word=word.trim().toLowerCase();
		Vector<ISearchResult> searchresults=new Vector<ISearchResult>();
		for(Page p:pages) {
			Vector<IBTree<String , Integer>> trees=p.gettrees();
			Vector<String> IDs=p.getIDs();
		
		int size=IDs.size();
		for(int i=0;i<size;i++) {
			
			if(trees.get(i).search(word)!=null)
			   searchresults.add(new SearchResult(IDs.elementAt(i),trees.get(i).search(word)));
			
		}
	}
		return searchresults;
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		// TODO Auto-generated method stub
		if(sentence == null) {
			Error e = null;
			throw new RuntimeErrorException(e);
			//return new ArrayList<>();
			
		}
		 String[] splited = sentence.trim().split("\\s+");
			Vector<ISearchResult> searchresults=new Vector<ISearchResult>();
			for(Page p:pages) {
				Vector<IBTree<String , Integer>> trees=p.gettrees();
				Vector<String> IDs=p.getIDs();
			
			int size=IDs.size();
			for(int i=0;i<size;i++) {
				int Min=100000000;
				for(String word:splited) {
					word=word.toLowerCase();
					if(trees.get(i).search(word)!=null) {
						Min=min(Min,trees.get(i).search(word));
					}
					else {
						Min=100000000;
						break;
					}
				}
				if(Min!=100000000)
				   searchresults.add(new SearchResult(IDs.elementAt(i),Min));
				
			}
		}
			return searchresults;
		
		
	}
	private class Page{
		
		    private Vector<IBTree<String , Integer>> trees=new Vector<IBTree<String , Integer>>();
		    private Vector<String> IDs=new Vector<String>();
		    private String filepath; 
		public Page(String path) throws ParserConfigurationException, SAXException, IOException {
			
			  this.filepath=path;
			  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			  DocumentBuilder builder = factory.newDocumentBuilder();
			  Document document = builder.parse(new File(path));
			  document.getDocumentElement().normalize();
			  Element root = document.getDocumentElement();
			  NodeList nList = document.getElementsByTagName("doc");
			  int size=nList.getLength();
			  for(int i=0;i<size;i++) {
				  Node temp=nList.item(i);
				  String[] splited = temp.getTextContent().split("\\s+");
				  
				  IDs.add(temp.getAttributes().item(0).getNodeValue());
				  IBTree<String,Integer>temptree=new BTree<String,Integer>(10);
				  for (String s: splited)
	              {   
					  s=s.toLowerCase();
	                  if(temptree.search(s)!=null) {
	                	  ((BTree)temptree).adjustvalue(s, temptree.search(s)+1);
					  }
	                  else {
	                	  temptree.insert(s, 1);
	                  }
	              }
				  trees.add(temptree);
			  }
		}
		public Vector<IBTree<String , Integer>> gettrees(){
			return this.trees;
		}
		public Vector<String> getIDs(){
			return this.IDs;
		}
		public String getpath() {
			return this.filepath;
		}
	}

}

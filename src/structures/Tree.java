package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> tags = new Stack<TagNode>();
        sc.nextLine();
        root = new TagNode("html", null, null);
        tags.push(root);
        
        while(sc.hasNextLine()) {
                String st = sc.nextLine();
                Boolean isTag = false;
                if(st.charAt(0) == '<') {
                        if(st.charAt(1) == '/') {
                                tags.pop();
                                continue;
                        } else {
                                st = st.replace("<", "");
                                st = st.replace(">", "");
                                isTag = true;
                        }
                }
                TagNode tmp = new TagNode(st, null, null);
                if(tags.peek().firstChild == null) {
                        tags.peek().firstChild = tmp; 
                } else {
                        TagNode temporary = tags.peek().firstChild;
                        while(temporary.sibling != null) {
                                temporary = temporary.sibling;
                        }
                        temporary.sibling = tmp;
                }
                if(isTag) tags.push(tmp);
        	}
	}
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		
        replaceTag(root, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) { 
		
        TagNode table = findTable(root);
        TagNode tablePointer = table.firstChild;
        
        for(int i=1; i != row; i++) {
                tablePointer = tablePointer.sibling;
        }
        for(TagNode tablePointer2 = tablePointer.firstChild; tablePointer2 != null; tablePointer2 = tablePointer2.sibling) {
                TagNode bold = new TagNode("b", tablePointer2.firstChild, null);
                tablePointer2.firstChild = bold;
        }
}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) { 
        if((tag.equals("em") || tag.equals("b") || tag.equals("p"))){
        	
        	removeSimpleTag(root, tag);
        }
        if((tag.equals("ul") || tag.equals("ol"))){
        	
        	removeUlOl(root, tag);
        }
	}	
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		
        if(!word.matches("[a-zA-Z]{"+word.length()+"}")){
        	
        	return;
        }
        if(tag.equals("b") || tag.equals("em")){
        	
        	addTag(root, word.toLowerCase(), tag);
        }
	}
	
	// these are all of the private helper methods that have been implemented
	
	private void replaceTag(TagNode root, String oldTag, String newTag) {
		
        if(root == null){
        	
        	return;
        	
        }
        if(root.tag.equals(oldTag) && root.firstChild != null) {
        	
                root.tag = newTag;
                
        }
        replaceTag(root.sibling, oldTag, newTag);
        replaceTag(root.firstChild, oldTag, newTag);
	}
	private TagNode findTable(TagNode root) { 
        if(root == null){
        	return null; 
        }
        if(root.tag.equals("table")){
        	return root; 
        }
        TagNode sibling = findTable(root.sibling);
        TagNode firstChild = findTable(root.firstChild);
        if(sibling != null){
        	return sibling; 
        }
        if(firstChild != null){
        	return firstChild; 
        }
        return null;
	}
	private void removeSimpleTag(TagNode root, String tag) {
        if(root == null){ 
        	return;
        }
        if(root.tag.equals(tag) && root.firstChild != null) {
                root.tag = root.firstChild.tag;
                if(root.firstChild.sibling != null) {
                        TagNode pointer = null;
                        for(pointer = root.firstChild; pointer.sibling != null; pointer = pointer.sibling); 
                        pointer.sibling = root.sibling;
                        root.sibling = root.firstChild.sibling;
                }
                root.firstChild = root.firstChild.firstChild;
        }
        removeSimpleTag(root.firstChild, tag); 
        removeSimpleTag(root.sibling, tag);
	}

	private void removeUlOl(TagNode root, String tag) {
        if(root == null) return;
        if(root.tag.equals(tag) && root.firstChild != null) {
                root.tag = "p";
                TagNode tempPointer = null;
                for(tempPointer = root.firstChild; tempPointer.sibling != null; tempPointer = tempPointer.sibling) tempPointer.tag = "p"; 
                tempPointer.tag = "p";
                tempPointer.sibling = root.sibling;
                root.sibling = root.firstChild.sibling;
                root.firstChild = root.firstChild.firstChild;
        }
        removeUlOl(root.firstChild, tag); 
        removeUlOl(root.sibling, tag);
	}
	
	private void addTag(TagNode root, String word, String tag) {
        if(root == null){
        	return; 
        }
        addTag(root.firstChild, word, tag);
        addTag(root.sibling, word, tag);
        if(root.firstChild == null) {
                while(root.tag.toLowerCase().contains(word)) {
                        String[] phrases = root.tag.split(" ");
                        Boolean found = false;
                        String tagWord = "";
                        StringBuilder stringbuild = new StringBuilder(root.tag.length());
                        int k = 0;
                        for(k=0; k<phrases.length; k++) {
                                if(phrases[k].toLowerCase().matches(word+"[.?!,]?")) {
                                        found = true;
                                        tagWord = phrases[k];
                                        for(int j=k+1; j<phrases.length; j++) stringbuild.append(phrases[j]+" ");
                                        break;
                                }
                        }
                        if(!found){
                        	return;
                        }
                        
                        String remaining = stringbuild.toString().trim();
                        if(k == 0) { 
                                root.firstChild = new TagNode(tagWord, null, null);
                                root.tag = tag;
                                if(!remaining.equals("")) { 
                                        root.sibling = new TagNode(remaining, null, root.sibling);
                                        root = root.sibling;
                                }
                        }else{
                                TagNode twn = new TagNode(tagWord, null, null);
                                TagNode tagNew = new TagNode(tag, twn, root.sibling);
                                root.sibling = tagNew;
                                root.tag = root.tag.replaceFirst(" "+tagWord, "");
                                if(!remaining.equals("")) {
                                        root.tag = root.tag.replace(remaining, "");
                                        tagNew.sibling = new TagNode(remaining, null, tagNew.sibling);
                                        root = tagNew.sibling;
                                	}
                        	}
                	} 
        	}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}

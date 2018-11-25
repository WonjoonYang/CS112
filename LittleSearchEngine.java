package lse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String,Occurrence> localIndex =  new HashMap<String,Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			Occurrence temp = new Occurrence(docFile,-1);
			String Word = sc.next();
			Word=getKeyword(Word);
			if (Word==null) {
				continue;
			}else if (localIndex.containsKey(Word)) {
				temp.frequency =localIndex.get(Word).frequency+1;
				localIndex.replace(Word,temp);
			}else if (!localIndex.containsKey(Word)) {
				temp.frequency =1;
				localIndex.put(Word,temp);
			}
		}
		sc.close();
		return localIndex;
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		if (!keywordsIndex.isEmpty()) {
			for(Map.Entry<String, Occurrence> comp: kws.entrySet()) {
				if (keywordsIndex.containsKey(comp.getKey())) {
					ArrayList<Occurrence> temp = keywordsIndex.get(comp.getKey());
					temp.add(kws.get(comp.getKey()));
					insertLastOccurrence(temp);
					keywordsIndex.replace(comp.getKey(),temp);
				}else {
					ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
					temp.add(kws.get(comp.getKey()));
					keywordsIndex.put(comp.getKey(), temp);
				}
			}
		}else {
			for(Map.Entry<String, Occurrence> comp: kws.entrySet()) {
				ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
				temp.add(kws.get(comp.getKey()));
				keywordsIndex.put(comp.getKey(), temp);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		String trimmedWord = word.toLowerCase().replaceAll("[^a-zA-Z]+$", "");
		boolean matcher = false;
		int i=0;
        while (i<trimmedWord.length()){
            if (trimmedWord.substring(i,i+1).matches("[^A-Za-z]")){
                matcher=true;
            }
            i++;
        }
		if (matcher==true) {
			return null;
		}else if(noiseWords.contains(trimmedWord)) {
			return null;
		}else if(trimmedWord.equals("")) {
			return null;
		}else{
			return trimmedWord;
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		int lastIndex = occs.size()-1;
		Occurrence lastOccs = occs.get(lastIndex);
		occs.remove(lastIndex);
		ArrayList<Occurrence> temp = occs;
		int left = 0;
		int right = temp.size()-1;
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		int mid=0;
		while (left<=right) {
			mid = (left+right)/2;
			retVal.add(mid);
			if (lastOccs.frequency==temp.get(mid).frequency) {
				occs.add(mid+1, lastOccs);
				return retVal;
			}else {
				if (lastOccs.frequency<temp.get(mid).frequency) {
					left = mid+1;
				}else {
					right = mid-1;
				}
			}
		}
		if (mid==right) {

			occs.add(mid+1, lastOccs);
			return retVal;
		}else if (mid==left) {

			occs.add(mid, lastOccs);
			return retVal;
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return retVal;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		if (!(keywordsIndex.containsKey(kw1)&&keywordsIndex.containsKey(kw2))) {
			return null;
		}else if (keywordsIndex.containsKey(kw1)&&!keywordsIndex.containsKey(kw2)) {
			ArrayList<String> retVal = new ArrayList<String>();
			for (int i=0; i<keywordsIndex.get(kw1).size(); i++) {
				if (retVal.size()>=5) {
					break;
				}
				retVal.add(keywordsIndex.get(kw1).get(i).document);
			}
			return retVal;
		}else if (!keywordsIndex.containsKey(kw1)&&keywordsIndex.containsKey(kw2)) {
			ArrayList<String> retVal = new ArrayList<String>();
			for (int i=0; i<keywordsIndex.get(kw2).size(); i++) {
				if (retVal.size()>=5) {
					break;
				}
				retVal.add(keywordsIndex.get(kw2).get(i).document);
			}
			return retVal;
		}
		ArrayList<Occurrence> keyWord1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> keyWord2 = keywordsIndex.get(kw2);
		ArrayList<Occurrence> combined = new ArrayList<Occurrence>();
		ArrayList<String> retVal = new ArrayList<String>();
		for (int i=0; i<keyWord1.size(); i++) {
			combined.add(keyWord1.get(i));
		}
		for (int i=0; i<keyWord2.size(); i++) {
			combined.add(keyWord2.get(i));
			insertLastOccurrence(combined);
		}
		for (int i=0; i<combined.size(); i++) {
			if (retVal.isEmpty()) {
				retVal.add(combined.get(0).document);
				continue;
			}
			if (retVal.size()>=5) {
				break;
			}
			if (retVal.contains(combined.get(i).document)) {
				continue;
			}else {
				retVal.add(combined.get(i).document);
			}
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return retVal;
	
	}
}

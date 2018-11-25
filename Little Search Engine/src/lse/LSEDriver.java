package lse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LSEDriver {
	public static void main(String[] args) throws IOException {

		LittleSearchEngine engine=new LittleSearchEngine();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter document file name => ");
		String docsFile=br.readLine();
		System.out.print("Enter noise words file name => ");
		String noiseWordsFile=br.readLine();
		engine.makeIndex(docsFile, noiseWordsFile);
		HashMap<String,ArrayList<Occurrence>> newkeywordsIndex = engine.keywordsIndex;
		for(Entry<String, ArrayList<Occurrence>> comp: newkeywordsIndex.entrySet()) {
			ArrayList<Occurrence> temp = newkeywordsIndex.get(comp.getKey());
			String k = temp.toString();
			System.out.println(comp.getKey());
			System.out.println(k);
		}
		System.out.println(engine.keywordsIndex.size());
		System.out.println(engine.top5search("space", "round"));
	}
}

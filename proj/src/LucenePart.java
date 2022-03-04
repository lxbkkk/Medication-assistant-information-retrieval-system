import java.io.*;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;



public class LucenePart {
	
	// �����ļ�Ĭ�ϴ���� D �̵� LuceneIndex �ļ�����
	public static final String luceneIndexPath = "D:\\LuceneIndex";
	// ��� txt �ļ�Ŀ¼
	public static final String luceneFilePath = "D:\\";
	
	public static void main(String[] args) {
		// �� MAIN �������ڴ�������, ֻ��Ҫ����һ��, ����������ɺ���Ҫ���ظ�����
		LucenePart lucene = new LucenePart();
		lucene.createIndexOuter();
	}
	
	public void createIndexOuter() {
		// ��14���ļ�
		// 0 ���Լ��غ��ȵ��صļ�����ϵͳ��ҩ.txt
		// 1 �о�����.txt
		// 2 ��������.txt
		// 3 ����-����ϵͳ.txt
		// 4 ��������ݡ�ɱ��ҩ������ҩ.txt
		// 5 ������ҩ�����߻��ܵ���ҩ.txt
		// 6 Ƥ������ҩ.txt
		// 7 ��ϵͳ.txt
		// 8 ��ֳ����ϵͳ���Լ���.txt
		// 9 ϵͳ�ÿ���Ⱦҩ.txt
		// 10 ����������л.txt
		// 11 ��Ѫ��ϵͳ.txt
		// 12 ѪҺ����Ѫ����.txt
		// 13 ����.txt
		LucenePart lucenePart=new LucenePart();
		// ҩƷ��Ϣ�ļ���
		String fileName[] = {"���Լ��غ��ȵ�����ļ�����ϵͳ��ҩ.txt","�о�����.txt","����ϵͳ.txt","����-����ϵͳ.txt","��������ҩ��ɱ��ҩ������ҩ.txt","������ҩ�����߻��ܵ���ҩ.txt","Ƥ������ҩ.txt","��ϵͳ.txt","��ֳ����ϵͳ���Լ���.txt","ϵͳ�ÿ���Ⱦҩ.txt","����������л.txt","��Ѫ��ϵͳ.txt","ѪҺ����Ѫ����.txt","����.txt"};
		// ҩƷ������
		String CategoryName[] = {"���Լ��غ��ȵ�����ļ�����ϵͳ��ҩ","�о�����","����ϵͳ","����-����ϵͳ","��������ҩ��ɱ��ҩ������ҩ","������ҩ�����߻��ܵ���ҩ","Ƥ������ҩ","��ϵͳ","��ֳ����ϵͳ���Լ���","ϵͳ�ÿ���Ⱦҩ","����������л","��Ѫ��ϵͳ","ѪҺ����Ѫ����","����"};
		// ���� 14 ���ļ���������
		for(int i=0;i<14;i++) {
			lucenePart.createIndex(luceneIndexPath, luceneFilePath + fileName[i], CategoryName[i]);
		}
	}
	
	public void searchIndexOuter(Integer SearchCategory, Integer SearchKey, String SearchInput) {
		LucenePart luceneSearch = new LucenePart();
		luceneSearch.searchIndex(luceneIndexPath, SearchCategory, SearchKey, SearchInput);
	}
	
	public void createIndex(String indexPath, String filePath, String CategoryName){
		File indexfile = new File(indexPath);
		IndexWriter indexWriter = null;
		try {
			Directory directory = FSDirectory.open(indexfile);
			Analyzer analyzer = new IKAnalyzer();
			
			IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_4_10_0,analyzer);
			// ���� IndexWriter
			indexWriter = new IndexWriter(directory,indexConfig);
			
			File file = new File(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
			BufferedReader bufferReader = null;
			
			bufferReader = new BufferedReader(inputStreamReader);
			
			String whileTmpString = "";
			String tmpString[] = new String[20];
			int tmpStringIndex = 0;
			// ���ļ��а��ж�������
			while((whileTmpString = bufferReader.readLine()) != null) {
				tmpString[tmpStringIndex] = whileTmpString;
				//System.out.println(" ------ " + CategoryName + " ------ " + tmpStringIndex + " ------ ");
				if(tmpStringIndex == 13) {
					Medicine tmpMedicine = new Medicine(tmpString[0],tmpString[1],tmpString[2],tmpString[3],tmpString[4],tmpString[5],tmpString[6],tmpString[7],tmpString[8],tmpString[9],tmpString[10],tmpString[11],tmpString[12],tmpString[13],file.getName().replace(".txt", ""));
					Document tmpDocument = tmpMedicine.toDocument(CategoryName);
					//��� document, Lucene �ļ����� document Ϊ������λ
					indexWriter.addDocument(tmpDocument);
					tmpStringIndex = 0;
				}else {
					tmpStringIndex++;
				}
			}
			bufferReader.close();
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void searchIndex(String indexPath, Integer CategoryKey, Integer SearchKey, String SearchInput){
		// ҩƷ�������
		String CategoryName[] = {"���Լ��غ��ȵ��صļ�����ϵͳ��ҩ","�о�����","��������","����-����ϵͳ","��������ݡ�ɱ��ҩ������ҩ","������ҩ�����߻��ܵ���ҩ","Ƥ������ҩ","��ϵͳ","��ֳ����ϵͳ���Լ���","ϵͳ�ÿ���Ⱦҩ","����������л","��Ѫ��ϵͳ","ѪҺ����Ѫ����","����"};
		
		// ����������
		String SearchName[] = {"name","component","indication"};
		
		String CategoryKeyString = "";
		String SearchKeyString = "";
		if(CategoryKey < 13) {
			CategoryKeyString = CategoryName[CategoryKey];
		}
		SearchKeyString = SearchName[SearchKey];
		
		File file = new File(indexPath);
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(file)));
			
			// �������
			Term term1 = new Term("category",CategoryKeyString);
			Term term2 = new Term(SearchKeyString, SearchInput);
			
			WildcardQuery wildcardQuery1 = new WildcardQuery(term1);
			WildcardQuery wildcardQuery2 = new WildcardQuery(term2);
			
			BooleanQuery booleanQuery = new BooleanQuery();
			if(CategoryKey < 13) {
				booleanQuery.add(wildcardQuery1, Occur.MUST);
			}
			booleanQuery.add(wildcardQuery2, Occur.MUST);
			
			TopDocs topDocs = searcher.search(booleanQuery,null,1000);
			Integer docSize = topDocs.scoreDocs.length;
			
			// ����������, ÿ���10��������û�ȷ���Ƿ���Ҫ�������
			System.out.println("���ҵ� " + docSize + " ��ƥ��ҩƷ");
			for(int i=0;i<docSize;i++) {
				if((i % 10 == 0)&&(i != 0)) {
					System.out.println("���� ��continue�� �����������ʮ���������");
					System.out.println("���� ��stop�� ֹͣ����������˴�����");
					Scanner sn = new Scanner(System.in);
					String GetInstruction = sn.nextLine();
					GetInstruction = GetInstruction.replaceAll("\n", "");
					while(!(GetInstruction.equals("continue")||GetInstruction.equals("stop"))) {
						System.out.println("���� ��continue�� �����������ʮ���������");
						System.out.println("���� ��stop�� ֹͣ����������˴�����");
						GetInstruction = sn.nextLine();
						GetInstruction = GetInstruction.replaceAll("\n", "");
					}
					//sn.close();
					if(GetInstruction.equals("continue")) {
						ScoreDoc scoreDoc = topDocs.scoreDocs[i];
						int docIndex = scoreDoc.doc;
						Document document = searcher.doc(docIndex);
						System.out.println(document.get("print"));
					}else {
						break;
					}
				}else {
					ScoreDoc scoreDoc = topDocs.scoreDocs[i];
					int docIndex = scoreDoc.doc;
					Document document = searcher.doc(docIndex);
					System.out.println(document.get("print"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

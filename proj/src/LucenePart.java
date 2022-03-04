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
	
	// 索引文件默认存放在 D 盘的 LuceneIndex 文件夹中
	public static final String luceneIndexPath = "D:\\LuceneIndex";
	// 存放 txt 文件目录
	public static final String luceneFilePath = "D:\\";
	
	public static void main(String[] args) {
		// 此 MAIN 函数用于创建索引, 只需要运行一次, 创建索引完成后不需要再重复创建
		LucenePart lucene = new LucenePart();
		lucene.createIndexOuter();
	}
	
	public void createIndexOuter() {
		// 共14个文件
		// 0 非性激素和胰岛素的激素类系统用药.txt
		// 1 感觉器官.txt
		// 2 呼吸器官.txt
		// 3 肌肉-骨骼系统.txt
		// 4 抗寄生虫草、杀虫药和驱虫药.txt
		// 5 抗肿瘤药和免疫机能调节药.txt
		// 6 皮肤病用药.txt
		// 7 神经系统.txt
		// 8 生殖泌尿系统和性激素.txt
		// 9 系统用抗感染药.txt
		// 10 消化道及代谢.txt
		// 11 心血管系统.txt
		// 12 血液和造血器官.txt
		// 13 杂类.txt
		LucenePart lucenePart=new LucenePart();
		// 药品信息文件名
		String fileName[] = {"非性激素和胰岛素类的激素类系统用药.txt","感觉器官.txt","呼吸系统.txt","肌肉-骨骼系统.txt","抗寄生虫药、杀虫药和驱虫药.txt","抗肿瘤药和免疫机能调节药.txt","皮肤病用药.txt","神经系统.txt","生殖泌尿系统和性激素.txt","系统用抗感染药.txt","消化道及代谢.txt","心血管系统.txt","血液和造血器官.txt","杂类.txt"};
		// 药品分类名
		String CategoryName[] = {"非性激素和胰岛素类的激素类系统用药","感觉器官","呼吸系统","肌肉-骨骼系统","抗寄生虫药、杀虫药和驱虫药","抗肿瘤药和免疫机能调节药","皮肤病用药","神经系统","生殖泌尿系统和性激素","系统用抗感染药","消化道及代谢","心血管系统","血液和造血器官","杂类"};
		// 遍历 14 个文件创建索引
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
			// 创建 IndexWriter
			indexWriter = new IndexWriter(directory,indexConfig);
			
			File file = new File(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
			BufferedReader bufferReader = null;
			
			bufferReader = new BufferedReader(inputStreamReader);
			
			String whileTmpString = "";
			String tmpString[] = new String[20];
			int tmpStringIndex = 0;
			// 从文件中按行读出内容
			while((whileTmpString = bufferReader.readLine()) != null) {
				tmpString[tmpStringIndex] = whileTmpString;
				//System.out.println(" ------ " + CategoryName + " ------ " + tmpStringIndex + " ------ ");
				if(tmpStringIndex == 13) {
					Medicine tmpMedicine = new Medicine(tmpString[0],tmpString[1],tmpString[2],tmpString[3],tmpString[4],tmpString[5],tmpString[6],tmpString[7],tmpString[8],tmpString[9],tmpString[10],tmpString[11],tmpString[12],tmpString[13],file.getName().replace(".txt", ""));
					Document tmpDocument = tmpMedicine.toDocument(CategoryName);
					//添加 document, Lucene 的检索以 document 为基本单位
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
		// 药品类别名称
		String CategoryName[] = {"非性激素和胰岛素的激素类系统用药","感觉器官","呼吸器官","肌肉-骨骼系统","抗寄生虫草、杀虫药和驱虫药","抗肿瘤药和免疫机能调节药","皮肤病用药","神经系统","生殖泌尿系统和性激素","系统用抗感染药","消化道及代谢","心血管系统","血液和造血器官","杂类"};
		
		// 搜索的属性
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
			
			// 添加条件
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
			
			// 输出搜索结果, 每输出10个结果向用户确认是否需要继续输出
			System.out.println("共找到 " + docSize + " 个匹配药品");
			for(int i=0;i<docSize;i++) {
				if((i % 10 == 0)&&(i != 0)) {
					System.out.println("输入 “continue” 继续输出下面十个搜索结果");
					System.out.println("输入 “stop” 停止输出并结束此次搜索");
					Scanner sn = new Scanner(System.in);
					String GetInstruction = sn.nextLine();
					GetInstruction = GetInstruction.replaceAll("\n", "");
					while(!(GetInstruction.equals("continue")||GetInstruction.equals("stop"))) {
						System.out.println("输入 “continue” 继续输出下面十个搜索结果");
						System.out.println("输入 “stop” 停止输出并结束此次搜索");
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

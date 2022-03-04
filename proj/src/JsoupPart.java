// TODO 关于close client, 增加 a b c 的关闭, 是否可以调整关闭位置

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


 
public class JsoupPart {
 
	// 此 MAIN 函数用于获取药品数据, 只需要运行一次获得文件就可
	public static void main(String[] args) throws Exception{
		
		long startTime =  System.currentTimeMillis();
		// HttpClient 请求获取 https://drugs.dxy.cn/ 首页 html
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://drugs.dxy.cn/");
		CloseableHttpResponse response = client.execute(get);
		String content = "";
		if(response.getStatusLine().getStatusCode() == 200){
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
		}
		
		//关闭链接
		//client.close();
		
		// 将前面获取到的 html 文本转换为 Document 对象
		Document doc = Jsoup.parse(content);
		
		// 首页左边分类名称
		String FirstLevelName[] = new String[50];
		
		// 首页左边分类id
		String FirstLevelId[] = new String[50];
		
		// 获取 json 对象(为了得到全部下一级链接)
		String tmpjson = doc.select("script[type=application/json]").toString();
		int tmpj1 = tmpjson.lastIndexOf("<");
		int tmpj2 = tmpjson.indexOf(">");
		tmpjson = tmpjson.substring(tmpj2+1, tmpj1);
		JSONObject json = JSON.parseObject(tmpjson);
		
		// 分层获取信息，直到得到 Category List
		JSONObject props = json.getJSONObject("props");
		JSONObject pageProps = props.getJSONObject("pageProps");
		
		// 首页左边的 List (即药品分类信息
		JSONArray firstLevelCategoryList =  pageProps.getJSONArray("firstLevelCategoryList");
		
		// 遍历 firstLevelCategoryList 将左边 List 的 name 和 id 存在数组中
		int FirstLevelSize = firstLevelCategoryList.size();
		for(int i=0;i<FirstLevelSize;i++) {
			FirstLevelName[i] = (firstLevelCategoryList.getJSONObject(i)).get("name").toString();
			FirstLevelId[i] = (firstLevelCategoryList.getJSONObject(i)).get("id").toString();
		}
		
		// 首页右边的 List (左边的下一层
		JSONArray secondLevelCategoryList =  pageProps.getJSONArray("secondLevelCategoryList");
		int SecondLevelSize = secondLevelCategoryList.size();
		
		// 遍历访问首页右边的所有 List
		for(int i=0;i<SecondLevelSize;i++) {
			
			// FirstName 为药品类别名, 即首页左边栏中的药品一层分类
			String FirstName = "";
			
			// SecondName 为首页右边栏中的药品二级分类, 用处不大所以未做处理
			//String SecondName = secondLevelCategoryList.getJSONObject(i).getString("name").toString();
			
			// FirstId 为首页左边栏中的药品分类id
			String FirstId = secondLevelCategoryList.getJSONObject(i).getString("supId").toString();
			
			// SecondId 为首页右边栏中的药品二层分类id, 用于获取右边链接的 url, 即 SecondUrl
			String SecondId = secondLevelCategoryList.getJSONObject(i).getString("id").toString();
			String SecondUrl = "https://drugs.dxy.cn/category/" + SecondId;
			
			// 遍历药品分类的 Id, 用于确定右边的药品二级分类属于哪一个一级分类
			for(int j=0;j<FirstLevelSize;j++) {
				if(FirstLevelId[j].equals(FirstId)) {
					FirstName = FirstLevelName[j];
					break;
				}
			}
			
			// HttpClient 请求获取网页 html
    		HttpGet get_c = new HttpGet(SecondUrl);
    		CloseableHttpResponse response_c = client.execute(get_c);
    		content = "";
    		if(response_c.getStatusLine().getStatusCode()==200){
    			content = EntityUtils.toString(response_c.getEntity(), "UTF-8");
    		}
    		
    		// 将前面获取到的 html 文本转换为Document对象
    		doc = Jsoup.parse(content);
    		
    		// 获取当前页面的 PageSize
    		String tmpstring = doc.select("script[id=__NEXT_DATA__]").toString();
    		int tmp1 = tmpstring.indexOf("pageSize");
    		int tmp2 = tmpstring.indexOf("total");
    		int PageSize = Integer.valueOf(tmpstring.substring(tmp1+10, tmp2-2));
    		
    		// 遍历当前页面的所有有效 Page
    		for(int j = 0;j < PageSize;j++) {
    			// HttpClient 请求获取网页 html
        		HttpGet get_b = new HttpGet(SecondUrl + "?page=" + j);
        		CloseableHttpResponse response_b = client.execute(get_b);
        		content = "";
        		if(response_b.getStatusLine().getStatusCode()==200){
        			content = EntityUtils.toString(response_b.getEntity(), "UTF-8");
        		}
        		
        		// 将前面获取到的 html 文本转换为Document对象
        		doc = Jsoup.parse(content);
        		
        		// 选择以 /drug 开头的链接的 Elements
    			Elements DrugsElements = doc.select("a[href^=/drug]");
    			//System.out.println(DrugsElements);
    			
    			// 遍历访问以上获得的 Elements
    			int NumberElements = DrugsElements.size();
    			for(int k = 0;k < NumberElements;k++) {
    				
    				// 获取具体药品链接地址
    				String ElementTmp = DrugsElements.get(k).toString();
    				int elementtmp1 = ElementTmp.indexOf("\"");
    				int elementtmp2 = ElementTmp.lastIndexOf("\"");
    				ElementTmp = ElementTmp.substring(elementtmp1+1, elementtmp2);
    				ElementTmp = "https://drugs.dxy.cn" + ElementTmp;
    				System.out.println(ElementTmp);
    				
    				// HttpClient 请求获取网页 html
            		HttpGet get_a = new HttpGet(ElementTmp);
            		CloseableHttpResponse response_a = client.execute(get_a);
            		content = "";
            		if(response_a.getStatusLine().getStatusCode() == 200){
            			content = EntityUtils.toString(response_a.getEntity(), "UTF-8");
            		}
            		
            		// 将前面获取到的 html 文本转换为Document对象
            		doc = Jsoup.parse(content);
            		
            		// 获取文本
        			String OneOfDrugs = doc.text();
        			//System.out.println(doc);
        			//System.out.println("------------------------------------------------------------");
        			
        			// 获得药品的必要属性
        			// 每行最后注释的数字为搜索标记的长度
        			int MedicineNameIndex1 = OneOfDrugs.indexOf("通用名称");                           // + 5 
        			int MedicineNameIndex2 = OneOfDrugs.indexOf("英文名称");                           // + 5 
        			int MedicineNameIndex3 = OneOfDrugs.indexOf("商品名称");                           // + 5 
        			int MedicineComponentIndex = OneOfDrugs.indexOf("【成份】");                       // + 5
        			int MedicineIndicationIndex = OneOfDrugs.indexOf("【适应症】");                    // + 6
        			int MedicineUsageIndex = OneOfDrugs.indexOf("【用法用量】");                       // + 7
        			int MedicinePrecautionsIndex = OneOfDrugs.indexOf("【注意事项】");                 // + 7
        			int MedicineContraindicationsIndex = OneOfDrugs.indexOf("【禁忌】");               // + 5
        			int MedicineGravidaIndex = OneOfDrugs.indexOf("【孕妇及哺乳期妇女用药】");          // + 13
        			int MedicinePharamacologicalActionIndex = OneOfDrugs.indexOf("【药理作用】");      // + 7
        			int MedicinePharmacokineticsIndex = OneOfDrugs.indexOf("【药代动力学】");          // + 8
        			int MedicineChemicalCompositionIndex = OneOfDrugs.indexOf("【化学成份】");         // + 7
        			int MedicineOTCIndex = OneOfDrugs.indexOf("【是否OTC】");                          // + 8
        			
        			// 获取各个属性的内容
        			// 获取通用名称
        			String MedicineNameString1 = "";
        			if(MedicineNameIndex1 >= 0) {
        				MedicineNameString1 = OneOfDrugs.substring(MedicineNameIndex1 + 5);
        				if(MedicineNameIndex2 >= 0) {
        					MedicineNameString1 = MedicineNameString1.substring(0,MedicineNameIndex2 - MedicineNameIndex1 - 5);
        				}else if(MedicineNameIndex3 >= 0) {
        					MedicineNameString1 = MedicineNameString1.substring(0,MedicineNameIndex3);
        				}else {
        					int NameIndexTmp = MedicineNameString1.indexOf("【");
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("药品资讯");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("同类型药品");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("。");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf(" ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.length() / 2;
            				}
        					MedicineNameString1 = MedicineNameString1.substring(0,NameIndexTmp);
        				}
        				MedicineNameString1 = MedicineNameString1.replace('\n', '。');
        			}
        			
        			// 获取英文名称
        			String MedicineNameString2 = "";
        			if(MedicineNameIndex2 >= 0) {
        				MedicineNameString2 = OneOfDrugs.substring(MedicineNameIndex2 + 5);
        				if(MedicineNameIndex3 >= 0) {
        					MedicineNameString2 = MedicineNameString2.substring(0,MedicineNameIndex3 - MedicineNameIndex2 - 5);
        				}else {
        					int NameIndexTmp = MedicineNameString2.indexOf("【");
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("药品资讯");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("同类型药品");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("。");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf(" ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.length() / 2;
            				}
        					MedicineNameString2 = MedicineNameString2.substring(0,NameIndexTmp);
        				}
        				MedicineNameString2 = MedicineNameString2.replace('\n', '。');
        			}
        			
        			// 获取商品名称
        			String MedicineNameString3 = "";
        			if(MedicineNameIndex3 >= 0) {
        				MedicineNameString3 = OneOfDrugs.substring(MedicineNameIndex3 + 5);
        				int NameIndexTmp = MedicineNameString3.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.length() / 2;
        				}
        				MedicineNameString3 = MedicineNameString3.substring(0,NameIndexTmp);
        				MedicineNameString3 = MedicineNameString3.replace('\n', '。');
        			}
        			
        			// 获取成份
        			String MedicineComponentString = "";
        			if(MedicineComponentIndex >= 0) {
        				MedicineComponentString = OneOfDrugs.substring(MedicineComponentIndex + 5);
        				int NameIndexTmp = MedicineComponentString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.length() / 2;
        				}
        				MedicineComponentString = MedicineComponentString.substring(0,NameIndexTmp);
        				MedicineComponentString = MedicineComponentString.replace('\n', '。');
        			}
        			
        			// 获取适应症
        			String MedicineIndicationString = "";
        			if(MedicineIndicationIndex >= 0) {
        				MedicineIndicationString = OneOfDrugs.substring(MedicineIndicationIndex + 6);
        				int NameIndexTmp = MedicineIndicationString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.length() / 2;
        				}
        				MedicineIndicationString = MedicineIndicationString.substring(0,NameIndexTmp);
        				MedicineIndicationString = MedicineIndicationString.replace('\n', '。');
        			}
        			
        			// 获取用法用量
        			String MedicineUsageString = "";
        			if(MedicineUsageIndex >= 0) {
        				MedicineUsageString = OneOfDrugs.substring(MedicineUsageIndex + 7);
        				int NameIndexTmp = MedicineUsageString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.length() / 2;
        				}
        				MedicineUsageString = MedicineUsageString.substring(0,NameIndexTmp);
        				MedicineUsageString = MedicineUsageString.replace('\n', '。');
        			}
        			
        			// 获取注意事项
        			String MedicinePrecautionsString = "";
        			if(MedicinePrecautionsIndex >= 0) {
        				MedicinePrecautionsString = OneOfDrugs.substring(MedicinePrecautionsIndex + 7);
        				int NameIndexTmp = MedicinePrecautionsString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.length() / 2;
        				}
        				MedicinePrecautionsString = MedicinePrecautionsString.substring(0,NameIndexTmp);
        				MedicinePrecautionsString = MedicinePrecautionsString.replace('\n', '。');
        			}
        			
        			// 获取用药禁忌
        			String MedicineContraindicationString = "";
        			if(MedicineContraindicationsIndex >= 0) {
        				MedicineContraindicationString = OneOfDrugs.substring(MedicineContraindicationsIndex + 5);
        				int NameIndexTmp = MedicineContraindicationString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.length() / 2;
        				}
        				MedicineContraindicationString = MedicineContraindicationString.substring(0,NameIndexTmp);
        				MedicineContraindicationString = MedicineContraindicationString.replace('\n', '。');
        			}
        			
        			// 获取孕妇及脯乳期妇女用药
        			String MedicineGravidaString = "";
        			if(MedicineGravidaIndex >= 0) {
        				MedicineGravidaString = OneOfDrugs.substring(MedicineGravidaIndex + 13);
        				int NameIndexTmp = MedicineGravidaString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.length() / 2;
        				}
        				MedicineGravidaString = MedicineGravidaString.substring(0,NameIndexTmp);
        				MedicineGravidaString = MedicineGravidaString.replace('\n', '。');
        			}
        			
        			// 获取药理作用
        			String MedicinePharmacologicalActionString = "";
        			if(MedicinePharamacologicalActionIndex >= 0) {
        				MedicinePharmacologicalActionString = OneOfDrugs.substring(MedicinePharamacologicalActionIndex + 7);
        				int NameIndexTmp = MedicinePharmacologicalActionString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.length() / 2;
        				}
        				MedicinePharmacologicalActionString = MedicinePharmacologicalActionString.substring(0,NameIndexTmp);
        				MedicinePharmacologicalActionString = MedicinePharmacologicalActionString.replace('\n', '。');
        			}
        			
        			// 获取药代动力学
        			String MedicinePharmacokineticsString = "";
        			if(MedicinePharmacokineticsIndex >= 0) {
        				MedicinePharmacokineticsString = OneOfDrugs.substring(MedicinePharmacokineticsIndex + 8);
        				int NameIndexTmp = MedicinePharmacokineticsString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.length() / 2;
        				}
        				MedicinePharmacokineticsString = MedicinePharmacokineticsString.substring(0,NameIndexTmp);
        				MedicinePharmacokineticsString = MedicinePharmacokineticsString.replace('\n', '。');
        			}
        			
        			// 获取化学成份
        			String MedicineChemicalCompositionString = "";
        			if(MedicineChemicalCompositionIndex >= 0) {
        				MedicineChemicalCompositionString = OneOfDrugs.substring(MedicineChemicalCompositionIndex + 7);
        				int NameIndexTmp = MedicineChemicalCompositionString.indexOf("【");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("药品资讯");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("。");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.length() / 2;
        				}
        				MedicineChemicalCompositionString = MedicineChemicalCompositionString.substring(0,NameIndexTmp);
        				MedicineChemicalCompositionString = MedicineChemicalCompositionString.replace('\n', '。');
        			}
        			
        			// 获取 OTC 信息
        			String MedicineOTCString = "";
        			if(MedicineOTCIndex >= 0) {
        				MedicineOTCString = OneOfDrugs.substring(MedicineOTCIndex + 8);
        				int NameIndexTmp = MedicineOTCString.indexOf(" ");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("。");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("药品资讯");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("同类型药品");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("【");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = 0;
        				}
        				MedicineOTCString = MedicineOTCString.substring(0,NameIndexTmp);
        				MedicineOTCString = MedicineOTCString.replace('\n', '。');
        			}
        			
        			// 该药品的准确网页地址
        			String MedicineAddressString = ElementTmp;
        			
        			// 在这里进行一个文件的写入
        			// 默认写入D盘, 每个药品类别为一个.txt文件, 共 14 个, 文件名为药品分类名
        			String FilePath = "D:\\" + FirstName + ".txt";
        			File file = new File(FilePath);
        			// 若文件不存在则创建文件
        			if(!file.exists()) {
        				file.createNewFile();
        			}
        			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
        			BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
        			
        			// 写入文件
        			bufferWriter.write(MedicineNameString1+"\n");
        			bufferWriter.write(MedicineNameString2+"\n");
        			bufferWriter.write(MedicineNameString3+"\n");
        			bufferWriter.write(MedicineComponentString+"\n");
        			bufferWriter.write(MedicineIndicationString+"\n");
        			bufferWriter.write(MedicineUsageString+"\n");
        			bufferWriter.write(MedicinePrecautionsString+"\n");
        			bufferWriter.write(MedicineContraindicationString+"\n");
        			bufferWriter.write(MedicineGravidaString+"\n");
        			bufferWriter.write(MedicinePharmacologicalActionString+"\n");
        			bufferWriter.write(MedicinePharmacokineticsString+"\n");
        			bufferWriter.write(MedicineChemicalCompositionString+"\n");
        			bufferWriter.write(MedicineOTCString+"\n");
        			bufferWriter.write(MedicineAddressString+"\n");
        			
        			bufferWriter.close();
    			}
    		}
		}
		//关闭链接
		client.close();
		long endTime =  System.currentTimeMillis();
		long usedTime = (endTime-startTime)/1000;
		
		System.out.print(usedTime);
	}
}
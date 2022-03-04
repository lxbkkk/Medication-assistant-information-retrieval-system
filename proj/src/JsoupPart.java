// TODO ����close client, ���� a b c �Ĺر�, �Ƿ���Ե����ر�λ��

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
 
	// �� MAIN �������ڻ�ȡҩƷ����, ֻ��Ҫ����һ�λ���ļ��Ϳ�
	public static void main(String[] args) throws Exception{
		
		long startTime =  System.currentTimeMillis();
		// HttpClient �����ȡ https://drugs.dxy.cn/ ��ҳ html
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://drugs.dxy.cn/");
		CloseableHttpResponse response = client.execute(get);
		String content = "";
		if(response.getStatusLine().getStatusCode() == 200){
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
		}
		
		//�ر�����
		//client.close();
		
		// ��ǰ���ȡ���� html �ı�ת��Ϊ Document ����
		Document doc = Jsoup.parse(content);
		
		// ��ҳ��߷�������
		String FirstLevelName[] = new String[50];
		
		// ��ҳ��߷���id
		String FirstLevelId[] = new String[50];
		
		// ��ȡ json ����(Ϊ�˵õ�ȫ����һ������)
		String tmpjson = doc.select("script[type=application/json]").toString();
		int tmpj1 = tmpjson.lastIndexOf("<");
		int tmpj2 = tmpjson.indexOf(">");
		tmpjson = tmpjson.substring(tmpj2+1, tmpj1);
		JSONObject json = JSON.parseObject(tmpjson);
		
		// �ֲ��ȡ��Ϣ��ֱ���õ� Category List
		JSONObject props = json.getJSONObject("props");
		JSONObject pageProps = props.getJSONObject("pageProps");
		
		// ��ҳ��ߵ� List (��ҩƷ������Ϣ
		JSONArray firstLevelCategoryList =  pageProps.getJSONArray("firstLevelCategoryList");
		
		// ���� firstLevelCategoryList ����� List �� name �� id ����������
		int FirstLevelSize = firstLevelCategoryList.size();
		for(int i=0;i<FirstLevelSize;i++) {
			FirstLevelName[i] = (firstLevelCategoryList.getJSONObject(i)).get("name").toString();
			FirstLevelId[i] = (firstLevelCategoryList.getJSONObject(i)).get("id").toString();
		}
		
		// ��ҳ�ұߵ� List (��ߵ���һ��
		JSONArray secondLevelCategoryList =  pageProps.getJSONArray("secondLevelCategoryList");
		int SecondLevelSize = secondLevelCategoryList.size();
		
		// ����������ҳ�ұߵ����� List
		for(int i=0;i<SecondLevelSize;i++) {
			
			// FirstName ΪҩƷ�����, ����ҳ������е�ҩƷһ�����
			String FirstName = "";
			
			// SecondName Ϊ��ҳ�ұ����е�ҩƷ��������, �ô���������δ������
			//String SecondName = secondLevelCategoryList.getJSONObject(i).getString("name").toString();
			
			// FirstId Ϊ��ҳ������е�ҩƷ����id
			String FirstId = secondLevelCategoryList.getJSONObject(i).getString("supId").toString();
			
			// SecondId Ϊ��ҳ�ұ����е�ҩƷ�������id, ���ڻ�ȡ�ұ����ӵ� url, �� SecondUrl
			String SecondId = secondLevelCategoryList.getJSONObject(i).getString("id").toString();
			String SecondUrl = "https://drugs.dxy.cn/category/" + SecondId;
			
			// ����ҩƷ����� Id, ����ȷ���ұߵ�ҩƷ��������������һ��һ������
			for(int j=0;j<FirstLevelSize;j++) {
				if(FirstLevelId[j].equals(FirstId)) {
					FirstName = FirstLevelName[j];
					break;
				}
			}
			
			// HttpClient �����ȡ��ҳ html
    		HttpGet get_c = new HttpGet(SecondUrl);
    		CloseableHttpResponse response_c = client.execute(get_c);
    		content = "";
    		if(response_c.getStatusLine().getStatusCode()==200){
    			content = EntityUtils.toString(response_c.getEntity(), "UTF-8");
    		}
    		
    		// ��ǰ���ȡ���� html �ı�ת��ΪDocument����
    		doc = Jsoup.parse(content);
    		
    		// ��ȡ��ǰҳ��� PageSize
    		String tmpstring = doc.select("script[id=__NEXT_DATA__]").toString();
    		int tmp1 = tmpstring.indexOf("pageSize");
    		int tmp2 = tmpstring.indexOf("total");
    		int PageSize = Integer.valueOf(tmpstring.substring(tmp1+10, tmp2-2));
    		
    		// ������ǰҳ���������Ч Page
    		for(int j = 0;j < PageSize;j++) {
    			// HttpClient �����ȡ��ҳ html
        		HttpGet get_b = new HttpGet(SecondUrl + "?page=" + j);
        		CloseableHttpResponse response_b = client.execute(get_b);
        		content = "";
        		if(response_b.getStatusLine().getStatusCode()==200){
        			content = EntityUtils.toString(response_b.getEntity(), "UTF-8");
        		}
        		
        		// ��ǰ���ȡ���� html �ı�ת��ΪDocument����
        		doc = Jsoup.parse(content);
        		
        		// ѡ���� /drug ��ͷ�����ӵ� Elements
    			Elements DrugsElements = doc.select("a[href^=/drug]");
    			//System.out.println(DrugsElements);
    			
    			// �����������ϻ�õ� Elements
    			int NumberElements = DrugsElements.size();
    			for(int k = 0;k < NumberElements;k++) {
    				
    				// ��ȡ����ҩƷ���ӵ�ַ
    				String ElementTmp = DrugsElements.get(k).toString();
    				int elementtmp1 = ElementTmp.indexOf("\"");
    				int elementtmp2 = ElementTmp.lastIndexOf("\"");
    				ElementTmp = ElementTmp.substring(elementtmp1+1, elementtmp2);
    				ElementTmp = "https://drugs.dxy.cn" + ElementTmp;
    				System.out.println(ElementTmp);
    				
    				// HttpClient �����ȡ��ҳ html
            		HttpGet get_a = new HttpGet(ElementTmp);
            		CloseableHttpResponse response_a = client.execute(get_a);
            		content = "";
            		if(response_a.getStatusLine().getStatusCode() == 200){
            			content = EntityUtils.toString(response_a.getEntity(), "UTF-8");
            		}
            		
            		// ��ǰ���ȡ���� html �ı�ת��ΪDocument����
            		doc = Jsoup.parse(content);
            		
            		// ��ȡ�ı�
        			String OneOfDrugs = doc.text();
        			//System.out.println(doc);
        			//System.out.println("------------------------------------------------------------");
        			
        			// ���ҩƷ�ı�Ҫ����
        			// ÿ�����ע�͵�����Ϊ������ǵĳ���
        			int MedicineNameIndex1 = OneOfDrugs.indexOf("ͨ������");                           // + 5 
        			int MedicineNameIndex2 = OneOfDrugs.indexOf("Ӣ������");                           // + 5 
        			int MedicineNameIndex3 = OneOfDrugs.indexOf("��Ʒ����");                           // + 5 
        			int MedicineComponentIndex = OneOfDrugs.indexOf("���ɷݡ�");                       // + 5
        			int MedicineIndicationIndex = OneOfDrugs.indexOf("����Ӧ֢��");                    // + 6
        			int MedicineUsageIndex = OneOfDrugs.indexOf("���÷�������");                       // + 7
        			int MedicinePrecautionsIndex = OneOfDrugs.indexOf("��ע�����");                 // + 7
        			int MedicineContraindicationsIndex = OneOfDrugs.indexOf("�����ɡ�");               // + 5
        			int MedicineGravidaIndex = OneOfDrugs.indexOf("���и��������ڸ�Ů��ҩ��");          // + 13
        			int MedicinePharamacologicalActionIndex = OneOfDrugs.indexOf("��ҩ�����á�");      // + 7
        			int MedicinePharmacokineticsIndex = OneOfDrugs.indexOf("��ҩ������ѧ��");          // + 8
        			int MedicineChemicalCompositionIndex = OneOfDrugs.indexOf("����ѧ�ɷݡ�");         // + 7
        			int MedicineOTCIndex = OneOfDrugs.indexOf("���Ƿ�OTC��");                          // + 8
        			
        			// ��ȡ�������Ե�����
        			// ��ȡͨ������
        			String MedicineNameString1 = "";
        			if(MedicineNameIndex1 >= 0) {
        				MedicineNameString1 = OneOfDrugs.substring(MedicineNameIndex1 + 5);
        				if(MedicineNameIndex2 >= 0) {
        					MedicineNameString1 = MedicineNameString1.substring(0,MedicineNameIndex2 - MedicineNameIndex1 - 5);
        				}else if(MedicineNameIndex3 >= 0) {
        					MedicineNameString1 = MedicineNameString1.substring(0,MedicineNameIndex3);
        				}else {
        					int NameIndexTmp = MedicineNameString1.indexOf("��");
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("ҩƷ��Ѷ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("ͬ����ҩƷ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf("��");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.indexOf(" ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString1.length() / 2;
            				}
        					MedicineNameString1 = MedicineNameString1.substring(0,NameIndexTmp);
        				}
        				MedicineNameString1 = MedicineNameString1.replace('\n', '��');
        			}
        			
        			// ��ȡӢ������
        			String MedicineNameString2 = "";
        			if(MedicineNameIndex2 >= 0) {
        				MedicineNameString2 = OneOfDrugs.substring(MedicineNameIndex2 + 5);
        				if(MedicineNameIndex3 >= 0) {
        					MedicineNameString2 = MedicineNameString2.substring(0,MedicineNameIndex3 - MedicineNameIndex2 - 5);
        				}else {
        					int NameIndexTmp = MedicineNameString2.indexOf("��");
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("ҩƷ��Ѷ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("ͬ����ҩƷ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf("��");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.indexOf(" ");
            				}
        					if(NameIndexTmp < 0) {
            					NameIndexTmp = MedicineNameString2.length() / 2;
            				}
        					MedicineNameString2 = MedicineNameString2.substring(0,NameIndexTmp);
        				}
        				MedicineNameString2 = MedicineNameString2.replace('\n', '��');
        			}
        			
        			// ��ȡ��Ʒ����
        			String MedicineNameString3 = "";
        			if(MedicineNameIndex3 >= 0) {
        				MedicineNameString3 = OneOfDrugs.substring(MedicineNameIndex3 + 5);
        				int NameIndexTmp = MedicineNameString3.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineNameString3.length() / 2;
        				}
        				MedicineNameString3 = MedicineNameString3.substring(0,NameIndexTmp);
        				MedicineNameString3 = MedicineNameString3.replace('\n', '��');
        			}
        			
        			// ��ȡ�ɷ�
        			String MedicineComponentString = "";
        			if(MedicineComponentIndex >= 0) {
        				MedicineComponentString = OneOfDrugs.substring(MedicineComponentIndex + 5);
        				int NameIndexTmp = MedicineComponentString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineComponentString.length() / 2;
        				}
        				MedicineComponentString = MedicineComponentString.substring(0,NameIndexTmp);
        				MedicineComponentString = MedicineComponentString.replace('\n', '��');
        			}
        			
        			// ��ȡ��Ӧ֢
        			String MedicineIndicationString = "";
        			if(MedicineIndicationIndex >= 0) {
        				MedicineIndicationString = OneOfDrugs.substring(MedicineIndicationIndex + 6);
        				int NameIndexTmp = MedicineIndicationString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineIndicationString.length() / 2;
        				}
        				MedicineIndicationString = MedicineIndicationString.substring(0,NameIndexTmp);
        				MedicineIndicationString = MedicineIndicationString.replace('\n', '��');
        			}
        			
        			// ��ȡ�÷�����
        			String MedicineUsageString = "";
        			if(MedicineUsageIndex >= 0) {
        				MedicineUsageString = OneOfDrugs.substring(MedicineUsageIndex + 7);
        				int NameIndexTmp = MedicineUsageString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineUsageString.length() / 2;
        				}
        				MedicineUsageString = MedicineUsageString.substring(0,NameIndexTmp);
        				MedicineUsageString = MedicineUsageString.replace('\n', '��');
        			}
        			
        			// ��ȡע������
        			String MedicinePrecautionsString = "";
        			if(MedicinePrecautionsIndex >= 0) {
        				MedicinePrecautionsString = OneOfDrugs.substring(MedicinePrecautionsIndex + 7);
        				int NameIndexTmp = MedicinePrecautionsString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePrecautionsString.length() / 2;
        				}
        				MedicinePrecautionsString = MedicinePrecautionsString.substring(0,NameIndexTmp);
        				MedicinePrecautionsString = MedicinePrecautionsString.replace('\n', '��');
        			}
        			
        			// ��ȡ��ҩ����
        			String MedicineContraindicationString = "";
        			if(MedicineContraindicationsIndex >= 0) {
        				MedicineContraindicationString = OneOfDrugs.substring(MedicineContraindicationsIndex + 5);
        				int NameIndexTmp = MedicineContraindicationString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineContraindicationString.length() / 2;
        				}
        				MedicineContraindicationString = MedicineContraindicationString.substring(0,NameIndexTmp);
        				MedicineContraindicationString = MedicineContraindicationString.replace('\n', '��');
        			}
        			
        			// ��ȡ�и��������ڸ�Ů��ҩ
        			String MedicineGravidaString = "";
        			if(MedicineGravidaIndex >= 0) {
        				MedicineGravidaString = OneOfDrugs.substring(MedicineGravidaIndex + 13);
        				int NameIndexTmp = MedicineGravidaString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineGravidaString.length() / 2;
        				}
        				MedicineGravidaString = MedicineGravidaString.substring(0,NameIndexTmp);
        				MedicineGravidaString = MedicineGravidaString.replace('\n', '��');
        			}
        			
        			// ��ȡҩ������
        			String MedicinePharmacologicalActionString = "";
        			if(MedicinePharamacologicalActionIndex >= 0) {
        				MedicinePharmacologicalActionString = OneOfDrugs.substring(MedicinePharamacologicalActionIndex + 7);
        				int NameIndexTmp = MedicinePharmacologicalActionString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacologicalActionString.length() / 2;
        				}
        				MedicinePharmacologicalActionString = MedicinePharmacologicalActionString.substring(0,NameIndexTmp);
        				MedicinePharmacologicalActionString = MedicinePharmacologicalActionString.replace('\n', '��');
        			}
        			
        			// ��ȡҩ������ѧ
        			String MedicinePharmacokineticsString = "";
        			if(MedicinePharmacokineticsIndex >= 0) {
        				MedicinePharmacokineticsString = OneOfDrugs.substring(MedicinePharmacokineticsIndex + 8);
        				int NameIndexTmp = MedicinePharmacokineticsString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicinePharmacokineticsString.length() / 2;
        				}
        				MedicinePharmacokineticsString = MedicinePharmacokineticsString.substring(0,NameIndexTmp);
        				MedicinePharmacokineticsString = MedicinePharmacokineticsString.replace('\n', '��');
        			}
        			
        			// ��ȡ��ѧ�ɷ�
        			String MedicineChemicalCompositionString = "";
        			if(MedicineChemicalCompositionIndex >= 0) {
        				MedicineChemicalCompositionString = OneOfDrugs.substring(MedicineChemicalCompositionIndex + 7);
        				int NameIndexTmp = MedicineChemicalCompositionString.indexOf("��");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("ҩƷ��Ѷ");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.indexOf(" ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineChemicalCompositionString.length() / 2;
        				}
        				MedicineChemicalCompositionString = MedicineChemicalCompositionString.substring(0,NameIndexTmp);
        				MedicineChemicalCompositionString = MedicineChemicalCompositionString.replace('\n', '��');
        			}
        			
        			// ��ȡ OTC ��Ϣ
        			String MedicineOTCString = "";
        			if(MedicineOTCIndex >= 0) {
        				MedicineOTCString = OneOfDrugs.substring(MedicineOTCIndex + 8);
        				int NameIndexTmp = MedicineOTCString.indexOf(" ");
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("��");
        				}
        				if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("ҩƷ��Ѷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("ͬ����ҩƷ");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = MedicineOTCString.indexOf("��");
        				}
    					if(NameIndexTmp < 0) {
        					NameIndexTmp = 0;
        				}
        				MedicineOTCString = MedicineOTCString.substring(0,NameIndexTmp);
        				MedicineOTCString = MedicineOTCString.replace('\n', '��');
        			}
        			
        			// ��ҩƷ��׼ȷ��ҳ��ַ
        			String MedicineAddressString = ElementTmp;
        			
        			// ���������һ���ļ���д��
        			// Ĭ��д��D��, ÿ��ҩƷ���Ϊһ��.txt�ļ�, �� 14 ��, �ļ���ΪҩƷ������
        			String FilePath = "D:\\" + FirstName + ".txt";
        			File file = new File(FilePath);
        			// ���ļ��������򴴽��ļ�
        			if(!file.exists()) {
        				file.createNewFile();
        			}
        			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
        			BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
        			
        			// д���ļ�
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
		//�ر�����
		client.close();
		long endTime =  System.currentTimeMillis();
		long usedTime = (endTime-startTime)/1000;
		
		System.out.print(usedTime);
	}
}
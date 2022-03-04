import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class Medicine {
	// 名称
	String ChineseName;
	String EnglishName;
	String DrugName;
	
	// 成份
	String Component;
	// 适应症
	String Indication;
	// 用法用量
	String Usage;
	// 注意事项
	String Precautions;
	
	// 禁忌
	String Contraindication;
	
	// 孕妇
	String Gravida;
	
	// 药理作用
	String PharmacologicalAction;
	
	// 药代动力学
	String Pharmacokinetics;
	
	// 化学成分
	String ChemicalComposition;
	
	// 是否OTC
	String OTC;
	
	// 具体网页地址
	String Address;
	
	// 打印信息
	String PrintMedicine;
	
	// 药品类别
	String CategoryName;
	
	Medicine(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String o){
		ChineseName = a;
		if(a.equals("")) {
			ChineseName = "暂无信息";
		}
		EnglishName = b;
		if(b.equals("")) {
			EnglishName = "暂无信息";
		}
		DrugName = c;
		if(c.equals("")) {
			DrugName = "暂无信息";
		}
		Component = d;
		if(d.equals("")) {
			Component = "暂无信息";
		}
		Indication = e;
		if(e.equals("")) {
			Indication = "暂无信息";
		}
		Usage = f;
		if(f.equals("")) {
			Usage = "暂无信息";
		}
		Precautions = g;
		if(g.equals("")) {
			Precautions = "暂无信息";
		}
		Contraindication = h;
		if(h.equals("")) {
			Contraindication = "暂无信息";
		}
		Gravida = i;
		if(i.equals("")) {
			Gravida = "暂无信息";
		}
		PharmacologicalAction = j;
		if(j.equals("")) {
			PharmacologicalAction = "暂无信息";
		}
		Pharmacokinetics = k;
		if(k.equals("")) {
			Pharmacokinetics = "暂无信息";
		}
		ChemicalComposition = l;
		if(l.equals("")) {
			ChemicalComposition = "暂无信息";
		}
		OTC = m;
		if(m.equals("")) {
			OTC = "暂无信息";
		}
		Address = n;
		if(n.equals("")) {
			Address = "暂无信息";
		}
		CategoryName = o;
		if(o.equals("")) {
			CategoryName = "暂无信息";
		}
		PrintMedicine = "--------------------------------------------------------\n"
						+ "【药品类别】\n" + CategoryName + "\n"
						+ "【药品名称】\n通用名称：" + ChineseName + "\n英文名称：" + EnglishName + "\n商品名称：" + DrugName + "\n"
						+ "【成份】\n" + Component + "\n"
						+ "【适应症】\n" + Indication + "\n"
						+ "【用法用量】\n" + Usage + "\n"
						+ "【禁忌】\n" + Precautions + "\n"
						+ "【注意事项】\n" + Contraindication + "\n"
						+ "【孕妇及脯乳期妇女用药】\n" + Gravida + "\n"
						+ "【药理作用】\n" + PharmacologicalAction + "\n"
						+ "【药代动力学】\n" + Pharmacokinetics + "\n"
						+ "【化学成份】\n" + ChemicalComposition + "\n"
						+ "【是否OTC】\n" + OTC + "\n"
						+ "【丁香园用药助手链接】\n" + Address + "\n"
						+ "--------------------------------------------------------\n";
	}
	
	Document toDocument(String Category) {
		
		Document doc = new Document();
		
		doc.add(new TextField("category",Category,Field.Store.YES));
		doc.add(new TextField("name",ChineseName + " " + EnglishName + " " + DrugName, Field.Store.YES));
		doc.add(new TextField("component",Component,Field.Store.YES));
		doc.add(new TextField("indication",Indication,Field.Store.YES));
		doc.add(new TextField("print",PrintMedicine,Field.Store.YES));
		
		return doc;
	}
	
}


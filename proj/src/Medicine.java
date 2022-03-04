import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class Medicine {
	// ����
	String ChineseName;
	String EnglishName;
	String DrugName;
	
	// �ɷ�
	String Component;
	// ��Ӧ֢
	String Indication;
	// �÷�����
	String Usage;
	// ע������
	String Precautions;
	
	// ����
	String Contraindication;
	
	// �и�
	String Gravida;
	
	// ҩ������
	String PharmacologicalAction;
	
	// ҩ������ѧ
	String Pharmacokinetics;
	
	// ��ѧ�ɷ�
	String ChemicalComposition;
	
	// �Ƿ�OTC
	String OTC;
	
	// ������ҳ��ַ
	String Address;
	
	// ��ӡ��Ϣ
	String PrintMedicine;
	
	// ҩƷ���
	String CategoryName;
	
	Medicine(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String o){
		ChineseName = a;
		if(a.equals("")) {
			ChineseName = "������Ϣ";
		}
		EnglishName = b;
		if(b.equals("")) {
			EnglishName = "������Ϣ";
		}
		DrugName = c;
		if(c.equals("")) {
			DrugName = "������Ϣ";
		}
		Component = d;
		if(d.equals("")) {
			Component = "������Ϣ";
		}
		Indication = e;
		if(e.equals("")) {
			Indication = "������Ϣ";
		}
		Usage = f;
		if(f.equals("")) {
			Usage = "������Ϣ";
		}
		Precautions = g;
		if(g.equals("")) {
			Precautions = "������Ϣ";
		}
		Contraindication = h;
		if(h.equals("")) {
			Contraindication = "������Ϣ";
		}
		Gravida = i;
		if(i.equals("")) {
			Gravida = "������Ϣ";
		}
		PharmacologicalAction = j;
		if(j.equals("")) {
			PharmacologicalAction = "������Ϣ";
		}
		Pharmacokinetics = k;
		if(k.equals("")) {
			Pharmacokinetics = "������Ϣ";
		}
		ChemicalComposition = l;
		if(l.equals("")) {
			ChemicalComposition = "������Ϣ";
		}
		OTC = m;
		if(m.equals("")) {
			OTC = "������Ϣ";
		}
		Address = n;
		if(n.equals("")) {
			Address = "������Ϣ";
		}
		CategoryName = o;
		if(o.equals("")) {
			CategoryName = "������Ϣ";
		}
		PrintMedicine = "--------------------------------------------------------\n"
						+ "��ҩƷ���\n" + CategoryName + "\n"
						+ "��ҩƷ���ơ�\nͨ�����ƣ�" + ChineseName + "\nӢ�����ƣ�" + EnglishName + "\n��Ʒ���ƣ�" + DrugName + "\n"
						+ "���ɷݡ�\n" + Component + "\n"
						+ "����Ӧ֢��\n" + Indication + "\n"
						+ "���÷�������\n" + Usage + "\n"
						+ "�����ɡ�\n" + Precautions + "\n"
						+ "��ע�����\n" + Contraindication + "\n"
						+ "���и��������ڸ�Ů��ҩ��\n" + Gravida + "\n"
						+ "��ҩ�����á�\n" + PharmacologicalAction + "\n"
						+ "��ҩ������ѧ��\n" + Pharmacokinetics + "\n"
						+ "����ѧ�ɷݡ�\n" + ChemicalComposition + "\n"
						+ "���Ƿ�OTC��\n" + OTC + "\n"
						+ "������԰��ҩ�������ӡ�\n" + Address + "\n"
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


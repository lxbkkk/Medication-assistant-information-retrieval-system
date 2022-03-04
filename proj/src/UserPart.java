import java.util.Scanner;

public class UserPart {
	public static Scanner sn = new Scanner(System.in);
	public static void main(String[] args) {
		while(true) {
	        System.out.println("  ▄ ▄     ▄▀▀▀▄▄▄▄▄▄▄▀▀▀▄  ▄█▄  █   █  ▄ ");
	        System.out.println(" ▀███▀    █▒▒░░░░░░░░░▒▒█   ▀   █▄▄▄█  ▄ ");
	        System.out.println("   ▀  ▄▄  ▄▀░░█░░░░░█░░▀▄  ▄▄   █   █  █ ");
	        System.out.println("     █░░█ ▀▄░░  ▀█▀░░░ ▄▀ █░░█           ");
	        System.out.println("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
			System.out.println("Welcome to kk的丁香园用药助手的药品搬运作业");
			System.out.println("现在你可以选择是否要指定药品的类别,也可以不指定");
			System.out.println("如果你想要指定药品的类别,请按照下文指示输入合法的数字");
			System.out.println("0 - 非性激素和胰岛素的激素类系统用药");
			System.out.println("1 - 感觉器官用药");
			System.out.println("2 - 呼吸器官用药");
			System.out.println("3 - 肌肉-骨骼系统用药");
			System.out.println("4 - 抗寄生虫草、杀虫药和驱虫药");
			System.out.println("5 - 抗肿瘤药和免疫机能调节药");
			System.out.println("6 - 皮肤病用药");
			System.out.println("7 - 神经系统用药");
			System.out.println("8 - 生殖泌尿系统和性激素用药");
			System.out.println("9 - 系统用抗感染药");
			System.out.println("10 - 消化道及代谢用药");
			System.out.println("11 - 心血管系统用药");
			System.out.println("12 - 血液和造血器官用药");
			System.out.println("13 - 不指定药品类别,进行全局搜索");
			System.out.println("14 - 退出程序");
			
			Integer CategoryType = Integer.parseInt(sn.nextLine());
			while(CategoryType < 0 || CategoryType > 14) {
				System.out.println("!!请输入合法的数字(在 0 ~ 14 之间)");
				CategoryType = Integer.parseInt(sn.nextLine());
			}
			if(CategoryType.equals(14)) {
				break;
			}
			
			System.out.println("现在你可以选择用药品名称/成份/适应症的关键词来搜索药品,请按照下文指示输入合法的数字:");
			System.out.println("0 - 药品名称");
			System.out.println("1 - 成份");
			System.out.println("2 - 适应症");
			System.out.println("3 - 退出程序");
			Integer SearchType = Integer.parseInt(sn.nextLine());
			while(SearchType < 0 || SearchType > 3) {
				System.out.println("!!请输入合法的数字(在 0 ~ 3 之间)");
				SearchType = Integer.parseInt(sn.nextLine());
			}
			if(SearchType.equals(3)) {
				break;
			}
			System.out.println("现在你可以输入想要搜索的关键词:");
			System.out.println("同时你也可以输入 “886” 以退出程序");
			String SearchKey = sn.nextLine();
			SearchKey = SearchKey.replaceAll("\n", "");
			
			if(SearchKey.equals("886")) {
				break;
			}
			
			LucenePart lucenePart = new LucenePart();
			lucenePart.searchIndexOuter(CategoryType, SearchType, SearchKey);
			
			System.out.println("输入回车以继续");
			sn.nextLine();
		}
		sn.close();
		System.out.println("------ Byebye -----");
		System.exit(0);
	}
}

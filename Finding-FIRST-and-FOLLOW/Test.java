public class Test 
{
	public static void main(String[] args)
	{
		String filePath="";
		if(args.length>=1)
			filePath = args[0];//ignoring rest of arguments
		else
			filePath = "./grammar1.txt";
		First_Follow f = new First_Follow(filePath);
		f.firstOfVariables();
		f.followOfVariable();
		f.printFirst();
		f.printFollow();
	}
}
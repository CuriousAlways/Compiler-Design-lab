import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;


public class LeaderAndBasicBlock
{
	private List<Integer> leader;
	private int lastLineNo;
	private String inputFile;
	

	public LeaderAndBasicBlock(String filePath)
	{
		inputFile=filePath;
		leader = new ArrayList<Integer>();
		readAndFindLeaders(filePath);
		leaderArrange();
	}

	public void leaderArrange()
	{
		Integer[] leaderArr = leader.toArray(new Integer[leader.size()]);
		Arrays.sort(leaderArr);
		leader = Arrays.asList(leaderArr); 
	}


	/*****************************************************************
	Description: Prints label of Leader lines
	*****************************************************************/
	public void printLeader()
	{
		System.out.println("--------  Leader Statements --------");
		for(Integer lineNo : leader)
			printLines(lineNo,1);
		System.out.println();
	}


	/***************************************************************************
	Description: Using leader statement it determines basic block and prints 
				 label of starting and ending line consisting of block.In case of
				 single line block only label of that line is printed 
	****************************************************************************/
	public void printBasicBlock()
	{
		System.out.println("-------- Basic Block ---------\n\n");
		int prev = 1;
		int blockNo = 1;
		for(int i=1;i<leader.size();i++)
		{
			int current = leader.get(i);
			if(current-1 == prev)
				{
					System.out.println("Block"+blockNo+":");
					printLines(prev,1);

				}
			else
				{
					System.out.println("Block"+blockNo+":");
					printLines(prev,current-prev);
				}
			prev = current;
			blockNo++;
		}

		if(lastLineNo==prev)
			{
				System.out.println("Block"+blockNo+":");
				printLines(prev,1);
			}
		else
		{
			System.out.println("Block"+blockNo+":");
			printLines(prev,lastLineNo-prev+1);
		}
	}

	/******************************************************
	Description : Prints appropriate line from input file
	******************************************************/
	private void printLines(int startLine,int noOfline)
	{
		Scanner input = null;
		try
		{
			input = new Scanner(new File(inputFile));
		}
		catch(FileNotFoundException e)
		{
			System.out.println("ERROR!!!!!!  FILE : "+inputFile+"Could not be found");
		}	

		int l=1;
		while(input.hasNext() && l<startLine)
		{
			input.nextLine();
			l++;
		}
		l=1;
		while(input.hasNext() && l<=noOfline)
		{
			System.out.println(input.nextLine());
			l++;
		}

		System.out.println();

	}

	/*****************************************************************************
	Descripton : Reads 3-address code from file and determines leader statements.
			   It is assumed that goto is only jump statement supported.
			   format: goto(lineNo)
			   lineNo in file is used as label
	******************************************************************************/
	private void readAndFindLeaders(String filePath)
	{
		Scanner input = null;
		try
		{
			input = new Scanner(new File(filePath));
		}
		catch(FileNotFoundException e)
		{
			System.out.println("ERROR!!!!!!  FILE : "+filePath+"Could not be found");
		}	

		int i=1;
		boolean prevJump=true;
		while(input.hasNext())
		{
			String line = input.nextLine();
			if(prevJump)
			{
				leader.add(i);
			}

			if(line.contains("goto"))
			{
				int j= line.indexOf("goto")+4;
				String l ="";
				while(line.charAt(j)!=')')
				{
					char ch = line.charAt(j);
					if(ch>='0'&&ch<='9')
						l=l+ch;
					j++;
				}

				int ln = Integer.parseInt(l);
				leader.add(ln);
				prevJump=true;
			}
			else
				prevJump=false;

			i++;
		}

		lastLineNo=i-1;
	}


}
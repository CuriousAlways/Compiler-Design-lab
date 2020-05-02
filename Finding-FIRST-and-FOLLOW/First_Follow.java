import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;



public class First_Follow
{
	private int nterminal ;
	private ReadGrammar g;
	private boolean[] nullable;
	private ArrayList<HashSet<Character>> FIRST;//list of set of FIRST
	private ArrayList<HashSet<Character>> FOLLOW;//list of set of FOLLOW
	private char start_symbol;


	public First_Follow(String filePath)
	{
		g = new ReadGrammar(filePath);
		printGrammar(g);

		nterminal = g.symbolTable.keySet().size(); //number of non-termials
		nullable = new boolean[nterminal];

		//initializing FIRST list
		FIRST = new ArrayList<HashSet<Character>>(nterminal);
		for(int i=0;i<nterminal;i++)
		{
			FIRST.add(new HashSet<Character>());
		} 

		//initializing FOLLOW list
		FOLLOW = new ArrayList<HashSet<Character>>();
		for(int i=0;i<nterminal;i++)
		{
			FOLLOW.add(new HashSet<Character>());
		}

		//determine if variables could produce empty string 
		for(Character variable : g.symbolTable.keySet())
		{
			int index= g.symbolTable.get(variable);
			if(nullable[index])//already determined to be true
				continue;
			isNullable(variable); //find symbols that could produce empty string
		}

		//find start_symbol
		for(Character ch : g.symbolTable.keySet())
		{
			if(g.symbolTable.get(ch)==0)
			{
				start_symbol = ch;
				break;
			}
		}
	}


	/************************************************************************* 
	description : function checks if variable can generate null(empty string)
	returns : true if can generate empty string else false
	**************************************************************************/
	private boolean isNullable(char variable)
	{
		if((variable<'A' || variable>'Z') && variable!='~')
			return false;
		if(variable=='~') // ~ symbol is used to denote epsilon or empty string
			return true;

		if(!g.symbolTable.containsKey(variable))
		{
			System.out.println("ERROR!!!!!! Grammar doesnot contains variable : "+variable);
			System.exit(0);
		}
		int index = g.symbolTable.get(variable);
		boolean status = false; 
		for(String prod : g.productions.get(index))//get the productions for the variable
		{	
			boolean intermidiateStatus = true; //Assuming production leads to empty string and this might change later on inside the loop
			for(int i=0;i<prod.length();i++)
			{
				char derived = prod.charAt(i);
				if(derived==variable )//recursive production then one of its production has to be ~ or some non-terminal
					{
						intermidiateStatus=false;
						break;
					}
				intermidiateStatus = intermidiateStatus && isNullable(derived); //RECURSIVE CALL
				if(derived<'A'||derived>'Z')//this is just an optimization to terminate further iteration if a non-terminal symbol is found
					break;
			}

			status = status || intermidiateStatus; //status is used to store nullability of each of it's production while intermidiateStatus is specific to one of its production 
		}

		nullable[index] = status; //storing the result

		return status;
	}



	/****************************************************************************
	description : find FIRST of each variable and put it into corrosponding set 
	*****************************************************************************/
	public void firstOfVariables()
	{
		for(Character symbol : g.symbolTable.keySet())
		{
			firstOf(symbol);
		}

	}
	private void firstOf(char symbol)
	{
		int index = g.symbolTable.get(symbol);

		if(!FIRST.get(index).isEmpty())//first already found
			return;

		ArrayList<String> prod = g.productions.get(index);
		for(String p : prod)
		{
			boolean continue_finding = true;
			for(int j=0;j<p.length();j++)
			{
				char current_symbol= p.charAt(j);

				if(continue_finding)
				{
					if(current_symbol<'A'||current_symbol>'Z')
					{
						FIRST.get(index).add(current_symbol);
						continue_finding=false;
					}
					else
					{
						firstOf(current_symbol);//find first of the given character
						int i = g.symbolTable.get(current_symbol);
						union(FIRST.get(index),FIRST.get(i));
						if(!nullable[i])
							continue_finding=false;

					}
					
				}
				else 
					break;
			}
		}

		if(!nullable[index])
		{
			FIRST.get(index).remove('~');
		}

	}
	/*****************************************************************************
	description : it perform union of set s1 and s2 and store result back to set1
	******************************************************************************/
	private void union(Set<Character> s1,Set<Character>s2)
	{
		for(Character ch : s2)
			s1.add(ch);
	}


	/***********************************************************************
	description : find FOLLOW of variable and store it in corrosponding set
	************************************************************************/
	public void followOfVariable()
	{
		FOLLOW.get(0).add('$');//start symbol would always have atleast $ as its follow
		for(Character symbol : g.symbolTable.keySet())
		{
			followOf(symbol);
		}
	}
	private void followOf(char symbol)
	{
		int index = g.symbolTable.get(symbol);
		Set<Character> variable = g.symbolTable.keySet();
		for(Character ch : variable)
		{
			int i = g.symbolTable.get(ch);
			ArrayList<String> prodSet = g.productions.get(i);
			for(String prod : prodSet)
			{
				int j = prod.indexOf(symbol);
				if(j==-1)
					continue;
				boolean continue_finding=true;
				for(int k=j+1;k<prod.length();k++)
				{
					char s = prod.charAt(k);
					if((s<'A'||s>'Z')&& s!='~')
					{
						FOLLOW.get(index).add(s);
						continue_finding=false;
						break;
					}
					else
					{
						int m = g.symbolTable.get(s);
						union(FOLLOW.get(index),FIRST.get(m));
						if(!nullable[m])
						{
							continue_finding=false;
							break;
						}
					}
				}
				if(continue_finding)
				{
					if(ch!=symbol)
						followOf(ch);
					union(FOLLOW.get(index),FOLLOW.get(i));
				}
			}

		}

		FOLLOW.get(index).remove('~');//We need to remove ~(represents epsilon) as it may be included in follow set because of union with FIRST of some variables

	}


	/***** Print Functions *****/
	public void printFirst()
	{
		System.out.println("----------------- FIRST -------------------");
		for(Character ch : g.symbolTable.keySet())
		{
			int index = g.symbolTable.get(ch);
			System.out.print(ch+" : ");
			for(Character f : FIRST.get(index))
				System.out.print(f+ " ");
			System.out.println();
		}
	}

	public void printFollow()
	{
		System.out.println("----------------- FOLLOW -------------------");
		for(Character ch : g.symbolTable.keySet())
		{
			int index = g.symbolTable.get(ch);
			System.out.print(ch+" : ");
			for(Character f : FOLLOW.get(index))
				System.out.print(f+ " ");
			System.out.println();
		}
	}

	private static void printGrammar(ReadGrammar r)
	{
		Set<Character> non_terminals = r.symbolTable.keySet();
		System.out.println("---------------- GRAMMAR -----------------");
		for(Character nt : non_terminals)
		{
			System.out.print(nt+"-> ");
			int index = r.symbolTable.get(nt);
			ArrayList<String> productions = r.productions.get(index);
			for(int i=0;i<productions.size();i++)
			{
				if(i==0)
					System.out.print(productions.get(i));
				else
					System.out.print(" | "+productions.get(i));
			}
			System.out.println();
		}
	}

}


/********************************************************************************************************
description : This class provides method to read,parse and store grammar into appropriate data structue.  
*********************************************************************************************************/
class ReadGrammar 
{
	HashMap<Character,Integer> symbolTable;
	ArrayList<ArrayList<String>> productions;

	public ReadGrammar(String filePath)
	{	
		symbolTable = new HashMap<Character,Integer>();
		productions = new ArrayList<ArrayList<String>>();

		File f = new File(filePath);
		Scanner input=null;
		
		try
		{
			input = new Scanner(f); //creating a Scanner object to read file containing grammar
		}
		catch(FileNotFoundException e)
		{
			System.out.println("FILE "+filePath+" NOT FOUND");
			System.exit(0);
		}

		int i=0;
		while(input.hasNext())
		{
			String nextLine = input.nextLine();
			char firstChar = nextLine.charAt(0);

			boolean contains = false; 
			if(!symbolTable.containsKey(firstChar))
			{
				symbolTable.put(firstChar,i++);//A non-terminal is added to the symbol table
			}
			else
				contains=true;


			nextLine = nextLine.substring(nextLine.indexOf('>')+1,nextLine.length());
			nextLine = nextLine.trim();

			if(contains)//if non-terminal was already present then add to its existing production list
			{
				ArrayList<String> arr = productions.get(symbolTable.get(firstChar));
				for(String prod:nextLine.split("[|]"))//create list of all the production from currently read symbol
				{
					prod = prod.trim();
					arr.add(prod);
				}

			}
			else
			{
				ArrayList<String> arr = new ArrayList<String>();
			
				for(String prod:nextLine.split("[|]"))//create list of all the production from currently read symbol
				{
					prod = prod.trim();
					arr.add(prod);
				}

				productions.add(arr);//add the list to production list of whole grammar
			}
		}
	}
}


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
// fuzzy search package
import me.xdrop.fuzzywuzzy.*;
import me.xdrop.fuzzywuzzy.model.*;


public class RecipeBook  
    {
	public static ArrayList<Recipe> recipe_book = new ArrayList<Recipe>();
    public static void main(String[] args) throws Exception  
        {
    	//String filename = "./recipebook.json";
        
        // !!!!!!!!! It seems that the path is different if you are running this on Eclipse or just with command line
        // so be careful. use ../recipebook.json if you run the program with run.sh
        read_json("../recipebook.json"); //reads recipebook.json and builds recipebook 
        int recipeIndex = 1000; //used to indicate which recipe is currently being read
        int currStep = 0; //used to indicate which step is currently being read
        


        int updateID = 0;
        String updateField = null;
        String updateData = null;
        // Recipe rp = new Recipe();
        // rp.setId(6);
        // rp.setDescription("test_description2");
        // rp.setIngredients(new String [] {"1", "2", "3"});
        // rp.setInstructions(new String [] {"4", "5", "6"});
        // rp.printAll();
        // addRecipe(rp, "../recipebook.json");
        
        // i/o
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Chocolate Java Cake's Recipe Book! Type 'h' or 'help' for a list of commands");
        
        while(true) 
            {
        	String s = in.nextLine();
        	
        	//help
        	if(s.equals("h") || s.equals("help")) 
            {
        		System.out.println("'b' or 'browse' to browse all recipes");
        		System.out.println("'s' or 'search' to search for a recipe");
                System.out.println("'u' or 'update' to update a recipe");
        	}

            if(s.equals("u") || s.equals("update")) 
            {
                System.out.println("What is the index of the recipe you would like to update?");
                Scanner inputInt = new Scanner(System.in);
                updateID =  inputInt.nextInt();
                // recipe_book.get(updateID);

                System.out.println("Is the field you want to update a string or array? Enter 'str' for string or 'arr' for array.");
                String field1 = in.nextLine();

                if (field1.equals("str")) 
                {

                    System.out.println("Do you want to update the name or description?");
                    String field2 = in.nextLine();
                    updateField = field2;

                    System.out.println("Please enter the data you would like to update in this field");
                    String field3 = in.nextLine();
                    updateData = field3;

                    updateRecipeStr(updateField,updateData,recipe_book.get(updateID)); //for updating string field of recipe
                
                }
                else if (s.equals("arr")) 
                {
                    System.out.println("Do you want to update the name or description?");
                    String field2 = in.nextLine();
                    updateField = field2;

                    System.out.println("Please enter the data you would like to update in this field");
                    String field3 = in.nextLine();
                    updateData = field3;

                    updateRecipeArr (updateField,updateData,updateID); //for updating string field of recipe
                }

                
                // System.out.println(updateData + " " + updateField + " did it work lol " + updateID);
                // System.out.println(recipe_book.get(recipeIndex).getInstructions()[currStep]);
            }


            if (s.equals("exit") ) 
            {
                System.exit(0);
            }


            // if (s.equals("test")) {
            //     recipe_book.get(1).printAll();
            // }
        	
        	//browse all recipes
            if(s.equals("b")||s.equals("browse") || s.equals("B")) 
            {
                System.out.println("Choose a recipe from the list below by entering the corresponding number");
                for(int i = 0; i <recipe_book.size(); i++) 
                {
                    System.out.println((i+1) + ". " + recipe_book.get(i).getName());
                }
                recipeIndex = Integer.parseInt(in.nextLine()) - 1;
                currStep = 0;
                //print entire recipe
                recipe_book.get(recipeIndex).printAll();
                System.out.println("Enjoy! Type 'i' to view instructions individually");
            }

            //initiate the step-by-step printout process
            if(s.equals("i") || s.equals("I")) {
                System.out.println("Step by step view. Type 'n' or 'next' to view the next instruction.");
                System.out.println(recipe_book.get(recipeIndex).getInstructions()[currStep]);
            }

            //print the next step
            if ( (s.equals("n") || s.equals("N")) && (currStep < recipe_book.get(recipeIndex).getIngredients().length)) 
                {
                    currStep++;
                    if(currStep == recipe_book.get(recipeIndex).getInstructions().length) 
                    {
                        System.out.println("That was the last step, enjoy! Type 'h' or 'help' for a list of commands");
                    } else 
                        {
                        System.out.println(recipe_book.get(recipeIndex).getInstructions()[currStep]);
                        }
                }

            //search
            if (s.equals("s") || s.equals("search")) 
            {
                System.out.println("Enter the recipe you would like to search for:");
                String searchStr = in.nextLine();
                // apply fuzzy search here
                ArrayList<String> recipeNames = new ArrayList<String>();
                for (Recipe r : recipe_book) 
                {
                    recipeNames.add(r.getName());
                }  
                // return top 3 most similar results
                List<ExtractedResult> resList = FuzzySearch.extractTop(searchStr, recipeNames, 3);
                // print them out
                System.out.println("Here's what we got.");
                for (int i = 0; i < resList.size(); i++) 
                {
                    String index = Integer.toString(i + 1);
                    System.out.println(index + ". " + resList.get(i).getString());
                }
            }

}
}

    //reads json file
    public static void read_json(String filename) throws FileNotFoundException, IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONArray book = (JSONArray) obj;
        int length = book.size();
        for (int i = 0; i < length; i++)
            parseRecipe((JSONObject) book.get(i));
    }

    //builds recipe object
    public static void parseRecipe(JSONObject recipe) {
        JSONArray ingre = (JSONArray) recipe.get("ingredients");
        String[] ingredients = new String[ingre.size()];
        JSONArray instr = (JSONArray) recipe.get("instructions");
        String[] instructions = new String[instr.size()];
        for (int i = 0; i < ingredients.length; i++)
            ingredients[i] = (String) ingre.get(i);
        for (int i = 0; i < instructions.length; i++)
            instructions[i] = (String) instr.get(i);
        Recipe new_recipe = new Recipe(
            Integer.valueOf(recipe.get("id").toString()),
            (String) recipe.get("name"),
            (String) recipe.get("description"),
            ingredients, instructions);
        recipe_book.add(new_recipe);
    }


    public static void addRecipe(Recipe r, String filename) throws FileNotFoundException, IOException, ParseException
    {
    	
    	//recipe_book.add(r); // store it locally, so the user can see the new recipe.
    	
    	FileWriter file = null;
    	Object obj = new JSONParser().parse(new FileReader(filename)); 
        JSONArray book = (JSONArray) obj; 
        r.setId(book.size()); // Need to set the id for our new entry.
        
        
        // New entry
        JSONObject entry = new JSONObject();
        entry.put("id", r.getId());
        entry.put("name", r.getName());
        entry.put("description", r.getDescription());
        entry.put("favorite", r.getFavorite());
        
        // Taking care of the arrays
        JSONArray ingredients = new JSONArray();
        
        for (int i = 0; i < r.getIngredients().length; i++)
        {
        	ingredients.add(r.getIngredients()[i]);
        }
        
        JSONArray instructions = new JSONArray();
        
        for (int i = 0; i < r.getInstructions().length; i++)
        {
        	instructions.add(r.getInstructions()[i]);
        }
        
        // Putting the last key-value pairs together
        entry.put("ingredients", ingredients);
        entry.put("instructions", instructions);
       
        book.add(entry);
        //System.out.println(book.toJSONString());
        try 
        {
        	 
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter(filename);
            file.write(book.toJSONString());
            
        }
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        finally 
        {
                file.flush();
                file.close();
        }
    }



    public static void updateRecipeStr(String oldFieldName, String newFieldData, Recipe r) throws FileNotFoundException, IOException, ParseException
    {
        // r.getId(fieldID);
        // Object obj = new JSONParser().parse(new FileReader("../recipebook.json"));
        // JSONArray book = (JSONArray) obj;
        // parseRecipe((JSONObject) book.get(fieldID));

        //recipe_book.get(1).printAll();

        // getId(fieldID);

        FileReader reader = new FileReader(("../recipebook.json")); //filepath 
        JSONParser jsonparser = new JSONParser();
        JSONObject jsonobject = (JSONObject) jsonparser.parse(reader);
        System.out.println(jsonobject);

       JSONObject idobj = 
        (
         (JSONObject) ( (JSONObject) ( (JSONObject) ( (JSONObject) ( (JSONObject) jsonobject.get("id") ).get("name") ).get("description") ).get("ingredients") ).get("instructions") 
        );

        idobj.put("name", newFieldData);

        System.out.println("After ID value updated : " + jsonobject);

        // if (oldFieldName.equals("name") || oldFieldName.equals("Name")) {
        //     r.setName(newFieldData);


        // } else if (oldFieldName.equals("description") || oldFieldName.equals("Description")) {
        //     r.setDescription(newFieldData);
        // }

    }

    public static void updateRecipeArr(String oldFieldName, String newFieldData, int fieldID) throws FileNotFoundException, IOException, ParseException
    {
        // r.getId(fieldID);
        if (oldFieldName.equals("name") || oldFieldName.equals("Name")) {
            // r.setName(newFieldData);

        } else if (oldFieldName.equals("description") || oldFieldName.equals("Description")) {
            // r.setDescription(newFieldData);
        }
    }
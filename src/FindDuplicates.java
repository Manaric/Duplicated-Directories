import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.nio.channels.FileChannel;
import java.util.Scanner;



/*
 * an amalgamation of the memory hungry "find duplicate files" program from here ...
 * https://jakut.is/2011/03/15/a-java-program-to-list-all/
 * with the space economic hashing code found here ...
 * http://stackoverflow.com/questions/1741545/java-calculate-sha-256-hash-of-large-file-efficiently
 */

public class FindDuplicates {
    
    /*
     * This object provides applications the functionality of a message digest algorithm, such as 
     * SHA-1 or SHA-256. Message digests are secure one-way hash functions that take arbitrary-sized data 
     * and output a fixed-length hash value.
    */
    private static MessageDigest md;
    
    //Map used for the hash method
    static Map<String, List<String>> lists;
    

    //Recursive method to find directories
    public static void find(Map<String, List<String>> lists, File directory) throws Exception  
    {
        String hash; //String where Hash will be saved
        
        //Optimized for loop
        for (File child : directory.listFiles()) //Check every file in current directory
        {
            if (child.isDirectory()) //If the file is directory, then enter it recursivly
            {
                find(lists, child); //Enter method again
            } else //It wasn't a directory, then check it
            {
                try 
                {
                    hash = makeHash(child); //enter method to get hash
                    List<String> list = lists.get(hash); // Get the file from the List
                    if (list == null) //If there are no files like it
                    {
                        list = new LinkedList<String>();
                        lists.put(hash, list); //Saves the file if it founds another one in the future
                    }
                    list.add(child.getAbsolutePath()); //get the path of file
                } catch (IOException e) 
                {
                    throw new RuntimeException("cannot read file " + child.getAbsolutePath());
                }
            }
        }
    }

    /*
     * quick but memory hungry (might like to run with java -Xmx2G or the like to increase heap space if RAM available)
     */
    public static String makeHash(File file) throws Exception 
    {
        try 
        {
            md = MessageDigest.getInstance("SHA-512"); //Returns a MessageDigest object that implements the specified digest algorithm.
        }
        catch (NoSuchAlgorithmException e) 
        {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
        
        FileInputStream fin = new FileInputStream(file); //File reader
        byte data[] = new byte[(int) file.length()]; //Length of file
        fin.read(data); //Read
        fin.close(); //Close
        String hash = new BigInteger(1, md.digest(data)).toString(16); //Here is the Hash string of the file
        //System.out.println("Está aquí!!!!! " + hash + "\n");
        return hash;
    }

    public static void main(String[] args) 
    {
        //Scanner for inputs
        Scanner miScanner = new Scanner(System.in);
        
        if (args.length < 1) //Check that a directory has been writen
        {
            System.out.println("Please supply a path to directory to find duplicate files in.");
            return;
        }
        
        File dir = new File(args[0]); //Make the directory file from the path given
        
        if (!dir.isDirectory()) //If the directory doesn't exist
        {
            System.out.println("Supplied directory does not exist.");
            return;
        }
        
        lists = new HashMap<String, List<String>>(); //Hash map used to compare files
        
        try 
        {
            FindDuplicates.find(lists, dir); //Enter recursive method
        } catch (Exception e) 
        {
            System.out.println("Exception trying to make 'find' method");
        }
        
        String destination = "C:\\Users\\shama_000\\Projects\\Netbeans\\DuplicatedDirectories\\directories\\copies";
        
        for (List<String> list : lists.values()) //Loop to print repeated files
        {
            if (list.size() > 1) //If there are repeated files)
            {
                System.out.println("--");
                for (String file : list) //Loop to print both identical files in pairs
                {
                    try
                    {
                        //Convert to file type
                        File f = new File(file);
                        
                        //Set destination
                        String destination2 = "C:\\Users\\shama_000\\Projects\\Netbeans\\DuplicatedDirectories\\directories\\copies";
                        destination2 += "\\";
                        destination2 += f.getName(); //Get the name of the file
                        
                        //Convert to file type
                        File dest = new File(destination2);
                        //System.out.println("impresion " + destination);
                        copyFile(f,dest);//Copy file method
                    }
                    catch(IOException e)
                    {
                        System.out.println("Exception to enter method copyFile");
                    }
                    finally
                    {
                        System.out.println(file); //Print repeated file
                        //System.out.println("esto se copia");
                    }
                }
            }
        }
        System.out.println("--");
        //System.out.println("\n The total of identical files moved to " + destination + " where " + counter);
        System.out.println("End of program.");
    }
    
    //Method to copy files
    private static void copyFile(File source, File dest) throws IOException 
    {
        FileChannel inputChannel = null; //File that will be copied
        FileChannel outputChannel = null; //Where will be copied
        
        try 
        {
            inputChannel = new FileInputStream(source).getChannel(); //Set file
            outputChannel = new FileOutputStream(dest).getChannel(); //Set destination
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size()); // //Transfer to destination
        } 
        finally 
        {
            inputChannel.close();
           //outputChannel.close();
        }
    }
}
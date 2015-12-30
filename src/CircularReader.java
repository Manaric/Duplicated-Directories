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

/**
 *
 * @author Yamil Elías
 */


public class CircularReader implements Buffery //This was CircularBuffer
{
    /*
        * This object provides applications the functionality of a message digest algorithm, such as 
        * SHA-1 or SHA-256. Message digests are secure one-way hash functions that take arbitrary-sized data 
        * and output a fixed-length hash value. */
    private static MessageDigest md;
   
    private final int[] buffer = {-1, -1, -1, -1, -1}; // shared buffer
    private int occupiedCells = 0; // count number of buffers used
    private int writeIndex = 0; // index of next element to write to
    private int readIndex = 0; // index of next element to read
    private int counter = 0;
   
    // display current operation and buffer state
    public synchronized void displayState(String operation)
    {
      // output operation and number of occupied buffer cells
      System.out.printf("%s%s%d)", operation, 
         " (buffer cells occupied: ", occupiedCells);

      System.out.printf("%n               ");

      System.out.printf("%n%n");
   } 
   
    //Method receive a Map and a Directory and return another map
    @Override
    public synchronized Map<String, List<String>> find(Map<String, List<String>> lists, File directory, int value) throws Exception  
    {
        //System.out.println("entro find");
        // wait until buffer has space avaialble, then write value;
        // while no empty locations, place thread in waiting state
        while (occupiedCells == buffer.length) 
        {
            //System.out.println("entro while find");
            System.out.printf("Buffer is full. Reader waits.%n");
            wait(); // wait until a buffer cell is free
        } 
        
        String hash; //String where Hash will be saved
        
        //Optimized for loop
        for (File child : directory.listFiles()) //Check every file in current directory
        {
            if (child.isDirectory()) //If the file is directory, then enter it recursivly
            {
                value++;
                find(lists, child, value); //Enter method again
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
        
        buffer[writeIndex] = value; // set new buffer value

        // update circular write index
        writeIndex = (writeIndex + 1) % buffer.length;

        ++occupiedCells; // one more buffer cell is full
        displayState("Reader read " + value);
        notifyAll(); // notify threads waiting to read from buffer
        
        return lists;
    }
   
    public void copyFile(File source, File dest) throws IOException 
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
   
    public String makeHash(File file) throws Exception 
    {
        // wait until buffer has data, then read value;
        // while no data to read, place thread in waiting state
        
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
   
    //Method to compare files from directory
    @Override
    public synchronized Map<String, List<String>> compare(Map<String, List<String>> lists, String destination) throws InterruptedException
    {
        //System.out.println("entro compare");
        
        // wait until buffer has data, then read value;
        // while no data to read, place thread in waiting state
        while (occupiedCells == 0) 
        {
            //System.out.println("entro while compare");
            System.out.printf("Buffer is empty. Comparator waits.%n");
            wait(); // wait until a buffer cell is filled
        }  
       
        for (List<String> list : lists.values()) //Loop to print repeated files
        {
            if (list.size() > 1) //If there are repeated files)
            {
                //System.out.println("--");
                for (String file : list) //Loop to print both identical files in pairs
                {
                    try
                    {
                        //Convert to file type
                        File f = new File(file);
                        
                        //Set destination
                        String destination2 = destination;
                        //destination2 += "\\";
                        destination2 += f.getName(); //Get the name of the file
                        //System.out.println(""+ destination2);
                        
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
                        //System.out.println(file); //Print repeated file
                        //System.out.println("esto se copia");
                    }
                }
            }
        }
        
        int readValue = buffer[readIndex]; // read value from buffer

        // update circular read index
        readIndex = (readIndex + 1) % buffer.length;

        --occupiedCells; // one fewer buffer cells are occupied
        displayState("Comparator compares " + readValue);
        notifyAll(); // notify threads waiting to write to buffer

        return lists;
    }
} // end class CircularBuffer

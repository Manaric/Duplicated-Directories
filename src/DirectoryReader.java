//
// Producer and Consumer threads correctly manipulating a circular buffer.
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

public class DirectoryReader //This was CircularBufferTest
{
    static Map<String, List<String>> lists; //Map used for the hash method
    static File dir; //Directory
    static String dest; //Destination
    
    public static void main(String[] args) throws InterruptedException
    {
        //Scanner for inputs
        Scanner miScanner = new Scanner(System.in);
        
        if (args.length < 1) //Check that a directory has been writen
        {
            System.out.println("Please supply a path to directory to find duplicate files in.");
            return;
        }
      
        dir = new File(args[0]); //Make the directory file from the path given
        
        if (!dir.isDirectory()) //If the directory doesn't exist
        {
            System.out.println("Supplied directory does not exist.");
            return;
        }
        
        boolean aux = true; //Auxiliar boolean for while loop
        
        while(aux)
        {
            //Set destination directory, where files will be copied
            System.out.print("Please write the Directory where repeated files will be copied:");
            dest = miScanner.next();
            dest+= "\\";
            
            File f = new File(dest); //Make the directory file from the path given

            if (!f.isDirectory()) //If the destination doesn't exist
            {
                System.out.println("Supplied directory does not exist");
            }
            else
            {
                aux=false;
            }
        }
        
        lists = new HashMap<String, List<String>>(); //Hash map used to compare files
        
        // create new thread pool with two threads
        ExecutorService executorService = Executors.newCachedThreadPool();

        // create CircularBuffer to store ints
        CircularReader sharedLocation = new CircularReader();

        // display the initial state of the CircularBuffer
        sharedLocation.displayState("Initial State");

        start();

        // execute the Producer and Consumer tasks
        executorService.execute(new Reader(sharedLocation,lists, dir));
        executorService.execute(new Comparator(sharedLocation, lists, dir, dest));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES); 
        
        //Application end message
        System.out.printf("%s%s%s","Application ended successfully.", "Repeated files copied to \n", dest);
    }
   
    
    public static void start()
    {
        lists = new HashMap<String, List<String>>(); //Hash map used to compare files
    }
} // end class CircularBufferTest

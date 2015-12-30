//
// Consumer with a run method that loops, reading 10 values from buffer.
import java.security.SecureRandom;
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

public class Comparator implements Runnable //This was consumer
{ 
   private static final SecureRandom generator = new SecureRandom();
   private final Buffery sharedLocation; // reference to shared object
   Map<String, List<String>> lists; //Map used for the hash method
   File dir; //Directory
   String dest; //Destination

   // constructor
   public Comparator(Buffery sharedLocation, Map<String, List<String>> lists, File dir, String dest)
   {
      this.sharedLocation = sharedLocation;
      this.lists = lists;
      this.dir = dir;
      this.dest = dest;
   }

   // read sharedLocation's value 10 times and sum the values
   public void run()                                           
   {
        int sum=0;
       
        for (File child : dir.listFiles()) //Check every file in current directory
        {
            // sleep 0 to 3 seconds, read value from buffer and add to sum
            try 
            {
                Thread.sleep(generator.nextInt(3000));
                sum++;
                //System.out.println("entro comp");
                lists = sharedLocation.compare(lists,dest);
            } 
            catch (InterruptedException exception) 
            {
                Thread.currentThread().interrupt(); 
            } 
        } 

        //System.out.printf("%n%s %d%n%s%n", "Comparator compared values totaling", sum, "Terminating Comparator");
   } 
} // end class Consumer


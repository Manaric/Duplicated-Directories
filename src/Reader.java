// 
// Producer with a run method that inserts the values 1 to 10 in buffer.
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

public class Reader implements Runnable //This was Producer
{
   private static final SecureRandom generator = new SecureRandom();
   private final Buffery sharedLocation; // reference to shared object
   Map<String, List<String>> lists; //Map used for the hash method
   File dir; //Directory
   
   // constructor
   public Reader(Buffery sharedLocation, Map<String, List<String>> lists, File dir)
   {
      this.sharedLocation = sharedLocation;
      this.lists = lists;
      this.dir = dir;
   } 

   // store values from 1 to 10 in sharedLocation
   public void run()                             
   {
        int sum = 0;

        for (File child : dir.listFiles()) //Check every file in current directory
        {
            try // sleep 0 to 3 seconds, then place value in Buffer
            {
               Thread.sleep(generator.nextInt(2000)); // random sleep
               sum++;
               //System.out.println("entro read");
               sharedLocation.find(lists, dir, sum); // set value in buffer
            } 
            catch (InterruptedException exception) 
            {
               Thread.currentThread().interrupt(); 
            }
            catch (Exception e)
            {
                System.out.println("Exception exception");
            }
        } 

        //System.out.printf("Reader done reading%nTerminating Reader%n");
   }
   
} // end class Producer

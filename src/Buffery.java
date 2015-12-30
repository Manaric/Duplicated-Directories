//
// Buffer interface specifies methods called by Reader and Comparator.

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

public interface Buffery //This was Buffer
{
   
   //Find files from Directory
   public Map<String, List<String>> find(Map<String, List<String>> lists, File directory, int value) throws Exception;
   
   //Copy files into new Directory
   public Map<String, List<String>> compare(Map<String, List<String>> lists, String destination) throws InterruptedException;
   
} // end interface Buffer

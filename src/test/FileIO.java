
package test;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yamil Elías
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileIO 
{
    public static void main(String[] args) 
    {
        File file = new File("C:\\Users\\shama_000\\Projects\\Netbeans\\DuplicatedDirectories\\directories\\");

        // Reading directory contents
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }

        // Reading conetent

        BufferedReader reader = null;

        try {
            for (int i = 0; i < files.length; i++) {
            reader = new BufferedReader(new FileReader(files[i]));
            String line = null;

            while(true)
            {
                line = reader.readLine();
                if(line == null)
                    break;

                System.out.println(line);
            }
            }
            
        }catch(Exception e) {
            System.out.println("Exception in main.");
        }finally {
            if(reader != null)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Exception in close.");
                }
            }
        }
    }
}

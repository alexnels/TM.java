//Alex Nelson 02/04/2018

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.io.*;
import java.util.*;


public class TM
{
   Log logFile = new Log();

   public void appMain(String args[])
   {
      try
      {
         String cmd = args[0];
         String data = "null";
         String programDescription = "null";
         if (args.length > 1)
            data = args[1];
         if (args.length > 2)
            programDescription = args[2];
                  
         switch(cmd)
         {
            case ("start"):
               cmdWrite(cmd, data, programDescription);
               break;
            case ("stop"):
               cmdWrite(cmd, data, programDescription);
               break;
            case ("describe"):
               cmdWrite(cmd, data, programDescription);
               break;
            case ("summary"):
               if(!(data.equals("null")))
                  logFile.readEntry(data);
               else
                  logFile.summarize();
               break;
         }

      }
      catch (ArrayIndexOutOfBoundsException exception)
      {
         System.out.println("Didn't pass enough arguments.");
      }     
   }
   
   public static void main (String args[])
   {
      TM tm = new TM();
      tm.appMain (args);
   }

   public void cmdWrite(String cmd, String data, String programDesc)
   {
      logFile.writeEntry(getTime() + "," + cmd + "," + data + "," + programDesc);      
   }
   
   public String getTime()
   {
      LocalDateTime now = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String formatDateTime = now.format(formatter);
      return formatDateTime;
      
      //https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html
   }
}//end of TM class


class Log
{
   int counter = 0;
   
   public void writeEntry(String output)
   {
      try{
         File file = new File("logFile.txt");
	      FileWriter fileWriter = new FileWriter(file, true);
         fileWriter.write(output);
         fileWriter.write(System.getProperty( "line.separator"));
         fileWriter.close();
         
         System.out.println("Written to logFile.txt...");
         
         //https://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html#FileWriter(java.io.File,%20boolean)
      }
      catch (IOException e)
      {
			 
      }   
   }
   
   public long getTimeDifference(LocalDateTime inputTimeA, LocalDateTime inputTimeB)
   {
      return Duration.between(inputTimeA, inputTimeB).toMillis();   
   }
   
   public String formatTimeOutput(long totalTime)
   {
      long diffInSeconds = totalTime / 1000 % 60;
      long diffInMinutes = totalTime / (60 * 1000) % 60;
      long diffInHours = totalTime / (60 * 60 * 1000) % 24;
            
      return String.format("%02d:%02d:%02d",diffInHours, diffInMinutes, diffInSeconds);
      
      //https://www.mkyong.com/java/how-to-calculate-date-time-difference-in-java/
   }
   
   public LocalDateTime formatTime(String input)
   {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime formatDateTime = LocalDateTime.parse(input, formatter);
      return formatDateTime;
   }
   
   
   public void readEntry(String programName)
   {
        try {
            ArrayList<String> programList = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader("logFile.txt"));
            String input = br.readLine();
    
            while(input != null)
            {
               String[] split = input.split(",");
               
               for(int i =0; i < split.length; i++)
                  programList.add(split[i]);

               input = br.readLine();  
            }     
              
            int index = programList.indexOf(programName);
            long totalRunTime = 0;
            String timeA = "null";
            String timeB = "null";
            String description = "";
            
            while((index-1)>=0)
            {
               if(programList.get(index-1).equals("start"))
                  timeA = programList.get(index-2);
               else if(programList.get(index-1).equals("stop"))
                  timeB = programList.get(index-2);
               else if(programList.get(index-1).equals("describe"))
                  description = programList.get(index+1);
                  
               programList.remove(index);
               programList.remove(0);
               programList.remove(0);
               programList.remove(0);
                  
               index = programList.indexOf(programName);
               
               if((!(timeA.equals("null"))) && (!(timeB.equals("null"))))
               {
                  totalRunTime += getTimeDifference(formatTime(timeA), formatTime(timeB));
                  timeA = "null";
                  timeB = "null";
               }
            }//end of while loop
            
            if(description.equals("null"))
               description = "";         
            
            System.out.println("Program Summary for:\t" + programName);
            System.out.println("Program Description:\t" + description);
            System.out.println("Total Running Time: \t" + formatTimeOutput(totalRunTime));
            
            br.close();

            } 
            catch (IOException ioe) 
            {
               System.out.println("\n An error with the Data.txt file occured.");
            }
   }//end of readEntry() function
   
   public void summarize()
   {
         try {
            ArrayList<String> inputList = new ArrayList<String>();
            ArrayList<String> programNames = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader("logFile.txt"));
            String input = br.readLine();
            String programName = "null";
            
            while(input != null)
            {
               String[] split = input.split(",");
               
               for(int i =0; i < split.length; i++)
                  inputList.add(split[i]);

               input = br.readLine();
            }       
            
            //System.out.println("Size: " + inputList.size());
            
            while(inputList.size() > 0)
            {                  
               inputList.remove(0);
               inputList.remove(0);              
                  
               programName = inputList.get(0);
               programNames.add(programName);

               inputList.remove(0);
               inputList.remove(0);
            }
            
            Set<String> noDuplicates = new HashSet<>();
            noDuplicates.addAll(programNames);
            programNames.clear();
            programNames.addAll(noDuplicates);
            
            //https://stackoverflow.com/questions/203984/how-do-i-remove-repeated-elements-from-arraylist
            
            while(programNames.size() > 0)
            {
               readEntry(programNames.get(0));
               System.out.println("");
               programNames.remove(0);
            }

            br.close();

        } catch (IOException ioe) {

            System.out.println("\nThe logFile.txt file does not exist.");
        }
   }//end of summarize() function
   
}//end of Log class
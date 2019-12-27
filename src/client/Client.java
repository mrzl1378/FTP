package client;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    static String fileName;
    static long fileSize;

    public static void main(String[] args) {

        DataInputStream input = null;
        DataOutputStream output = null;

        
            Socket socket;
        try {
            socket = new Socket("localhost", 13137);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        

        Scanner sc = new Scanner(System.in);

        String choice;
        while (true) {
            System.out.println("Please enter your choice");
            choice = sc.nextLine();
            String result = "";
            try {
                output.writeUTF(choice);
                output.flush();
                
                while (true) {
                    result = input.readUTF();
                    
                        if(result.startsWith("Erorr")||result.startsWith("Connected")||result.startsWith("Loged")||result.startsWith("Cancled")||result.startsWith("Finish")){
                            System.out.println(result);
                            break;
                        }    
                        else if(result.startsWith("Enter")){
                            System.out.println(result);
                            output.writeUTF(sc.nextLine());
                            output.flush();
                        }    
                        else if(result.startsWith("Upload")){
                            System.out.println(result);
                            upload(output, input, sc.nextLine());
                        }
                            
                        else if(result.startsWith("uploading")||result.startsWith("replacing")||result.startsWith("keeping")){
                            System.out.println(result);
                            sendFile(output, input);
                        }
                        else if(result.endsWith("Download"))
                            getFile(input,output,sc);
                        else
                            System.out.println(result);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    static public void upload (DataOutputStream out ,DataInputStream in , String path ) throws IOException{
        File f1 = new File(path);
        if(!f1.exists()){
            out.writeBoolean(false);
            out.flush();
        }
        else{
            out.writeBoolean(true);
            out.flush();

            fileSize = f1.length();

            String f1Name = path.substring(path.lastIndexOf('/') + 1);
            out.writeUTF(f1Name);
            out.flush();

            fileName = f1Name;
        }
    }
    private static void sendFile(DataOutputStream write, DataInputStream read) throws IOException {

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        
        while (fileSize > 0) {

            if (fileSize < 512) {

                write.writeInt((int) fileSize);
                write.flush();

                byte[] data = new byte[(int) fileSize];
                fis.read(data, 0, (int) fileSize);

                fileSize = 0;

                write.write(data);
                write.flush();
            } else {

                write.writeInt(512);
                write.flush();

                byte[] data = new byte[512];
                fis.read(data, 0, 512);

                fileSize -= 512;
 
                write.write(data);
                write.flush();
            }

            System.gc();
        }

        write.writeInt(0);
        write.flush();

        System.out.println("Sent.");
    }
    private static void getFile(DataInputStream read, DataOutputStream write, Scanner sc) throws IOException {

        String username = read.readUTF();
        String FileName = read.readUTF();

        File dir = new File("downloads/" + username);
        if (! dir.exists()) {
            dir.mkdirs();
        }
        File file = new File("downloads/" + username + '/' + FileName);

        if (file.exists()) {
            System.out.println("Existed :\nReplace / Keep / Cancel ?");

            
            while(true) {
                switch (sc.nextLine()) {
                    case "replace":
                        file.delete();
                        break ;
                    case "keep":
                        do {
                            FileName = FileName.substring(0, FileName.lastIndexOf('.')) + "-Duplicate" + FileName.substring(FileName.lastIndexOf('.'));
                            file = new File("downloads/" + username + '/' + FileName);
                        } while (file.exists());
                        break ;
                    case "cancel":
                        System.out.println("canceled process");
                        write.writeBoolean(false);
                        write.flush();
                        return;
                    default:
                        System.out.println("Erorr !! try again");
                        System.out.println("Replace / Keep / Cancel ");
                }
            }
        }

        write.writeBoolean(true);
        write.flush();

        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes;

        while (true) {

            int size = read.readInt();

            if (size == 0) {
                break;
            }

            bytes = new byte[size];

            read.read(bytes, 0, size);

            fos.write(bytes);
            fos.flush();

            System.gc();
        }

        fos.close();

    }

}

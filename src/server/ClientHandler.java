package server;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private Socket socket ;
    private DataInputStream in;
    private DataOutputStream out;
    User user=null;
    
    public ClientHandler(Socket aSocket){
        socket = aSocket;
        try {
            in=new DataInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
    }
    public void run(){
        while (true) {
            String choice="";
            boolean isEnd=false;
            
            try {
                  choice = in.readUTF();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
           
            switch (choice) {
                case "login":
            {
                try {
                    login();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;
                case "register":
            {
                try {
                    signup();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;
                case "upload":
            {
                try {
                    upload();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;
                case "list":
            {
                try {
                    download();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;
                case "logout":
                    
            {
                try {
                    logout();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;
                case "end":{
                    isEnd=true;
                    break;
                }
            }
                    if(isEnd)
                    break;
        }
    }
    private void login() throws IOException {

        if (user != null) {
            out.writeUTF("Erorr !! Already entered");
            out.flush();
            return;
        }

        out.writeUTF("Enter your username");
        out.flush();
        boolean isFind=false;
        while (true) {
            String username = in.readUTF();
            for (User user1 : Server.users) {
                if (user1.getUsername().equals(username)) {
                    if (user1.isLogin()) {
                        out.writeUTF("Erorr");
                        out.flush();
                        return;
                    }
                    out.writeUTF("Enter your password");
                    out.flush();
                    while (true) {
                        String password = in.readUTF();
                        if (user1.getPassword().equals(password)) {
                            user = user1;
                            isFind=true;
                            break;
                        }
                        if(isFind)
                            break;
                        out.writeUTF("Erorr !! Try again");
                        out.flush();
                    }
                }
            }
            if(isFind)
                break;
            out.writeUTF("Erorr !! no match found, try again...");
            out.flush();
        }

        out.writeUTF("Connected ");
        out.flush();
        

    }
    private void signup
        () throws IOException {

        if (user != null) {
            out.writeUTF("Erorr !! Please logout first");
            out.flush();
            return;
        }
        out.writeUTF("Please enter your username");
        out.flush();

        boolean isFind = false;

        while (true) {
            out.writeUTF("Enter :");
            out.flush();
            
            String username = in.readUTF();
            for (User user1 : Server.users) {
                if (user1.getUsername().equals(username)) {
                    out.writeUTF("Already taken . Try again .");
                    out.flush();
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                out.writeUTF("Please enter your password:");
                out.flush();
                while (true) {
                    out.writeUTF("Enter :");
                    out.flush();
                    String password = in.readUTF();
                    if (isCorrectPassword(password)) {
                        user = new User(username, password);
                        Server.users.add(user);
                        new File("Main/" + username).mkdir();
                        out.writeUTF("Connected successfully");
                        out.flush();
                        return;
                    }
                    out.writeUTF("Erorr !! Try again .");
                    out.flush();
                }
            }
        }
    }
    boolean isCorrectPassword(String password) {
        boolean isFindNumber = false, isFindChar = false;
        if (password.length() < 6) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            if ((int) password.charAt(i) <= 57 && (int) password.charAt(i) >= 48) {
                isFindNumber = true;
            }
            if ((int) password.charAt(i) <= 122 && (int) password.charAt(i) >= 97) {
                isFindChar = true;
            }
        }
        return isFindChar && isFindNumber;
    }   
    private void logout() throws IOException {

        user = null;
        out.writeUTF("Loged out");
        out.flush();
    }
    private void upload ()throws IOException{
        if (user == null) {
            out.writeUTF("Erorr !! You are not in.");
            out.flush();
            return;
        }
        out.writeUTF("Upload: Enter path of file...");
        out.flush();
        boolean isExist = in.readBoolean();
        if (isExist) {
            String fileName = in.readUTF();
            File f1 = new File("Main/" + user.getUsername() + '/' + fileName);
            if (f1.exists()) {
                out.writeUTF("Enter your choice : (Existed)\nReplace / Keep / Cancel ?");
                out.flush();
                while(true) {
                    if (in.readUTF().equals("replace")) {
                        
                            f1.delete();
                            out.writeUTF("replacing ...");
                            out.flush();
                            getFile(fileName);
                            break ;
                    }
                    else if(in.readUTF().equals("keep")){
                            do {
                                fileName = fileName.substring(0, fileName.lastIndexOf('.')) + "-Duplicate" + fileName.substring(fileName.lastIndexOf('.'));
                                f1= new File("Main/" + user.getUsername() + '/' + fileName);
                            } while (f1.exists());
                            out.writeUTF("keeping...");
                            out.flush();
                            getFile(fileName);
                            break ;
                    }
                    else if(in.readUTF().equals("cancle")){
                            out.writeUTF("Cancled");
                            out.flush();
                            break;
                    }
                    else{
                            out.writeUTF("Incorrect choice!!");
                            out.flush();

                            out.writeUTF("Enter : \n Replace / Keep / Cancel ?");
                            out.flush();
                    }
                }
            } else {
                out.writeUTF("uploading ...");
                out.flush();
                getFile(fileName);
            }
        } else {

            out.writeUTF("Erorr !! File not existed");
            out.flush();
        }
    }
    private void getFile(String fileName) throws IOException {

        File f1 = new File("Main/" + user.getUsername() + '/' + fileName);
        FileOutputStream fos = new FileOutputStream(f1);
        byte[] data;

        while (true) {
            int size = in.readInt();
            if (size == 0) {
                break;
            }

            data = new byte[size];

            in.read(data, 0, size);
            
            fos.write(data);
            fos.flush();

            System.gc();
        }
    }
    private void download() throws IOException {

        if (user == null) {
            out.writeUTF("Erorr !! You are not in.");
            out.flush();
            return;
        }
        File f1 = new File("Main"+user.getUsername());
        if(f1.isDirectory()){
            String s[]=f1.list();
            out.writeInt(s.length);
            for(int i=0 ; i<s.length ; i++){
                out.writeUTF(s[i]);
                out.flush();
            }
        }
        out.writeUTF("Enter the name of file that you want to download");
        out.flush();
        String fName = in.readUTF();
        File file = new File("Main/" + user.getUsername() + '/' + fName);

        if (file.exists()) {
            out.writeUTF("Download");
            out.flush();

            if (! sendFile(file)) {
                return;
            }
        } else {
            out.writeUTF("Erorr !! not exist");
            out.flush();

            return;
        }

        out.writeUTF("Finish");
        out.flush();
    }
    private boolean sendFile(File file) throws IOException {

        out.writeUTF(user.getUsername());
        out.flush();

        out.writeUTF(file.getName());
        out.flush();

        if (! in.readBoolean()) {
            return false;
        }

        FileInputStream fis = new FileInputStream(file);

        long sizeOfFile = file.length();

        while (sizeOfFile > 0) {

            if (sizeOfFile < 512) {

                out.writeInt((int) sizeOfFile);
                out.flush();

                byte[] data = new byte[(int) sizeOfFile];
                fis.read(data, 0, (int) sizeOfFile);

                sizeOfFile = 0;

                out.write(data);
                out.flush();
            } else {

                out.writeInt(512);
                out.flush();

                byte[] data = new byte[512];
                fis.read(data, 0, 512);

                sizeOfFile -= 512;

                out.write(data);
                out.flush();
            }

            System.gc();
        }

        out.writeInt(0);
        out.flush();

        return true;
    }
              
}

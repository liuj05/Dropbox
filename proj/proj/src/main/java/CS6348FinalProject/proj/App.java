package CS6348FinalProject.proj;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxDelta.Entry;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.async.LaunchEmptyResult.Tag;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.sharing.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.io.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.text.html.parser.Entity;

public class App {
	
	private static final String pbKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArK69SjX3ht6iVbA49QNAX72S2VXQrclGpUEKsMZRUerFHzRJ6mDzxoIi9RotUC4vUm7fM0CdfKiKe1ojpuYT3bFNwkXgnVqhIDfSa8KshxIUoRIIZE2xgOqlSC6fIowau2fRFjc3T3B67si+SF+S5e0i5LoelXTLykM67RCog97+MtRkAadDTstOkCexVSL32Encl4vlu7mYfM1t/85CuWyx0fRXupogJd8ZxlDpbZd2cq/NLhDPGR6uP4RVBMm77Ptxec/4JgRQpIvJer9+FMzyC5ZnwgO2mBHIOkvNCl186cvg5mEi1H3IQH/5LsPLc0WJ2dMv4IMREKe5YhmmBwIDAQAB";
    private static final String prKey = "MIICOQIBADANBgkqhkiG9w0BAQEFAASCAiMwggIfAgEAAoIBAQCsrr1KNfeG3qJVsDj1A0BfvZLZVdCtyUalQQqwxlFR6sUfNEnqYPPGgiL1Gi1QLi9Sbt8zQJ18qIp7WiOm5hPdsU3CReCdWqEgN9JrwqyHEhShEghkTbGA6qVILp8ijBq7Z9EWNzdPcHruyL5IX5Ll7SLkuh6VdMvKQzrtEKiD3v4y1GQBp0NOy06QJ7FVIvfYSdyXi+W7uZh8zW3/zkK5bLHR9Fe6miAl3xnGUOltl3Zyr80uEM8ZHq4/hFUEybvs+3F5z/gmBFCki8l6v34UzPILlmfCA7aYEcg6S80KXXzpy+DmYSLUfchAf/kuw8tzRYnZ0y/ggxEQp7liGaYHAgEAAoIBAQCTQt04OTei85+6tXKNN72hKBjgcPdqDPjLGuGUCTv2QODkEIJsd0vM4NQQ88bq1sgSY2zC13q2gUC/mwpGXXVZnMe0QkUfy9cWk6RFs5nQAlJHXgp8B2m1h+V13hRlsNdnNEyxxlrLyx97HQRw0diDbixQpIxKZnUYQycwXgFnms+Ms0rI343uYeFA1F9b6WIop8VO8C/gd5UOHg2esivT4iMftEctSOlIJb7e2zR6C+BkhsldtgVjNIRAgjON96VJpOmHoHAE75qBiEkrMt2eSyHRRJpnYPYQOhnTGsop0zeRticPKSBy0e9pfcA86YK6gfnGe4Yk8SG05XojSvBhAgEAAgEAAgEAAgEAAgEA";
	private static final String ACCESS_TOKEN = "OTc3CCGa3mAAAAAAAAAACqeFwpCcohAGJOloaIX4ADetv-VDwkw8Imknthkf_Uls";
    private final String APP_KEY = "82efxri0sevo94n";
    private final String APP_SECRET = "y6b5e71lp1n1y1r";
    public static int count = 0;
    //master key (user password)
    private static final String mk_str = "dbsec6348";
    
    //display the menu of files in dropbox
    public void show() throws ListFolderContinueErrorException, DbxException, IOException
    {
    	// Create Dropbox client
        @SuppressWarnings("deprecation")
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println("Hi, " + account.getName().getDisplayName()+ ", Here are the Files in Your Menu:");
      
        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
        
    }
    
    
    
    //upload encrypted file
    public void upload() throws Exception
    {    	
    	// Create Dropbox client
        @SuppressWarnings("deprecation")
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        //restore rsa public key
        RSAEncryption rsa = new RSAEncryption();
    	RSAPublicKey pbk = rsa.restorePbKey(pbKey);
        
        //get path
        String path = "D:\\UTD\\1. Data and Application Security\\Project\\test.txt";
        
        
        //get file name
        String[] split = path.split("\\\\");
        String file_name = split[split.length - 1];
        
        //get content in the file
        @SuppressWarnings("resource")
		String file_content = new Scanner(new File(path)).useDelimiter("\\Z").next();
        
        //initiallize instant for encryption
        AESEncryption aes = new AESEncryption();
        
        //get secret key for encrypt file
        SecretKey key_file = AESEncryption.generateKey();
        
        //get iv byte array
        byte[] iv_byte = aes.get_iv_bytes();
        
        
        //get key for hmac
        SecretKey key_hmac = AESEncryption.generateKey();
        
        //encrypt file content
        String file_enc = aes.doEnc(file_content, key_file, iv_byte);
        
        //hmac of cipher
        String file_hmac = aes.SHA512(file_enc, key_hmac);
        
        //Concatenate file key, iv and hmac key;
        String key_file_str = AESEncryption.saveKey(key_file);
        String key_hmac_str = AESEncryption.saveKey(key_hmac);
        String iv_str = aes.bytesToStr(iv_byte);
        String key_set = key_file_str + " " + iv_str + " " + key_hmac_str;
        
        SecretKeySpec mk = aes.getMK(mk_str);
        
        //encrypt the key set with master key
        String enc_key_set = aes.encKey(key_set, mk);
        
        // All content in the file
        // string order splited by space
        // order in all content: file_enc -> file_hmac -> enc_key_set 
        // order in enc_key_set: file key -> iv -> hmac key 
        String all_content = file_enc + " " + file_hmac + " " + enc_key_set;
        
        
        //encrypt key set with rsa
        String key_set_rsa = RSAEncryption.encryptByPublicKey(key_set, rsa.restorePbKey(pbKey));
        
        //write all content in to temp file
        String temp_path = "D:\\UTD\\1. Data and Application Security\\Project\\temp\\"+file_name;
        
        //the path temproray save key set
        String temp_path_for_key = "D:\\UTD\\1. Data and Application Security\\Project\\temp\\key_for_"+file_name;
        		
        
        
        //write content into file
        Files.write(Paths.get(temp_path), all_content.getBytes());
        Files.write(Paths.get(temp_path_for_key), key_set_rsa.getBytes());
        
        
        // Upload "test.txt" to Dropbox
        String metatdata = "/" + file_name;
        String metatdata_0 = "/" + "key_for_"+ file_name;
        try{
        	InputStream in = new FileInputStream(temp_path);
        	InputStream in_file = new FileInputStream(temp_path_for_key);
            FileMetadata metadata = client.files().uploadBuilder(metatdata).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
            FileMetadata metadata_rsa = client.files().uploadBuilder(metatdata_0).withMode(WriteMode.OVERWRITE).uploadAndFinish(in_file);
            System.out.println(file_name + " Upload Success!");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        File file = new File(temp_path);
        //delete temp file after upload
        if (file.exists())
        {
        	file.delete();
        }
        
        File file_rsa = new File(temp_path_for_key);
        if(file_rsa.exists())
        {
        	file_rsa.delete();
        }
    }
    
    //down load and decrypt file
    public void download() throws Exception
    {
    	DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.getAccessToken();

        DbxClientV1 client = new DbxClientV1(config, accessToken);

        System.out.println("Linked account: " + client.getAccountInfo().displayName);

       

        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }

        FileOutputStream outputStream = new FileOutputStream("test.txt");
        try {
            DbxEntry.File downloadedFile = client.getFile("/test.txt", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
        } finally {
            outputStream.close();
        }
        
        //get path
        String path = "D:\\UTD\\1. Data and Application Security\\proj\\proj\\test.txt";

        //get content in the file
        @SuppressWarnings("resource")
		String file_content = new Scanner(new File(path)).useDelimiter("\\Z").next();
        
        String[] all_content = file_content.split("\\s+");
        
        //String of Secert key 
        String enc_file_str = all_content[0];
        String hmac_str = all_content[1];
        String key_sets_enc = all_content[2];
        
        AESEncryption aes = new AESEncryption();
        String key_sets = aes.decKey(key_sets_enc, aes.getMK(mk_str));
        
        //get key from key sets
        String[] all_keys = key_sets.split("\\s+");
        
        //get file key
        SecretKey file_key = aes.restoreKey(all_keys[0]);
        
        //get iv
        byte[] iv = aes.stringToByte(all_keys[1]);
        
        //get hmac key
        SecretKey hmac_key = aes.restoreKey(all_keys[2]);
        
        //confirm integrity
        String hmac_copy = aes.SHA512(enc_file_str, hmac_key);

        
        if (aes.SHA512(enc_file_str, hmac_key).equals(hmac_str))
        {
        	//decrypt to plain text
            String plain_text = aes.doDec(enc_file_str, file_key, iv); 
            
            //the path save download content
            String path_download = "D:\\UTD\\1. Data and Application Security\\download\\test.txt";
            		
            //write content into file
            Files.write(Paths.get(path_download), plain_text.getBytes());
            System.out.println("File has been Writen.");
            
            File temp = new File(path);
            if(temp.exists())
            {
            	temp.delete();
            }
            
        }
        else
        {
        	System.out.println("File has been Damaged");
        }
    }
    
    
    public void share() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, Exception
    {
    	DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.getAccessToken();

        DbxClientV1 client = new DbxClientV1(config, accessToken);

        System.out.println("Linked account: " + client.getAccountInfo().displayName);

       

        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }

        FileOutputStream outputStream = new FileOutputStream("test.txt");
        FileOutputStream outputStream_key = new FileOutputStream("key_for_test.txt");
        try {
            DbxEntry.File downloadedFile = client.getFile("/test.txt", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
            DbxEntry.File downloadedFile_key = client.getFile("/key_for_test.txt", null,
                    outputStream_key);
            System.out.println("Metadata: " + downloadedFile_key.toString());
        } finally {
            outputStream.close();
        }
        
      //get path for encryption file
        String path = "D:\\UTD\\1. Data and Application Security\\proj\\proj\\test.txt";

        //get content in the file
        @SuppressWarnings("resource")
		String file_content = new Scanner(new File(path)).useDelimiter("\\Z").next();
        
        String[] all_content = file_content.split("\\s+");
    	
        //file enc str
        String file_enc_str = all_content[0];
        
        //hamc str
        String hmac_str = all_content[1];
        
        
        //get path for key file
        String path_key = "D:\\UTD\\1. Data and Application Security\\proj\\proj\\key_for_test.txt";

        //get content in the file
        @SuppressWarnings("resource")
		String key_content = new Scanner(new File(path_key)).useDelimiter("\\Z").next();
        
        //decrypt for keys
        RSAEncryption rsa = new RSAEncryption();
        RSAPrivateKey prk = rsa.restorePRKey(prKey);
        String keys_set = rsa.decryptByPrivateKey(key_content, prk);
        String[] keys_split = keys_set.split("\\s+");
        
        //key for file
        String file_key_str = keys_split[0];
        
        //iv
        String iv_str = keys_split[1];
        
        //hmac key
        String hmac_key_str = keys_split[2];

        AESEncryption aes = new AESEncryption();
        
        //get keys
        SecretKey file_key = aes.restoreKey(file_key_str);
        byte[] iv = aes.stringToByte(iv_str);
        SecretKey hmac_key = aes.restoreKey(hmac_key_str);
        
        //decrypt
        if(aes.SHA512(file_enc_str, hmac_key).equals(hmac_str))
        {
        	String dec = aes.doDec(file_enc_str, file_key, iv);
        	
        	//the path save download content
            String path_download = "D:\\UTD\\1. Data and Application Security\\download\\test_share.txt";
            		
            //write content into file
            Files.write(Paths.get(path_download), dec.getBytes());
            System.out.println("File has been Writen.");
            
            File temp = new File(path);
            File temp_0 = new File(path_key);
            if(temp.exists())
            {
            	temp.delete();
            }
            if(temp_0.exists())
            {
            	temp_0.delete();
            }
        }
        else
        {
        	System.out.println("File has been Damaged.");
        }
    }
    
    
    public static void main(String args[]) throws Exception 
    {   
    	 
    	App a = new App();
    	a.show();
    	boolean flag = true;
    	while(flag)
    	{
    		//Show options
    		System.out.println("Options：");
    	    System.out.println("[1] \t Upload File");
    	    System.out.println("[2] \t Download File");
    	    System.out.println("[3] \t Sharing File");
    		try
    		{
    			@SuppressWarnings("resource")
				Scanner reader = new Scanner(System.in);  // Reading from System.in
        		//prompt for option
        		System.out.println("Please Choose one of Option (Press any Other Key to Terminate.):");
        		int option = reader.nextInt(); // Scans the next token of the input as an int.
        		
        		switch(option)
        		{
        			case 1:	a.upload(); break;
        			case 2: a.download(); break;
        			case 3: a.share(); break;
        			default: flag = false; System.out.println("Program Terminated."); break;
        		}
    		}
			
    		catch(Exception e)
    		{
    			flag = false;
    			System.out.println("Program Terminated.");
    		}
    	}
    }
}
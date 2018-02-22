
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Based on the following tutorial on hashing passwords: 
 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 */
public class Password{

	private String hashedPassword;
	
	public Password(String passwordInput, boolean exists) { 
		if (exists) setHashedPassword(passwordInput); 
		else hashedPassword = getSHA256(passwordInput);
	}
	
	public String getHashedPassword() {
		return hashedPassword;
	}
	
	private void setHashedPassword(String existingPassword) {
		hashedPassword = existingPassword;
	}
	
	public String getSHA256 (String password) {
		String hashed = "";
		try {
			//creates a MessageDigest object and create an array of bytes which encrypts
			//the input with the SHA-256 encryption algorithm
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] passwordDigest = messageDigest.digest(password.getBytes());
			StringBuilder stringBuilder = new StringBuilder();
			
			for(int i = 0; i < passwordDigest.length; i++) {
				stringBuilder.append(Integer.toString((passwordDigest[i] & 0xff) + 0x100, 16).substring(1));;
			}
			//creates the hashed-password
			hashed = stringBuilder.toString();
		}
		catch(NoSuchAlgorithmException e) {
			Report.error("Error: password hashing algorithm doesn't exist");
			e.printStackTrace();
		}
		return hashed;
	}
}

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/*
 * Based on the following tutorial on hashing passwords: 
 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 */
public class Password implements Serializable{

	private static final long serialVersionUID = 1L;
	private String hashedPassword;
	private byte[] salt;
	
	public Password(String passwordInput) { 
		try {
			salt = createSalt();
		} catch (NoSuchAlgorithmException e) {
			Report.error("Error: password hashing algorithm doesn't exist");
			e.printStackTrace();
		}
		hashedPassword = getSHA256(passwordInput, salt);
	}
	
	public String getHashedPassword() {
		return hashedPassword;
	}
	
	public byte[] getSalt() {
		return salt;
	}
	
	public String getSHA256 (String password, byte[] salt) {
		String hashed = "";
		try {
			//creates a MessageDigest object and create an array of bytes which encrypts
			//the input with the SHA-256 encryption algorithm
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(salt);
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
	
	private byte[] createSalt() throws NoSuchAlgorithmException{
		//generates a salt to secure the password
		SecureRandom randomBytes = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		randomBytes.nextBytes(salt);
		return salt;
	}
	
}

package sfdata;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHelper
{
	private String secretKeyString = "";
	
	
	public EncryptionHelper()
	{
		this.secretKeyString = "762F76F17A2ADFE6";
	}
	public EncryptionHelper(String secretKeyString)
	{
		this.secretKeyString = secretKeyString;
	}
	
	public String getNewKey()
	{
		try {
			KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
			SecretKey myDesKey = keygenerator.generateKey(); 
			byte [] secretKeyBytes = myDesKey.getEncoded();
			String secretKeyString = convertBytesToHex(secretKeyBytes);				
			
			return secretKeyString;
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public String encrypt(String value)
	{
		try {
			
			byte [] newSecretKeyBytes = convertHexToBytes(secretKeyString);
			SecretKey myDesKey = new SecretKeySpec(newSecretKeyBytes, 0, newSecretKeyBytes.length, "DES");
			
			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");	// Create the cipher
			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);					// Initialize the cipher for encryption

			byte[] text = value.getBytes();									
			byte[] textEncrypted = desCipher.doFinal(text);					

			return convertBytesToHex(textEncrypted);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public String decrypt(String value) 
	{
		try {
			byte [] newSecretKeyBytes = convertHexToBytes(secretKeyString);
			SecretKey myDesKey = new SecretKeySpec(newSecretKeyBytes, 0, newSecretKeyBytes.length, "DES");
			
			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey);

			byte[] textEncrypted = convertHexToBytes(value);
			byte[] textDecrypted = desCipher.doFinal(textEncrypted);		

			return new String(textDecrypted);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public String convertBytesToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
	public byte[] convertHexToBytes(String s)
	{
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	
	public String computeHash(String valueToEncode) 
	{
		try {
			java.security.MessageDigest d = java.security.MessageDigest.getInstance("SHA-256");
			
			d.reset();
			d.update(valueToEncode.getBytes());
			
			byte [] digest = d.digest(); 
			
			return convertBytesToHex(digest);
		}
		catch (Exception e) {
			System.out.print(e);
			return "";
		}
	}
	
}

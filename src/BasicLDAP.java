import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class BasicLDAP {

	public static void main(String[] args) {
		BasicLDAP basicLdap = new BasicLDAP();
		basicLdap.connect();
	}
	
	public void connect() {
		
		/**
		 * Note that you may need to have a keystore with appropriate certifications setup
		 * 
		 * e.g. make a temp key store from command line...
		 * keytool -keystore clientkeystore -genkey -alias client
		 * keytool -import -trustcacerts -keystore "C:\clientkeystore" -alias "MyKeyFile" -import -file "C:\certs\MyCert.cer"
		 * 
		 * e.g. update global java keystore...
		 * cd C:\Program Files\Java\jre1.8.0_201\lib\security
		 * keytool -import -trustcacerts -keystore "C:\Program Files\Java\jre1.8.0_201\lib\security\cacerts" -alias "MyKeyFile" -import -file "C:\certs\MyCert.cer"
		 */
		// System.setProperty("javax.net.ssl.trustStore", "C:\\clientkeystore"); // Point to a temp key store
		
		String username = "username";
		String userDomain = "MyUserDomain";
		String password = "password";
		String ldapProviderUrl = "ldaps://my.domain.com:636";
		
		Hashtable<String, String> ldapEnvironment = new Hashtable<>();
		ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnvironment.put("com.sun.jndi.ldap.connect.timeout", "10000");
		ldapEnvironment.put(Context.PROVIDER_URL, ldapProviderUrl);
		ldapEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
		Control[] connCtrls = new Control[] { new FastBindConnectionControl() };
		
		LdapContext ctx = null;
		try {
			ctx = new InitialLdapContext(ldapEnvironment, connCtrls);
			String securityPrincipal = (username != null ? username : "") + (userDomain != null ? "@" + userDomain : "");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, securityPrincipal);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtrls);
			System.out.println("Auth success!");
		} catch ( NamingException e ) {
			System.out.println("Error authenticating...");
			e.printStackTrace();
		}
	}
	
	public class FastBindConnectionControl implements Control {
		private static final long serialVersionUID = 1L;
		@Override public String getID() { return "1.2.333.444444.5.6.7777"; }
		@Override public boolean isCritical() { return true; }
		@Override public byte[] getEncodedValue() { return null; }
	}
	
}

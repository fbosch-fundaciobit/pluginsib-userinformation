package org.fundaciobit.plugins.userinformation.ldap;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.fundaciobit.plugins.userinformation.IUserInformationPlugin;
import org.fundaciobit.plugins.userinformation.RolesInfo;
import org.fundaciobit.plugins.userinformation.UserInfo;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;
import org.fundaciobit.plugins.utils.CertificateUtils;
import org.fundaciobit.plugins.utils.ldap.LDAPConstants;
import org.fundaciobit.plugins.utils.ldap.LDAPUser;
import org.fundaciobit.plugins.utils.ldap.LDAPUserManager;

/**
 * 
 * @author anadal
 *
 */
public class LdapUserInformationPlugin extends AbstractPluginProperties
   implements IUserInformationPlugin {
  
  
  protected final Logger log = Logger.getLogger(getClass());
  
  public static final String LDAP_BASE_PROPERTIES = USERINFORMATION_BASE_PROPERTY;

  private LDAPUserManager ldapUserManager = null;

  /**
   * 
   */
  public LdapUserInformationPlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public LdapUserInformationPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  /**
   * @param propertyKeyBase
   */
  public LdapUserInformationPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  public LDAPUserManager getLDAPUserManager() {

    if (ldapUserManager == null) {

      Properties ldapProperties = new Properties();

      for (String attrib :  LDAPConstants.LDAP_PROPERTIES) {
	    String value = getProperty(LDAP_BASE_PROPERTIES + attrib);
		if (value == null) {
		    System.err.println("Property[" + LDAP_BASE_PROPERTIES + attrib + " is NULL");
		} else {
          ldapProperties.setProperty(attrib, value);
		}
      }

      ldapUserManager = new LDAPUserManager(ldapProperties);
    }
    return ldapUserManager;
  }

  @Override
  public RolesInfo getRolesByUsername(String username) throws Exception {

    LDAPUserManager ldapManager = getLDAPUserManager();
    
    List<String> roles = ldapManager.getRolesOfUser(username);
    
    if (roles == null) {
      return null;
    } else {
      RolesInfo roleInfo = new RolesInfo(username, roles.toArray(new String[roles.size()]));
      return roleInfo;
    }
  }

  
  public UserInfo getUserInfoByAdministrationID(String nif) throws Exception {
    final boolean paramIsNif = true;
    return getUserInfo(paramIsNif, nif);
  }


  public UserInfo getUserInfoByUserName(String username) throws Exception {
    final boolean paramIsNif = false;
    return getUserInfo(paramIsNif, username);
  }
  
  
  
  private UserInfo getUserInfo(boolean paramIsNif, String param) throws Exception {
  
    
    LDAPUserManager ldapManager = getLDAPUserManager();
    LDAPUser ldapUser;
    if (paramIsNif) {
      ldapUser = ldapManager.getUserByAdministrationID(param);
    } else {
      ldapUser = ldapManager.getUserByUsername(param);
    }
    
    if (ldapUser == null) {
      return null;
    }
    
    UserInfo info = new UserInfo();
    info.setLanguage("ca");
    info.setName(ldapUser.getName());
    if(ldapUser.getSurname1() == null) {
      info.setSurname1(ldapUser.getSurnames());
      info.setSurname2(ldapUser.getSurname2());
    } else {
      info.setSurname1(ldapUser.getSurname1());
      info.setSurname2(ldapUser.getSurname2());
    }

    info.setAdministrationID(ldapUser.getAdministrationID());
    info.setUsername(ldapUser.getUserName());
    info.setEmail(ldapUser.getEmail());
    info.setPhoneNumber(ldapUser.getTelephoneNumber());
    
    return info;

  }


  @Override
  public boolean authenticate(String username, String password) throws Exception {
    
    LDAPUserManager ldapManager = getLDAPUserManager();
    
    return ldapManager.authenticateUser(username, password);

  }


  @Override
  public boolean authenticate(X509Certificate certificate) throws Exception {
    
    if (certificate == null) {
      return false;
    }
    
    String nif = CertificateUtils.getDNI(certificate);
    if (nif == null) {
      throw new Exception("No puc extreure el NIF del Certificat " + certificate.toString());
    }

    return (getUserInfoByAdministrationID(nif) == null)? false : true;
    
  }


  @Override
  public String[] getAllUsernames() throws Exception {
    LDAPUserManager ldapManager = getLDAPUserManager();
    List<String> usernames = ldapManager.getAllUserNames();
    return usernames.toArray(new String[usernames.size()]);
  }

  @Override
  public String[] getUsernamesByRol(String rol) throws Exception {
    LDAPUserManager ldapManager = getLDAPUserManager();
    List<String> allUsernames = ldapManager.getAllUserNames();
    
    List<String> usernames = new ArrayList<String>();
    for (String un : allUsernames) {
      List<String> roles = ldapManager.getRolesOfUser(un);
      if (roles.contains(rol)) {
        usernames.add(un);
      }
    }
    return usernames.toArray(new String[usernames.size()]);
  }

}

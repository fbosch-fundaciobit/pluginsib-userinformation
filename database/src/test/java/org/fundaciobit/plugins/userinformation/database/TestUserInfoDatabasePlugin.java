package org.fundaciobit.plugins.userinformation.database;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.plugins.userinformation.IUserInformationPlugin;
import org.fundaciobit.plugins.userinformation.UserInfo;
import org.fundaciobit.plugins.utils.PluginsManager;

import javax.naming.*;

/**
 * 
 * @author anadal
 *
 */
public class TestUserInfoDatabasePlugin {

  
  public static void main(String[] args) {
    
    Connection connection = null;
    try {

      
     
      File f = new File("test.properties");

      
      if (!f.exists()) {

        throw new Exception("You must define test.properties. Copy from test.properties.sample");

      }

      Properties databaseProperties = new Properties();
      databaseProperties.load(new FileInputStream(f));
      
      
      String username = databaseProperties.getProperty("test.username");
      String password = databaseProperties.getProperty("test.password");
      
      // Passam pro
      IUserInformationPlugin databasePlugin;
      databasePlugin = (IUserInformationPlugin)PluginsManager.instancePluginByClass(TestDataBaseUserInformationPlugin.class, "es.caib.portafib.", databaseProperties);
      
      UserInfo userInfo = databasePlugin.getUserInfoByUserName(username);
      if (userInfo != null) {
        System.out.println( " ------- getUserInfoByUserName ------- ");
        System.out.println(userInfo.toString());
        System.out.println();
      }


      UserInfo ui = databasePlugin.getUserInfoByAdministrationID(userInfo.getAdministrationID());
      
      if (ui != null) {
        System.out.println( " ------- getUserInfoByAdministrationID ------- ");
        System.out.println(ui.toString());
        System.out.println();
      }
      
      
      org.fundaciobit.plugins.userinformation.RolesInfo rolesInfo = databasePlugin.getRolesByUsername(username);
      if (rolesInfo != null) {
        System.out.println( " ------- rolesInfo(" + username + ") ------- ");
        String[] roles = rolesInfo.getRoles();
        
        for (String rol : roles) {
          System.out.println("    - " + rol );
        }
        System.out.println();
      }
      
      
      String[] roles = new String[] { "PFI_ADMIN", "PFI_USER" };

      for (int i = 0; i < roles.length; i++) {
        System.out.println();
        System.out.println( " ------- Users with role " + roles[i] + ") ------- ");
        String[] users = databasePlugin.getUsernamesByRol(roles[i]);
        System.out.println(Arrays.toString(users));        
      }
      
      
      
      
      
      //LDAPUserManager um = ldap.getLDAPUserManager();

      // 1.- Mètode per autenticar amb usuari contrasenya
      System.out.println();
      System.out.println("------------- Authenticate: " + databasePlugin.authenticate(username, password));
      System.out.println();
      System.out.println("------------- Authenticate amb contrasenya erronia: " + databasePlugin.authenticate(username, password + "22"));


      // 2.- LLista de Tots els Usuaris
      String[] all = databasePlugin.getAllUsernames();
      System.out.println();
      System.out.println(" ------------------ ALL USERNAMES (" + all.length + ")");
      for (int i = 0; i < all.length ; i++) {
        System.out.println((i++) + ".- " + all[i]);
        if (i > 10) {
          System.out.println("...");
          break;
        }
      }

      // ========= Altres mètodes ========

      // 3.- Obtenir Usuari

      // 3.1.- Obtenir usuari per Nom
      System.out.println();
      System.out.println(" ------------------ getUserInfoByUserName -----------");
      UserInfo u = databasePlugin.getUserInfoByUserName(username);
      System.out.println("    - Info usuari [ username = " + username + "]: ");
      System.out.println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
      System.out.println("          + Nom Complet : " + u.getFullName());
      System.out.println("          + NIF: " + u.getAdministrationID());
      System.out.println("          + Email: " + u.getEmail());
      
      

      // 3.2.- Obtenir usuari per NIF
      String nif = u.getAdministrationID();
      u = databasePlugin.getUserInfoByAdministrationID(nif);
      System.out.println();
      System.out.println(" ------------------ getUserInfoByAdministrationID -----------");
      System.out.println("    - Info usuari [ nif ="  + nif + "]: ");
      System.out.println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
      System.out.println("          + Nom Complet : " + u.getFullName());
      System.out.println("          + NIF: " + u.getAdministrationID());
      System.out.println("          + Email: " + u.getEmail());
       

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
        }
      }
    }
  } 
  
  
  public static class TestDataBaseUserInformationPlugin extends DataBaseUserInformationPlugin {
    
    
    
    
    /**
     * 
     */
    public TestDataBaseUserInformationPlugin() {
      super();
      // TODO Auto-generated constructor stub
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public TestDataBaseUserInformationPlugin(String propertyKeyBase, Properties properties) {
      super(propertyKeyBase, properties);
      // TODO Auto-generated constructor stub
    }

    /**
     * @param propertyKeyBase
     */
    public TestDataBaseUserInformationPlugin(String propertyKeyBase) {
      super(propertyKeyBase);
      // TODO Auto-generated constructor stub
    }
    
    


    protected Connection getConnection(String jndi) throws NamingException, SQLException {
      try {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/seycon","seycon", "seycon");
        return connection;
      } catch (ClassNotFoundException e) {
        throw new SQLException(e);
      }
    }
  }
  
  
}

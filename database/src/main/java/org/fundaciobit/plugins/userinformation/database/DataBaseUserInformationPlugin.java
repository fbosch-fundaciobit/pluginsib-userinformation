package org.fundaciobit.plugins.userinformation.database;

import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.fundaciobit.plugins.userinformation.IUserInformationPlugin;
import org.fundaciobit.plugins.userinformation.RolesInfo;
import org.fundaciobit.plugins.userinformation.UserInfo;
import org.fundaciobit.plugins.userinformation.UserInfo.Gender;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;
import org.fundaciobit.plugins.utils.CertificateUtils;

/**
 * 
 * @author anadal
 *
 */
public class DataBaseUserInformationPlugin extends AbstractPluginProperties
  implements IUserInformationPlugin {

  protected final Logger log = Logger.getLogger(getClass());

  public static final String DB_BASE_PROPERTIES = USERINFORMATION_BASE_PROPERTY + "database.";
  
  public static final String DATABASE_JNDI= DB_BASE_PROPERTIES + "jndi";

  public static final String USERS_TABLE=DB_BASE_PROPERTIES + "users_table";
  
  public static final String USERS_USERNAME_COLUMN=DB_BASE_PROPERTIES + "username_column";
  
  public static final String USERS_PASSWORD_COLUMN=DB_BASE_PROPERTIES + "password_column";
  
  public static final String USERS_ADMINISTRATIONID_COLUMN=DB_BASE_PROPERTIES + "administrationid_column";
  
  public static final String USERS_NAME_COLUMN=DB_BASE_PROPERTIES + "name_column";

  public static final String USERS_SURNAME_1_COLUMN=DB_BASE_PROPERTIES + "surname_1_column";
  
  public static final String USERS_SURNAME_2_COLUMN=DB_BASE_PROPERTIES + "surname_2_column";

  public static final String USERS_LANGUAGE_COLUMN=DB_BASE_PROPERTIES + "language_column";

  public static final String USERS_TELEPHONE_COLUMN=DB_BASE_PROPERTIES + "telephone_column";
  
  public static final String USERS_EMAIL_COLUMN=DB_BASE_PROPERTIES + "email_column";
  
  public static final String USERS_GENDER_COLUMN=DB_BASE_PROPERTIES + "gender_column";

  public static final String USERROLES_TABLE=DB_BASE_PROPERTIES + "userroles_table";

  public static final String USERROLES_ROLENAME_COLUMN=DB_BASE_PROPERTIES + "userroles_rolename_column";

  public static final String USERROLES_USERNAME_COLUMN=DB_BASE_PROPERTIES + "userroles_username_column";

  
  public static final String[] USERS_OPTIONAL_COLUMNS = {
    USERS_NAME_COLUMN, USERS_SURNAME_1_COLUMN, USERS_SURNAME_2_COLUMN,
    USERS_LANGUAGE_COLUMN, USERS_TELEPHONE_COLUMN, USERS_EMAIL_COLUMN,
    USERS_GENDER_COLUMN
  };
  
  /**
   * 
   */
  public DataBaseUserInformationPlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   */
  public DataBaseUserInformationPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public DataBaseUserInformationPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }


  
  
  @Override
  public RolesInfo getRolesByUsername(String username) throws Exception {

    if (username == null) {
      throw new NullPointerException("Parameter UserName is NULL");
    }

    String rolename = getPropertyRequired(USERROLES_ROLENAME_COLUMN);
    String roletable = getPropertyRequired(USERROLES_TABLE);
    String usercolumn = getPropertyRequired(USERROLES_USERNAME_COLUMN);
    String jndi = getPropertyRequired(DATABASE_JNDI);

    final String query = "select " + rolename + " from " + roletable 
        + " where " + usercolumn + " = ?";

    // TODO Llevar
    //log.info("QUERY = " + query);
    
    List<String> roles = new ArrayList<String>();
    Connection c = getConnection(jndi);
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        try {
          while (rs.next()) {
            roles.add(rs.getString(1));
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }

    

    RolesInfo info = new RolesInfo(username, roles.toArray(new String[roles
        .size()]));

    return info;
  }

  @Override
  public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {
    final boolean paramIsNif = true;    
    return getUserInfo(paramIsNif, administrationID);
  }

  @Override
  public UserInfo getUserInfoByUserName(String username) throws Exception {
    final boolean paramIsNif = false;
    return getUserInfo(paramIsNif, username);
  }
  
  
  
  private UserInfo getUserInfo(boolean paramIsNif, String param) throws Exception {

    if (param == null) {
      String paramName = paramIsNif? "AdministrationID" : "UserName";
      throw new NullPointerException("Parameter " + paramName +" is NULL");
    }

    String userstable = getPropertyRequired(USERS_TABLE);
    String usernamecolumn = getPropertyRequired(USERS_USERNAME_COLUMN);
    String nifcolumn = getPropertyRequired(USERS_ADMINISTRATIONID_COLUMN);
    
    StringBuffer queryColumns = new StringBuffer();
    ArrayList<String> additionalColumns = new ArrayList<String>();
    for (int i = 0; i < USERS_OPTIONAL_COLUMNS.length; i++) {
      String column = getProperty(USERS_OPTIONAL_COLUMNS[i]);
      if (column != null && column.trim().length() != 0) {
        additionalColumns.add(USERS_OPTIONAL_COLUMNS[i]);
        queryColumns.append(", ").append(column);
      }
    }
    
    

    //final String field = paramIsNif? nifcolumn : usernamecolumn;
    
    String where;
    if (paramIsNif) {
      where = "(" + nifcolumn + " = ?) OR (" + nifcolumn + " = ? ) OR (" + nifcolumn + " = ? )";
    } else {
      where = usernamecolumn + " = ?";
    }
    
    
    final String query = "select " + usernamecolumn + ", " + nifcolumn + queryColumns.toString()
         + " from "+ userstable + " where " + where;
    
    // TODO Llevar
    //log.info("QUERY = " + query);

    String jndi = getPropertyRequired(DATABASE_JNDI);
    
    Connection c = getConnection(jndi);
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {

        if (paramIsNif) {
          ps.setString(1, param);
          ps.setString(2, param.toUpperCase());
          ps.setString(3, param.toLowerCase());
        } else {
          ps.setString(1, param);
        }

        ResultSet rs = ps.executeQuery();
        try {
          if (rs.next()) {
            String username = rs.getString(1);            
            String dni = rs.getString(2);

            UserInfo info = new UserInfo();
            info.setAdministrationID(dni);
            info.setUsername(username);
            
            int columnIndex = 2;
            
            for(String column : additionalColumns) {
              columnIndex++;
              
              if (USERS_NAME_COLUMN.equals(column)) {
                info.setName(rs.getString(columnIndex));
                continue;
              }
              
              if (USERS_SURNAME_1_COLUMN.equals(column)) {
                info.setSurname1(rs.getString(columnIndex));
                continue;
              }
              
              if (USERS_SURNAME_2_COLUMN.equals(column)) {
                info.setSurname2(rs.getString(columnIndex));
                continue;
              }
              
              if (USERS_LANGUAGE_COLUMN.equals(column)) {
                info.setLanguage(rs.getString(columnIndex));
                continue;
              }

              if (USERS_TELEPHONE_COLUMN.equals(column)) {
                info.setPhoneNumber(rs.getString(columnIndex));
                continue;
              }
              
              if (USERS_EMAIL_COLUMN.equals(column)) {
                info.setEmail(rs.getString(columnIndex));
                continue;
              }
              
              if (USERS_GENDER_COLUMN.equals(column)) {
                String val = rs.getString(columnIndex);
                if (val == null) {
                  info.setGender(null);
                } else {
                  val = val.toLowerCase(); 
                  if (val.equals("true") || val.equals("1") || val.equals("male")) {
                    info.setGender(Gender.MALE);
                  } else {
                    if (val.equals("false") || val.equals("0") || val.equals("female")) {
                      info.setGender(Gender.FEMALE);
                    } else {
                      log.warn("Cannot process gender value: ]" + val + "[");
                      info.setGender(Gender.UNKNOWN);
                    }
                  }
                }
                continue;
              }
              
              if (USERS_PASSWORD_COLUMN.equals(column)) {
                info.setPassword(rs.getString(columnIndex));
                continue;
              }
              
            
            }
            
           return info;
          } else {
            return null;
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }

  }

  private static DataSource datasource;

  protected Connection getConnection(String jndi) throws NamingException, SQLException {

      if (datasource == null) {
        Context ctx = new InitialContext();
        datasource = (DataSource) ctx.lookup(jndi);
      }
      
      Connection c = datasource.getConnection();
      return c;

  }


  @Override
  public boolean authenticate(String username, String password) throws Exception {
    
    if (username == null || password == null) {
      return false;
    }
    
    String passwordColumn = getPropertyRequired(USERS_PASSWORD_COLUMN);
    
    String usersTable = getPropertyRequired(USERS_TABLE);
    
    String userColumn = getPropertyRequired(USERS_USERNAME_COLUMN);
    
    
    String where = userColumn + " = ? AND " + passwordColumn + " = ? ";

    final String query = "select " + userColumn + " from "+ usersTable + " where " + where;

   

    String jndi = getPropertyRequired(DATABASE_JNDI);

    Connection c = getConnection(jndi);
   
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {        
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        try {
          if (rs.next()) {
            return true;
          } else {
            return false;
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }
    
    
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
  
    String usersTable = getPropertyRequired(USERS_TABLE);
    String nifColumn = getPropertyRequired(USERS_ADMINISTRATIONID_COLUMN);

    String where = nifColumn + " = ? OR " + nifColumn + " = ? OR " + nifColumn + " = ?";
    final String query = "select " + nifColumn + " from "+ usersTable + " where " + where;

    String jndi = getPropertyRequired(DATABASE_JNDI);

    Connection c = getConnection(jndi);
   
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {
        ps.setString(1, nif);
        ps.setString(2, nif.toUpperCase());
        ps.setString(3, nif.toLowerCase());
 
        ResultSet rs = ps.executeQuery();
        try {
          if (rs.next()) {
            return true;
          } else {
            return false;
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }
    
    
    
    
  }


  @Override
  public String[] getAllUsernames() throws Exception {
    
    String usersTable = getPropertyRequired(USERS_TABLE);
    String userColumn = getPropertyRequired(USERS_USERNAME_COLUMN);
    final String query = "select " + userColumn + " from "+ usersTable;

    String jndi = getPropertyRequired(DATABASE_JNDI);

    Connection c = getConnection(jndi);
    List<String> usuaris = new ArrayList<String>();
    
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {
        ResultSet rs = ps.executeQuery();
        try {
          while (rs.next()) {
             usuaris.add(rs.getString(1));
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }
    return usuaris.toArray(new String[usuaris.size()]);
    
   

  }

  @Override
  public String[] getUsernamesByRol(String rol) throws Exception {
    
    if (rol == null) {
      return new String[] {};
    }

    
    String userRolesTable = getPropertyRequired(USERROLES_TABLE);
    String rolename = getPropertyRequired(USERROLES_ROLENAME_COLUMN);
    
    String usercolumn = getPropertyRequired(USERROLES_USERNAME_COLUMN);
    
    final String where = rolename + " = ? ";
    final String query = "select " + usercolumn + " from "+ userRolesTable + " where " + where;

    String jndi = getPropertyRequired(DATABASE_JNDI);

    Connection c = getConnection(jndi);
    List<String> usuaris = new ArrayList<String>();
    
    try {
      PreparedStatement ps = c.prepareStatement(query);
      try {        
        ps.setString(1, rol);
        ResultSet rs = ps.executeQuery();
        try {
          while (rs.next()) {
             usuaris.add(rs.getString(1));
          }
        } finally {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }
      } finally {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    } finally {
      try {
        c.close();
      } catch (Exception e) {
      }
    }
    return usuaris.toArray(new String[usuaris.size()]);
    
  }

}
